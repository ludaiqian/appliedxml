package org.jczh.appliedxml;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;

import org.jczh.appliedxml.Element.Namespace;
import org.jczh.appliedxml.annotation.Document;
import org.jczh.appliedxml.utils.ReflectUtil;
import org.jczh.appliedxml.utils.StringUtil;

import com.google.gsoncode.internal.Primitives;
import com.google.gsoncode.internal.TypeToken;

/**
 * Serializer提供对象与XML之间的序列化和反序列化转换。
 * 
 * @author ludaiqian@126.com
 * @version 1.0 beta
 * @since 1.0
 */
public class Serializer {

	private boolean nullValueSerializeRequired = true;
	private final TypeAdapterManager typeAdapterManager;
	private final ElementConstructorManager elementConstructorManager;
	private FieldNamingStrategy fieldNamingStrategy;
	private ClassNamingStrategy classNamingStrategy;
	private String defaultElementPrefix;
	private String defaultAttributePrefix;
	private Format formatter;
	private boolean associatedWithSuperClass = true;

	public Serializer() {
		typeAdapterManager = new TypeAdapterManager(this);
		elementConstructorManager = new ElementConstructorManager(this);
		fieldNamingStrategy = new FieldNamingStrategy() {

			@Override
			public String translateName(Field f) {
				return f.getName();
			}
		};
		classNamingStrategy = new ClassNamingStrategy() {

			@SuppressWarnings("rawtypes")
			@Override
			public String translateName(Class type) {
				String typeName = type.getSimpleName();
				return typeName.substring(0, 1).toLowerCase() + typeName.substring(1);
			}
		};

		formatter = new Format(0);
	}

	/**
	 * 将 对象序列化为XML
	 * 
	 * @param object
	 * @return
	 * @throws IOException
	 */
	public String toXml(Object object) throws IOException {
		return toXml(object, object.getClass());
	}

	/**
	 * 将对象按照给定的 type序列化为XML
	 * 
	 * @param object
	 * @param typeOfSrc
	 * @return
	 * @throws IOException
	 */
	public String toXml(Object object, Type typeOfSrc) throws IOException {
		StringWriter writer = new StringWriter();
		toXml(object, typeOfSrc, writer);
		return writer.toString();

	}

	/**
	 * 将对象按照给定的 type序列化为XML并写入到writer流
	 * 
	 * 
	 * @param object
	 * @param typeOfSrc
	 * @param writer
	 * @throws IOException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void toXml(Object object, Type typeOfSrc, Writer writer) throws IOException {
		if (object instanceof Element) {
			toXml((Element) object, writer);
			return;
		}

		TypeToken type = TypeToken.get(typeOfSrc);
		TypeAdapter adapter = getAdapter(type);
		Class clazz = type.getRawType();
		String name = null;
		String prefix = defaultElementPrefix;
		Document documentAnnotation = (Document) clazz.getAnnotation(Document.class);
		ArrayList<Namespace> namespaces = ReflectUtil.extractNamespaces(type.getRawType());
		if (documentAnnotation != null) {
			if (!StringUtil.isEmpty(documentAnnotation.name())) {
				name = documentAnnotation.name();
			}
			if (!StringUtil.isEmpty(prefix)) {
				prefix = documentAnnotation.prefix();
			}
		}
		if (name == null)
			name = classNamingStrategy.translateName(clazz);
		XmlWriter xmlWriter = new XmlWriter(writer, formatter);
		xmlWriter.writeStart(name, prefix);
		if (namespaces != null)
			for (Namespace namespace : namespaces) {
				xmlWriter.writeNamespace(namespace.getReference(), namespace.getPrefix());
			}
		adapter.write(xmlWriter, object);
		xmlWriter.writeEnd(name, prefix);
		xmlWriter.flush();
	}

	<T> TypeAdapter<T> getAdapter(Class<T> type) {
		return getAdapter(TypeToken.get(type));
	}

	<T> TypeAdapter<T> getAdapter(TypeToken<T> type) {
		return typeAdapterManager.getAdapter(type);
	}

	/**
	 * 获取{@link TypeAdapter}管理器， 可通过实现{@link TypeAdapter}来添加自定义的序列化/反序列化策略
	 * 
	 * <pre>
	 * Serializer serialzer = new Serializer();
	 * serialzer.getTypeAdapterManager().registerTypeAdapter(type, myTypeAdapter);
	 * 
	 * <pre>
	 * @return TypeAdapterManager
	 */
	public TypeAdapterManager getTypeAdapterManager() {
		return typeAdapterManager;
	}

	/**
	 * 默认情况下将对象转换为一个{@link Element} 如果在类上配置注解{@link Document},对象将被转换为
	 * {@link org.jczh.appliedxml.Document}
	 * @see DocumentTreeBuilderFactory.DocumentTreeBuilder#construct(Object)
	 * @param object
	 * @return Element
	 */
	public Element toElementTree(Object object) {
		@SuppressWarnings("unchecked")
		ElementConstructor<Object> elementConstructor = elementConstructorManager
				.getConstructor(TypeToken.get(object.getClass()));
		return elementConstructor.construct(object);

	}

	/**
	 * Element构造管理器，可通过实现{@link ElementConstructor}来添加自定义的{@link Element} 构造策略
	 * 
	 * <pre>
	 * Serializer serialzer = new Serializer();
	 * serialzer.getElementConstructorManager().registerConstructorFactory(type, myElementConstructor);
	 * </pre>
	 * 
	 * @return
	 */
	public ElementConstructorManager getElementConstructorManager() {
		return elementConstructorManager;
	}

	/**
	 * 将{@link Element} 转换为XML
	 * 
	 * @param element
	 * @return xml of String
	 * @throws IOException
	 */
	public String toXml(Element element) throws IOException {
		StringWriter writer = new StringWriter();
		typeAdapterManager.getDocumentAdapter().writeElement(new XmlWriter(writer, formatter), element);
		return writer.toString();
	}

	/**
	 * 将{@link Element} 转换为XML写入到writer流
	 * 
	 * @param element
	 * @throws IOException
	 */
	public void toXml(Element element, Writer writer) throws IOException {
		typeAdapterManager.getDocumentAdapter().writeElement(new XmlWriter(writer, formatter), element);
	}

	/**
	 * 将childObjec作为parent的子节点合并转换为XML
	 * 
	 * @param element
	 * @return xml of String
	 * @throws IOException
	 */
	public String toXml(Element parent, Object childObject) throws IOException {
		StringWriter writer = new StringWriter();
		parent.addChildElement(toElementTree(childObject));
		typeAdapterManager.getDocumentAdapter().writeElement(new XmlWriter(writer, formatter), parent);
		return writer.toString();
	}

	/**
	 * 将childObjec作为parent的子节点合并转换为XML写入到writer流
	 * 
	 * 
	 * @param parent
	 * @param childObject
	 * @param writer
	 * @throws IOException
	 */
	public void toXml(Element parent, Object childObject, Writer writer) throws IOException {
		parent.addChildElement(toElementTree(childObject));
		typeAdapterManager.getDocumentAdapter().writeElement(new XmlWriter(writer, formatter), parent);
	}

	boolean isNullValueSerializeRequired() {
		return nullValueSerializeRequired;
	}

	FieldNamingStrategy getFieldNamingStrategy() {
		return fieldNamingStrategy;
	}

	/**
	 * 设置[属性->xml节点名] 转换策略
	 * 
	 * @param fieldNamingStrategy
	 */
	public void setFieldNamingStrategy(FieldNamingStrategy fieldNamingStrategy) {
		this.fieldNamingStrategy = fieldNamingStrategy;
	}

	ClassNamingStrategy getClassNamingStrategy() {
		return classNamingStrategy;
	}

	/**
	 * 设置[类->xml节点名] 转换策略
	 * 
	 * @param classNamingStrategy
	 */
	public void setClassNamingStrategy(ClassNamingStrategy classNamingStrategy) {
		this.classNamingStrategy = classNamingStrategy;
	}

	String getDefaultElementPrefix() {
		return defaultElementPrefix;
	}

	/**
	 * 设置默认节点前缀,如果需要对序列化后的XML进行反序列化，请尽量避免使用前缀
	 * 
	 * @param defaultElementPrefix
	 */
	public void setDefaultElementPrefix(String defaultElementPrefix) {
		this.defaultElementPrefix = defaultElementPrefix;
	}

	String getDefaultAttributePrefix() {
		return defaultAttributePrefix;
	}

	/**
	 * 设置默认属性前缀,如果需要对序列化后的XML进行反序列化，请尽量避免使用前缀
	 * 
	 * @param defaultElementPrefix
	 */
	public void setDefaultAttributePrefix(String defaultAttributePrefix) {
		this.defaultAttributePrefix = defaultAttributePrefix;
	}

	/**
	 * 设置空值是否被序列化(默认空值将会被序列化)
	 * 
	 * @param defaultElementPrefix
	 */
	public void setNullValueSerializeRequired(boolean nullValueSerializRequired) {
		this.nullValueSerializeRequired = nullValueSerializRequired;
	}

	/**
	 * 设置是否在序列化时进行格式化缩进(默认情况将不会缩进)
	 * 
	 * @param defaultElementPrefix
	 */
	public void setFormatted(boolean formatted) {
		if (formatted)
			formatter = new Format(4);
		else
			formatter = new Format(0);
	}

	boolean isAssociatedWithSuperClass() {
		return associatedWithSuperClass;
	}

	/**
	 * 设置在序列化是是否关联到父类， 默认情况为<code>true</code>，即非Object的父类属性都会被序列化
	 * 
	 * 若设置为<code>false</code>，则只会序列化该类本身的属性
	 * 
	 * @param associatedWithSuperClass
	 */
	public void setAssociatedWithSuperClass(boolean associatedWithSuperClass) {
		this.associatedWithSuperClass = associatedWithSuperClass;
	}

	/**
	 * 根据指定Class类型,从字符串中读取并解析XML生成对象
	 * 
	 * @param <T>
	 * @param xml
	 * @param classOfT
	 * @return Object of T
	 */
	public <T> T fromXml(String xml, Class<T> classOfT) {
		Object object = fromXml(xml, (Type) classOfT);
		return Primitives.wrap(classOfT).cast(object);

	}

	/**
	 * 根据指定Class类型,从字符串中读取并解析XML生成对象
	 * 
	 * 
	 * @param <T>
	 * @param reader
	 * @param typeOfT
	 * @return Object of T
	 */
	@SuppressWarnings("unchecked")
	public <T> T fromXml(Reader reader, Type typeOfT) {
		try {
			T object = (T) fromXml(ProviderFactory.getInstance().provide(reader), typeOfT);
			return object;
		} catch (Exception e) {
			e.printStackTrace();
			throw new NodeException(e);
		}
		// assertFullConsumption(object, jsonReader);
	}

	/**
	 * 根据指定Type类型,从Reader字符流中读取并解析XML生成对象
	 * 
	 * @param <T>
	 * @param xmlReader
	 * @param typeOfT
	 * @return Object of T
	 */
	@SuppressWarnings("unchecked")
	private <T> T fromXml(XmlReader xmlReader, Type typeOfT) {

		TypeToken<T> typeToken = (TypeToken<T>) TypeToken.get(typeOfT);
		TypeAdapter<T> typeAdapter = getAdapter(typeToken);
		T object = null;
		try {
			String startName = getClassNamingStrategy().translateName(typeToken.getRawType());
			Document document = typeToken.getRawType().getAnnotation(Document.class);
			if (document != null && !StringUtil.isEmpty(document.name()))
				startName = document.name();
			boolean findNodeName = false;
			while (xmlReader.hasNext()) {
				EventNode eventNode = xmlReader.next();
				String name = eventNode.getName();
				findNodeName = eventNode.isStart() && ((startName.equals(name)));
				if (findNodeName)
					break;
			}
			if (findNodeName)
				object = typeAdapter.read(xmlReader);
			return object;
		} catch (Exception e) {
			e.printStackTrace();
			throw new NodeException(e);
		}
	}

	/**
	 * 根据指定Class类型,从Reader字符流中读取并解析XML生成对象
	 * 
	 * @param <T>
	 * @param reader
	 * @param classOfT
	 * @return Object of T
	 */
	public <T> T fromXml(Reader reader, Class<T> classOfT) {
		try {
			XmlReader xmlReader = ProviderFactory.getInstance().provide(reader);
			Object object = fromXml(xmlReader, classOfT);
			return Primitives.wrap(classOfT).cast(object);
		} catch (Exception e) {
			e.printStackTrace();
			throw new NodeException(e);
		}

	}

	/**
	 * 根据指定Type类型从字符串中解析XML生成对象
	 * 
	 * @param <T>
	 * @param xml
	 * @param typeOfT
	 * @return Object of T
	 */
	@SuppressWarnings("unchecked")
	public <T> T fromXml(String xml, Type typeOfT) {
		if (xml == null) {
			return null;
		}
		StringReader reader = new StringReader(xml);
		T target = (T) fromXml(reader, typeOfT);
		return target;
	}

}
