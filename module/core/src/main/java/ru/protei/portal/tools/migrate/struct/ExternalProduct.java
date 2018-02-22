package ru.protei.portal.tools.migrate.struct;

import protei.sql.Column;
import protei.sql.PrimaryKey;
import protei.sql.Table;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.core.model.ent.LegacyEntity;
import ru.protei.portal.tools.migrate.Const;

import java.util.Date;

@Table(name="\"Resource\".Tm_Product")
public class ExternalProduct implements LegacyEntity {

    @PrimaryKey
    @Column(name = "nID")
    private Long id;

    @Column(name = "dtCreation")
    private Date created;

    @Column(name = "strCreator")
    private String creator = Const.CREATOR_FIELD_VALUE;

    @Column(name = "strValue")
    private String name;

    @Column(name = "strInfo")
    private String info;

    @Column(name = "dtLastUpdate")
    private Date lastUpdate;


    public ExternalProduct() {

    }

    public ExternalProduct (DevUnit unit) {
        this.id = unit.getLegacyId();
        this.created = unit.getCreated();
        this.name = unit.getName();
        this.info = unit.getInfo();
    }

    public ExternalProduct updateFrom (DevUnit unit) {
        this.info = unit.getInfo();
        this.name = unit.getName();
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

    @Override
    public String toString() {
        return new StringBuilder("product{").append(getId()).append("/").append(getName()).append("}").toString();
    }
}
