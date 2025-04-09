package adc.dxp.rest.api.application.data.vo;

import java.util.Date;
import java.util.List;

import com.liferay.calendar.model.Calendar;
import com.liferay.calendar.model.CalendarBooking;
import com.liferay.calendar.service.CalendarLocalServiceUtil;

import adc.dxp.rest.api.application.data.Content;
import adc.dxp.rest.api.application.utils.ContentType;

public class CalendarBookingVO extends Content {

	Long calendarBookingId;
	String title;
	Boolean allDay;
	Long calendarId;
	String calendarName;
	String description;
	String location;
	Long parentCalendarBookingId;
	String recurrence;
	Object recurrenceObj;
	Object recurringBookingId;
	Integer status;
	Date startTime;
	Date endTime;
	Long userId;
	Integer currentUserStatus;
	
	List<CalendarBoobingUserVO> calendarBoobingUserVO;

	public CalendarBookingVO(Long calendarBookingId, String title, Boolean allDay, Long calendarId, String description,
			String location, Long parentCalendarBookingId, String recurrence, Object recurrenceObj,
			Object recurringBookingId, Integer status, Date startTime, Date endTime, Long userId) {
		super(ContentType.EVENT);
		this.calendarBookingId = calendarBookingId;
		this.title = title;
		this.allDay = allDay;
		this.calendarId = calendarId;
		this.description = description;
		this.location = location;
		this.parentCalendarBookingId = parentCalendarBookingId;
		this.recurrence = recurrence;
		this.recurrenceObj = recurrenceObj;
		this.recurringBookingId = recurringBookingId;
		this.status = status;
		this.startTime = startTime;
		this.endTime = endTime;
		this.userId = userId;
	}

	public CalendarBookingVO(CalendarBooking b, String lenguageId) {
		super(ContentType.EVENT);
		this.calendarBookingId = b.getCalendarBookingId();
		this.title = b.getTitle(lenguageId);
		this.allDay = b.getAllDay();
		this.calendarId = b.getCalendarId();
		this.description = b.getDescription(lenguageId);
		this.location = b.getLocation();
		this.parentCalendarBookingId = b.getParentCalendarBookingId();
		this.recurrence = b.getRecurrence();
		this.recurrenceObj = b.getRecurrenceObj();
		this.recurringBookingId = b.getRecurringCalendarBookingId();
		this.status = b.getStatus();
		this.startTime = new Date(b.getStartTime());
		this.endTime = new Date(b.getEndTime());
		this.userId = b.getUserId();
		
		Calendar calendar = CalendarLocalServiceUtil.fetchCalendar(calendarId);
		
		if (calendar != null) {
			this.setCalendarName(calendar.getName(lenguageId));
		}
		
	}

	public Long getCalendarBookingId() {
		return calendarBookingId;
	}

	public void setCalendarBookingId(Long calendarBookingId) {
		this.calendarBookingId = calendarBookingId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Boolean getAllDay() {
		return allDay;
	}

	public void setAllDay(Boolean allDay) {
		this.allDay = allDay;
	}

	public Long getCalendarId() {
		return calendarId;
	}

	public void setCalendarId(Long calendarId) {
		this.calendarId = calendarId;
	}

	public String getCalendarName() {
		return calendarName;
	}

	public void setCalendarName(String calendarName) {
		this.calendarName = calendarName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Long getParentCalendarBookingId() {
		return parentCalendarBookingId;
	}

	public void setParentCalendarBookingId(Long parentCalendarBookingId) {
		this.parentCalendarBookingId = parentCalendarBookingId;
	}

	public String getRecurrence() {
		return recurrence;
	}

	public void setRecurrence(String recurrence) {
		this.recurrence = recurrence;
	}

	public Object getRecurrenceObj() {
		return recurrenceObj;
	}

	public void setRecurrenceObj(Object recurrenceObj) {
		this.recurrenceObj = recurrenceObj;
	}

	public Object getRecurringBookingId() {
		return recurringBookingId;
	}

	public void setRecurringBookingId(Object recurringBookingId) {
		this.recurringBookingId = recurringBookingId;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public List<CalendarBoobingUserVO> getCalendarBoobingUserVO() {
		return calendarBoobingUserVO;
	}

	public void setCalendarBoobingUserVO(List<CalendarBoobingUserVO> calendarBoobingUserVO) {
		this.calendarBoobingUserVO = calendarBoobingUserVO;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Integer getCurrentUserStatus() {
		return currentUserStatus;
	}

	public void setCurrentUserStatus(Integer currentUserStatus) {
		this.currentUserStatus = currentUserStatus;
	}
	
	
	
}
