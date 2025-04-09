package com.liferaysavvy.employee.rest.application.rest.application.resources;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.liferay.calendar.model.CalendarBooking;
import com.liferay.calendar.service.CalendarBookingLocalServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferaysavvy.employee.rest.application.AdcDxpRestApiApplication;
import com.liferaysavvy.employee.rest.application.data.vo.CalendarBookingVO;
import com.liferaysavvy.employee.rest.application.rest.application.utils.Constants;
import com.liferaysavvy.employee.rest.application.rest.application.utils.UserUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;

/**
 * REST endpoints for managing calendar events.
 *
 * @author ricardo.gomes
 */
@Path("/events")
@Produces(MediaType.APPLICATION_JSON)
public class EventResource extends BasicResource {

    private static final Log _log = LogFactoryUtil.getLog(EventResource.class);

    private final AdcDxpRestApiApplication _app;

    public EventResource(AdcDxpRestApiApplication app) {
        this._app = app;
    }

    /*
    @GET
    @Path("/search")
    @Operation(description = "Search calendar events with optional filters and pagination.")
    @Parameters(value = {
            @Parameter(in = ParameterIn.QUERY, name = "keywords", description = "Search keywords"),
            @Parameter(in = ParameterIn.QUERY, name = "calendarId", description = "Filter by calendar ID"),
            @Parameter(in = ParameterIn.QUERY, name = "startDate", description = "Start date (dd-MM-yyyy)"),
            @Parameter(in = ParameterIn.QUERY, name = "endDate", description = "End date (dd-MM-yyyy)"),
            @Parameter(in = ParameterIn.QUERY, name = "status", description = "Event status"),
            @Parameter(in = ParameterIn.QUERY, name = "page", description = "Page number"),
            @Parameter(in = ParameterIn.QUERY, name = "pageSize", description = "Page size")
    })

    @Produces(MediaType.APPLICATION_JSON)
    public Page<CalendarBookingVO> searchEvents(
            @Parameter(hidden = true) @QueryParam("keywords") String keywords,
            @Parameter(hidden = true) @QueryParam("calendarId") Long calendarId,
            @Parameter(hidden = true) @QueryParam("startDate") String startDateParam,
            @Parameter(hidden = true) @QueryParam("endDate") String endDateParam,
            @Parameter(hidden = true) @QueryParam("status") Integer status,
            @Context HttpServletRequest request,
            @Context Pagination pagination) throws PortalException {

        User currentUser = UserUtil.getCurrentUser(request, _app);
        long companyId = getCompanyId(request);
        String languageId = getLanguageId(request);

        // Get group ID from header or default
        String groupIdString = request.getHeader("groupId");
        String defaultGroupId = GetterUtil.get(PropsUtil.get("default-group-id"), "0");
        long groupId = groupIdString != null ? Long.parseLong(groupIdString) : Long.parseLong(defaultGroupId);

        // Parse dates
        Date startDate = null;
        try {
            startDate = DateUtils.parseDate(startDateParam, "dd-MM-yyyy");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        Date endDate = null;
        try {
            endDate = DateUtils.parseDate(endDateParam, "dd-MM-yyyy");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        if (endDate != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(endDate);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            calendar.add(Calendar.SECOND, -1);
            endDate = calendar.getTime();
        }

        // Build calendar IDs array
        long[] calendarIds = calendarId != null && calendarId != -1
                ? new long[] {calendarId}
                : _app._dxpRESTConfiguration.calendars().stream()
                .mapToLong(Long::longValue)
                .toArray();

        // Get calendar resources
        DynamicQuery resourceQuery = CalendarResourceLocalServiceUtil.dynamicQuery()
                .add(PropertyFactoryUtil.forName("companyId").eq(companyId))
                .add(PropertyFactoryUtil.forName("groupId").eq(groupId));

        List<CalendarResource> resources = CalendarResourceLocalServiceUtil.dynamicQuery(resourceQuery);
        long[] resourceIds = resources.stream()
                .map(CalendarResource::getCalendarResourceId)
                .mapToLong(Long::longValue)
                .toArray();

        // Set up search parameters
        long startTime = startDate != null ? startDate.getTime() : 0;
        long endTime = endDate != null ? endDate.getTime() : Long.MAX_VALUE;
        int[] statuses = status != null
                ? new int[] {status}
                : new int[] {WorkflowConstants.STATUS_APPROVED};

        // Create dynamic query for calendar bookings
        DynamicQuery bookingQuery = CalendarBookingLocalServiceUtil.dynamicQuery()
                .add(PropertyFactoryUtil.forName("companyId").eq(companyId))
                .add(PropertyFactoryUtil.forName("groupId").eq(groupId))
                .add(PropertyFactoryUtil.forName("startTime").ge(startTime))
                .add(PropertyFactoryUtil.forName("endTime").le(endTime))
                .add(PropertyFactoryUtil.forName("status").in(statuses));

        if (keywords != null && !keywords.isEmpty()) {
            bookingQuery.add(
                    RestrictionsFactoryUtil.or(
                            PropertyFactoryUtil.forName("title").like("%" + keywords + "%"),
                            PropertyFactoryUtil.forName("description").like("%" + keywords + "%")
                    )
            );
        }

        // Add calendar ID filter if specified
        if (calendarId != null && calendarId != -1) {
            bookingQuery.add(PropertyFactoryUtil.forName("calendarId").eq(calendarId));
        }

        // Add ordering
        bookingQuery.addOrder(PropertyFactoryUtil.forName("startTime").asc());

        // Execute query with pagination
        int start = pagination.getStartPosition();
        int end = pagination.getEndPosition();

        List<CalendarBooking> bookings = CalendarBookingLocalServiceUtil.dynamicQuery(
                bookingQuery, start, end);
        long total = CalendarBookingLocalServiceUtil.dynamicQueryCount(bookingQuery);

        // Convert to VOs and filter by permissions
        List<CalendarBookingVO> result = bookings.stream()
                .filter(booking -> {
                    try {
                        return isAllowed(
                                Constants.CALENDAR_ENTITY,
                                booking.getCalendarId(),
                                currentUser,
                                companyId
                        );
                    } catch (PortalException e) {
                        _log.error("Error checking permissions for booking " + booking.getCalendarBookingId(), e);
                        return false;
                    }
                })
                .map(booking -> new CalendarBookingVO(booking, languageId))
                .collect(Collectors.toList());

        return PageUtils.createPage(result, pagination, total);
    }
*/
    @GET
    @Path("/{calendarBookingId}")
    @Operation(description = "Get a calendar event by ID.")
    @Parameters(value = {
            @Parameter(in = ParameterIn.PATH, name = "calendarBookingId", description = "Calendar booking ID")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public CalendarBookingVO getEvent(
            @PathParam("calendarBookingId") long calendarBookingId,
            @Context HttpServletRequest request) throws PortalException {

        User currentUser = UserUtil.getCurrentUser(request, _app);
        long companyId = currentUser.getCompanyId();

        CalendarBooking booking = CalendarBookingLocalServiceUtil.getCalendarBooking(calendarBookingId);

        // Check permissions
        if (!isAllowed(Constants.CALENDAR_ENTITY, booking.getCalendarId(), currentUser, companyId)) {
            throw new ForbiddenException("User not authorized to view this event");
        }

        return new CalendarBookingVO(booking, getLanguageId(request));
    }

    @PATCH
    @Path("/{calendarBookingId}/status/{status}")
    @Operation(description = "Update the status of a calendar event.")
    @Parameters(value = {
            @Parameter(in = ParameterIn.PATH, name = "calendarBookingId", description = "Calendar booking ID"),
            @Parameter(in = ParameterIn.PATH, name = "status", description = "New status (PENDING=0, APPROVED=1, DENIED=2, DRAFT=3)")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public CalendarBookingVO updateEventStatus(
            @PathParam("calendarBookingId") long calendarBookingId,
            @PathParam("status") int status,
            @Context HttpServletRequest request) throws PortalException {

        User currentUser = UserUtil.getCurrentUser(request, _app);
     //   long companyId = getCompanyId(request);
        String languageId = getLanguageId(request);

        CalendarBooking booking = CalendarBookingLocalServiceUtil.getCalendarBooking(calendarBookingId);

        // Check permissions
        if (!isAllowed(Constants.CALENDAR_ENTITY, booking.getCalendarId(), currentUser, Long.valueOf("20119"))) {
            throw new ForbiddenException("User not authorized to modify this event");
        }

        // Update status
        ServiceContext serviceContext = new ServiceContext();
        serviceContext.setCompanyId(20119);
        serviceContext.setUserId(currentUser.getUserId());

        booking = CalendarBookingLocalServiceUtil.updateStatus(
                currentUser.getUserId(),
                calendarBookingId,
                status,
                serviceContext
        );

        return new CalendarBookingVO(booking, languageId);
    }
}