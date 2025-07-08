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
    @Path("/booking/{id}")
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
            throw new BadRequestException("calendarId is required and must be > 0");
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

        // Build DynamicQuery
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

        // ** NO ORDERING **
        _log.debug("Executing DynamicQuery WITHOUT any ORDER BY");
        int start = pagination.getStartPosition();
        int end = pagination.getEndPosition();

        List<CalendarBooking> bookings = _calendarBookingLocalService.dynamicQuery(bookingQuery, start, end);
        long total = _calendarBookingLocalService.dynamicQueryCount(bookingQuery);

        String languageId = getLanguageId(request);

        List<CalendarBookingVO> result = bookings.stream()
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