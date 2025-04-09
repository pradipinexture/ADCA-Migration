package adc.dxp.rest.api.application.data.vo;

public class ContactVO {
    private long contactId;
    private String contactType;
    private String contactValue;

    public ContactVO() {
    }

    public ContactVO(long contactId, String contactType, String contactValue) {
        this.contactId = contactId;
        this.contactType = contactType;
        this.contactValue = contactValue;
    }

    // Getters and Setters

    public long getContactId() {
        return contactId;
    }

    public void setContactId(long contactId) {
        this.contactId = contactId;
    }

    public String getContactType() {
        return contactType;
    }

    public void setContactType(String contactType) {
        this.contactType = contactType;
    }

    public String getContactValue() {
        return contactValue;
    }

    public void setContactValue(String contactValue) {
        this.contactValue = contactValue;
    }
}