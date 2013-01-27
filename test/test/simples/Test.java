package test.simples;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;

import org.jczh.appliedxml.ClassNamingStrategy;
import org.jczh.appliedxml.FieldNamingStrategy;
import org.jczh.appliedxml.Serializer;
import org.jczh.appliedxml.annotation.Attribute;

/**
 * 示例读取allsubjects.xml并转换为对象
 * 
 * 
 */
public class Test {
	Serializer serializer;

	public Test() {
		serializer = new Serializer();
		serializer.setNullValueSerializeRequired(false);
		// 设置缩进
		serializer.setFormatted(true);
		// 设置obj转换xml节点的映射名称
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
				return f.getName().substring(0, 1).toUpperCase() + f.getName().substring(1);
			}
		});
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		Test mainTest = new Test();
		String xml = mainTest.service2Xml();
		System.out.println(xml);
		System.out.println("----------分割线-----------------");
		Service service = mainTest.xml2Service();
		System.out.println(service);
	}

	public String service2Xml() throws IOException {
		Service service = new Service();
		service.setNo(123);
		service.setErrno(123);
		service.setVer(234);
		//
		ArrayList<ExamType> examTypeList = new ArrayList<ExamType>();
		//
		ExamType examType1 = new ExamType();
		examType1.setDictName("DictName");
		examType1.setID("0x111");
		examTypeList.add(examType1);
		//
		ExamType examType2 = new ExamType();
		examType2.setDictName("DictName2");
		examType2.setID("0x1112");
		examTypeList.add(examType2);
		//
		service.setExamTypes(examTypeList);
		//
		ArrayList<Exam> examList = new ArrayList<Exam>();
		Exam exam1 = new Exam();
		exam1.setExamName("ExamName-XXX");
		exam1.setExamTypeID("0x2222");
		exam1.setID("55555");
		examList.add(exam1);
		//
		Exam exam2 = new Exam();
		exam2.setExamName("ExamName-XXX1");
		exam2.setExamTypeID("0x1111");
		exam2.setID("11111");
		examList.add(exam2);
		//
		service.setExams(examList);

		ArrayList<Subject> subjectsList = new ArrayList<Subject>();
		Subject subject = new Subject();
		subject.setID("subjectid");
		subjectsList.add(subject);
		service.setSubjects(subjectsList);
		//
		String result = serializer.toXml(service);
		return result;
	}

	public Service xml2Service() throws IOException {
		InputStream in = Test.class.getResourceAsStream("allsubjects.xml");
		InputStreamReader reader = new InputStreamReader(in, "utf-8");
		Service t = serializer.fromXml(reader, Service.class);
		return t;
	}
}
