package adc.dxp.rest.api.application.resources;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import adc.dxp.rest.api.application.AdcDxpRestApiApplication;
import adc.dxp.rest.api.application.AdcDxpRestApiConfiguration;
import adc.dxp.rest.api.application.data.vo.UserVO;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.*;
import com.liferay.portal.kernel.model.Image;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.service.ImageLocalServiceUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.kernel.webserver.WebServerServletTokenUtil;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchContextFactory;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import adc.dxp.rest.api.application.utils.Constants;
import adc.dxp.rest.api.application.utils.PageUtils;
import adc.dxp.rest.api.application.utils.RequestUtil;
import adc.dxp.rest.api.application.utils.UserUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;


@Component(
        property = {
                "osgi.jaxrs.application.select=(osgi.jaxrs.name=ADC.Services)",
                "osgi.jaxrs.resource=true"
        },
        scope = ServiceScope.PROTOTYPE,
        service = UserResource.class
)
@Path("/users")
@Tag(name = "User")
public class UserResource {

    private static final Log _log = LogFactoryUtil.getLog(UserResource.class);

    AdcDxpRestApiApplication _app;

    @Reference
    private UserLocalService _userLocalService;
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


    // This is causing a dependency issue - switching to use normal utility classes instead
    //@Reference(target = "(component.name=adc.dxp.rest.api.application.AdcDxpRestApiApplication)")
    //private AdcDxpRestApiApplication _app;

    /**
     * Get current user
     */
    @GET
    @Path("/currentUser")
    @Operation(description = "Get current user")
    @Produces(MediaType.APPLICATION_JSON)
    public User getCurrentUser(
            @Context HttpServletRequest request,
            @HeaderParam(Constants.HEADER_GROUP_ID) long groupId) throws PortalException {

        User currentUser = UserUtil.getCurrentUser(request,_app);
        _log.debug("Current user: " + currentUser.getFullName());

        ThemeDisplay themeDisplay = RequestUtil.getTheme(request);
        try {
            String portraitURL = currentUser.getPortraitURL(themeDisplay);
            _log.debug("Portrait URL: " + portraitURL);
        } catch (Exception e) {
            _log.error("Error getting portrait URL", e);
        }
        return currentUser;
    }

    /**
     * Search users
     */
    @GET
    @Operation(
            description = "Retrieves the list all of the users. Results can be paginated, filtered, searched, and sorted."
    )
    @Parameters(
            value = {
                    @Parameter(in = ParameterIn.QUERY, name = "search"),
                    @Parameter(in = ParameterIn.QUERY, name = "filter"),
                    @Parameter(in = ParameterIn.QUERY, name = "page"),
                    @Parameter(in = ParameterIn.QUERY, name = "pageSize"),
                    @Parameter(in = ParameterIn.QUERY, name = "sort")
            }
    )
    public Page<UserVO> search(
            @Parameter(hidden = true) @QueryParam("search") String searchValue,
            @QueryParam("pageSize") Integer pageSize,
            @Context Filter filter,
            @Context Pagination pagination,
            @Context Sort[] sorts,
            @Context HttpServletRequest request) throws PortalException {

        int paginationSize = pageSize == null ? _dxpRESTConfiguration.paginationSize() : pageSize;
        int paginationPage = pagination.getPage();
        _log.debug("Get all users");

        long companyId = _portal.getCompanyId(request);
        _log.debug("Company ID: " + companyId);

        // Create the base query
        DynamicQuery usersDynamicQuery = _userLocalService.dynamicQuery();

        // Add company ID filter - this is essential
        usersDynamicQuery.add(PropertyFactoryUtil.forName("companyId").eq(companyId));

        // Add status filter - only approved users
        usersDynamicQuery.add(RestrictionsFactoryUtil.eq("status", WorkflowConstants.STATUS_APPROVED));

        // Handle search term if provided
        if (searchValue != null && !searchValue.isEmpty()) {
            Junction disjunction = RestrictionsFactoryUtil.disjunction();
            String[] terms = searchValue.split(" ");

            for (String term : terms) {
                String searchTerm = StringPool.PERCENT + term + StringPool.PERCENT;
                disjunction.add(RestrictionsFactoryUtil.ilike("screenName", searchTerm));
                disjunction.add(RestrictionsFactoryUtil.ilike("emailAddress", searchTerm));
                disjunction.add(RestrictionsFactoryUtil.ilike("firstName", searchTerm));
                disjunction.add(RestrictionsFactoryUtil.ilike("middleName", searchTerm));
                disjunction.add(RestrictionsFactoryUtil.ilike("lastName", searchTerm));
                disjunction.add(RestrictionsFactoryUtil.ilike("jobTitle", searchTerm));
            }
            usersDynamicQuery.add(disjunction);
        }

        // IMPORTANT: Create a separate query for counting without the order by clause
        DynamicQuery countQuery = _userLocalService.dynamicQuery();
        countQuery.add(PropertyFactoryUtil.forName("companyId").eq(companyId));
        countQuery.add(RestrictionsFactoryUtil.eq("status", WorkflowConstants.STATUS_APPROVED));

        // Add the same search criteria if present
        if (searchValue != null && !searchValue.isEmpty()) {
            Junction disjunction = RestrictionsFactoryUtil.disjunction();
            String[] terms = searchValue.split(" ");

            for (String term : terms) {
                String searchTerm = StringPool.PERCENT + term + StringPool.PERCENT;
                disjunction.add(RestrictionsFactoryUtil.ilike("screenName", searchTerm));
                disjunction.add(RestrictionsFactoryUtil.ilike("emailAddress", searchTerm));
                disjunction.add(RestrictionsFactoryUtil.ilike("firstName", searchTerm));
                disjunction.add(RestrictionsFactoryUtil.ilike("middleName", searchTerm));
                disjunction.add(RestrictionsFactoryUtil.ilike("lastName", searchTerm));
                disjunction.add(RestrictionsFactoryUtil.ilike("jobTitle", searchTerm));
            }
            countQuery.add(disjunction);
        }

        // Get the count using the count query (without ORDER BY)
        long totalCount = 0;
        try {
            totalCount = _userLocalService.dynamicQueryCount(countQuery);
            _log.debug("Filtered user count: " + totalCount);
        } catch (Exception e) {
            _log.error("Error executing count query", e);
        }

        // Apply sorting to the main query (not to the count query)
        if (sorts != null && sorts.length > 0) {
            Sort sort = sorts[0];
            String fieldName = sort.getFieldName();
            _log.debug("Sorting by: " + fieldName);

            // Map common field names
            if ("name".equals(fieldName)) {
                fieldName = "firstName";
            }

            if (sort.isReverse()) {
                usersDynamicQuery.addOrder(OrderFactoryUtil.desc(fieldName));
            } else {
                usersDynamicQuery.addOrder(OrderFactoryUtil.asc(fieldName));
            }
        } else {
            // Default sort
            usersDynamicQuery.addOrder(OrderFactoryUtil.asc("firstName"));
        }

        // Execute the query with pagination
        int start = (paginationPage - 1) * paginationSize;
        int end = start + paginationSize;

        List<User> users;
        try {
            users = _userLocalService.dynamicQuery(usersDynamicQuery, start, end);
            _log.debug("Retrieved " + users.size() + " users");
        } catch (Exception e) {
            _log.error("Error executing user query", e);
            users = Collections.emptyList();
        }

        List<UserVO> result = new ArrayList<>();
        for (User user : users) {
            result.add(convertToUserVO(user));
        }

        return Page.of(result, pagination, totalCount);
    }

    /**
     * Convert User to UserVO with portrait URL
     */
    private UserVO convertToUserVO(User user) {
        UserVO userVO = new UserVO();
        userVO.setUserId(user.getUserId());
        userVO.setFirstName(user.getFirstName());
        userVO.setLastName(user.getLastName());
        userVO.setScreenName(user.getScreenName());
        userVO.setEmailAddress(user.getEmailAddress());
        userVO.setLanguageId(user.getLanguageId());

        String portraitURL = StringPool.BLANK;
        try {
            if (user.getPortraitId() > 0) {
                Image image = ImageLocalServiceUtil.getImage(user.getPortraitId());
                if (image != null) {
                    String token = WebServerServletTokenUtil.getToken(user.getPortraitId());
                    portraitURL = "/user/" + user.getUserId() + "/portrait?img_id_token=" + token;
                }
            }
        } catch (PortalException e) {
            _log.error("Error getting portrait for user " + user.getUserId(), e);
        }
        userVO.setPortraitURL(portraitURL);

        return userVO;
    }

    /**
     * Returns a user by primary key
     */
    @GET
    @Path("/{id}")
    @Operation(description = "Get a user by primary key.")
    @Parameters(value = { @Parameter(in = ParameterIn.PATH, name = "id")})
    @Produces(MediaType.APPLICATION_JSON)
    public UserVO getUserById(
            @Parameter(hidden = true) @PathParam("id") long id,
            @Context HttpServletRequest request) throws PortalException {

        _log.debug("Get user by id " + id);

        User user = _userLocalService.fetchUser(id);

        if (user == null) {
            throw new PortalException("User with id " + id + " not found");
        }

        UserVO result = new UserVO(user);
        result.complementValues();
        result.setContacts(null);

        return result;
    }

    /**
     * Returns a user by screen name
     */
    @GET
    @Path("/screen-names/{screenName}")
    @Operation(description = "Get a user by screen name.")
    @Parameters(value = { @Parameter(in = ParameterIn.PATH, name = "screenName")})
    @Produces(MediaType.APPLICATION_JSON)
    public UserVO getUserByScreenName(
            @Parameter(hidden = true) @PathParam("screenName") String screenName,
            @Context HttpServletRequest request) throws PortalException {

        _log.debug("Get user by screen name " + screenName);

        long companyId = _portal.getCompanyId(request);
        User userTmp = _userLocalService.fetchUserByScreenName(companyId, screenName);

        if (userTmp == null) {
            throw new PortalException("User with screen name " + screenName + " not found");
        }

        User user = _userLocalService.fetchUser(userTmp.getUserId());

        UserVO result = new UserVO(user);
        result.complementValues();
        result.setContacts(null);

        return result;
    }
}