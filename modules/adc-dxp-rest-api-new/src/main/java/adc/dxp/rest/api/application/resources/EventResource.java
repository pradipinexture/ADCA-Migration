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
import com.liferay.portal.kernel.dao.orm.Criterion;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
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
    @Path("/{id}")
    @Operation(description = "Get a booking.")
    @Parameters({ @Parameter(in = ParameterIn.PATH, name = "id") })
    @Produces(MediaType.APPLICATION_JSON)
    public CalendarBookingVO getCalendarBooking(
            @Parameter(hidden = true) @PathParam("id") long id,
            @Context HttpServletRequest request) throws PortalException {

        _log.debug("Get booking by ids " + id);

        User currentUser = UserUtil.getCurrentUser(request,_app);
        CalendarBooking booking = _calendarBookingLocalService.fetchCalendarBooking(id);

        if (!isAllowed("com.liferay.calendar.model.Calendar",
                _calendarBookingLocalService.getCalendarBooking(booking.getParentCalendarBookingId()).getCalendarId(),
                currentUser,
                getCompanyId(request))) {
            throw new ForbiddenException();
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

        List<CalendarBooking> bookings = _calendarLocalService.dynamicQuery(bookingsDQ, -1, -1);

        for (CalendarBooking b : bookings) {
            CalendarResource calendarResource = _calendarResourceLocalService.fetchCalendarResource(b.getCalendarResourceId());
            User user = _userLocalService.fetchUser(calendarResource.getUserId());

            if (user == null) {
                _log.warn("User is null");
            } else {
                UserVO userVo = new UserVO(user);
                userVo.complementValues();
                userVo.setContacts(null);

                CalendarBoobingUserVO calendarBookingUser = new CalendarBoobingUserVO(userVo, b.getStatus());

                if (currentUser.getUserId() == calendarBookingUser.getUser().getUserId()) {
                    result.setCurrentUserStatus(calendarBookingUser.getStatus());
                }

                if (result.getCalendarBoobingUserVO().indexOf(calendarBookingUser) == -1) {
                    result.getCalendarBoobingUserVO().add(calendarBookingUser);
                }
            }
        }

        return result;
    }

    @PATCH
    @Path("/{id}/accept")
    @Operation(description = "Subscribe in a calendar booking")
    @Parameters({ @Parameter(in = ParameterIn.PATH, name = "id") })
    @Produces(MediaType.APPLICATION_JSON)
    public CalendarBookingVO addCalendarBooking(
            @Parameter(hidden = true) @PathParam("id") long id,
            @Context HttpServletRequest request) throws PortalException {

        _log.debug("Accept calendar booking");

        String languageId = getLanguageId(request);
        User currentUser = UserUtil.getCurrentUser(request,_app);

        CalendarBooking booking = setCalendarBookingStatus(id, 0, languageId, currentUser, getCompanyId(request));
        CalendarBookingVO result = new CalendarBookingVO(booking, languageId);

        return result;
    }

    @PATCH
    @Path("/{id}/maybe")
    @Operation(description = "Think about in a calendar booking")
    @Parameters({ @Parameter(in = ParameterIn.PATH, name = "id") })
    @Produces(MediaType.APPLICATION_JSON)
    public CalendarBookingVO maybeCalendarBooking(
            @Parameter(hidden = true) @PathParam("id") long id,
            @Context HttpServletRequest request) throws PortalException {

        _log.debug("Think about calendar booking");

        String languageId = getLanguageId(request);
        User currentUser = UserUtil.getCurrentUser(request,_app);

        CalendarBooking booking = setCalendarBookingStatus(id, 9, languageId, currentUser, getCompanyId(request));
        CalendarBookingVO result = new CalendarBookingVO(booking, languageId);

        return result;
    }

    @PATCH
    @Path("/{id}/decline")
    @Operation(description = "Decline a calendar booking")
    @Parameters({ @Parameter(in = ParameterIn.PATH, name = "id") })
    @Produces(MediaType.APPLICATION_JSON)
    public CalendarBookingVO declineCalendarBooking(
            @Parameter(hidden = true) @PathParam("id") long id,
            @Context HttpServletRequest request) throws PortalException {

        _log.debug("Decline calendar booking");

        String languageId = getLanguageId(request);
        User currentUser = UserUtil.getCurrentUser(request,_app);

        CalendarBooking booking = setCalendarBookingStatus(id, 4, languageId, currentUser, getCompanyId(request));
        CalendarBookingVO result = new CalendarBookingVO(booking, languageId);

        return result;
    }

    private CalendarBooking setCalendarBookingStatus(
            long calendarBookingId,
            int status,
            String languageId,
            User currentUser,
            Long companyId) throws PortalException {

        if (!isAllowed("com.liferay.calendar.model.Calendar",
                _calendarBookingLocalService.getCalendarBooking(calendarBookingId).getCalendarId(),
                currentUser,
                companyId)) {
            throw new ForbiddenException();
        }

        ServiceContext serviceContext = new ServiceContext();
        List<CalendarBooking> bookings = getCalendarBookings(calendarBookingId, currentUser, serviceContext, true);

        CalendarBooking result = null;

        for (CalendarBooking booking : bookings) {
            result = _calendarBookingLocalService.updateStatus(
                    currentUser.getUserId(),
                    booking.getCalendarBookingId(),
                    status,
                    serviceContext);
        }

        return result;
    }

    private List<CalendarBooking> getCalendarBookings(
            Long calendarBookingId,
            User currentUser,
            ServiceContext serviceContext,
            Boolean createIfMissing) throws PortalException {

        DynamicQuery bookingsDQ = _calendarBookingLocalService.dynamicQuery();
        Criterion idCriterion = RestrictionsFactoryUtil.ne("calendarBookingId", calendarBookingId);
        bookingsDQ.add(idCriterion);

        Criterion parentIdCriterion = RestrictionsFactoryUtil.eq("parentCalendarBookingId", calendarBookingId);
        bookingsDQ.add(parentIdCriterion);

        DynamicQuery resourceDQ = _calendarResourceLocalService.dynamicQuery();
        Criterion userIdCriterion = RestrictionsFactoryUtil.eq("userId", currentUser.getUserId());
        resourceDQ.add(userIdCriterion);

        List<CalendarResource> calendarResources = _calendarResourceLocalService.dynamicQuery(resourceDQ);

        List<Long> ids = new ArrayList<>();
        for (CalendarResource resource : calendarResources) {
            ids.add(resource.getCalendarResourceId());
        }

        Criterion calendarResourceCriterion = RestrictionsFactoryUtil.in("calendarResourceId", ids);
        bookingsDQ.add(calendarResourceCriterion);

        List<CalendarBooking> bookings = _calendarLocalService.dynamicQuery(bookingsDQ);

        if (createIfMissing && bookings.isEmpty()) {
            //addCalendar(calendarBookingId, calendarResources, currentUser, serviceContext);
            bookings = _calendarLocalService.dynamicQuery(bookingsDQ);
        }

        return bookings;
    }

    @GET
    @Operation(description = "Retrieves the list all of the events. Results can be paginated, filtered, searched, and sorted.")
    @Parameters({
            @Parameter(in = ParameterIn.QUERY, name = "search"),
            @Parameter(in = ParameterIn.QUERY, name = "filter"),
            @Parameter(in = ParameterIn.QUERY, name = "page"),
            @Parameter(in = ParameterIn.QUERY, name = "pageSize"),
            @Parameter(in = ParameterIn.QUERY, name = "sort")
    })
    public Page<CalendarBookingVO> searchV2(
            @Parameter(hidden = true) @QueryParam("search") String searchValue,
            @Parameter(hidden = true) @QueryParam("calendarId") Long calendarId,
            @Parameter(hidden = true) @QueryParam("startDate") String startDateParam,
            @Parameter(hidden = true) @QueryParam("endDate") String endDateParam,
            @QueryParam("pageSize") Integer pageSize,
            @Context Filter filter,
            @Context Pagination pagination,
            @Context Sort[] sorts,
            @Context HttpServletRequest request) throws PortalException {

        _log.debug("Get all calendar bookings");

        // Configuration and pagination setup
        int paginationSize = (pageSize == null) ?
                this._dxpRESTConfiguration.paginationSize() : pageSize;
        int paginationPage = pagination.getPage();

        // Date parsing with improved error handling
        Date startTime = null;
        Date endTime = null;

        try {
            if (Validator.isNotNull(startDateParam)) {
                startTime = new SimpleDateFormat("dd-MM-yyyy").parse(startDateParam);
            }
            if (Validator.isNotNull(endDateParam)) {
                endTime = new SimpleDateFormat("dd-MM-yyyy").parse(endDateParam);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(endTime);
                calendar.add(Calendar.HOUR_OF_DAY, 23);
                calendar.add(Calendar.MINUTE, 59);
                calendar.add(Calendar.SECOND, 59);
                endTime = calendar.getTime();
            }
        } catch (ParseException e) {
            _log.error("Error parsing date parameters", e);
            throw new PortalException("Invalid date format. Expected: dd-MM-yyyy", e);
        }

        List<CalendarBookingVO> result = new ArrayList<>();
        long companyId = PortalUtil.getCompanyId(request);

        // Group ID configuration
        String defaultGroupId = GetterUtil.getString(
                PropsUtil.get("default-group-id"), "0");
        String groupIdString = request.getHeader("groupId");
        long groupId = GetterUtil.getLong(groupIdString, GetterUtil.getLong(defaultGroupId));
        long[] groupIds = {groupId};

        // Language configuration
        String languageIdString = request.getHeader("languageId");
        String languageIdAcceptLanguage =
                (request.getHeader("Accept-Language") != null &&
                        request.getHeader("Accept-Language").equalsIgnoreCase("ar-AE")) ?
                        "ar_AE" : "en_US";
        String languageIdRequest =
                (languageIdString != null) ? languageIdString : languageIdAcceptLanguage;
        String languageId = (languageIdRequest != null) ? languageIdRequest : "en";

        // TimeZone configuration - NEW for 7.4
        TimeZone displayTimeZone = null;
        try {
            // Get user's timezone or fall back to company/portal timezone
            User currentUser = UserUtil.getCurrentUser(request, this._app);
            if (currentUser != null) {
                displayTimeZone = currentUser.getTimeZone();
            }

            // Fallback to company timezone if user timezone not available
            if (displayTimeZone == null) {
                Company company = CompanyLocalServiceUtil.getCompany(companyId);
                displayTimeZone = company.getTimeZone();
            }

            // Final fallback to system default
            if (displayTimeZone == null) {
                displayTimeZone = TimeZone.getDefault();
            }
        } catch (Exception e) {
            _log.warn("Could not determine display timezone, using system default", e);
            displayTimeZone = TimeZone.getDefault();
        }

        // Order by comparator setup
        OrderByComparator<CalendarBooking> orderByComparator = null;
        if (sorts != null && sorts.length > 0) {
            if (sorts[0].getFieldName().equalsIgnoreCase("startTime")) {
                orderByComparator = new CalendarBookingStartTimeComparator(!sorts[0].isReverse());
            } else if (sorts[0].getFieldName().equalsIgnoreCase("title")) {
                orderByComparator = OrderByComparatorFactoryUtil.create(
                        "CalendarBooking",
                        new Object[]{"title", Boolean.valueOf(!sorts[0].isReverse())}
                );
            }
        }

        // Calendar and resource IDs setup
        List<Long> resourceIds = new ArrayList<>();
        List<com.liferay.calendar.model.Calendar> calendars = CalendarLocalServiceUtil.getCalendars(-1, -1);

        for (com.liferay.calendar.model.Calendar cal : calendars) {
            resourceIds.add(cal.getCalendarResourceId());
        }

        long[] calendarResourceIds = resourceIds.stream()
                .mapToLong(Long::longValue)
                .toArray();

        // Calendar IDs configuration
        long[] calendarIds;
        if (calendarId != null && calendarId != -1L) {
            calendarIds = new long[]{calendarId};
        } else {
            List<Long> configuredCalendars = new ArrayList<>();
            try {
                configuredCalendars = this._dxpRESTConfiguration.calendars();
                calendarIds = configuredCalendars.stream()
                        .mapToLong(Long::longValue)
                        .toArray();
            } catch (Exception e) {
                calendarIds = new long[0];
            }
        }

        // Time range setup
        long startTimeLong = (startTime != null) ? startTime.getTime() : 0L;
        long endTimeLong = (endTime != null) ? endTime.getTime() : Long.MAX_VALUE;

        // Search parameters
        boolean recurring = false;
        int[] statuses = {WorkflowConstants.STATUS_APPROVED};

        // Perform search - Updated for 7.4 with displayTimeZone parameter
        List<CalendarBooking> bookings = CalendarBookingServiceUtil.search(
                companyId,
                groupIds,
                calendarIds,
                calendarResourceIds,
                -1L,  // parentCalendarBookingId
                searchValue,
                startTimeLong,
                endTimeLong,
                displayTimeZone,  // NEW: displayTimeZone parameter for 7.4
                recurring,
                statuses,
                -1,  // start
                -1,  // end
                orderByComparator
        );

        _log.debug("bookings SIZE: " + bookings.size());

        // Process results with permission checks
        User currentUser = UserUtil.getCurrentUser(request, this._app);
        long currentCompanyId = getCompanyId(request);

        for (CalendarBooking booking : bookings) {
            try {
                // Get parent calendar booking for permission check
                CalendarBooking parentBooking =
                        CalendarBookingLocalServiceUtil.getCalendarBooking(
                                booking.getParentCalendarBookingId()
                        );

                // Permission check using the calendar ID
                if (isAllowed(
                        "com.liferay.calendar.model.Calendar",
                        parentBooking.getCalendarId(),
                        currentUser,
                        currentCompanyId)) {
                    result.add(new CalendarBookingVO(booking, languageId));
                }
            } catch (Exception e) {
                _log.warn("Error processing calendar booking: " + booking.getCalendarBookingId(), e);
                // Continue processing other bookings
            }
        }

        return PageUtils.createPage(result, pagination, result.size());
    }

    // Helper method to get company ID
     public long getCompanyId(HttpServletRequest request) {
        return PortalUtil.getCompanyId(request);
    }

    @Override
    public boolean isAllowed(String name, Long primaryKey, User currentUser, Long companyId) throws PortalException {
        Boolean result = super.isAllowed(name, primaryKey, currentUser, companyId);

        if (!result) {
            result = !getCalendarBookings(primaryKey, currentUser, new ServiceContext(), false).isEmpty();
        }

        return result;
    }

    public String getLanguageId(HttpServletRequest request) {
        String languageIdRequestRequest = request.getHeader("languageId");
        String languageIdAcceptLanguage = request.getHeader("Accept-Language") != null &&
                request.getHeader("Accept-Language").equalsIgnoreCase("ar-AE") ? "ar_AE" : "en_US";
        String languageIdRequest = languageIdRequestRequest != null ? languageIdRequestRequest : languageIdAcceptLanguage;
        return languageIdRequest != null ? languageIdRequest : "en";
    }


}