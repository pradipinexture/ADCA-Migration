package adc.dxp.rest.api.application.data.dto;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author ricardo.gomes
 *
 */
@XmlRootElement(name="notification")
public class NotificationDTO {

	long entryId;
	String notificationMessage;
	String title;
	String senderName;
	String notificationText;
	String workflowTaskId;
	String workflowInstanceId;
	String entryClassName;
	String entryClassPK;
	String entryType;
	String portletId;
	long userIdTarget;

	public NotificationDTO(long entryId, String notificationMessage, String title, String senderName,
			String notificationText, String workflowTaskId, String workflowInstanceId, String entryClassName,
			String entryClassPK, String entryType, String portletId, long userIdTarget) {
		super();
		this.entryId = entryId;
		this.notificationMessage = notificationMessage;
		this.title = title;
		this.senderName = senderName;
		this.notificationText = notificationText;
		this.workflowTaskId = workflowTaskId;
		this.workflowInstanceId = workflowInstanceId;
		this.entryClassName = entryClassName;
		this.entryClassPK = entryClassPK;
		this.entryType = entryType;
		this.portletId = portletId;
		this.userIdTarget = userIdTarget;
	}

	public NotificationDTO() {
		super();
	}

	public long getEntryId() {
		return entryId;
	}

	public void setEntryId(long entryId) {
		this.entryId = entryId;
	}

	public String getNotificationMessage() {
		return notificationMessage;
	}

	public void setNotificationMessage(String notificationMessage) {
		this.notificationMessage = notificationMessage;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public String getNotificationText() {
		return notificationText;
	}

	public void setNotificationText(String notificationText) {
		this.notificationText = notificationText;
	}

	public String getWorkflowTaskId() {
		return workflowTaskId;
	}

	public void setWorkflowTaskId(String workflowTaskId) {
		this.workflowTaskId = workflowTaskId;
	}

	public String getWorkflowInstanceId() {
		return workflowInstanceId;
	}

	public void setWorkflowInstanceId(String workflowInstanceId) {
		this.workflowInstanceId = workflowInstanceId;
	}

	public String getEntryClassName() {
		return entryClassName;
	}

	public void setEntryClassName(String entryClassName) {
		this.entryClassName = entryClassName;
	}

	public String getEntryClassPK() {
		return entryClassPK;
	}

	public void setEntryClassPK(String entryClassPK) {
		this.entryClassPK = entryClassPK;
	}

	public String getEntryType() {
		return entryType;
	}

	public void setEntryType(String entryType) {
		this.entryType = entryType;
	}

	public String getPortletId() {
		return portletId;
	}

	public void setPortletId(String portletId) {
		this.portletId = portletId;
	}

	public long getUserIdTarget() {
		return userIdTarget;
	}

	public void setUserIdTarget(long userIdTarget) {
		this.userIdTarget = userIdTarget;
	}

}
