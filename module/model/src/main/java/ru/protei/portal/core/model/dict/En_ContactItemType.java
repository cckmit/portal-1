package ru.protei.portal.core.model.dict;

import java.io.Serializable;

/**
 * Created by michael on 08.11.16.
 */
public enum En_ContactItemType implements Serializable {
    TEXT,
    EMAIL,
    ADDRESS,
    FAX,
    MOBILE_PHONE,
    GENERAL_PHONE,
    IM,
    SOCIAL_NET;

    En_ContactItemType() {

    }
}
