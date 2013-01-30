package test.helloworld;

import java.io.IOException;

import org.jczh.appliedxml.Serializer;
import org.jczh.appliedxml.annotation.Attribute;
import org.jczh.appliedxml.annotation.Transient;

public class BeanTest {
	// 该字段将不会被序列化
	@Transient
	private String version;
	// 属性
	@Attribute
	private String attr1;

	private String element1;
	private String element2;
	private String[] testKeys = { "a", "b", "c" };
	private JavaBean javabean;

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getAttr1() {
		return attr1;
	}

	public void setAttr1(String attr1) {
		this.attr1 = attr1;
	}

	public String getElement1() {
		return element1;
	}

	public void setElement1(String element1) {
		this.element1 = element1;
	}

	public String getElement2() {
		return element2;
	}

	public void setElement2(String element2) {
		this.element2 = element2;
	}

	public String[] getTestKeys() {
		return testKeys;
	}

	public void setTestKeys(String[] testKeys) {
		this.testKeys = testKeys;
	}

	public JavaBean getJavabean() {
		return javabean;
	}

	public void setJavabean(JavaBean javabean) {
		this.javabean = javabean;
	}

	public BeanTest() {

	}

	public static void main(String[] args) throws IOException {
		// 创建序列化转换对象
		Serializer serializer = new Serializer();
		// 设置是否格式化
		serializer.setFormatted(true);
		// 空置是否序列化
		serializer.setNullValueSerializeRequired(false);
		BeanTest beanTest = new BeanTest();
		beanTest.setVersion("1.0");
		beanTest.setAttr1("attr");
		beanTest.setElement1("e1");
		beanTest.setElement2("e2");
		JavaBean javabean = new JavaBean();
		javabean.setBean1("b1");
		beanTest.setJavabean(javabean);
		// 将对象转为xml
		String xml = serializer.toXml(beanTest);
		System.out.println(xml);
	}

	public static class JavaBean {
		String bean1;
		String bean2;

		public String getBean1() {
			return bean1;
		}

		public void setBean1(String bean1) {
			this.bean1 = bean1;
		}

		public String getBean2() {
			return bean2;
		}

		public void setBean2(String bean2) {
			this.bean2 = bean2;
		}

		@Override
		public String toString() {
			return "JavaBean [bean1=" + bean1 + ", bean2=" + bean2 + "]";
		}

	}
}
