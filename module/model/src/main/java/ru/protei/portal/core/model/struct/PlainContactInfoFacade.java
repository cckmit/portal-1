package ru.protei.portal.core.model.struct;

import ru.protei.portal.core.model.dict.En_ContactDataAccess;
import ru.protei.portal.core.model.dict.En_PhoneType;

/**
 * Created by Mike on 09.11.2016.
 */
public class PlainContactInfoFacade extends CustomContactInfoFacade {

    public PlainContactInfoFacade() {
    }

    public PlainContactInfoFacade(ContactInfo info) {
        super(info);
    }


    public String getWorkPhone() {
        return findPhoneValue(En_PhoneType.GENERAL, En_ContactDataAccess.PUBLIC);
    }

    public void setWorkPhone(String workPhone) {
        findOrCreatePhone(En_PhoneType.GENERAL, En_ContactDataAccess.PUBLIC).setPhone(workPhone);
    }

    public String getHomePhone() {
        return findPhoneValue(En_PhoneType.GENERAL, En_ContactDataAccess.PRIVATE);
    }

    public void setHomePhone(String homePhone) {
        findOrCreatePhone(En_PhoneType.GENERAL, En_ContactDataAccess.PRIVATE).setPhone(homePhone);
    }

    public String getMobilePhone() {
        return findPhoneValue(En_PhoneType.MOBILE, En_ContactDataAccess.PUBLIC);
    }

    public void setMobilePhone(String mobilePhone) {
        findOrCreatePhone(En_PhoneType.MOBILE, En_ContactDataAccess.PUBLIC).setPhone(mobilePhone);
    }

    public String getEmail() {
        return findEmailValue(En_ContactDataAccess.PUBLIC);
    }

    public void setEmail(String email) {
        findOrCreateEmail(En_ContactDataAccess.PUBLIC).setEmail(email);
    }

    public String getEmail_own() {
        return findEmailValue(En_ContactDataAccess.PRIVATE);
    }

    public void setEmail_own(String email_own) {
        findOrCreateEmail(En_ContactDataAccess.PRIVATE).setEmail(email_own);
    }

    public String getFax() {
        return findPhoneValue(En_PhoneType.FAX, En_ContactDataAccess.PUBLIC);
    }

    public void setFax(String fax) {
        findOrCreatePhone(En_PhoneType.FAX, En_ContactDataAccess.PUBLIC).setPhone(fax);
    }

    public String getFaxHome() {
        return findPhoneValue(En_PhoneType.FAX, En_ContactDataAccess.PRIVATE);
    }

    public void setFaxHome(String faxHome) {
        findOrCreatePhone(En_PhoneType.FAX, En_ContactDataAccess.PRIVATE).setPhone(faxHome);
    }

    public String getIcq() {
        return editItem().icq;
    }

    public void setIcq(String icq) {
        editItem().icq = icq;
    }

    public String getJabber() {
        return editItem().jabber;
    }

    public void setJabber(String jabber) {
        editItem().jabber = jabber;
    }


    public String getWebSite () {
        return editItem().webSite;
    }

    public void setWebSite (String webSite) {
        editItem().webSite = webSite;
    }
}
