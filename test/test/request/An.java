package test.request;

import java.util.ArrayList;
import java.util.List;

import org.jczh.appliedxml.Element;
import org.jczh.appliedxml.Serializer;
import org.jczh.appliedxml.annotation.Attribute;
import org.jczh.appliedxml.annotation.Transient;

public class An {

	@Attribute
	private String id;
	@Attribute
	private String time;
	@Transient
	private ArrayList<Q> qs = new ArrayList<Q>();

	public static void main(String[] args) {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public ArrayList<Q> getQs() {
		return qs;
	}

	public void addQ(Q q) {
		qs.add(q);
	}

	public Element toElement(Serializer serializer) {
		Element anElement = serializer.toElementTree(this);
		Element qsElement = serializer.toElementTree(qs);
		//
		List<Element> elements = qsElement.removeChildElemnts();
		anElement.addChildElements(elements);
		return anElement;

	}
}
