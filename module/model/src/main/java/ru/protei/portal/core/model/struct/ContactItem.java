package ru.protei.portal.core.model.struct;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.protei.portal.core.model.dict.En_ContactDataAccess;
import ru.protei.portal.core.model.dict.En_ContactItemType;
import ru.protei.portal.core.model.dict.En_PhoneType;

import java.io.Serializable;

/**
 * Created by michael on 07.11.16.
 */
@JsonAutoDetect
public class ContactItem implements Serializable {

    @JsonProperty("v")
    public String value;

    @JsonProperty("c")
    public String comment;

    @JsonProperty("a")
    public En_ContactDataAccess access;

    @JsonProperty("t")
    public En_ContactItemType type;

    public ContactItem () {
        this.access = En_ContactDataAccess.PUBLIC;
        this.type = En_ContactItemType.TEXT;
    }

    public ContactItem (En_ContactItemType type, String value) {
        this (type, En_ContactDataAccess.PUBLIC, value);
    }

    public ContactItem (En_ContactItemType type, En_ContactDataAccess access, String value) {
        this.access = access;
        this.type = type;
        this.value = value;
    }

    @JsonIgnore
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @JsonIgnore
    public En_ContactItemType getType() {
        return type;
    }

    public void setType(En_ContactItemType type) {
        this.type = type;
    }

    @JsonIgnore
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @JsonIgnore
    public En_ContactDataAccess getAccess() {
        return access;
    }

    public void setAccess(En_ContactDataAccess access) {
        this.access = access;
    }

    @JsonIgnore
    public boolean isPrivateItem () {
        return this.access == En_ContactDataAccess.PRIVATE;
    }

    @JsonIgnore
    public boolean isPublicItem () {
        return this.access == En_ContactDataAccess.PUBLIC;
    }
}
