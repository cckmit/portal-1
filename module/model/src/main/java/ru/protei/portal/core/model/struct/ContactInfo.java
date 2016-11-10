package ru.protei.portal.core.model.struct;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.protei.portal.core.model.dict.En_PhoneType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static ru.protei.portal.core.model.helper.HelperFunc.nvlt;

/**
 * Created by michael on 07.11.16.
 */
@JsonAutoDetect
public class ContactInfo implements Serializable {

    @JsonProperty("emails")
    public List<ContactItem> emailList;

    @JsonProperty("phones")
    public List<ContactItem> phoneList;

    @JsonProperty("scn")
    public List<ContactItem> socialNetworks;

    @JsonProperty("icq")
    public String icq;

    @JsonProperty("jabber")
    public String jabber;

    @JsonProperty("web")
    public String webSite;


    public ContactInfo () {
        emailList = new ArrayList<>();
        socialNetworks = new ArrayList<>();
        phoneList = new ArrayList<>();
    }

    public ContactInfo (ContactInfo src) {
        this.emailList = new ArrayList<>(nvlt(src.emailList, Collections.emptyList()));
        this.phoneList = new ArrayList<>(nvlt(src.phoneList,Collections.emptyList()));
        this.socialNetworks = new ArrayList<>(nvlt(src.socialNetworks,Collections.emptyList()));
        this.icq = src.icq;
        this.jabber = src.jabber;
        this.webSite = src.webSite;
    }

    @JsonIgnore
    public List<ContactItem> getEmailList() {
        return emailList;
    }

    @JsonIgnore
    public List<ContactItem> getPhoneList() {
        return phoneList;
    }

    @JsonIgnore
    public List<ContactItem> getSocialNetworkLinks() {
        return socialNetworks;
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


    public ContactInfo addPhone (String phone) {
       this.phoneList.add(ContactItemFactory.phone(phone));
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
}
