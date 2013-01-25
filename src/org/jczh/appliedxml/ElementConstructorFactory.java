package org.jczh.appliedxml;

import com.google.gsoncode.internal.TypeToken;

public interface ElementConstructorFactory {
	public <T> ElementConstructor<T> create(Serializer context, TypeToken<T> type);
}
