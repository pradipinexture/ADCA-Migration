package adc.dxp.rest.api.application.data;

public class Question {
	
	private String questionID;

	
	
	public Question(String questionID) {
		super();
		this.questionID = questionID;
	}
	

	// getters + setters 
	
	public String getQuestionID() {
		return questionID;
	}

	public void setQuestionID(String questionID) {
		this.questionID = questionID;
	}
	
}
