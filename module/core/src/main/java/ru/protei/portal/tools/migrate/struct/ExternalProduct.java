package ru.protei.portal.tools.migrate.struct;

import protei.sql.Column;
import protei.sql.PrimaryKey;
import protei.sql.Table;

import java.util.Date;

@Table(name="\"Resource\".Tm_Product")
public class ExternalProduct {

    @PrimaryKey
    @Column(name = "nID")
    private Long id;

    @Column(name = "dtCreation")
    private Date created;

    @Column(name = "strCreator")
    private String creator;

    @Column(name = "strValue")
    private String name;

    @Column(name = "strInfo")
    private String info;

    @Column(name = "dtLastUpdate")
    private Date lastUpdate;

    @Column(name = "ext_id")
    private Long extId;

    public ExternalProduct() {
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

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Long getExtId() {
        return extId;
    }

    public void setExtId(Long extId) {
        this.extId = extId;
    }

    @Override
    public String toString() {
        return new StringBuilder("product{").append(getId()).append("/").append(getName()).append("}").toString();
    }
}
