package org.jczh.appliedxml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 根据对象和对象field 生成的xml树，可代表 普通节点、文本及未序列化内容
 * 
 * @version 1.0
 * @since 1.0
 * @author ludaiqian@126.com
 * @see TextElement
 * @see UnSerializedElement
 * 
 */
public class Element extends Node {

	private Element parent;
	private ArrayList<Element> childElements;
	private Map<String, Attribute> attributes;
	private boolean required = true;
	private List<Namespace> namespaces;
	private int depth;

	public Element() {
		this(null);
	}

	public Element(String name) {
		setName(name);
		childElements = new ArrayList<Element>();
		attributes = new HashMap<String, Attribute>();
	}

	public int depth() {
		return depth;
	}

	protected void setDepth(int depth) {
		this.depth = depth;
	}

	public List<Namespace> getNamespaces() {
		return namespaces;
	}

	public void setNamespaces(List<Namespace> namespaces) {
		this.namespaces = namespaces;
	}

	public boolean isText() {
		return false;
	}

	public boolean isSerialized() {
		return true;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	@Override
	public Node getParentNode() {
		return parent;
	}

	protected void setParentNode(Element parent) {
		this.parent = parent;
	}

	public Collection<Element> getChildElements() {
		return childElements;
	}

	public Collection<Attribute> getAttributes() {
		return attributes.values();
	}

	public boolean hasNamespaces() {
		return namespaces != null && namespaces.size() > 0;
	}

	public boolean hasAttributes() {
		return attributes != null && attributes.size() > 0;
	}

	public boolean hasChildElements() {
		return childElements != null && childElements.size() > 0;
	}

	public Element findChildElement(String name) {
		for (Element e : childElements) {
			if (e.getName().equals(name))
				return e;
		}
		return null;
	}

	public Element getChildElement(int index) {
		return childElements.get(index);
	}

	public Attribute getAttribute(String name) {
		return attributes.get(name);
	}

	@Override
	public short getNodeType() {
		return ELEMENT_NODE;
	}

	public boolean isEmpty() {
		return (!hasAttributes()) && (!hasChildElements()) && (!hasNamespaces());
	}

	public void addAttribute(Attribute attribute) {
		attribute.setParentNode(this);
		attributes.put(attribute.getName(), attribute);
	}

	public void addChildElement(Element element) {
		if (element.getParentNode() != null)
			throw new NodeException("element " + element + ",alreay has parent!");
		element.setParentNode(this);
		element.setDepth(depth + 1);
		childElements.add(element);
	}

	public void addChildElements(Collection<Element> elements) {
		for (Element element : elements) {
			element.setParentNode(this);
			element.setDepth(depth + 1);
			childElements.add(element);
		}
	}

	public Element createChildElement(String name) {
		Element element = new Element();
		element.setName(name);
		element.setParentNode(this);
		element.setDepth(depth + 1);
		childElements.add(element);
		return element;
	}

	public void removeChildElement(Element element) {
		if (childElements.remove(element))
			element.setParentNode(null);
	}

	public List<Element> removeChildElemnts() {
		ArrayList<Element> removed = new ArrayList<Element>();
		for (Element e : childElements) {
			e.setParentNode(null);
			removed.add(e);
		}
		childElements.clear();
		return removed;
	}

	public Attribute removeAttribute(String name) {
		return attributes.remove(name);
	}

	public static class Namespace {
		String prefix;
		String reference;

		public Namespace(String prefix, String reference) {
			super();
			this.prefix = prefix;
			this.reference = reference;
		}

		public String getPrefix() {
			return prefix;
		}

		public void setPrefix(String prefix) {
			this.prefix = prefix;
		}

		public String getReference() {
			return reference;
		}

		public void setReference(String reference) {
			this.reference = reference;
		}

	}

	public void addAttribute(String name, String value) {
		Attribute attribute = new Attribute(name, null, value, required);
		addAttribute(attribute);
	}

}
