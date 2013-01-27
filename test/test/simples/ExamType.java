package test.simples;


public class ExamType {
	private String ID;

	private String dictName;

	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}

	public String getDictName() {
		return dictName;
	}

	public void setDictName(String dictName) {
		this.dictName = dictName;
	}

	@Override
	public String toString() {
		return "ExamType [ID=" + ID + ", dictName=" + dictName + "]";
	}

	
}
