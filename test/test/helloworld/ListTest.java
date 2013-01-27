package test.helloworld;

import java.io.IOException;
import java.util.ArrayList;

import org.jczh.appliedxml.Serializer;
import org.jczh.appliedxml.annotation.ElementList;

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
