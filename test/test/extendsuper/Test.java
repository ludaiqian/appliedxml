package test.extendsuper;

import java.io.IOException;

import org.jczh.appliedxml.Serializer;

public class Test {
	public static void main(String[] args) throws IOException {
		SonBean sonBean=new SonBean();
		sonBean.setSuper1("methd_super");
		sonBean.setSuper2("super2");
		sonBean.setSon1("son1");
		sonBean.setSon2("son2");
		
		Serializer serializer=new Serializer();
		serializer.setFormatted(true);
		//生成xml
		String result=serializer.toXml(sonBean);
		System.out.println(result);
		//需要重新创建一个对象，因为反射比较耗时,TypeAdapter会被缓存,
		Serializer serializer2=new Serializer();
		serializer2.setAssociatedWithSuperClass(false);
		serializer2.setFormatted(true);
		result=serializer2.toXml(sonBean);
		System.out.println(result);
		
	}
}
