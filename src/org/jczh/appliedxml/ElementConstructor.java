package org.jczh.appliedxml;

/**
 * Element¹¹Ôì²ßÂÔ
 * 
 * @param <T>
 * 
 * @author ludaiqian@126.com
 * @version 1.0
 * @since 1.0
 * 
 */
public interface ElementConstructor<T> {

	public abstract Element construct(T object);

}
