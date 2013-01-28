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
 * Serializer�ṩ������XML֮������л��ͷ����л�ת����
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
	 * �� �������л�ΪXML
	 * 
	 * @param object
	 * @return
	 * @throws IOException
	 */
	public String toXml(Object object) throws IOException {
		return toXml(object, object.getClass());
	}

	/**
	 * �������ո����� type���л�ΪXML
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
	 * �������ո����� type���л�ΪXML��д�뵽writer��
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
	 * ��ȡ{@link TypeAdapter}�������� ��ͨ��ʵ��{@link TypeAdapter}������Զ�������л�/�����л�����
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
	 * Ĭ������½�����ת��Ϊһ��{@link Element} �������������ע��{@link Document},���󽫱�ת��Ϊ
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
	 * Element�������������ͨ��ʵ��{@link ElementConstructor}������Զ����{@link Element} �������
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
	 * ��{@link Element} ת��ΪXML
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
	 * ��{@link Element} ת��ΪXMLд�뵽writer��
	 * 
	 * @param element
	 * @throws IOException
	 */
	public void toXml(Element element, Writer writer) throws IOException {
		typeAdapterManager.getDocumentAdapter().writeElement(new XmlWriter(writer, formatter), element);
	}

	/**
	 * ��childObjec��Ϊparent���ӽڵ�ϲ�ת��ΪXML
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
	 * ��childObjec��Ϊparent���ӽڵ�ϲ�ת��ΪXMLд�뵽writer��
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
	 * ����[����->xml�ڵ���] ת������
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
	 * ����[��->xml�ڵ���] ת������
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
	 * ����Ĭ�Ͻڵ�ǰ׺,�����Ҫ�����л����XML���з����л����뾡������ʹ��ǰ׺
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
	 * ����Ĭ������ǰ׺,�����Ҫ�����л����XML���з����л����뾡������ʹ��ǰ׺
	 * 
	 * @param defaultElementPrefix
	 */
	public void setDefaultAttributePrefix(String defaultAttributePrefix) {
		this.defaultAttributePrefix = defaultAttributePrefix;
	}

	/**
	 * ���ÿ�ֵ�Ƿ����л�(Ĭ�Ͽ�ֵ���ᱻ���л�)
	 * 
	 * @param defaultElementPrefix
	 */
	public void setNullValueSerializeRequired(boolean nullValueSerializRequired) {
		this.nullValueSerializeRequired = nullValueSerializRequired;
	}

	/**
	 * �����Ƿ������л�ʱ���и�ʽ������(Ĭ���������������)
	 * 
	 * @param defaultElementPrefix
	 */
	public void setFormatted(boolean formatted) {
		if (formatted)
			formatter = new Format(4);
		else
			formatter = new Format(0);
	}

	public boolean isAssociatedWithSuperClass() {
		return associatedWithSuperClass;
	}

	/**
	 * ���������л����Ƿ���������࣬ Ĭ�����Ϊ<code>true</code>������Object�ĸ������Զ��ᱻ���л�
	 * 
	 * ������Ϊ<code>false</code>����ֻ�����л����౾�������
	 * 
	 * @param associatedWithSuperClass
	 */
	public void setAssociatedWithSuperClass(boolean associatedWithSuperClass) {
		this.associatedWithSuperClass = associatedWithSuperClass;
	}

	/**
	 * ����ָ��Class����,���ַ����ж�ȡ������XML���ɶ���
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
	 * ����ָ��Class����,���ַ����ж�ȡ������XML���ɶ���
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
	 * ����ָ��Type����,��Reader�ַ����ж�ȡ������XML���ɶ���
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
	 * ����ָ��Class����,��Reader�ַ����ж�ȡ������XML���ɶ���
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
	 * ����ָ��Type���ʹ��ַ����н���XML���ɶ���
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
