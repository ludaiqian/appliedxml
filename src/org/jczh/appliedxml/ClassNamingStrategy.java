package org.jczh.appliedxml;

/**
 * [��->xml�ڵ���] ӳ�����
 * 
 * @author ludaiqian@126.com
 * @version 1.0 beta
 * @since 1.0
 */
public interface ClassNamingStrategy {
	@SuppressWarnings("rawtypes")
	public String translateName(Class type);
}
