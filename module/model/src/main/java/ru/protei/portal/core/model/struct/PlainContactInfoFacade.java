package ru.protei.portal.core.model.struct;

import ru.protei.portal.core.model.dict.En_ContactDataAccess;
import ru.protei.portal.core.model.dict.En_ContactItemType;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ru.protei.portal.core.model.helper.PhoneUtils.prettyPrintPhoneNumber;
import static ru.protei.portal.core.model.helper.PhoneUtils.prettyPrintWorkPhoneNumber;

public class PlainContactInfoFacade extends CustomContactInfoFacade {

    public PlainContactInfoFacade() {
        this (new ContactInfo());
    }

    public PlainContactInfoFacade(ContactInfo info) {
        super(info);
    }

    public Stream<ContactItem> allPhonesStream () {
        return this.contactInfo.getItems().stream().filter(
                item -> (item.isItemOf(En_ContactItemType.GENERAL_PHONE)
                        || item.isItemOf(En_ContactItemType.MOBILE_PHONE))
                        && !item.isEmptyValue()
        );
    }

    public Stream<ContactItem> emailsStream () {
        return this.contactInfo.getItems(En_ContactItemType.EMAIL).stream().filter(item -> !item.isEmptyValue());
    }

    public Stream<ContactItem> publicEmailsStream () {
        return this.contactInfo.getItems(En_ContactItemType.EMAIL).stream().filter(item -> !item.isEmptyValue() && item.accessType().equals(En_ContactDataAccess.PUBLIC));
    }

    public Stream<ContactItem> notInternalEmailsStream () {
        return this.contactInfo.getItems(En_ContactItemType.EMAIL).stream().filter(item -> !item.isEmptyValue() && !item.accessType().equals(En_ContactDataAccess.INTERNAL));
    }

    public String allPhonesAsString () {
        return allPhonesAsString(false);
    }

    public String allPhonesAsString (boolean isPrettyPrintPhoneNumber) {
        return allPhonesStream()
                .map(p -> {
                    En_ContactItemType type = p.type();
                    En_ContactDataAccess accessType = p.accessType();
                    if (isPrettyPrintPhoneNumber) {
                        if (type == En_ContactItemType.GENERAL_PHONE && accessType == En_ContactDataAccess.PUBLIC) {
                            return prettyPrintWorkPhoneNumber(p.value());
                        } else {
                            return prettyPrintPhoneNumber(p.value());
                        }
                    }
                    return p.value();
                })
                .collect( Collectors.joining( ", " ) );
    }

    public String publicPhonesAsString () {
        return publicPhonesAsFormattedString(false);
    }

    public String publicPhonesAsFormattedString (boolean isPrettyPrintPhoneNumber) {
        return allPhonesStream()
                .filter(ci -> ci.accessType().equals(En_ContactDataAccess.PUBLIC))
                .map(p -> {
                    En_ContactItemType type = p.type();
                    if (isPrettyPrintPhoneNumber) {
                        if (type == En_ContactItemType.GENERAL_PHONE) {
                            return prettyPrintWorkPhoneNumber(p.value());
                        } else {
                            return prettyPrintPhoneNumber(p.value());
                        }
                    }
                    return p.value();
                })
                .collect( Collectors.joining( ", " ) );
    }

    public String allEmailsAsString () {
        return emailsStream()
                .map(ContactItem::value)
                .collect(Collectors.joining(", "));
    }

    public String publicEmailsAsString () {
        return emailsStream()
                .filter(ci -> En_ContactDataAccess.PUBLIC.equals(ci.accessType()))
                .map(ContactItem::value)
                .collect(Collectors.joining(", "));
    }

    public String allNotInternalEmailsAsString () {
        return emailsStream()
                .filter(ci -> !En_ContactDataAccess.INTERNAL.equals(ci.accessType()))
                .map(ContactItem::value)
                .collect(Collectors.joining(", "));
    }

    public List<String> publicEmails () {
        return emailsStream()
                .filter(ci -> En_ContactDataAccess.PUBLIC.equals(ci.accessType()))
                .map(ContactItem::value)
                .collect(Collectors.toList());
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
        return findItemValue(En_ContactItemType.MOBILE_PHONE, En_ContactDataAccess.PUBLIC);
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

    public String getInternalEmail() {
        return findItemValue(En_ContactItemType.EMAIL, En_ContactDataAccess.INTERNAL);
    }

    public void setInternalEmail(String email) {
        contactInfo.findOrCreate(En_ContactItemType.EMAIL, En_ContactDataAccess.INTERNAL).modify(email);
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

    public String getWebSite () {
        return findItemValue(En_ContactItemType.WEB_SITE, En_ContactDataAccess.PUBLIC);
    }

    public void setWebSite (String webSite) {
        contactInfo.findOrCreate(En_ContactItemType.WEB_SITE, En_ContactDataAccess.PUBLIC).modify(webSite);
    }

    public String getLegalAddress () {
        return findItemValue(En_ContactItemType.ADDRESS_LEGAL, En_ContactDataAccess.PUBLIC);
    }

    public void setLegalAddress (String address) {
        contactInfo.findOrCreate(En_ContactItemType.ADDRESS_LEGAL, En_ContactDataAccess.PUBLIC).modify(address);
    }

    public String getFactAddress () {
        return findItemValue(En_ContactItemType.ADDRESS, En_ContactDataAccess.PUBLIC);
    }

    public void setFactAddress (String address) {
        contactInfo.findOrCreate(En_ContactItemType.ADDRESS, En_ContactDataAccess.PUBLIC).modify(address);
    }

    public String getHomeAddress () {
        return findItemValue(En_ContactItemType.ADDRESS, En_ContactDataAccess.PRIVATE);
    }

    public void setHomeAddress (String address) {
        contactInfo.findOrCreate(En_ContactItemType.ADDRESS, En_ContactDataAccess.PRIVATE).modify(address);
    }
}
