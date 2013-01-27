package test.request;

import java.io.IOException;
import java.util.ArrayList;

import org.jczh.appliedxml.Element;
import org.jczh.appliedxml.Serializer;

public class Ans {

	private int flag;

	private final ArrayList<An> ans = new ArrayList<An>();

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public ArrayList<An> getAns() {
		return ans;
	}

	public void addAn(An an) {
		ans.add(an);
	}

	public String toXml(Serializer serializer) throws IOException {
		Element element = new Element("ans");
		serializer.setFormatted(true);
		for (An an : ans) {
			element.addChildElement(an.toElement(serializer));
		}
		element.addAttribute("flag", String.valueOf(flag));
		return serializer.toXml(element);
	}
}
