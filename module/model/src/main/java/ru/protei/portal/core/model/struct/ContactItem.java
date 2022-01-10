package ru.protei.portal.core.model.struct;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.protei.portal.core.model.dict.En_ContactDataAccess;
import ru.protei.portal.core.model.dict.En_ContactEmailSubscriptionType;
import ru.protei.portal.core.model.dict.En_ContactItemType;
import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;

@JsonAutoDetect
@JdbcEntity(table = "contact_item")
public class ContactItem implements Serializable {

    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JsonProperty("t")
    @JdbcColumn(name = "item_type")
    @JdbcEnumerated(EnumType.ID)
    private En_ContactItemType itemType;

    @JsonProperty("a")
    @JdbcColumn(name = "access_type")
    @JdbcEnumerated(EnumType.ID)
    private En_ContactDataAccess accessType;

    @JsonProperty("v")
    @JdbcColumn(name = "value")
    private String value;

    @JsonProperty("s")
    @JdbcColumn(name = "subscription_type")
    @JdbcEnumerated(EnumType.ID)
    private En_ContactEmailSubscriptionType subscriptionType;

    @Deprecated
    @JsonProperty("c")
    // not db column (legacy field from json)
    private String comment;

    public ContactItem() {}

    public ContactItem (String value, En_ContactItemType type) {
        this.value = value;
        this.itemType = type;
    }

    public ContactItem (En_ContactItemType type) {
        this (type, En_ContactDataAccess.PUBLIC);
    }

    public ContactItem (En_ContactItemType type, En_ContactDataAccess access) {
        this.accessType = access;
        this.itemType = type;
    }

    public ContactItem (String value, En_ContactItemType type, En_ContactDataAccess access) {
        this(type, access);
        this.value = value;
    }

    public ContactItem (String value, En_ContactItemType type, En_ContactDataAccess access, En_ContactEmailSubscriptionType subscriptionType) {
        this(value, type, access);
        this.subscriptionType = subscriptionType;
    }

    public Long id() {
        return this.id;
    }

    public String value () {
        return this.value;
    }

    public En_ContactItemType type() {
        return itemType;
    }

    public En_ContactDataAccess accessType () {
        return this.accessType;
    }

    public En_ContactEmailSubscriptionType subscriptionType() {
        return this.subscriptionType;
    }

    public ContactItem modify ( String value) {
        this.value = value;
        return this;
    }

    public ContactItem modify (En_ContactItemType type) {
        this.itemType = type;
        return this;
    }

    public ContactItem modify (En_ContactDataAccess accessType) {
        this.accessType = accessType;
        return this;
    }

    public ContactItem modify (En_ContactEmailSubscriptionType subscriptionType) {
        this.subscriptionType = subscriptionType;
        return this;
    }


    @JsonIgnore
    public boolean isItemOf (En_ContactItemType type) {
        return this.itemType == type;
    }

    @JsonIgnore
    public boolean isEmptyValue () {
        return this.value == null || this.value.trim().isEmpty();
    }

    @JsonIgnore
    public boolean isEmpty () {
        return isEmptyValue();
    }

    @JsonIgnore
    public boolean isPrivateItem () {
        return this.accessType == En_ContactDataAccess.PRIVATE;
    }

    @JsonIgnore
    public boolean isPublicItem () {
        return this.accessType == En_ContactDataAccess.PUBLIC;
    }

    @JsonIgnore
    public boolean isInternalItem () {
        return this.accessType == En_ContactDataAccess.INTERNAL;

    }

    @JsonIgnore
    public boolean isSubscribedToTheEndOfProbation () {
        return this.subscriptionType == En_ContactEmailSubscriptionType.SUBSCRIPTION_TO_END_OF_PROBATION;
    }

    @JsonIgnore
    public boolean isSubscribedToTheEmployeeRegistration () {
        return this.subscriptionType == En_ContactEmailSubscriptionType.SUBSCRIPTION_TO_EMPLOYEE_REGISTRATION;
    }


}
