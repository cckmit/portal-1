package ru.protei.portal.core.model.struct;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.protei.portal.core.model.dict.En_ContactDataAccess;
import ru.protei.portal.core.model.dict.En_PhoneType;

/**
 * Created by michael on 07.11.16.
 */
@JsonAutoDetect
public class ContactPhone {

    @JsonProperty("v")
    public String phone;

    @JsonProperty("c")
    public String comment;

    @JsonProperty("a")
    public En_ContactDataAccess access;

    public En_PhoneType type;

    public ContactPhone() {
        this.access = En_ContactDataAccess.PUBLIC;
    }

    public ContactPhone(String phone, String comment) {
        this.phone = phone;
        this.comment = comment;
        this.access = En_ContactDataAccess.PUBLIC;
        this.type = En_PhoneType.GENERAL;
    }

    public ContactPhone(String phone, String comment, En_PhoneType type) {
        this.phone = phone;
        this.comment = comment;
        this.type = type;
    }

    public ContactPhone(En_PhoneType type, En_ContactDataAccess access) {
        this.access = access;
        this.type = type;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public En_PhoneType getType() {
        return type;
    }

    public void setType(En_PhoneType type) {
        this.type = type;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public En_ContactDataAccess getAccess() {
        return access;
    }

    public void setAccess(En_ContactDataAccess access) {
        this.access = access;
    }

    public boolean isPrivateItem () {
        return this.access == En_ContactDataAccess.PRIVATE;
    }

    public boolean isPublicItem () {
        return this.access == En_ContactDataAccess.PUBLIC;
    }
}
