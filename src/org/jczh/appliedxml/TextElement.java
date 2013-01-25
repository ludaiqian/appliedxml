package org.jczh.appliedxml;


public class TextElement extends Element {

	private String text;

	public TextElement(String name, String text) {
		setName(name);
		this.text = text;
	}

	public TextElement() {
		super();
	}

	@Override
	public boolean isText() {
		return true;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public boolean isEmpty() {
		return text == null || text.equals("");
	}

	@Override
	public String toString() {
		return "TextElement [text=" + text + "]";
	}

	@Override
	public Element createChildElement(String name) {
		throw new UnsupportedOperationException("text element cannot add child");
	}

	@Override
	public void addChildElement(Element element) {
		throw new UnsupportedOperationException("text element cannot add child");
	}

}
