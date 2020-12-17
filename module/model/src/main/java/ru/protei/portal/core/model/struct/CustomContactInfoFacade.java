package ru.protei.portal.core.model.struct;

import ru.protei.portal.core.model.dict.En_ContactDataAccess;
import ru.protei.portal.core.model.dict.En_ContactItemType;

import java.util.List;

/**
 * Created by Mike on 09.11.2016.
 */
public class CustomContactInfoFacade {

    protected ContactInfo contactInfo;


    public CustomContactInfoFacade () {
    }

    public CustomContactInfoFacade (ContactInfo info) {
        this.contactInfo = info;
    }

    public ContactInfo editInfo() {
        return contactInfo == null ? (contactInfo = new ContactInfo()) : contactInfo;
    }

    public ContactInfo editInfo(ContactInfo contactInfo) {
        return (this.contactInfo = contactInfo);
    }


    public List<ContactItem> getFaxList () {
        return contactInfo.getItems(En_ContactItemType.FAX);
    }

    public List<ContactItem> getMobilePhoneList () {
        return contactInfo.getItems(En_ContactItemType.MOBILE_PHONE);
    }

    public List<ContactItem> getWorkPhoneList () {
        return contactInfo.getItems(En_ContactItemType.GENERAL_PHONE);
    }

    public List<ContactItem> getGeneralPhoneList () {
        return contactInfo.getItems(En_ContactItemType.GENERAL_PHONE);
    }

    public String findItemValue (En_ContactItemType type, En_ContactDataAccess accessType) {
        ContactItem item = contactInfo.findFirst(type, accessType);
        return  item == null ? null : item.value();
    }
}
