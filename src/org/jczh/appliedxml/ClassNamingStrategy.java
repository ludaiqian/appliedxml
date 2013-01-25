package org.jczh.appliedxml;

/**
 * [类->xml节点名] 映射策略
 * 
 * @author ludaiqian@126.com
 * @version 1.0 beta
 * @since 1.0
 */
public interface ClassNamingStrategy {
	@SuppressWarnings("rawtypes")
	public String translateName(Class type);
}
