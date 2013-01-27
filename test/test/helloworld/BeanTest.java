package test.helloworld;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;

import org.jczh.appliedxml.Serializer;
import org.jczh.appliedxml.annotation.ElementMap;

public class BeanTest {
//	// 该字段将不会被序列化
//	@Transient
//	private String version;
//	// 属性
//	@Attribute
//	private String attr1;
//
//	private String element1;
//	private String element2;
//	private String[] testKeys = { "a", "b", "c" };
	@ElementMap(keyAsAttribute = true)
	private HashMap<String, JavaBean> beans = new HashMap<String, JavaBean>();

	public BeanTest() {
		beans.put("bean1", new JavaBean());
		
	}

	public static void main(String[] args) throws IOException {
		Serializer serializer=new Serializer();
		serializer.setFormatted(true);
		String xml = serializer.toXml(new BeanTest());
		System.out.println(xml);
//		for (Field f : JavaBean.class.getDeclaredFields()) {
//			if(f.getName().equals("this$0"))
//				System.out.println("good");
//			System.out.println(f.getName());
//		}
	}

	public  class JavaBean {
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
