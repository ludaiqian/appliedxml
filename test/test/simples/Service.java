package test.simples;

import java.util.ArrayList;

import org.jczh.appliedxml.annotation.Attribute;
import org.jczh.appliedxml.annotation.Namespace;

@Namespace(reference="httt://wwww.baidu.com")
public class Service {
	@Attribute
	private int no;
	@Attribute
	private int errno;
	@Attribute
	private int ver;
	private ArrayList<Exam> exams = new ArrayList<Exam>();
	private ArrayList<ExamType> examTypes = new ArrayList<ExamType>();
	private ArrayList<Subject> subjects = new ArrayList<Subject>();

	public ArrayList<Exam> getExams() {
		return exams;
	}

	public void setExams(ArrayList<Exam> exams) {
		this.exams = exams;
	}

	public ArrayList<ExamType> getExamTypes() {
		return examTypes;
	}

	public void setExamTypes(ArrayList<ExamType> examTypes) {
		this.examTypes = examTypes;
	}

	public ArrayList<Subject> getSubjects() {
		return subjects;
	}

	public void setSubjects(ArrayList<Subject> subjects) {
		this.subjects = subjects;
	}

	public int getNo() {
		return no;
	}

	public void setNo(int no) {
		this.no = no;
	}

	public int getErrno() {
		return errno;
	}

	public void setErrno(int errno) {
		this.errno = errno;
	}

	public int getVer() {
		return ver;
	}

	public void setVer(int ver) {
		this.ver = ver;
	}

	@Override
	public String toString() {
		return "Service [no=" + no + ", errno=" + errno + ", ver=" + ver + ", exams=" + exams + ", examTypes=" + examTypes
				+ ", subjects=" + subjects + "]";
	}

}
