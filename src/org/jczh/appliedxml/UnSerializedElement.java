package org.jczh.appliedxml;

import java.lang.reflect.Type;

/**
 * Î´ÐòÁÐ»¯µÄ{@link Element}
 * 
 * @author ludaiqian@126.com
 * @version 1.0
 * @since 1.0
 * 
 */
public class UnSerializedElement extends Element {

	private Type unSerializedChildType;
	private Object unSerializedChildValue;
	private ContainerDefine containerDefine;

	UnSerializedElement(String name, Type unSerializedChildType, Object unSerializedChildValue) {
		this.unSerializedChildType = unSerializedChildType;
		this.unSerializedChildValue = unSerializedChildValue;
		setName(name);
	}

	public Type getUnSerializedChildType() {
		return unSerializedChildType;
	}

	public void setUnSerializedChildType(Type unSerializedChildType) {
		this.unSerializedChildType = unSerializedChildType;
	}

	public Object getUnSerializedChildValue() {
		return unSerializedChildValue;
	}

	void setUnSerializedChildValue(Object unSerializedChildValue) {
		this.unSerializedChildValue = unSerializedChildValue;
	}

	@Override
	public boolean isSerialized() {
		return false;
	}

	@Override
	public boolean isEmpty() {
		return unSerializedChildValue == null;
	}

	@Override
	public Element createChildElement(String name) {
		throw new UnsupportedOperationException("unserialized element cannot add child");
	}

	@Override
	public void addChildElement(Element element) {
		throw new UnsupportedOperationException("unserialized element cannot add child");
	}

	public ContainerDefine getContainerDefine() {
		return containerDefine;
	}

	public void setContainerDefine(ContainerDefine containerDefine) {
		this.containerDefine = containerDefine;
	}

	public boolean isContainer() {
		return containerDefine != null;
	}
}
