package test.request;

import java.io.IOException;

import org.jczh.appliedxml.Serializer;

/**
 * 生成ans.xml里的描述的xml
 * 
 */
public class Test {

	public static void main(String[] args) throws IOException {
		Ans ans = new Ans();
		An an1 = new An();
		an1.setId("id1");
		an1.setTime("time1");
		Q q = new Q();
		q.setQid("qid1");
		q.setQr("qr1");
		q.setQt("qt1");
		q.setUa("ua1");
		Q q2 = new Q();
		q2.setQid("qid2");
		q2.setQr("qr2");
		q2.setQt("qt2");
		q2.setUa("ua2");
		an1.addQ(q2);
		an1.addQ(q);
		ans.addAn(an1);
		// /
		An an2 = new An();
		an2.setId("id2");
		an2.setTime("time2");
		//
		Q q3 = new Q();
		q3.setQid("qid3");
		q3.setQr("qr3");
		q3.setQt("qt3");
		q3.setUa("ua3");
		an2.addQ(q3);

		ans.addAn(an2);
		System.out.println(ans.toXml(new Serializer()));

	}
}
