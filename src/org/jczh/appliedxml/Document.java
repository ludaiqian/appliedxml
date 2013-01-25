package org.jczh.appliedxml;

public class Document extends Element {

	public Document(String name) {
		super(name);
	}

	public Document() {
		super();
	}

	@Override
	public Node getParentNode() {
		return null;
	}

	@Override
	public short getNodeType() {
		return DOCUMENT_NODE;
	}

}
