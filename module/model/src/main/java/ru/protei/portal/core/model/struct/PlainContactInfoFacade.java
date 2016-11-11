package ru.protei.portal.core.model.struct;

import ru.protei.portal.core.model.dict.En_ContactDataAccess;
import ru.protei.portal.core.model.dict.En_ContactItemType;

/**
 * Created by Mike on 09.11.2016.
 */
public class PlainContactInfoFacade extends CustomContactInfoFacade {

    public PlainContactInfoFacade() {
        this (new ContactInfo());
    }

    public PlainContactInfoFacade(ContactInfo info) {
        super(info);
    }


    public String getWorkPhone() {
        return findItemValue(En_ContactItemType.GENERAL_PHONE,En_ContactDataAccess.PUBLIC);
    }

    public void setWorkPhone(String workPhone) {
        contactInfo.findOrCreate(En_ContactItemType.GENERAL_PHONE, En_ContactDataAccess.PUBLIC).modify(workPhone);
    }

    public String getHomePhone() {
        return findItemValue(En_ContactItemType.GENERAL_PHONE, En_ContactDataAccess.PRIVATE);
    }

    public void setHomePhone(String homePhone) {
        contactInfo.findOrCreate(En_ContactItemType.GENERAL_PHONE, En_ContactDataAccess.PRIVATE).modify(homePhone);
    }

    public String getMobilePhone() {
        return findItemValue(En_ContactItemType.GENERAL_PHONE, En_ContactDataAccess.PUBLIC);
    }

    public void setMobilePhone(String mobilePhone) {
        contactInfo.findOrCreate(En_ContactItemType.MOBILE_PHONE, En_ContactDataAccess.PUBLIC).modify(mobilePhone);
    }

    public String getEmail() {
        return findItemValue(En_ContactItemType.EMAIL, En_ContactDataAccess.PUBLIC);
    }

    public void setEmail(String email) {
        contactInfo.findOrCreate(En_ContactItemType.EMAIL, En_ContactDataAccess.PUBLIC).modify(email);
    }

    public String getEmail_own() {
        return findItemValue(En_ContactItemType.EMAIL, En_ContactDataAccess.PRIVATE);
    }

    public void setEmail_own(String email_own) {
        contactInfo.findOrCreate(En_ContactItemType.EMAIL,En_ContactDataAccess.PRIVATE).modify(email_own);
    }

    public String getFax() {
        return findItemValue(En_ContactItemType.FAX, En_ContactDataAccess.PUBLIC);
    }

    public void setFax(String fax) {
        contactInfo.findOrCreate(En_ContactItemType.FAX, En_ContactDataAccess.PUBLIC).modify(fax);
    }

    public String getFaxHome() {
        return findItemValue(En_ContactItemType.FAX, En_ContactDataAccess.PRIVATE);
    }

    public void setFaxHome(String faxHome) {
        contactInfo.findOrCreate(En_ContactItemType.FAX, En_ContactDataAccess.PRIVATE).modify(faxHome);
    }

    public String getIcq() {
        return findItemValue(En_ContactItemType.ICQ, En_ContactDataAccess.PUBLIC);
    }

    public void setIcq(String icq) {
        contactInfo.findOrCreate(En_ContactItemType.ICQ, En_ContactDataAccess.PUBLIC).modify(icq);
    }

    public String getJabber() {
        return findItemValue(En_ContactItemType.JABBER, En_ContactDataAccess.PUBLIC);
    }

    public void setJabber(String jabber) {
        contactInfo.findOrCreate(En_ContactItemType.JABBER,En_ContactDataAccess.PUBLIC).modify(jabber);
    }

    public String getWebSite () {
        return findItemValue(En_ContactItemType.WEB_SITE, En_ContactDataAccess.PUBLIC);
    }

    public void setWebSite (String webSite) {
        contactInfo.findOrCreate(En_ContactItemType.WEB_SITE, En_ContactDataAccess.PUBLIC).modify(webSite);
    }
}
