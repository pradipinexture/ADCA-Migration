package adc.dxp.rest.api.application.notifications;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.UserNotificationEvent;
import com.liferay.portal.kernel.notifications.BaseUserNotificationHandler;
import com.liferay.portal.kernel.notifications.UserNotificationFeedEntry;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;

import javax.portlet.ActionRequest;
import javax.portlet.PortletURL;
import javax.portlet.WindowState;

public class AnnouncementsNotificationHandler extends BaseUserNotificationHandler {

	public static final String PORTLET_ID = "adc-dxp-announcements";
	
	/**
	 * logging instance
	 */
	private static Log _log = LogFactoryUtil.getLog(AnnouncementsNotificationHandler.class);

	public AnnouncementsNotificationHandler() {
		setPortletId(AnnouncementsNotificationHandler.PORTLET_ID);
		_log.setLogWrapperClassName("****************************");
	}

	@Override
	protected String getBody(UserNotificationEvent userNotificationEvent, ServiceContext serviceContext)
			throws Exception {
		
		_log.setLogWrapperClassName("****************************");
		
		
		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(userNotificationEvent.getPayload());

		long employeeId = jsonObject.getLong("employeeId");

		String title = "A new Employee data has been added ";

		String bodyText = "";

		LiferayPortletResponse liferayPortletResponse = serviceContext.getLiferayPortletResponse();

		PortletURL confirmURL = liferayPortletResponse.createActionURL(
				AnnouncementsNotificationHandler.PORTLET_ID);
		confirmURL.setParameter(ActionRequest.ACTION_NAME, "doSomethingGood");
		confirmURL.setParameter("redirect", serviceContext.getLayoutFullURL());
		confirmURL.setParameter("employeeId", String.valueOf(employeeId));
		confirmURL.setParameter("userNotificationEventId",
				String.valueOf(userNotificationEvent.getUserNotificationEventId()));
		confirmURL.setWindowState(WindowState.NORMAL);

		PortletURL ignoreURL = liferayPortletResponse.createActionURL(
				AnnouncementsNotificationHandler.PORTLET_ID);
		ignoreURL.setParameter(ActionRequest.ACTION_NAME, "cancelForExample");
		ignoreURL.setParameter("redirect", serviceContext.getLayoutFullURL());
		ignoreURL.setParameter("employeeId", String.valueOf(employeeId));
		ignoreURL.setParameter("userNotificationEventId",
				String.valueOf(userNotificationEvent.getUserNotificationEventId()));
		ignoreURL.setWindowState(WindowState.NORMAL);

		String body = StringUtil.replace(getBodyTemplate(),
				new String[] { "[$CONFIRM$]", "[$CONFIRM_URL$]", "[$IGNORE$]", "[$IGNORE_URL$]", "[$TITLE$]",
						"[$BODY_TEXT$]" },
				new String[] { serviceContext.translate("approve"), confirmURL.toString(),
						serviceContext.translate("reject"), ignoreURL.toString(), title, bodyText });

		return body;

	}

	@Override
	protected String getLink(UserNotificationEvent userNotificationEvent, ServiceContext serviceContext)
			throws Exception {
		
		_log.setLogWrapperClassName("****************************");
		
		
		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(userNotificationEvent.getPayload());

		long employeeId = jsonObject.getLong("employeeId");

		LiferayPortletResponse liferayPortletResponse = serviceContext.getLiferayPortletResponse();
		PortletURL confirmURL = liferayPortletResponse.createRenderURL(
				AnnouncementsNotificationHandler.PORTLET_ID);
		confirmURL.setParameter("redirect", serviceContext.getLayoutFullURL());
		confirmURL.setParameter("employeeId", String.valueOf(employeeId));
		confirmURL.setParameter("userNotificationEventId",
				String.valueOf(userNotificationEvent.getUserNotificationEventId()));
		confirmURL.setWindowState(WindowState.NORMAL);

		return confirmURL.toString();

	}

	
	@Override
	public String getPortletId() {
		_log.setLogWrapperClassName("****************************");
		return super.getPortletId();
	}
	
	@Override
	public String getSelector() {
		_log.setLogWrapperClassName("****************************");
		return super.getSelector();
	}
	
	@Override
	protected UserNotificationFeedEntry doInterpret(UserNotificationEvent userNotificationEvent,
			ServiceContext serviceContext) throws Exception {
		_log.setLogWrapperClassName("****************************");
		return super.doInterpret(userNotificationEvent, serviceContext);
	}
	
	@Override
	public UserNotificationFeedEntry interpret(UserNotificationEvent userNotificationEvent,
			ServiceContext serviceContext) throws PortalException {
		_log.setLogWrapperClassName("****************************");
		return super.interpret(userNotificationEvent, serviceContext);
	}
	
	@Override
	protected boolean isActionable() {
		_log.setLogWrapperClassName("****************************");
		return super.isActionable();
	}
	
	@Override
	public boolean equals(Object obj) {
		_log.setLogWrapperClassName("****************************");
		return super.equals(obj);
	}
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		_log.setLogWrapperClassName("****************************");
		return super.clone();
	}
	
	@Override
	protected void finalize() throws Throwable {
		_log.setLogWrapperClassName("****************************");
		super.finalize();
	}
	
	@Override
	public int hashCode() {
		_log.setLogWrapperClassName("****************************");
		return super.hashCode();
	}
	@Override
	public boolean isApplicable(UserNotificationEvent userNotificationEvent, ServiceContext serviceContext) {
		_log.setLogWrapperClassName("****************************");
		return super.isApplicable(userNotificationEvent, serviceContext);
	}
	@Override
	public boolean isDeliver(long userId, long classNameId, int notificationType, int deliveryType,
			ServiceContext serviceContext) throws PortalException {
		_log.setLogWrapperClassName("****************************");
		return super.isDeliver(userId, classNameId, notificationType, deliveryType, serviceContext);
	}
	@Override
	public boolean isOpenDialog() {
		_log.setLogWrapperClassName("****************************");
		return super.isOpenDialog();
	}
	@Override
	protected void setActionable(boolean actionable) {
		_log.setLogWrapperClassName("****************************");
		super.setActionable(actionable);
	}
	@Override
	protected void setOpenDialog(boolean openDialog) {
		_log.setLogWrapperClassName("****************************");
		super.setOpenDialog(openDialog);
	}
	@Override
	protected void setPortletId(String portletId) {
		_log.setLogWrapperClassName("****************************");
		super.setPortletId(portletId);
	}
	@Override
	protected void setSelector(String selector) {
		_log.setLogWrapperClassName("****************************");
		super.setSelector(selector);
	}
	@Override
	public String toString() {
		_log.setLogWrapperClassName("****************************");
		return super.toString();
	}
	
	
	protected String getBodyTemplate() throws Exception {
		
		_log.setLogWrapperClassName("****************************");
		
		StringBundler sb = new StringBundler(5);
		sb.append("<div class=\"title\">[$TITLE$]</div><div ");
		sb.append("class=\"body\">[$BODY_TEXT$]<a class=\"btn btn-action ");
		sb.append("btn-success\" href=\"[$CONFIRM_URL$]\">[$CONFIRM$]</a>");
		sb.append("<a class=\"btn btn-action btn-warning\" href=\"");
		sb.append("[$IGNORE_URL$]\">[$IGNORE$]</a></div>");
		return sb.toString();
	}

}
