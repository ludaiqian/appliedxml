package test.simples;

public class Subject {
	private String ID;
	private String examID;
	private String subjectName;

	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}

	public String getExamID() {
		return examID;
	}

	public void setExamID(String examID) {
		this.examID = examID;
	}

	public String getSubjectName() {
		return subjectName;
	}

	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}

	@Override
	public String toString() {
		return "Subject [ID=" + ID + ", examID=" + examID + ", subjectName=" + subjectName + "]";
	}

}
