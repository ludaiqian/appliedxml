package test.fromlist;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import org.jczh.appliedxml.ClassNamingStrategy;
import org.jczh.appliedxml.Serializer;
import org.jczh.appliedxml.annotation.Attribute;
import org.jczh.appliedxml.annotation.ElementMap;

import com.google.gsoncode.internal.TypeToken;

public class Test {
	ArrayList<Qw> qws = new ArrayList<Qw>();

	// 从一个xml文本中解析List
	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		Serializer serializer = new Serializer();
		Type type = new TypeToken<ArrayList<Qw>>() {
		}.getType();
		serializer.setClassNamingStrategy(new ClassNamingStrategy() {

			@SuppressWarnings("rawtypes")
			@Override
			public String translateName(Class type) {
				String typeName = type.getSimpleName();
				if (type == ArrayList.class)
					return "qws";
				return typeName.substring(0, 1).toLowerCase() + typeName.substring(1);
			}
		});
		InputStream in = Test.class.getResourceAsStream("test.xml");
		InputStreamReader inReader = new InputStreamReader(in, "UTF-8");
		ArrayList<Qw> qws = serializer.fromXml(inReader, type);
		System.out.println(qws);
	}
}

class Qw {
	@Attribute
	private String hasimg;
	private String eid;
	private String sid;
	private String pid;
	private String qid;
	private String qs;
	private String ua;
	private String qt;
	private String t;
	private String s;
	private String an;
	private String p;
	private String r;
	@ElementMap(key = "s", entry = "t", keyAsAttribute = true, valueAsText = true)
	private HashMap<String, String> ops = new HashMap<String, String>();

	public String getHasimg() {
		return hasimg;
	}

	public void setHasimg(String hasimg) {
		this.hasimg = hasimg;
	}

	public String getEid() {
		return eid;
	}

	public void setEid(String eid) {
		this.eid = eid;
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getQid() {
		return qid;
	}

	public void setQid(String qid) {
		this.qid = qid;
	}

	public String getQs() {
		return qs;
	}

	public void setQs(String qs) {
		this.qs = qs;
	}

	public String getUa() {
		return ua;
	}

	public void setUa(String ua) {
		this.ua = ua;
	}

	public String getQt() {
		return qt;
	}

	public void setQt(String qt) {
		this.qt = qt;
	}

	public String getT() {
		return t;
	}

	public void setT(String t) {
		this.t = t;
	}

	public String getS() {
		return s;
	}

	public void setS(String s) {
		this.s = s;
	}

	public String getAn() {
		return an;
	}

	public void setAn(String an) {
		this.an = an;
	}

	public String getP() {
		return p;
	}

	public void setP(String p) {
		this.p = p;
	}

	public String getR() {
		return r;
	}

	public void setR(String r) {
		this.r = r;
	}

	public HashMap<String, String> getOps() {
		return ops;
	}

	public void setOps(HashMap<String, String> ops) {
		this.ops = ops;
	}

	@Override
	public String toString() {
		return "Qw [hasimg=" + hasimg + ", eid=" + eid + ", sid=" + sid + ", pid=" + pid + ", qid=" + qid + ", qs=" + qs
				+ ", ua=" + ua + ", qt=" + qt + ", t=" + t + ", s=" + s + ", an=" + an + ", p=" + p + ", r=" + r + ", ops=" + ops
				+ "]";
	}

}
