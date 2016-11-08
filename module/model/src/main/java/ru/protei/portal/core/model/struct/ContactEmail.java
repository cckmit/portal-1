package ru.protei.portal.core.model.struct;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.protei.portal.core.model.dict.En_ContactDataAccess;

import java.beans.Transient;

/**
 * Created by michael on 07.11.16.
 */
@JsonAutoDetect
public class ContactEmail {

    @JsonProperty("e")
    public String email;

    @JsonProperty("c")
    public String comment;

    @JsonProperty("a")
    public En_ContactDataAccess access;

    public ContactEmail() {
        this.access = En_ContactDataAccess.PUBLIC;
    }

    public ContactEmail(En_ContactDataAccess access) {
        this.access = access;
    }

    public ContactEmail(String email, String comment) {
        this.email = email;
        this.comment = comment;
        this.access = En_ContactDataAccess.PUBLIC;
    }

    @JsonIgnore
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
