package ru.protei.portal.core.model.struct;

import ru.protei.portal.core.model.dict.En_ContactItemType;

/**
 * Created by Mike on 11.11.2016.
 */
public class ContactItemFactory {

    public static ContactItem mobilePhone (String phone) {
        return new ContactItem (En_ContactItemType.MOBILE_PHONE).modify(phone);
    }

    public static ContactItem phone (String phone) {
        return new ContactItem(En_ContactItemType.GENERAL_PHONE).modify(phone);
    }

    public static ContactItem email (String email) {
        return new ContactItem(En_ContactItemType.EMAIL).modify(email);
    }

}
