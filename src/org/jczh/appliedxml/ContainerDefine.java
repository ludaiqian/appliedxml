package org.jczh.appliedxml;

public class ContainerDefine {
	public final ContainerType type;
	public final String entry;
	public final String key;
	public final String value;
	public final boolean keyAsAttr;
	public final boolean valueAsText;

	ContainerDefine(ContainerType type, String entry, String key, String value, boolean keyAsAttr, boolean valueAsText) {
		super();
		this.type = type;
		this.entry = entry;
		this.key = key;
		this.value = value;
		this.keyAsAttr = keyAsAttr;
		this.valueAsText = valueAsText;
	}

	public static enum ContainerType {
		Array, List, Map
	}

}
