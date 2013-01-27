package test.simples;


public class Exam {
	private String ID;
	private String examTypeID;
	private String examName;

	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}

	public String getExamTypeID() {
		return examTypeID;
	}

	public void setExamTypeID(String examTypeID) {
		this.examTypeID = examTypeID;
	}

	public String getExamName() {
		return examName;
	}

	public void setExamName(String examName) {
		this.examName = examName;
	}

	@Override
	public String toString() {
		return "Exam [ID=" + ID + ", examTypeID=" + examTypeID + ", examName=" + examName + "]";
	}



}
