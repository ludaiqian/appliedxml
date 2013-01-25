package org.jczh.appliedxml;

public abstract class Node {
	public static final short ELEMENT_NODE = 1;
	public static final short ATTRIBUTE_NODE = 2;
	public static final short DOCUMENT_NODE = 4;
	private String name;
	private String prefix;

	public Node(String name) {
		this.name = name;
	}

	public Node() {
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public void setName(String name) {
		this.name = name;
	}

	public abstract short getNodeType();

	public abstract Node getParentNode();

	public String getName() {
		return name;
	}

	public boolean isAttribute() {
		return getNodeType() == ATTRIBUTE_NODE;
	}

	public boolean isDocument() {
		return getNodeType() == DOCUMENT_NODE;
	}

	public boolean isElement() {
		return getNodeType() == ELEMENT_NODE;
	}
}
