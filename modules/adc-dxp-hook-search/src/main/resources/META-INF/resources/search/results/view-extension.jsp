<%@ page import="com.liferay.journal.model.JournalArticle" %>
<%@ page import="com.liferay.journal.service.JournalArticleLocalServiceUtil" %>
<%@ page import="com.liferay.portal.kernel.util.PropsUtil" %>
<%@ page import="com.liferay.portal.kernel.util.GetterUtil" %>

<%
    String ddmTemplateKeyForCustomWebcontents = GetterUtil.getString(169145, "");
    long defaultGroupId = GetterUtil.getLong(20124, 0L);
    long structureIdFaqs = GetterUtil.getLong(61686, 0L);

    String journalArticleClass = "com.liferay.journal.model.JournalArticle";
    String calendarBookingClass = "com.liferay.calendar.model.CalendarBooking";
%>