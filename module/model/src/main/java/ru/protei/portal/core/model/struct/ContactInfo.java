package ru.protei.portal.core.model.struct;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.protei.portal.core.model.dict.En_ContactDataAccess;
import ru.protei.portal.core.model.dict.En_PhoneType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.HelperFunc.nvlt;

/**
 * Created by michael on 07.11.16.
 */
@JsonAutoDetect
public class ContactInfo implements Serializable {

    @JsonProperty("emails")
    public List<ContactEmail> emailList;

    @JsonProperty("phones")
    public List<ContactPhone> phoneList;

    @JsonProperty("scn")
    public List<SocialNetworkLink> socialNetworkLinks;

    @JsonProperty("icq")
    public String icq;

    @JsonProperty("jabber")
    public String jabber;

    @JsonProperty("web")
    public String webSite;


    public ContactInfo () {
        emailList = new ArrayList<>();
        socialNetworkLinks = new ArrayList<>();
        phoneList = new ArrayList<>();
    }

    public ContactInfo (ContactInfo src) {
        this.emailList = new ArrayList<>(nvlt(src.emailList, Collections.emptyList()));
        this.phoneList = new ArrayList<>(nvlt(src.phoneList,Collections.emptyList()));
        this.socialNetworkLinks = new ArrayList<>(nvlt(src.socialNetworkLinks,Collections.emptyList()));
        this.icq = src.icq;
        this.jabber = src.jabber;
        this.webSite = src.webSite;
    }

    @JsonIgnore
    public List<ContactEmail> getEmailList() {
        return emailList;
    }

    @JsonIgnore
    public List<ContactPhone> getPhoneList() {
        return phoneList;
    }

    @JsonIgnore
    public List<SocialNetworkLink> getSocialNetworkLinks() {
        return socialNetworkLinks;
    }

    @JsonIgnore
    public String getIcq() {
        return icq;
    }

    public void setIcq(String icq) {
        this.icq = icq;
    }

    @JsonIgnore
    public String getJabber() {
        return jabber;
    }

    public void setJabber(String jabber) {
        this.jabber = jabber;
    }

    @JsonIgnore
    public String getWebSite() {
        return webSite;
    }

    public void setWebSite(String webSite) {
        this.webSite = webSite;
    }

    public ContactInfo addPhone (String phone, String comment, En_PhoneType type) {
        if (phone != null)
            this.phoneList.add(new ContactPhone(phone, comment, type));

        return this;
    }

    public ContactInfo addPhone (String phone, String comment) {
        return addPhone(phone,comment,En_PhoneType.GENERAL);
    }

    public ContactInfo addMobilePhone (String phone, String comment) {
        return addPhone(phone, comment, En_PhoneType.MOBILE);
    }

    public ContactInfo addFax (String fax, String comment) {
        return addPhone(fax,comment,En_PhoneType.FAX);
    }

    public ContactInfo addEmail (String email, String comment) {
        if (email != null)
            this.emailList.add(new ContactEmail(email, comment));
        return this;
    }

    @JsonIgnore
    public List<ContactPhone> getFaxList () {
        return filterPhoneList(contactPhone -> contactPhone.type == En_PhoneType.FAX);
    }

    @JsonIgnore
    public List<ContactPhone> getMobilePhoneList () {
        return filterPhoneList(contactPhone -> contactPhone.type == En_PhoneType.MOBILE);
    }

    @JsonIgnore
    public List<ContactPhone> getGeneralPhoneList () {
        return filterPhoneList(contactPhone -> contactPhone.type == En_PhoneType.GENERAL);
    }


    public String defaultWorkPhone() {
        return findPhoneValue(contactPhone -> contactPhone.type == En_PhoneType.GENERAL);
    }

    public String defaultMobilePhone() {
        return findPhoneValue(contactPhone -> contactPhone.type == En_PhoneType.MOBILE);
    }

    public String defaultFax() {
        return findPhoneValue(contactPhone -> contactPhone.type == En_PhoneType.FAX);
    }

    public String privatePhone() {
        return findPhoneValue(ContactPhone::isPrivateItem);
    }

    public String privateEmail () {
        ContactEmail email = findEmail(En_ContactDataAccess.PRIVATE);
        return email != null ? email.getEmail() : null;
    }

    public String defaultEmail() {
        ContactEmail email = findEmail(En_ContactDataAccess.PUBLIC);
        return email != null ? email.getEmail() : null;
    }

    public void updateDefaultEmail (String email) {
        ContactEmail e = findOrCreateEmail(En_ContactDataAccess.PUBLIC);
        e.setEmail(email);
    }

    public void updateDefaultPhone (En_PhoneType type, String number) {
        ContactPhone item = findOrCreatePhone(type);
        item.setPhone(number);
    }

    public void updateDefaultPhone (En_PhoneType type, String number, String comment) {
        ContactPhone item = findOrCreatePhone(type);
        item.setPhone(number);
        item.setComment(comment);
    }

    public void updatePrivatePhone (En_PhoneType type, String number) {
        ContactPhone item = findOrCreatePhone(p -> p.type==type && p.isPrivateItem(), ()->new ContactPhone(type,En_ContactDataAccess.PRIVATE));
        item.setPhone(number);
    }



    /*
        UTILITY PART
     */

    public ContactPhone findOrCreatePhone (En_PhoneType type) {
        return findOrCreatePhone (phone -> phone.type == type, () -> new ContactPhone("", "", type));
    }

    public ContactPhone findOrCreatePhone (Predicate<ContactPhone> predicate, Supplier<ContactPhone> factory) {
        ContactPhone phone = findPhone(predicate);
        if (phone == null) {
            phoneList.add(phone = factory.get());
        }
        return phone;
    }

    public List<ContactPhone> filterPhoneList (Predicate<ContactPhone> predicate) {
        return phoneList == null ? new ArrayList<>() : phoneList.stream().filter(predicate).collect(Collectors.toList());
    }

    public String findPhoneValue (Predicate<ContactPhone> predicate) {
        return findPhoneValue(predicate, null);
    }

    public String findPhoneValue (Predicate<ContactPhone> predicate, String defValue) {
        ContactPhone item = findPhone(predicate);
        return  item != null ? item.getPhone() : defValue;
    }

    public ContactPhone findPhone (Predicate<ContactPhone> predicate) {
        return phoneList != null && !phoneList.isEmpty() ? phoneList.stream().filter(predicate).findFirst().orElse(null) : null;
    }



    public ContactEmail findEmail (En_ContactDataAccess accessType) {
        return emailList != null && !emailList.isEmpty() ?
                emailList.stream().filter(item -> item.access == accessType).findFirst().orElse(null) : null;
    }

    public ContactEmail findOrCreateEmail (En_ContactDataAccess accessType) {
        ContactEmail email = findEmail (accessType);
        if (email == null) {
            emailList.add(email = new ContactEmail(accessType));
        }
        return email;
    }
}
