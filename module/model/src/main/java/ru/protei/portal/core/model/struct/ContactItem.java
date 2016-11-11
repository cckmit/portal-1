package ru.protei.portal.core.model.struct;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.protei.portal.core.model.dict.En_ContactDataAccess;
import ru.protei.portal.core.model.dict.En_ContactItemType;

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
    public En_ContactDataAccess accessType;

    @JsonProperty("t")
    public final En_ContactItemType itemType;

    public ContactItem () {
        this.accessType = En_ContactDataAccess.PUBLIC;
        this.itemType = En_ContactItemType.UNDEFINED;
    }

    public ContactItem (En_ContactItemType type) {
        this (type, En_ContactDataAccess.PUBLIC);
    }

    public ContactItem (En_ContactItemType type, En_ContactDataAccess access) {
        this.accessType = access;
        this.itemType = type;
    }


    /*
     *
     *   readers
     *
     */

    public String value () {
        return this.value;
    }

    public En_ContactItemType type() {
        return itemType;
    }

    public String comment() {
        return comment;
    }

    public En_ContactDataAccess accessType () {
        return this.accessType;
    }


    /*
     *
     *   writers
     *
     */

    public ContactItem modify (String value) {
        this.value = value;
        return this;
    }

    public ContactItem modify (String value, String comment) {
        this.value = value;
        this.comment = comment;
        return this;
    }

    public ContactItem modify (En_ContactDataAccess accessType) {
        this.accessType = accessType;
        return this;
    }

    public ContactItem toggleAccessType () {
        this.accessType = (this.accessType == En_ContactDataAccess.PRIVATE ? En_ContactDataAccess.PUBLIC : En_ContactDataAccess.PRIVATE);
        return this;
    }


    /*
     *
     *   predicates
     *
     */

    @JsonIgnore
    public boolean isItemOf (En_ContactItemType type) {
        return this.itemType == type;
    }

    @JsonIgnore
    public boolean isEmptyValue () {
        return this.value == null || this.value.trim().isEmpty();
    }

    @JsonIgnore
    public boolean isEmptyComment () {
        return this.comment == null || this.comment.trim().isEmpty();
    }

    @JsonIgnore
    public boolean isEmpty () {
        return isEmptyValue() && isEmptyComment();
    }

    @JsonIgnore
    public boolean isPrivateItem () {
        return this.accessType == En_ContactDataAccess.PRIVATE;
    }

    @JsonIgnore
    public boolean isPublicItem () {
        return this.accessType == En_ContactDataAccess.PUBLIC;
    }
}
