package ru.protei.portal.tools.migrate.struct;

import protei.sql.Column;
import protei.sql.PrimaryKey;
import protei.sql.Table;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.LegacyEntity;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.struct.PlainContactInfoFacade;
import ru.protei.portal.tools.migrate.Const;

import java.util.Date;

@Table(name="\"Resource\".Tm_Company")
public class ExternalCompany implements LegacyEntity {

    @PrimaryKey
    @Column(name = "nID")
    private Long id;

    @Column(name = "dtCreation")
    private Date created;

    @Column(name = "strCreator")
    private String creator = Const.CREATOR_FIELD_VALUE;

    @Column(name = "strClient")
    private String client = Const.CLIENT_FIELD_VALUE;

    @Column(name = "strClientIP")
    private String clientIp = Const.CREATOR_HOST_VALUE;

    @Column(name = "strName")
    private String name;

    @Column(name = "strInfo")
    private String info;

    @Column(name = "strDeJureAddress")
    private String legalAddress;

    @Column(name = "strPhysicalAddress")
    private String address;

    @Column(name = "strE_Mail")
    private String email;

    @Column(name = "strHTTP_URL")
    private String website;

    @Column(name = "dtLastUpdate")
    private Date lastUpdated;


    public ExternalCompany() {
    }

    public ExternalCompany (Company company) {
        this.id = company.getOldId();
        this.created = company.getCreated();
        this.contactDataFrom(company);
    }

    public ExternalCompany contactDataFrom(Company company) {
        this.name = company.getCname();
        this.info = company.getInfo();
        PlainContactInfoFacade contactInfo = new PlainContactInfoFacade(company.getContactInfo());
        this.legalAddress = contactInfo.getLegalAddress();
        this.address = HelperFunc.nvlt(contactInfo.getFactAddress(), contactInfo.getHomeAddress());
        this.email = contactInfo.allEmailsAsString();
        this.website = contactInfo.getWebSite();
        this.created = company.getCreated();
        return this;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getLegalAddress() {
        return legalAddress;
    }

    public void setLegalAddress(String legalAddress) {
        this.legalAddress = legalAddress;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    @Override
    public String toString() {
        return new StringBuilder("company{")
                .append(getId())
                .append("/")
                .append(getName())
                .append("}")
                .toString();
    }
}
