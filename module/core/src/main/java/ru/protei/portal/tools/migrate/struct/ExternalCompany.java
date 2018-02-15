package ru.protei.portal.tools.migrate.struct;

import protei.sql.Column;
import protei.sql.PrimaryKey;
import protei.sql.Table;

import java.util.Date;

@Table(name="\"Resource\".Tm_Company")
public class ExternalCompany {

    @PrimaryKey
    @Column(name = "nID")
    private Long id;

    @Column(name = "dtCreation")
    private Date created;

    @Column(name = "strCreator")
    private String creator;

    @Column(name = "strClient")
    private String client;

    @Column(name = "strClientIP")
    private String clientIp;

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

    @Column(name = "ext_id")
    private long externalId;


    public ExternalCompany() {
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


    public long getExternalId() {
        return externalId;
    }

    public void setExternalId(long externalId) {
        this.externalId = externalId;
    }
}
