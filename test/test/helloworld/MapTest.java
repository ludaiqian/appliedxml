package test.helloworld;

import java.io.IOException;
import java.util.HashMap;

import org.jczh.appliedxml.Serializer;
import org.jczh.appliedxml.annotation.ElementMap;

public class MapTest {
	@ElementMap(entry = "hello", keyAsAttribute = true, valueAsText = true)
	private HashMap<String, String> hellos = new HashMap<String, String>();
	@ElementMap(entry = "word")
	private HashMap<String, String> words = new HashMap<String, String>();

	public MapTest() {
		hellos.put("attr1", "text1");
		hellos.put("attr2", "text2");
		hellos.put("attr3", "text3");
		hellos.put("attr4", "text4");
		//
		words.put("k1", "v1");
		words.put("k2", "v2");
		words.put("k3", "v3");
		words.put("k4", "v4");
	}

	public static void main(String[] args) throws IOException {
		Serializer serializer = new Serializer();
		serializer.setFormatted(true);
		System.out.println(serializer.toXml(new MapTest()));
	}
}
