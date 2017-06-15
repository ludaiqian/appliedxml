# 项目描述
appliedxml是对object和xml之间的序列化和反序列化的库，设计时主要参考了google开源项目GSON，平均效率约为另一个开源库SimpleXML的5倍左右，并且极大的提高易用性
使用的方式类似于Gson，同样可以使用TypeToken，支持xml字符串与各种类型对象互转。

tips：此项目是自己4年前编写，如今看来有很多可改善之处，未来会对它进行部分重构。

### 通过注解可以修改字段映射xml节点名称、解析方式等


* Namespace  标记该字段或类为xml中的 namespace
* NamespaceList 标记该字段或类为xml中的 namespace
* Attribute 标记该字段为xml 中的attribute属性
* Document 标记被序列化的类 
* Element  标记普通字段
* ElementList 标记集合类型字段
* ElementArray  标记数组类型字段
* ElementMap  标记map类型字段
* Transient 标记字段不可被序列化
* Serializable 标记类是否可被序列化

### 可统一配置改类名or字段名转xml节点 映射策略，若注解中有指定名称，会优先使用。
#### 例如：
<pre><code>
// 设置class转换xml节点的映射名称
serializer = new Serializer();
serializer.setNullValueSerializeRequired(false);
// 设置缩进
serializer.setFormatted(true);
// 设置class转换xml节点的映射名称
serializer.setClassNamingStrategy(new ClassNamingStrategy() {

	@SuppressWarnings("rawtypes")
	@Override
	public String translateName(Class type) {
		return type.getSimpleName();
	}
});
// 设置fields转换xml节点的映射名称
serializer.setFieldNamingStrategy(new FieldNamingStrategy() {

	@Override
	public String translateName(Field f) {
		// 如果是Attribute则默认小写
		if (f.getAnnotation(Attribute.class) != null)
			return f.getName();
		//Xml中定义为首字母大写
		return f.getName().substring(0, 1).toUpperCase() + f.getName().substring(1);
	}
});
</code></pre>		

## demo示例：


<pre><code>
package test.helloworld;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jczh.appliedxml.Serializer;
import org.jczh.appliedxml.annotation.Attribute;
import org.jczh.appliedxml.annotation.Document;
import org.jczh.appliedxml.annotation.Element;
import org.jczh.appliedxml.annotation.ElementList;
import org.jczh.appliedxml.annotation.Transient;
@Document(name="bean")
public class BeanTest {
	// 该字段将不会被序列化
	@Transient
	private String version;
	// 标记该字段为属性
	@Attribute
	private String attr1;
	@Element(name="elment_1")//映射xml中名称为element_1节点
	private String element1;
	private String element2;
	//数组 [默认生成的子节点名为数组内元素的类名，若需要修改，可配置注解 ElementArray，指定entry名称]
	private String[] testKeys = { "a", "b", "c" };
	@ElementList(name="array",entry="key")//字段对应xml “array”节点，entry对应 “key”节点
	private List<String> testArray=new ArrayList<String>();
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
	

	public static void main(String[] args) throws IOException {
		//编写测试bean
		BeanTest beanTest = new BeanTest();
		beanTest.setVersion("1.0");
		beanTest.setAttr1("attr测试");
		beanTest.setElement1("e1");
		beanTest.setElement2("e2");
		beanTest.testArray.add("你好");
		beanTest.testArray.add("list测试");
		JavaBean javabean = new JavaBean();
		javabean.setBean1("b1");
		beanTest.setJavabean(javabean);
		// 创建序列化转换对象
		Serializer serializer = new Serializer();
		// 设置是否格式化
		serializer.setFormatted(true);
		// 空置是否序列化
		serializer.setNullValueSerializeRequired(false);
		// 将对象转为xml
		String xml = serializer.toXml(beanTest);
		System.out.println(xml);
	}

}

</code></pre>

运行结果：
<pre><code>
&lt;bean attr1=&quot;attr测试&quot;&gt;
    &lt;elment_1&gt;e1&lt;/elment_1&gt;
    &lt;element2&gt;e2&lt;/element2&gt;
    &lt;testKeys&gt;
        &lt;string&gt;a&lt;/string&gt;
        &lt;string&gt;b&lt;/string&gt;
        &lt;string&gt;c&lt;/string&gt;
    &lt;/testKeys&gt;
    &lt;array&gt;
        &lt;key&gt;你好&lt;/key&gt;
        &lt;key&gt;list测试&lt;/key&gt;
    &lt;/array&gt;
    &lt;javabean&gt;
        &lt;bean1&gt;b1&lt;/bean1&gt;
    &lt;/javabean&gt;
&lt;/bean&gt;

</code></pre>
