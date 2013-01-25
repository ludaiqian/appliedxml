package org.jczh.appliedxml;

import java.io.IOException;

/**
 * 对象与XML之间序列化和反序列化策略
 * 
 * @author ludaiqian@126.com
 * @version 1.0
 * @since 1.0
 * @param <T>
 */
public abstract class TypeAdapter<T> {

	abstract void write(XmlWriter out, T value) throws IOException;

	abstract T read(XmlReader in) throws IOException;
}
