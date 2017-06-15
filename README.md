# 项目描述
appliedxml是对object和xml之间的序列化和反序列化的库，设计时主要参考了google开源项目GSON，平均效率约为另一个开源库SimpleXML的5倍左右，并且极大的提高易用性
使用的方式类似于Gson，使用TypeToken，支持xml转各种类型对象。
#demo示例：


<pre><code>
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
</code></pre>

运行结果：
<pre><code>
&lt;beanTest attr1=&quot;attr&quot;&gt;
    &lt;element1&gt;e1&lt;/element1&gt;
    &lt;element2&gt;e2&lt;/element2&gt;
    &lt;testKeys&gt;
        &lt;string&gt;a&lt;/string&gt;
        &lt;string&gt;b&lt;/string&gt;
        &lt;string&gt;c&lt;/string&gt;
    &lt;/testKeys&gt;
    &lt;javabean&gt;
        &lt;bean1&gt;b1&lt;/bean1&gt;
    &lt;/javabean&gt;
&lt;/beanTest&gt;
</code></pre>

List序列化：
<pre><code>
public class ListTest {
        @ElementList(name = "items", entry = "item")
        private ArrayList<String> arrayList = new ArrayList<String>();

        public ListTest() {
                arrayList.add("test1");
                arrayList.add("test2");
                arrayList.add("test3");
                arrayList.add("test4");
                arrayList.add("test5");
        }

        public static void main(String[] args) throws IOException {
                Serializer serializer = new Serializer();
                serializer.setFormatted(true);
                System.out.println(serializer.toXml(new ListTest()));
        }
}
</code></pre>

运行结果：
<pre><code>
&lt;listTest&gt;
    &lt;items&gt;
        &lt;item&gt;test1&lt;/item&gt;
        &lt;item&gt;test2&lt;/item&gt;
        &lt;item&gt;test3&lt;/item&gt;
        &lt;item&gt;test4&lt;/item&gt;
        &lt;item&gt;test5&lt;/item&gt;
    &lt;/items&gt;
&lt;/listTest&gt;
</code></pre>


