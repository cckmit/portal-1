package ru.protei.portal.core.model.dict;

import java.io.Serializable;

/**
 * Created by michael on 08.11.16.
 */
public enum En_ContactItemType implements Serializable {
    /**
     * используется для заполнения в default-конструкторе, в UI не используется
     */
    UNDEFINED,

    EMAIL,
    /**
     * адрес
     */
    ADDRESS,
    /**
     * De-Jure == LEGAL
     */
    ADDRESS_LEGAL,

    FAX,
    MOBILE_PHONE,
    GENERAL_PHONE,

    ICQ,
    JABBER,
    SKYPE,

    WEB_SITE,

    SOCIAL_NET;

    private String name;

    En_ContactItemType() {

    }

    public String getName(){
        return name == null? name(): name;
    }

    public void setName(String name){
        this.name = name;
    }
}
