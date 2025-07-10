package adc.dxp.rest.api.application.resources;

import adc.dxp.rest.api.application.AdcDxpRestApiApplication;
import adc.dxp.rest.api.application.AdcDxpRestApiConfiguration;
import adc.dxp.rest.api.application.data.vo.CalendarBoobingUserVO;
import adc.dxp.rest.api.application.data.vo.CalendarBookingVO;
import adc.dxp.rest.api.application.data.vo.CalendarVO;
import adc.dxp.rest.api.application.data.vo.UserVO;
import adc.dxp.rest.api.application.utils.Constants;
import adc.dxp.rest.api.application.utils.PageUtils;
import adc.dxp.rest.api.application.utils.UserUtil;
import com.liferay.calendar.model.CalendarBooking;
import com.liferay.calendar.model.CalendarResource;
import com.liferay.calendar.service.*;
import com.liferay.calendar.util.comparator.CalendarBookingStartTimeComparator;
import com.liferay.portal.kernel.dao.orm.*;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.*;
import com.liferay.portal.kernel.util.*;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import org.apache.commons.lang3.time.DateUtils;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;



@Component(
        property = {
                "osgi.jaxrs.application.select=(osgi.jaxrs.name=ADC.Services)",
                "osgi.jaxrs.resource=true"
        },
        scope = ServiceScope.PROTOTYPE,
        service = EventResource.class
)
@Path("/events")
@Produces(MediaType.APPLICATION_JSON)
public class EventResource extends BasicResource {

    private static final Log _log = LogFactoryUtil.getLog(EventResource.class);

    AdcDxpRestApiApplication _app;


    @Reference
    private UserLocalService _userLocalService;

    @Reference
    private CalendarBookingService _calendarBookingService;

    @Reference
    private CalendarLocalService _calendarLocalService;

    @Reference
    private CalendarResourceLocalService _calendarResourceLocalService;

    @Reference
    private CalendarBookingLocalService _calendarBookingLocalService;

    @Reference
    private GroupLocalService _groupLocalService;

    @Reference
    private ClassNameLocalService _classNameLocalService;

    @Reference
    private ResourcePermissionLocalService _resourcePermissionLocalService;

    @Reference
    private ConfigurationProvider _configurationProvider;

    @Reference
    private Portal _portal;

    private volatile AdcDxpRestApiConfiguration _dxpRESTConfiguration;

    @Activate
    protected void activate() {
        try {
            _dxpRESTConfiguration = _configurationProvider.getCompanyConfiguration(AdcDxpRestApiConfiguration.class, 0);
        } catch (ConfigurationException e) {
            _log.error("Error loading configuration", e);
        }
    }

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
            @Context Pagination pagination) throws PortalException, ParseException {

        User currentUser = UserUtil.getCurrentUser(request, _app);
        long companyId = getCompanyId(request);
        String languageId = getLanguageId(request);

        // Get group ID from header or default
        String groupIdString = request.getHeader("groupId");
        String defaultGroupId = GetterUtil.get(PropsUtil.get("default-group-id"), "0");
        long groupId = groupIdString != null ? Long.parseLong(groupIdString) : Long.parseLong(defaultGroupId);

        // Parse dates
        Date startDate = DateUtils.parseDate(startDateParam, "dd-MM-yyyy");
        Date endDate = DateUtils.parseDate(endDateParam, "dd-MM-yyyy");
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
                : _dxpRESTConfiguration.calendars().stream()
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
        long companyId = getCompanyId(request);

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
        long companyId = getCompanyId(request);
        String languageId = getLanguageId(request);

        CalendarBooking booking = CalendarBookingLocalServiceUtil.getCalendarBooking(calendarBookingId);

        // Check permissions
        if (!isAllowed(Constants.CALENDAR_ENTITY, booking.getCalendarId(), currentUser, companyId)) {
            throw new ForbiddenException("User not authorized to modify this event");
        }

        // Update status
        ServiceContext serviceContext = new ServiceContext();
        serviceContext.setCompanyId(companyId);
        serviceContext.setUserId(currentUser.getUserId());

        booking = CalendarBookingLocalServiceUtil.updateStatus(
                currentUser.getUserId(),
                calendarBookingId,
                status,
                serviceContext
        );

        return new CalendarBookingVO(booking, languageId);
    }
    @GET
    @Path("/calendars")
    @Operation(description = "Get all calendars.")
    @Produces(MediaType.APPLICATION_JSON)
    public Page<CalendarVO> getCalendars(
            @Parameter(hidden = true) @QueryParam("isRestricted") @DefaultValue("true") boolean isRestricted,
            @QueryParam("pageSize") Integer pageSize,
            @Context Filter filter,
            @Context Pagination pagination,
            @Context HttpServletRequest request) throws PortalException {

        _log.debug("Get all calendars");

        List<CalendarVO> result = new ArrayList<>();
        int paginationSize = pageSize == null ?
                _dxpRESTConfiguration.paginationSize() : pageSize;

        int paginationPage = pagination.getPage();

        List<com.liferay.calendar.model.Calendar> calendars = _calendarLocalService.getCalendars(-1, -1);

        String languageIdRequestRequest = request.getHeader("languageId");
        String languageIdAcceptLanguage = request.getHeader("Accept-Language") != null &&
                request.getHeader("Accept-Language").equalsIgnoreCase("ar-AE") ? "ar_AE" : "en_US";
        String languageIdRequest = languageIdRequestRequest != null ? languageIdRequestRequest : languageIdAcceptLanguage;
        String languageId = languageIdRequest != null ? languageIdRequest : "en";

        for (com.liferay.calendar.model.Calendar calendar : calendars) {
            result.add(new CalendarVO(calendar.getPrimaryKey(), calendar.getName(languageId)));
//            if (!isRestricted || _dxpRESTConfiguration.calendars().indexOf(calendar.getCalendarId()) != -1) {
//                if (isAllowed("com.liferay.calendar.model.Calendar",
//                        calendar.getCalendarId(),
//                        UserUtil.getCurrentUser(request,_app),
//                        getCompanyId(request))) {
//
//                    result.add(new CalendarVO(calendar.getPrimaryKey(), calendar.getName(languageId)));
//                }
//            }
        }

        return Page.of(result, pagination, paginationPage);
    }


    @GET
    @Path("/booking/{id}")
    @Operation(description = "Get a booking.")
    @Parameters({ @Parameter(in = ParameterIn.PATH, name = "id") })
    @Produces(MediaType.APPLICATION_JSON)
    public CalendarBookingVO getCalendarBooking(
            @Parameter(hidden = true) @PathParam("id") long id,
            @Context HttpServletRequest request) throws PortalException {

        _log.debug("Get booking by ids " + id);

        long userId = PrincipalThreadLocal.getUserId();
        if (userId == 0) {
            throw new PrincipalException("User not authenticated");
        }

        CalendarBooking booking = _calendarBookingLocalService.fetchCalendarBooking(id);
        if (booking == null) {
            throw new NotFoundException("Calendar booking not found");
        }

        if (!isUserAllowedCalendarBooking(booking, userId)) {
            throw new ForbiddenException("User does not have access to this calendar booking");
        }

        String languageIdRequestRequest = request.getHeader("languageId");
        String languageIdAcceptLanguage = request.getHeader("Accept-Language") != null &&
                request.getHeader("Accept-Language").equalsIgnoreCase("ar-AE") ? "ar_AE" : "en";
        String languageIdRequest = languageIdRequestRequest != null ? languageIdRequestRequest : languageIdAcceptLanguage;
        String languageId = languageIdRequest != null ? languageIdRequest : "en";

        CalendarBookingVO result = new CalendarBookingVO(booking, languageId);
        result.setCalendarBoobingUserVO(new ArrayList<>());

        DynamicQuery bookingsDQ = _calendarBookingLocalService.dynamicQuery();
        Criterion idCriterion = RestrictionsFactoryUtil.ne("calendarBookingId", id);
        bookingsDQ.add(idCriterion);

        Criterion parentIdCriterion = RestrictionsFactoryUtil.eq("parentCalendarBookingId", id);
        bookingsDQ.add(parentIdCriterion);

        List<CalendarBooking> bookings = _calendarBookingLocalService.dynamicQuery(bookingsDQ, -1, -1);

        for (CalendarBooking b : bookings) {
            if (isUserAllowedCalendarBooking(b, userId)) {
                CalendarResource calendarResource = _calendarResourceLocalService.fetchCalendarResource(b.getCalendarResourceId());
                User user = _userLocalService.fetchUser(calendarResource.getUserId());

                if (user == null) {
                    _log.warn("User is null");
                } else {
                    UserVO userVo = new UserVO(user);
                    userVo.complementValues();
                    userVo.setContacts(null);

                    CalendarBoobingUserVO calendarBookingUser = new CalendarBoobingUserVO(userVo, b.getStatus());

                    if (userId == calendarBookingUser.getUser().getUserId()) {
                        result.setCurrentUserStatus(calendarBookingUser.getStatus());
                    }

                    if (result.getCalendarBoobingUserVO().indexOf(calendarBookingUser) == -1) {
                        result.getCalendarBoobingUserVO().add(calendarBookingUser);
                    }
                }
            }
        }

        return result;
    }


    @PATCH
    @Path("/{id}/accept")
    @Operation(description = "Accept a calendar booking invitation")
    @Parameters({ @Parameter(in = ParameterIn.PATH, name = "id") })
    @Produces(MediaType.APPLICATION_JSON)
    public CalendarBookingVO addCalendarBooking(
            @Parameter(hidden = true) @PathParam("id") long id,
            @Context HttpServletRequest request) throws PortalException {

        _log.debug("Accept calendar booking invitation");

        long userId = PrincipalThreadLocal.getUserId();
        if (userId == 0) {
            throw new PrincipalException("User not authenticated");
        }

        String languageId = getLanguageId(request);

        CalendarBooking booking = updateCalendarBookingStatus(id, 0, userId);
        CalendarBookingVO result = new CalendarBookingVO(booking, languageId);

        return result;
    }

    @PATCH
    @Path("/{id}/maybe")
    @Operation(description = "Maybe attend a calendar booking")
    @Parameters({ @Parameter(in = ParameterIn.PATH, name = "id") })
    @Produces(MediaType.APPLICATION_JSON)
    public CalendarBookingVO maybeCalendarBooking(
            @Parameter(hidden = true) @PathParam("id") long id,
            @Context HttpServletRequest request) throws PortalException {

        _log.debug("Maybe attend calendar booking");

        long userId = PrincipalThreadLocal.getUserId();
        if (userId == 0) {
            throw new PrincipalException("User not authenticated");
        }

        String languageId = getLanguageId(request);

        CalendarBooking booking = updateCalendarBookingStatus(id, 9, userId);
        CalendarBookingVO result = new CalendarBookingVO(booking, languageId);

        return result;
    }

    @PATCH
    @Path("/{id}/decline")
    @Operation(description = "Decline a calendar booking invitation")
    @Parameters({ @Parameter(in = ParameterIn.PATH, name = "id") })
    @Produces(MediaType.APPLICATION_JSON)
    public CalendarBookingVO declineCalendarBooking(
            @Parameter(hidden = true) @PathParam("id") long id,
            @Context HttpServletRequest request) throws PortalException {

        _log.debug("Decline calendar booking invitation");

        long userId = PrincipalThreadLocal.getUserId();
        if (userId == 0) {
            throw new PrincipalException("User not authenticated");
        }

        String languageId = getLanguageId(request);

        CalendarBooking booking = updateCalendarBookingStatus(id, 4, userId);
        CalendarBookingVO result = new CalendarBookingVO(booking, languageId);

        return result;
    }
    private CalendarBooking updateCalendarBookingStatus(long calendarBookingId, int status, long userId)
            throws PortalException {

        CalendarBooking parentBooking = _calendarBookingLocalService.fetchCalendarBooking(calendarBookingId);
        if (parentBooking == null) {
            throw new NotFoundException("Calendar booking not found");
        }

        if (!isUserAllowedCalendarBooking(parentBooking, userId)) {
            throw new ForbiddenException("User does not have access to this calendar booking");
        }

        CalendarBooking userBooking = findUserCalendarBooking(calendarBookingId, userId);

        if (userBooking == null) {
            throw new ForbiddenException("User is not invited to this calendar booking");
        }

        ServiceContext serviceContext = new ServiceContext();
        serviceContext.setUserId(userId);

        try {
            return _calendarBookingLocalService.updateStatus(
                    userId,
                    userBooking.getCalendarBookingId(),
                    status,
                    serviceContext
            );
        } catch (Exception e) {
            _log.error("Error updating calendar booking status", e);
            throw new ForbiddenException("Failed to update calendar booking status");
        }
    }

    private CalendarBooking findUserCalendarBooking(long parentCalendarBookingId, long userId) {
        try {
            List<CalendarBooking> childBookings = _calendarBookingLocalService.getChildCalendarBookings(parentCalendarBookingId);

            for (CalendarBooking childBooking : childBookings) {
                CalendarResource resource = _calendarResourceLocalService.fetchCalendarResource(
                        childBooking.getCalendarResourceId()
                );

                if (resource != null && resource.getUserId() == userId) {
                    return childBooking;
                }
            }

            return null;
        } catch (Exception e) {
            _log.error("Error finding user calendar booking", e);
            return null;
        }
    }

    private CalendarBooking createUserCalendarBooking(CalendarBooking parentBooking, long userId)
            throws PortalException {

        List<CalendarResource> userCalendarResources = getUserCalendarResources(userId);

        if (userCalendarResources.isEmpty()) {
            throw new PortalException("No calendar resource found for user");
        }

        CalendarResource userCalendarResource = userCalendarResources.get(0);

        List<com.liferay.calendar.model.Calendar> userCalendars = _calendarLocalService.getCalendarResourceCalendars(
                userCalendarResource.getGroupId(),
                userCalendarResource.getCalendarResourceId()
        );

        if (userCalendars.isEmpty()) {
            throw new PortalException("No calendar found for user calendar resource");
        }

        com.liferay.calendar.model.Calendar userCalendar = userCalendars.get(0);

        ServiceContext serviceContext = new ServiceContext();
        serviceContext.setUserId(userId);
        serviceContext.setScopeGroupId(parentBooking.getGroupId());

        return _calendarBookingLocalService.addCalendarBooking(
                userId,
                userCalendar.getCalendarId(),
                new long[0],
                parentBooking.getParentCalendarBookingId(),
                userCalendarResource.getCalendarResourceId(),
                parentBooking.getTitleMap(),
                parentBooking.getDescriptionMap(),
                parentBooking.getLocation(),
                parentBooking.getStartTime(),
                parentBooking.getEndTime(),
                parentBooking.getAllDay(),
                parentBooking.getRecurrence(),
                parentBooking.getFirstReminder(),
                parentBooking.getFirstReminderType(),
                parentBooking.getSecondReminder(),
                parentBooking.getSecondReminderType(),
                serviceContext
        );
    }

    private List<CalendarResource> getUserCalendarResources(long userId) {
        try {
            DynamicQuery resourceQuery = _calendarResourceLocalService.dynamicQuery();
            resourceQuery.add(PropertyFactoryUtil.forName("userId").eq(userId));
            resourceQuery.add(PropertyFactoryUtil.forName("classNameId").eq(
                    _classNameLocalService.getClassNameId(User.class.getName())
            ));

            return _calendarResourceLocalService.dynamicQuery(resourceQuery);
        } catch (Exception e) {
            _log.error("Error getting user calendar resources", e);
            return new ArrayList<>();
        }
    }



    private boolean isUserAllowedCalendarBooking(CalendarBooking calendarBooking, long userId) {
        try {
            if (calendarBooking.getUserId() == userId) {
                return true;
            }

            com.liferay.calendar.model.Calendar calendar = _calendarLocalService.getCalendar(calendarBooking.getCalendarId());
            if (isUserAllowedCalendar(calendar, userId)) {
                return true;
            }

            if (isUserInvitedToBooking(calendarBooking, userId)) {
                return true;
            }

            if (hasCalendarResourcePermission(calendarBooking.getCalendarId(), userId, ActionKeys.VIEW)) {
                return true;
            }

            return false;

        } catch (Exception e) {
            _log.error("Error checking calendar booking access for user " + userId + " and booking " +
                    calendarBooking.getCalendarBookingId(), e);
            return false;
        }
    }

    private boolean isUserAllowedCalendar(com.liferay.calendar.model.Calendar calendar, long userId) {
        try {
            if (calendar.getUserId() == userId) {
                return true;
            }

            PermissionChecker permissionChecker = PermissionThreadLocal.getPermissionChecker();
            if (permissionChecker == null) {
                return false;
            }

            try {
                return permissionChecker.hasPermission(
                        calendar.getGroupId(),
                        "com.liferay.calendar.model.Calendar",
                        calendar.getCalendarId(),
                        ActionKeys.VIEW
                );
            } catch (Exception e) {
                _log.debug("PermissionChecker.hasPermission failed for calendar " + calendar.getCalendarId(), e);
                return false;
            }

        } catch (Exception e) {
            _log.error("Error checking calendar access for user " + userId + " and calendar " +
                    calendar.getCalendarId(), e);
            return false;
        }
    }

    private boolean isUserInvitedToBooking(CalendarBooking calendarBooking, long userId) {
        try {
            List<CalendarBooking> childBookings = _calendarBookingLocalService.getChildCalendarBookings(
                    calendarBooking.getCalendarBookingId());

            for (CalendarBooking childBooking : childBookings) {
                CalendarResource resource = _calendarResourceLocalService.fetchCalendarResource(
                        childBooking.getCalendarResourceId()
                );

                if (resource != null && resource.getUserId() == userId) {
                    return true;
                }
            }

            return false;

        } catch (Exception e) {
            _log.error("Error checking booking invitation for user " + userId + " and booking " +
                    calendarBooking.getCalendarBookingId(), e);
            return false;
        }
    }

    private boolean hasCalendarResourcePermission(long calendarId, long userId, String actionId) {
        try {
            com.liferay.calendar.model.Calendar calendar = _calendarLocalService.getCalendar(calendarId);

            return _resourcePermissionLocalService.hasResourcePermission(
                    calendar.getCompanyId(),
                    "com.liferay.calendar.model.Calendar",
                    ResourceConstants.SCOPE_INDIVIDUAL,
                    String.valueOf(calendarId),
                    userId,
                    actionId
            );

        } catch (Exception e) {
            _log.error("Error checking resource permission for user " + userId + " and calendar " + calendarId, e);
            return false;
        }
    }

    private List<CalendarBooking> filterBookingsByUserPermission(List<CalendarBooking> bookings, long userId) {
        return bookings.stream()
                .filter(booking -> isUserAllowedCalendarBooking(booking, userId))
                .collect(Collectors.toList());
    }


    @GET
    @Path("")
    @Produces(MediaType.APPLICATION_JSON)
    public Page<CalendarBookingVO> searchV2(
            @QueryParam("search") String searchValue,
            @QueryParam("calendarId") Long calendarId,
            @QueryParam("startDate") String startDateParam,
            @QueryParam("endDate") String endDateParam,
            @QueryParam("pageSize") Integer pageSize,
            @Context Pagination pagination,
            @Context HttpServletRequest request
    ) throws PortalException {

        _log.debug("Get all calendar bookings");

        if (calendarId == null || calendarId <= 0) {
            throw new IllegalArgumentException("calendarId is required and must be > 0");
        }
        User currentUser = UserUtil.getCurrentUser(request,_app);
        long userId = PrincipalThreadLocal.getUserId();
        if (userId == 0) {
            throw new PrincipalException("User not authenticated");
        }

        com.liferay.calendar.model.Calendar calendar = _calendarLocalService.getCalendar(calendarId);
        if (!isUserAllowedCalendar(calendar, userId)) {
            throw new PrincipalException("User does not have access to this calendar");
        }

        long startTimeLong = 0L;
        long endTimeLong = Long.MAX_VALUE;

        try {
            if (Validator.isNotNull(startDateParam)) {
                Date startDate = new SimpleDateFormat("dd-MM-yyyy").parse(startDateParam);
                startTimeLong = startDate.getTime();
            }
            if (Validator.isNotNull(endDateParam)) {
                Date endDate = new SimpleDateFormat("dd-MM-yyyy").parse(endDateParam);
                Calendar cal = Calendar.getInstance();
                cal.setTime(endDate);
                cal.add(Calendar.HOUR_OF_DAY, 23);
                cal.add(Calendar.MINUTE, 59);
                cal.add(Calendar.SECOND, 59);
                endTimeLong = cal.getTime().getTime();
            }
        } catch (Exception e) {
            _log.error("Invalid date format", e);
            throw new PortalException("Invalid date format. Expected: dd-MM-yyyy", e);
        }

        DynamicQuery bookingQuery = _calendarBookingLocalService.dynamicQuery()
                .add(PropertyFactoryUtil.forName("calendarId").eq(calendarId))
                .add(PropertyFactoryUtil.forName("startTime").ge(startTimeLong))
                .add(PropertyFactoryUtil.forName("endTime").le(endTimeLong));

        if (Validator.isNotNull(searchValue)) {
            bookingQuery.add(
                    RestrictionsFactoryUtil.or(
                            PropertyFactoryUtil.forName("title").like("%" + searchValue + "%"),
                            PropertyFactoryUtil.forName("description").like("%" + searchValue + "%")
                    )
            );
        }

        List<CalendarBooking> allBookings = _calendarBookingLocalService.dynamicQuery(bookingQuery);

        List<CalendarBooking> allowedBookings = filterBookingsByUserPermission(allBookings, userId);

        int start = pagination.getStartPosition();
        int end = Math.min(pagination.getEndPosition(), allowedBookings.size());

        List<CalendarBooking> paginatedBookings = allowedBookings.subList(start, Math.min(end, allowedBookings.size()));
        long total = allowedBookings.size();

        String languageId = getLanguageId(request);

        List<CalendarBookingVO> result = paginatedBookings.stream()
                .map(booking -> new CalendarBookingVO(booking, languageId))
                .collect(Collectors.toList());

        return PageUtils.createPage(result, pagination, total);
    }

    // Helper method to get company ID
    public long getCompanyId(HttpServletRequest request) {
        return PortalUtil.getCompanyId(request);
    }

    @Override
    public boolean isAllowed(String name, Long primaryKey, User currentUser, Long companyId) throws PortalException {
//        Boolean result = super.isAllowed(name, primaryKey, currentUser, companyId);
//
//        if (!result) {
//            result = !getCalendarBookings(primaryKey, currentUser, new ServiceContext(), false).isEmpty();
//        }
//
//        return result;
        return true;
    }

    public String getLanguageId(HttpServletRequest request) {
        String languageIdRequestRequest = request.getHeader("languageId");
        String languageIdAcceptLanguage = request.getHeader("Accept-Language") != null &&
                request.getHeader("Accept-Language").equalsIgnoreCase("ar-AE") ? "ar_AE" : "en_US";
        String languageIdRequest = languageIdRequestRequest != null ? languageIdRequestRequest : languageIdAcceptLanguage;
        return languageIdRequest != null ? languageIdRequest : "en";
    }


}