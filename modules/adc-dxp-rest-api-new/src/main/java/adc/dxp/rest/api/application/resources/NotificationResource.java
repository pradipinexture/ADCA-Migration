package adc.dxp.rest.api.application.resources;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserNotificationDeliveryConstants;
import com.liferay.portal.kernel.model.UserNotificationEvent;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserNotificationEventLocalServiceUtil;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import adc.dxp.rest.api.application.AdcDxpRestApiApplication;
import adc.dxp.rest.api.application.data.dto.NotificationDTO;
import adc.dxp.rest.api.application.utils.Constants;
import adc.dxp.rest.api.application.utils.PageUtils;
import adc.dxp.rest.api.application.utils.UserUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

/**
 * REST endpoints for managing user notifications.
 * 
 * @author ricardo.gomes
 */
@Path("/notifications")
public class NotificationResource extends BasicResource {

    private static final Log _log = LogFactoryUtil.getLog(NotificationResource.class);
    private static AdcDxpRestApiApplication _app;


    @GET
    @Path("")
    @Operation(description = "Get user notifications with optional filters.")
    @Parameters(value = {
        @Parameter(in = ParameterIn.QUERY, name = "onlyUnread", description = "Filter only unread notifications"),
        @Parameter(in = ParameterIn.QUERY, name = "type", description = "Filter by notification type"),
        @Parameter(in = ParameterIn.QUERY, name = "page", description = "Page number"),
        @Parameter(in = ParameterIn.QUERY, name = "pageSize", description = "Page size")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Page<UserNotificationEvent> getNotifications(
            @Parameter(hidden = true) @QueryParam("onlyUnread") Boolean onlyUnread,
            @Parameter(hidden = true) @QueryParam("type") String type,
            @Context HttpServletRequest request,
            @Context Pagination pagination) throws PortalException {

        User currentUser = UserUtil.getCurrentUser(request, _app);
        
        DynamicQuery query = UserNotificationEventLocalServiceUtil.dynamicQuery()
            .add(PropertyFactoryUtil.forName("userId").eq(currentUser.getUserId()));

        if (Boolean.TRUE.equals(onlyUnread)) {
            query.add(PropertyFactoryUtil.forName("archived").eq(false));
        }

        if (type != null && !type.isEmpty()) {
            query.add(PropertyFactoryUtil.forName("type").eq(type));
        }

        int start = pagination.getStartPosition();
        int end = pagination.getEndPosition();

        List<UserNotificationEvent> notifications = 
            UserNotificationEventLocalServiceUtil.dynamicQuery(query, start, end);
        long total = UserNotificationEventLocalServiceUtil.dynamicQueryCount(query);

        return PageUtils.createPage(notifications, pagination, total);
    }

    @POST
    @Path("")
    @Operation(description = "Create a new notification.")
    @Produces(MediaType.APPLICATION_JSON)
    public UserNotificationEvent createNotification(
            @Context HttpServletRequest request,
            @RequestBody NotificationDTO notification,
            @HeaderParam(Constants.HEADER_GROUP_ID) long groupId) throws PortalException {

        User currentUser = UserUtil.getCurrentUser(request, _app);
        notification.getEntryId();
        JSONObject payload = JSONFactoryUtil.createJSONObject()
            .put("entryId", notification.getEntryId())
            .put("notificationMessage", notification.getNotificationMessage())
            .put("userId", notification.getUserIdTarget())
            .put("title", notification.getTitle())
            .put("senderName", notification.getSenderName())
            .put("notificationText", notification.getNotificationText())
            .put("groupId", groupId)
            .put("workflowTaskId", notification.getWorkflowTaskId())
            .put("workflowInstanceId", notification.getWorkflowInstanceId())
            .put("entryClassName", notification.getEntryClassName())
            .put("entryClassPK", notification.getEntryClassPK())
            .put("entryType", notification.getEntryType())
            .put("actionRequired", true);

        ServiceContext serviceContext = new ServiceContext();
        serviceContext.setCompanyId(getCompanyId(request));
        serviceContext.setScopeGroupId(groupId);
        serviceContext.setUserId(currentUser.getUserId());

        // Create notification for the target user
        UserNotificationEventLocalServiceUtil.addUserNotificationEvent(
            notification.getUserIdTarget(),
            notification.getPortletId(),
            System.currentTimeMillis(),
            UserNotificationDeliveryConstants.TYPE_WEBSITE,
            notification.getUserIdTarget(),
            payload.toString(),
            false,
            serviceContext
        );

        return UserNotificationEventLocalServiceUtil.addUserNotificationEvent(
                notification.getUserIdTarget(),
                notification.getPortletId(),
                System.currentTimeMillis(),
                UserNotificationDeliveryConstants.TYPE_WEBSITE,
                notification.getUserIdTarget(),
                payload.toString(),
                false,
                serviceContext
        );
    }

    @PATCH
    @Path("{userNotificationEventId}/markRead")
    @Operation(description = "Mark a notification as read.")
    @Parameters(value = {
        @Parameter(in = ParameterIn.PATH, name = "userNotificationEventId", description = "ID of the notification to mark as read")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public UserNotificationEvent markAsRead(
            @PathParam("userNotificationEventId") long userNotificationEventId,
            @Context HttpServletRequest request) throws PortalException {

        User currentUser = UserUtil.getCurrentUser(request, _app);
        UserNotificationEvent notification = UserNotificationEventLocalServiceUtil.getUserNotificationEvent(userNotificationEventId);

        // Verify the notification belongs to the current user
        if (notification.getUserId() != currentUser.getUserId()) {
            throw new PortalException("User not authorized to modify this notification");
        }

        notification.setArchived(true);
        return UserNotificationEventLocalServiceUtil.updateUserNotificationEvent(notification);
    }

    @POST
    @Path("{userNotificationEventId}/delete")
    @Operation(description = "Delete a notification.")
    @Parameters(value = {
        @Parameter(in = ParameterIn.PATH, name = "userNotificationEventId", description = "ID of the notification to delete")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public void deleteNotification(
            @PathParam("userNotificationEventId") long userNotificationEventId,
            @Context HttpServletRequest request) throws PortalException {

        User currentUser = UserUtil.getCurrentUser(request, _app);
        UserNotificationEvent notification = UserNotificationEventLocalServiceUtil.getUserNotificationEvent(userNotificationEventId);

        // Verify the notification belongs to the current user
        if (notification.getUserId() != currentUser.getUserId()) {
            throw new PortalException("User not authorized to delete this notification");
        }

        UserNotificationEventLocalServiceUtil.deleteUserNotificationEvent(userNotificationEventId);
    }
}
