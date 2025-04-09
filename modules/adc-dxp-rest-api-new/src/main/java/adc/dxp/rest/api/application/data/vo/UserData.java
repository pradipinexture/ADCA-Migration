package adc.dxp.rest.api.application.data.vo;

import com.liferay.portal.kernel.dao.orm.Criterion;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSON;
import com.liferay.portal.kernel.model.Address;
import com.liferay.portal.kernel.model.Contact;
import com.liferay.portal.kernel.model.Image;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.AddressLocalServiceUtil;
import com.liferay.portal.kernel.service.ContactLocalServiceUtil;
import com.liferay.portal.kernel.service.ImageLocalServiceUtil;
import com.liferay.portal.kernel.util.DigesterUtil;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.webserver.WebServerServletTokenUtil;

import java.io.Serializable;
import java.util.List;


public class UserData implements Serializable { 

	private static final long serialVersionUID = 1L;
	
	private long userId;
	private String name;
	private String firstName;
	private String lastName;
	private String imageSrc;
	private long portraitId;
	private String tokenId;
	private String imgIdToken;
	private String email;
	private String jobTitle;
	private String languageId;
	private String timeZoneId;
	
	private boolean darkMode;
	private String department;
	private String description;
	private String displayName;
	private String mobileNumber;
	private String office;
	private String telephoneNumber;



	public UserData(User user) {

		this.userId = user.getUserId();
		this.firstName = user.getFirstName();
		this.lastName = user.getLastName();
		this.name = user.getFirstName().concat(user.getMiddleName().isEmpty() ? "" : " ").concat(user.getMiddleName()).concat(user.getLastName().isEmpty() ? "" : " ").concat(user.getLastName());
		this.portraitId = user.getPortraitId();
		this.email = user.getEmailAddress();
		this.jobTitle = user.getJobTitle();
		this.languageId = user.getLanguageId();
		this.timeZoneId = user.getTimeZoneId();

		try {			
			
			//darkMode = user.getExpandoBridge().getAttribute("dark-mode");
			if (user.getExpandoBridge() != null) {
				department = user.getExpandoBridge().getAttribute("department") != null ? user.getExpandoBridge().getAttribute("department").toString() : null;
				description = user.getExpandoBridge().getAttribute("description") != null ? user.getExpandoBridge().getAttribute("description").toString() : null;
				displayName = user.getExpandoBridge().getAttribute("display-name") != null ? user.getExpandoBridge().getAttribute("display-name").toString() : null;
				mobileNumber = user.getExpandoBridge().getAttribute("mobile-number") != null ? user.getExpandoBridge().getAttribute("mobile-number").toString() : null;
				office = user.getExpandoBridge().getAttribute("office") != null ? user.getExpandoBridge().getAttribute("office").toString() : null;
				telephoneNumber = user.getExpandoBridge().getAttribute("telephone-number") != null ? user.getExpandoBridge().getAttribute("telephone-number").toString() : null;							
			}
			Image image;
			image = ImageLocalServiceUtil.getImage(portraitId);

			if (image != null) {
				setTokenId(WebServerServletTokenUtil.getToken(image.getImageId()));
			}

			setImgIdToken(HttpUtil.encodeURL(DigesterUtil.digest(user.getUuid())));

		} catch (PortalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	
	//getters + setters

	/**
	 *
	 * @return Id of user
	 */
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
	
	
	//equals and hashcode

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
		UserData other = (UserData) obj;
		if (userId != other.userId)
			return false;
		return true;
	}
	
	


}