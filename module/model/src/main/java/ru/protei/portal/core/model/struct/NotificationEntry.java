package ru.protei.portal.core.model.struct;

import ru.protei.portal.core.model.dict.En_ContactItemType;

import java.io.Serializable;

/**
 * Created by michael on 26.05.17.
 */
public class NotificationEntry implements Serializable {

    private String address;
    private En_ContactItemType contactItemType;
    private String langCode;


    public NotificationEntry() {
    }

    public NotificationEntry(String address, En_ContactItemType contactItemType, String langCode) {
        this.address = address;
        this.contactItemType = contactItemType;
        this.langCode = langCode;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public int hashCode() {
        return this.address.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof NotificationEntry) {
            return ((NotificationEntry)obj).getAddress().equals(this.address);
        }
        return false;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public En_ContactItemType getContactItemType() {
        return contactItemType;
    }

    public void setContactItemType(En_ContactItemType contactItemType) {
        this.contactItemType = contactItemType;
    }

    public String getLangCode() {
        return langCode;
    }

    public void setLangCode(String langCode) {
        this.langCode = langCode;
    }

    public static NotificationEntry email (String email, String langCode) {
        return new NotificationEntry(email, En_ContactItemType.EMAIL, langCode);
    }
}
