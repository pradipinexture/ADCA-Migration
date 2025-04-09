package adc.dxp.rest.api.application.data;

public class Ratings {


	private double positiveRatings;
	private double negativeRatings;
	private double totalCount;
	private boolean thumbsUp;
	private boolean thumbsDown;
	private boolean voted;
	
	public Ratings() {
		
	}
	
	//getters + setters
	
	public double getPositiveRatings() {
		return positiveRatings;
	}

	public void setPositiveRatings(double positiveRatings) {
		this.positiveRatings = positiveRatings;
	}

	public double getNegativeRatings() {
		return negativeRatings;
	}

	public void setNegativeRatings(double negativeRatings) {
		this.negativeRatings = negativeRatings;
	}
	
	public boolean isThumbsUp() {
		return thumbsUp;
	}

	public void setThumbsUp(boolean thumbsUp) {
		this.thumbsUp = thumbsUp;
	}

	public boolean isThumbsDown() {
		return thumbsDown;
	}

	public void setThumbsDown(boolean thumbsDown) {
		this.thumbsDown = thumbsDown;
	}

	public double getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(double totalCount) {
		this.totalCount = totalCount;
	}

	public boolean isVoted() {
		return voted;
	}

	public void setVoted(boolean voted) {
		this.voted = voted;
	}
	
	
}
