package org.jczh.appliedxml;

import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Map;

import org.jczh.appliedxml.ContainerDefine.ContainerType;
import org.jczh.appliedxml.annotation.ElementArray;
import org.jczh.appliedxml.annotation.ElementList;
import org.jczh.appliedxml.annotation.ElementMap;
import org.jczh.appliedxml.annotation.Serializable;
import org.jczh.appliedxml.annotation.Transient;
import org.jczh.appliedxml.utils.ReflectUtil;
import org.jczh.appliedxml.utils.StringUtil;
import org.jczh.appliedxml.utils.TextTypeUtil;

import com.google.gsoncode.internal.TypeToken;

class DocumentTreeBuilderFactory implements ElementConstructorFactory {
	private Serializer context;
	private final String[] unSerializablePacketPrefixs = { "java", "javax", "android", "com.sun", "org.apache", "com.google" };

	DocumentTreeBuilderFactory(Serializer context) {
		this.context = context;
	}

	@Override
	public <T> ElementConstructor<T> create(Serializer context, TypeToken<T> type) {
		return new DocumentTreeBuilder<T>();
	}

	class DocumentTreeBuilder<T> implements ElementConstructor<T> {

		@SuppressWarnings({ "rawtypes", "unchecked" })
		private Element build(T value) {
			Class type = value.getClass();
			if (TextTypeUtil.isTextType(type))
				return new TextElement(context.getClassNamingStrategy().translateName(type), value != null ? value.toString()
						: null);
			String name = context.getClassNamingStrategy().translateName(type);
			if (type == Object.class) {
				return new UnSerializedElement(name, type, value);
			}
			String packetName = type.getPackage().getName();
			for (String unSerializablePacketPrefix : unSerializablePacketPrefixs) {
				if (packetName.startsWith(unSerializablePacketPrefix)) {
					Serializable serializable = (Serializable) type.getAnnotation(Serializable.class);
					if (serializable == null && !org.jczh.appliedxml.XmlSerializable.class.isAssignableFrom(type))
						return new UnSerializedElement(name, type, value);
				}
			}

			Element document;
			String prefix = context.getDefaultElementPrefix();
			org.jczh.appliedxml.annotation.Document documentAnnotation = (org.jczh.appliedxml.annotation.Document) type
					.getAnnotation(org.jczh.appliedxml.annotation.Document.class);
			if (documentAnnotation != null) {
				document = new Document();
				if (!StringUtil.isEmpty(documentAnnotation.prefix()))
					prefix = documentAnnotation.prefix();
				if (!StringUtil.isEmpty(documentAnnotation.name()))
					name = documentAnnotation.name();
			} else {
				document = new Element();

			}
			document.setName(name);
			document.setPrefix(prefix);
			document.setNamespaces(ReflectUtil.extractNamespaces(type));
			buildChildNodes(document, value);
			return document;
		}

		public void buildChildNodes(Element parent, Object parentValue) {

			Class<?> type = parentValue.getClass();
			while (type != Object.class) {
				Field[] fields = type.getDeclaredFields();
				for (Field field : fields) {
					Node node = buildChildNode(parent, parentValue, field);
					if (node != null) {
						if (node.isAttribute()) {
							parent.addAttribute((Attribute) node);
						} else if (node.isElement()) {
							parent.addChildElement((Element) node);
						}
					}
				}
				if (!context.isAssociatedWithSuperClass())
					break;
				type = type.getSuperclass();
			}
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		public Node buildChildNode(Element parent, Object parentValue, Field field) {

			boolean isAttribute = false;
			Node node = null;
			String name = context.getFieldNamingStrategy().translateName(field);
			String prefix = null;
			boolean isTransient = field.getAnnotation(Transient.class) != null;
			boolean required = context.isNullValueSerializeRequired();
			if (!isTransient) {

				org.jczh.appliedxml.annotation.Attribute attributeAnnotation = field
						.getAnnotation(org.jczh.appliedxml.annotation.Attribute.class);
				if (attributeAnnotation != null) {
					if (!StringUtil.isEmpty(attributeAnnotation.name()))
						name = attributeAnnotation.name();
					isAttribute = true;
					prefix = StringUtil.isEmpty(attributeAnnotation.prefix()) ? context.getDefaultAttributePrefix()
							: attributeAnnotation.prefix();
					required = attributeAnnotation.required();
				}

				Object fieldValue = null;
				try {
					field.setAccessible(true);
					fieldValue = field.get(parentValue);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (!isAttribute) {
					prefix = context.getDefaultElementPrefix();
					org.jczh.appliedxml.annotation.Element elementAnnotation = field
							.getAnnotation(org.jczh.appliedxml.annotation.Element.class);
					ElementList elementList = field.getAnnotation(ElementList.class);
					ElementArray elementArray = field.getAnnotation(ElementArray.class);
					ElementMap elementMap = field.getAnnotation(ElementMap.class);

					if (Modifier.isStatic(field.getModifiers()) && elementAnnotation == null && elementList == null
							&& elementArray == null && elementMap == null) {
						return null;
					}
					TypeToken<?> fieldType = TypeToken.get(field.getGenericType());
					ContainerDefine containerDefine = null;
					if (elementAnnotation != null) {
						if (!StringUtil.isEmpty(elementAnnotation.name()))
							name = elementAnnotation.name();
						if (!StringUtil.isEmpty(elementAnnotation.prefix()))
							prefix = elementAnnotation.prefix();
						required = elementAnnotation.required();
					} else if (elementList != null && Collection.class.isAssignableFrom(fieldType.getRawType())) {
						if (!StringUtil.isEmpty(elementList.name()))
							name = elementList.name();
						if (!StringUtil.isEmpty(elementList.prefix()))
							prefix = elementList.prefix();
						required = elementList.required();
						containerDefine = new ContainerDefine(ContainerType.List, elementList.entry(), null, null, false, false);
					} else if (elementArray != null
							&& (fieldType.getType() instanceof GenericArrayType || fieldType.getType() instanceof Class
									&& ((Class<?>) fieldType.getType()).isArray())) {
						if (!StringUtil.isEmpty(elementArray.name()))
							name = elementArray.name();
						if (!StringUtil.isEmpty(elementArray.prefix()))
							prefix = elementArray.prefix();
						required = elementArray.required();
						containerDefine = new ContainerDefine(ContainerType.Array, elementArray.entry(), null, null, false, false);
					} else if (elementMap != null && Map.class.isAssignableFrom(fieldType.getRawType())) {
						if (!StringUtil.isEmpty(elementMap.name()))
							name = elementMap.name();
						if (!StringUtil.isEmpty(elementMap.prefix()))
							prefix = elementMap.prefix();
						required = elementMap.required();
						containerDefine = new ContainerDefine(ContainerType.Map, elementMap.entry(), elementMap.key(),
								elementMap.value(), elementMap.keyAsAttribute(), elementMap.valueAsText());
					}

					boolean serializable = validateSerializable(field);
					Element element = null;
					if (serializable) {
						element = new Element();
						if (fieldValue != null)
							buildChildNodes(element, fieldValue);
					} else {
						if (TextTypeUtil.isTextType(field.getType())) {
							String text = fieldValue != null ? fieldValue.toString() : "";
							TextElement textElement = new TextElement();
							textElement.setText(text);
							element = textElement;
						} else {
							TypeToken type = TypeToken.get(field.getGenericType());

							ElementConstructor constructor = context.getElementConstructorManager().getConstructor(type);
							if (constructor != null)
								element = constructor.construct(fieldValue);
							else
								element = new UnSerializedElement(context.getClassNamingStrategy().translateName(
										type.getRawType()), field.getGenericType(), fieldValue);
							if (element instanceof UnSerializedElement) {
								((UnSerializedElement) element).setContainerDefine(containerDefine);
							}
						}
					}
					element.setNamespaces(ReflectUtil.extractNamespaces(field));
					element.setRequired(required);
					element.setName(name);
					element.setPrefix(prefix);
					node = element;
				} else {
					String valueStr;
					try {
						valueStr = fieldValue != null ? fieldValue.toString() : "";
					} catch (Exception e) {
						e.printStackTrace();
						valueStr = "";
					}
					node = new Attribute(name, prefix, valueStr, required);
				}

			}

			return node;
		}

		@SuppressWarnings("rawtypes")
		private boolean validateSerializable(Field field) {
			Class type = field.getType();
			if (type.isInterface())
				return false;
			if (!Object.class.isAssignableFrom(type))
				return false;
			if (Collection.class.isAssignableFrom(type))
				return false;
			if (Map.class.isAssignableFrom(type))
				return false;
			if (org.jczh.appliedxml.XmlSerializable.class.isAssignableFrom(type))
				return true;
			Serializable serializable = field.getAnnotation(Serializable.class);
			if (serializable != null)
				return serializable.value();
			else
				return field.getType().getPackage() == field.getDeclaringClass().getPackage();
		}

		/**
		 * 将对象解析并构建为{@link Element} ,为了提高API的易用性仅对传入对象做了粗略限制
		 * 默认下列情况不会对其进行解析和构建，将直接返回一个未序列化的{@link UnSerializedElement}
		 * <ol>
		 * <li>属性包名和所属类包名不一致
		 * <li>
		 * 包名以java、javax、android、com.google、org.apache、com.sun开头
		 * </ol>
		 * 当遇到上述情况，又需要解析时 可以实现{@link XmlSerializable}或者使用注解{@link Serializable}
		 * 
		 * @param object
		 */

		@Override
		public Element construct(T object) {
			return build(object);
		}

	}

}
