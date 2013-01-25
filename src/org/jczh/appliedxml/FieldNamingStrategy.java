package org.jczh.appliedxml;

import java.lang.reflect.Field;

/**
 * [属性->xml节点名] 映射策略
 * @author ludaiqian@126.com
 * @version 1.0 beta
 * @since 1.0
 */
public interface FieldNamingStrategy {

	public String translateName(Field f);
}
