package adc.dxp.rest.api.application.data.vo;

import com.liferay.portal.kernel.dao.orm.Criterion;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.model.Address;
import com.liferay.portal.kernel.model.Contact;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.AddressLocalServiceUtil;
import com.liferay.portal.kernel.service.ContactLocalServiceUtil;
import com.liferay.portal.kernel.util.DigesterUtil;
import java.util.List;

/**
 * Value Object for User data to be returned by the REST API.
 * This separates the internal Liferay User model from what's exposed in the API.
 */
public class UserVO {
    private long userId;
    private String name;
    private String firstName;
    private String lastName;
    private String imageSrc;
    private String portraitURL; // Added for compatibility with other code
    private long portraitId;
    private String tokenId;
    private String imgIdToken;
    private String email;
    private String screenName; // Added for compatibility with other code
    private String emailAddress; // Added for compatibility with other code
    private String jobTitle;
    private String languageId;
    private String timeZoneId;
    private List<Address> adresses;
    private List<Contact> contacts;

    private boolean darkMode;
    private String department;
    private String description;
    private String displayName;
    private String mobileNumber;
    private String office;
    private String telephoneNumber;

    public UserVO() {
        // Default constructor
    }

    public UserVO(User user) {
        if (user == null) {
            return;
        }

        this.userId = user.getUserId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.screenName = user.getScreenName();
        this.emailAddress = user.getEmailAddress();

        // Constructing full name
        StringBuilder nameBuilder = new StringBuilder(user.getFirstName());
        if (user.getMiddleName() != null && !user.getMiddleName().isEmpty()) {
            nameBuilder.append(" ").append(user.getMiddleName());
        }
        if (user.getLastName() != null && !user.getLastName().isEmpty()) {
            nameBuilder.append(" ").append(user.getLastName());
        }
        this.name = nameBuilder.toString();

        this.portraitId = user.getPortraitId();
        this.email = user.getEmailAddress();
        this.jobTitle = user.getJobTitle();
        this.languageId = user.getLanguageId();
        this.timeZoneId = user.getTimeZoneId();

        try {
            // Handle expandos - using null checks to prevent NPEs
            if (user.getExpandoBridge() != null) {
                if (user.getExpandoBridge().getAttribute("department") != null) {
                    department = user.getExpandoBridge().getAttribute("department").toString();
                }
                if (user.getExpandoBridge().getAttribute("description") != null) {
                    description = user.getExpandoBridge().getAttribute("description").toString();
                }
                if (user.getExpandoBridge().getAttribute("display-name") != null) {
                    displayName = user.getExpandoBridge().getAttribute("display-name").toString();
                }
                if (user.getExpandoBridge().getAttribute("mobile-number") != null) {
                    mobileNumber = user.getExpandoBridge().getAttribute("mobile-number").toString();
                }
                if (user.getExpandoBridge().getAttribute("office") != null) {
                    office = user.getExpandoBridge().getAttribute("office").toString();
                }
                if (user.getExpandoBridge().getAttribute("telephone-number") != null) {
                    telephoneNumber = user.getExpandoBridge().getAttribute("telephone-number").toString();
                }
            }

            // In Liferay 7.4, HttpUtil.encodeURL has been deprecated or removed
            // Use URLCodec from Apache Commons Codec instead
            try {
                setImgIdToken(java.net.URLEncoder.encode(DigesterUtil.digest(user.getUuid()), "UTF-8"));
            } catch (java.io.UnsupportedEncodingException e) {
                // Fallback to a simpler approach if encoding fails
                setImgIdToken(DigesterUtil.digest(user.getUuid()));
            }

        } catch (Exception e) {
            // Log error but continue processing
            System.err.println("Error processing user data: " + e.getMessage());
        }
    }

    /**
     * Complements user data with additional information
     */
    public void complementValues() {
        try {
            DynamicQuery dq = ContactLocalServiceUtil.dynamicQuery();
            Criterion userIdCriterion = RestrictionsFactoryUtil.eq("emailAddress", getEmail());
            dq.add(userIdCriterion);
            List<Contact> contacts = ContactLocalServiceUtil.dynamicQuery(dq);
            setContacts(contacts);

            if (!contacts.isEmpty()) {
                Contact contact = contacts.get(0);
                setAdresses(AddressLocalServiceUtil.getAddresses(contact.getCompanyId(),
                        Contact.class.getName(), contact.getContactId()));
            }
        } catch (Exception e) {
            // Log error but continue processing
            System.err.println("Error complementing user values: " + e.getMessage());
        }
    }

    // Getters and Setters

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getImageSrc() {
        return imageSrc;
    }

    public void setImageSrc(String imageSrc) {
        this.imageSrc = imageSrc;
        // For compatibility with other code that might use portraitURL
        this.portraitURL = imageSrc;
    }

    public String getPortraitURL() {
        return portraitURL != null ? portraitURL : imageSrc;
    }

    public void setPortraitURL(String portraitURL) {
        this.portraitURL = portraitURL;
        // For compatibility with other code that might use imageSrc
        this.imageSrc = portraitURL;
    }

    public long getPortraitId() {
        return portraitId;
    }

    public void setPortraitId(long portraitId) {
        this.portraitId = portraitId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        // For compatibility with other code that might use emailAddress
        this.emailAddress = email;
    }

    public String getEmailAddress() {
        return emailAddress != null ? emailAddress : email;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
        // For compatibility with other code that might use email
        this.email = emailAddress;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getLanguageId() {
        return languageId;
    }

    public void setLanguageId(String languageId) {
        this.languageId = languageId;
    }

    public String getTimeZoneId() {
        return timeZoneId;
    }

    public void setTimeZoneId(String timeZoneId) {
        this.timeZoneId = timeZoneId;
    }

    public List<Address> getAdresses() {
        return adresses;
    }

    public void setAdresses(List<Address> adresses) {
        this.adresses = adresses;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public String getImgIdToken() {
        return imgIdToken;
    }

    public void setImgIdToken(String imgIdToken) {
        this.imgIdToken = imgIdToken;
    }

    public List<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }

    public boolean isDarkMode() {
        return darkMode;
    }

    public void setDarkMode(boolean darkMode) {
        this.darkMode = darkMode;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getOffice() {
        return office;
    }

    public void setOffice(String office) {
        this.office = office;
    }

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (userId ^ (userId >>> 32));
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        UserVO other = (UserVO) obj;
        if (userId != other.userId)
            return false;
        return true;
    }
}