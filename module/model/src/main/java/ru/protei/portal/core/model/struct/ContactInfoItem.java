package ru.protei.portal.core.model.struct;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by michael on 07.11.16.
 */
@JsonAutoDetect
public class ContactInfoItem {

    @JsonProperty("v")
    public String value;

    @JsonProperty("c")
    public String comment;

    @JsonProperty("a")
    public En_ContactInfoAccess access;


    public ContactInfoItem () {
        this.access = En_ContactInfoAccess.PUBLIC;
    }

    public ContactInfoItem(String value, String comment) {
        this.value = value;
        this.comment = comment;
        this.access = En_ContactInfoAccess.PUBLIC;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public En_ContactInfoAccess getAccess() {
        return access;
    }

    public void setAccess(En_ContactInfoAccess access) {
        this.access = access;
    }

    public boolean isPrivateItem () {
        return this.access == En_ContactInfoAccess.PRIVATE;
    }

    public boolean isPublicItem () {
        return this.access == En_ContactInfoAccess.PUBLIC;
    }
}
