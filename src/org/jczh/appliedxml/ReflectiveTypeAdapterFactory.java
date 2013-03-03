package org.jczh.appliedxml;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jczh.appliedxml.ContainerDefine.ContainerType;
import org.jczh.appliedxml.Element.Namespace;
import org.jczh.appliedxml.PullReader.EventAttribute;
import org.jczh.appliedxml.PullReader.Start;
import org.jczh.appliedxml.annotation.Attribute;
import org.jczh.appliedxml.annotation.Element;
import org.jczh.appliedxml.annotation.ElementArray;
import org.jczh.appliedxml.annotation.ElementList;
import org.jczh.appliedxml.annotation.ElementMap;
import org.jczh.appliedxml.annotation.Serializable;
import org.jczh.appliedxml.utils.ReflectUtil;
import org.jczh.appliedxml.utils.StringUtil;
import org.jczh.appliedxml.utils.TextTypeUtil;

import com.google.gsoncode.internal.$Gson$Types;
import com.google.gsoncode.internal.ConstructorConstructor;
import com.google.gsoncode.internal.ObjectConstructor;
import com.google.gsoncode.internal.TypeToken;

final class ReflectiveTypeAdapterFactory implements TypeAdapterFactory {
	private final ConstructorConstructor constructorConstructor;
	private final Excluder excluder = Excluder.DEFAULT;

	ReflectiveTypeAdapterFactory(ConstructorConstructor constructorConstructor) {
		this.constructorConstructor = constructorConstructor;
	}

	public boolean checkFieldSerialialbe(Field f, boolean serialize) {
		return !excluder.excludeClass(f.getType(), serialize) && !excluder.excludeField(f, serialize);
	}

	public <T> TypeAdapter<T> create(Serializer context, final TypeToken<T> type) {
		Class<? super T> raw = type.getRawType();
		if (!Object.class.isAssignableFrom(raw)) {
			return null; // it's a primitive!
		}
		Serializable serializable = raw.getAnnotation(Serializable.class);
		if (serializable != null && !serializable.value())
			throw new NodeException("unerializable class " + raw.getSimpleName());
		return new Adapter<T>(context, type);
	}

	static class BoundField {
		String name;
		// final boolean serialized;
		String prefix;
		TypeToken<?> fieldType;
		boolean isElement = true;
		boolean isAttribute;
		boolean isRequired;
		Field field;
		ContainerDefine containerDefine;
		@SuppressWarnings("rawtypes")
		TypeAdapter typeAdapter;
		ArrayList<Namespace> namespaces;
		final boolean serialized;
		final boolean deserialized;

		protected BoundField(Serializer context, Field field, TypeToken<?> fieldType, boolean serialized, boolean deserialized) {
			this.serialized = serialized;
			this.deserialized = deserialized;
			name = context.getFieldNamingStrategy().translateName(field);
			isRequired = context.isNullValueSerializeRequired();
			Attribute attributeAnnotation = field.getAnnotation(Attribute.class);
			if (attributeAnnotation != null) {
				if (!StringUtil.isEmpty(attributeAnnotation.name()))
					name = attributeAnnotation.name();
				isAttribute = true;
				isElement = false;
				prefix = StringUtil.isEmpty(attributeAnnotation.prefix()) ? context.getDefaultAttributePrefix()
						: attributeAnnotation.prefix();
				isRequired = attributeAnnotation.required();
			} else {
				prefix = context.getDefaultElementPrefix();
				Element elementAnnotation = field.getAnnotation(Element.class);
				ElementArray elementArray = field.getAnnotation(ElementArray.class);
				ElementList elementList = field.getAnnotation(ElementList.class);
				ElementMap elementMap = field.getAnnotation(ElementMap.class);

				if (elementAnnotation != null) {
					if (!StringUtil.isEmpty(elementAnnotation.name()))
						name = elementAnnotation.name();
					if (!StringUtil.isEmpty(elementAnnotation.prefix()))
						prefix = elementAnnotation.prefix();
					isRequired = elementAnnotation.required();
				} else if (elementList != null && Collection.class.isAssignableFrom(fieldType.getRawType())) {
					if (!StringUtil.isEmpty(elementList.name()))
						name = elementList.name();
					if (!StringUtil.isEmpty(elementList.prefix()))
						prefix = elementList.prefix();
					isRequired = elementList.required();
					// elementName=elementList.entry();
					containerDefine = new ContainerDefine(ContainerType.List, elementList.entry(), null, null, false, false);
				} else if (elementMap != null && Map.class.isAssignableFrom(fieldType.getRawType())) {
					if (!StringUtil.isEmpty(elementMap.name()))
						name = elementMap.name();
					if (!StringUtil.isEmpty(elementMap.prefix()))
						prefix = elementMap.prefix();
					isRequired = elementMap.required();
					containerDefine = new ContainerDefine(ContainerType.Map, elementMap.entry(), elementMap.key(),
							elementMap.value(), elementMap.keyAsAttribute(), elementMap.valueAsText());
				} else if (elementArray != null
						&& (fieldType.getType() instanceof GenericArrayType || fieldType.getType() instanceof Class
								&& ((Class<?>) fieldType.getType()).isArray())) {
					if (!StringUtil.isEmpty(elementArray.name()))
						name = elementArray.name();
					if (!StringUtil.isEmpty(elementArray.prefix()))
						prefix = elementArray.prefix();
					isRequired = elementArray.required();
					// elementName=elementList.entry();
					containerDefine = new ContainerDefine(ContainerType.Array, elementArray.entry(), null, null, false, false);
				}

			}
			this.field = field;
			this.fieldType = fieldType;
			this.namespaces = ReflectUtil.extractNamespaces(field);
			typeAdapter = context.getAdapter(fieldType);
		}

		public Object get(Object object) throws IllegalArgumentException, IllegalAccessException {
			field.setAccessible(true);
			return field.get(object);
		}

	}

	public final class Adapter<T> extends TypeAdapter<T> {
		private final ObjectConstructor<T> constructor;
		private final Map<String, BoundField> boundFields;
		private final Map<String, BoundField> boundAttrs;
		private final Serializer context;

		private Adapter(Serializer context, TypeToken<T> type) {
			this.constructor = constructorConstructor.get(type);
			this.context = context;
			boundFields = new LinkedHashMap<String, BoundField>();
			boundAttrs = new LinkedHashMap<String, BoundField>();
			getBoundFields(context, type);
		}

		@SuppressWarnings("rawtypes")
		@Override
		public T read(XmlReader in) throws IOException {

			if (!in.hasNext())
				return null;
			T instance = constructor.construct();
			EventNode last = in.last();
			if (last != null && last.isStart()) {
				Start start = (Start) last;
				ArrayList<EventAttribute> attributes = start.getEventAttributes();
				for (EventAttribute attribute : attributes) {
					BoundField boundField = boundAttrs.get(attribute.getName());
					if (boundField != null) {
						try {
							boundField.field.setAccessible(true);
							boundField.field.set(instance,
									TextTypeUtil.convert(boundField.fieldType.getRawType(), attribute.getValue()));
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
							throw new NodeException(e);
						} catch (IllegalAccessException e) {
							e.printStackTrace();
							throw new NodeException(e);
						}
					}

				}
			}

			while (in.hasNext()) {
				EventNode node = in.next();
				if (node.isStart()) {
					// boundFields.get(key);
					BoundField boundField = boundFields.get(node.getName());
					if (boundField != null) {
						if (!boundField.deserialized)
							continue;
						TypeAdapter adapter = boundField.typeAdapter;
						if (boundField.containerDefine != null) {
							TypeAdapter containerAdapter = context.getTypeAdapterManager().getContainerTypeAdater(
									boundField.fieldType, boundField.containerDefine);
							if (containerAdapter != null) {
								adapter = containerAdapter;
							}
						}
						Object obj = adapter.read(in);
						try {
							boundField.field.setAccessible(true);
							boundField.field.set(instance, obj);
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
							throw new NodeException(e);
						} catch (IllegalAccessException e) {
							e.printStackTrace();
							throw new NodeException(e);
						}
					}

				}
				if (node.isEnd() && last.getName().equals(node.getName()) && last.depth() == node.depth()) {
					break;
				}

			}
			return instance;

		}

		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		public void write(XmlWriter out, T value) throws IOException {
			try {
				for (BoundField boundAttr : boundAttrs.values()) {
					Object fieldValue = boundAttr.get(value);
					if (fieldValue != null || boundAttr.isRequired)
						out.writeAttribute(boundAttr.name, fieldValue != null ? fieldValue.toString() : "", boundAttr.prefix);
				}
				for (BoundField boundField : boundFields.values()) {
					// if (boundField.serialized) {
					if (!boundField.serialized)
						continue;
					Object fieldValue = boundField.get(value);
					if (fieldValue != null || boundField.isRequired) {
						out.writeStart(boundField.name, boundField.prefix);
						if (boundField.namespaces != null)
							for (Namespace namespace : boundField.namespaces) {
								out.writeNamespace(namespace.reference, namespace.prefix);
							}
						if (fieldValue != null) {
							TypeAdapter adapter = new TypeAdapterRuntimeTypeWrapper(context, boundField.typeAdapter,
									boundField.fieldType.getType());
							if (boundField.containerDefine != null) {
								TypeAdapter containerAdapter = context.getTypeAdapterManager().getContainerTypeAdater(
										boundField.fieldType, boundField.containerDefine);
								if (containerAdapter != null) {
									adapter = containerAdapter;
								}
							}
							adapter.write(out, fieldValue);
						}
						out.writeEnd(boundField.name, boundField.prefix);
					}
				}
			} catch (IllegalArgumentException e) {
				throw new NodeException(e);
			} catch (IllegalAccessException e) {
				throw new NodeException(e);
			} finally {
				out.flush();
			}

		}

		private void getBoundFields(Serializer context, TypeToken<?> type) {
			Class<?> raw = type.getRawType();
			if (raw.isInterface()) {
				return;
			}
			Type declaredType = type.getType();
			while (raw != Object.class) {
				Field[] fields = raw.getDeclaredFields();
				for (Field field : fields) {
					field.setAccessible(true);
					Type fieldType = $Gson$Types.resolve(type.getType(), raw, field.getGenericType());
					boolean serialize = checkFieldSerialialbe(field, true);
					boolean deserialize = checkFieldSerialialbe(field, false);
					if (!serialize && !deserialize) {
						continue;
					}
					BoundField boundField = new BoundField(context, field, TypeToken.get(fieldType), serialize, deserialize);
					BoundField previous = null;
					if (boundField.isAttribute) {
						previous = boundAttrs.put(boundField.name, boundField);
					} else if (boundField.isElement) {
						previous = boundFields.put(boundField.name, boundField);
					}
					if (previous != null) {
						throw new IllegalArgumentException(declaredType + " declares multiple JSON fields named " + previous.name);
					}

				}
				if (!context.isAssociatedWithSuperClass()){
					break;
				}
				type = TypeToken.get($Gson$Types.resolve(type.getType(), raw, raw.getGenericSuperclass()));
				raw = type.getRawType();
			}
		}

		@SuppressWarnings("unused")
		private boolean isInnerClass(Class<?> clazz) {
			return clazz.isMemberClass() && !isStatic(clazz);
		}

		private boolean isStatic(Class<?> clazz) {
			return (clazz.getModifiers() & Modifier.STATIC) != 0;
		}
	}

}
