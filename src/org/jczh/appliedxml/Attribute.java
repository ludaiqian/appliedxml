package org.jczh.appliedxml;

public class Attribute extends Node {

	private String value;
	private Node parent;
	private boolean required = true;

	public Attribute(String name, String prefix, String value, boolean required) {
		super();
		setName(name);
		setPrefix(prefix);
		this.value = value;
		this.required = required;
	}

	void setParentNode(Node parent) {
		this.parent = parent;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public Node getParentNode() {
		return parent;
	}

	@Override
	public short getNodeType() {
		return ATTRIBUTE_NODE;
	}

	public boolean isRequired() {
		return required;
	}

	void setRequired(boolean required) {
		this.required = required;
	}

}
