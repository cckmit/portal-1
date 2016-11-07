package ru.protei.portal.core.model.struct;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.protei.portal.core.utils.HelperFunc.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import static ru.protei.portal.core.utils.HelperFunc.nvl;
import static ru.protei.portal.core.utils.HelperFunc.nvlt;
import static ru.protei.portal.core.utils.HelperFunc.toTime;

/**
 * Created by michael on 07.11.16.
 */
@JsonAutoDetect
public class ContactInfo implements Serializable {

    @JsonProperty("emails")
    public List<ContactInfoItem> emailList;

    @JsonProperty("faxes")
    public List<ContactInfoItem> faxList;

    @JsonProperty("mobile-phones")
    public List<ContactInfoItem> mobilePhoneList;

    @JsonProperty("phones")
    public List<ContactInfoItem> generalPhoneList;

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
        faxList = new ArrayList<>();
        mobilePhoneList = new ArrayList<>();
        generalPhoneList = new ArrayList<>();
        socialNetworkLinks = new ArrayList<>();
    }

    public ContactInfo (ContactInfo src) {
        this.emailList = new ArrayList<>(nvlt(src.emailList, Collections.emptyList()));
        this.faxList = new ArrayList<>(nvlt(src.faxList, Collections.emptyList()));
        this.mobilePhoneList = new ArrayList<>(nvlt(src.mobilePhoneList,Collections.emptyList()));
        this.generalPhoneList = new ArrayList<>(nvlt(src.generalPhoneList,Collections.emptyList()));
        this.socialNetworkLinks = new ArrayList<>(nvlt(src.socialNetworkLinks,Collections.emptyList()));
        this.icq = src.icq;
        this.jabber = src.jabber;
        this.webSite = src.webSite;
    }


    public ContactInfo addPhone (String phone, String comment) {
        if (phone != null)
            this.generalPhoneList.add(new ContactInfoItem(phone, comment));
        return this;
    }

    public ContactInfo addMobilePhone (String phone, String comment) {
        if (phone != null)
            this.mobilePhoneList.add(new ContactInfoItem(phone, comment));
        return this;
    }

    public ContactInfo addEmail (String email, String comment) {
        if (email != null)
            this.emailList.add(new ContactInfoItem(email, comment));
        return this;
    }

    public ContactInfo addFax (String fax, String comment) {
        if (fax != null)
            this.emailList.add(new ContactInfoItem(fax, comment));
        return this;
    }

    public static String getFirstContactItemValue (List<ContactInfoItem> list) {
        return list != null && list.size() > 0 ? list.get(0).getValue() : null;
    }

    public static String getFirstContactItemValue (List<ContactInfoItem> list, Predicate<ContactInfoItem> predicate) {
         ContactInfoItem item = list != null && list.size() > 0 ? list.stream().filter(predicate).findFirst().get() : null;
         return  item != null ? item.getValue() : null;
    }


    public String defaultWorkPhone() {
        return getFirstContactItemValue(this.generalPhoneList);
    }

    public String defaultMobilePhone() {
        return getFirstContactItemValue(this.mobilePhoneList);
    }

    public String defaultEmail() {
        return getFirstContactItemValue(this.emailList);
    }

    public String defaultFax() {
        return getFirstContactItemValue(this.faxList);
    }

    public String privatePhone() {
        String v = getFirstContactItemValue(this.generalPhoneList, ContactInfoItem::isPrivateItem);
        return v != null ? v : getFirstContactItemValue(this.mobilePhoneList, ContactInfoItem::isPrivateItem);
    }
}
