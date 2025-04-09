package adc.dxp.rest.api.application.data.vo;

/**
 * 
 * Announcement Value Object
 * 
 * @author ricardo.gomes
 *
 */
public class AnnouncementsVO {

	private boolean alert;
	private String content;

	/**
	 * 
	 * @return The announcement has alert flag
	 */
	public boolean isAlert() {
		return alert;
	}

	public void setAlert(boolean alert) {
		this.alert = alert;
	}

	/**
	 * 
	 * @return Content of announcement
	 */
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
