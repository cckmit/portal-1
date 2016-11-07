package ru.protei.portal.core.model.struct;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by michael on 07.11.16.
 */
@JsonAutoDetect
public class ContactInfo {

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


}
