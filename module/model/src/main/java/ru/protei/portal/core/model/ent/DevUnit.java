package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.dict.En_DevUnitState;
import ru.protei.portal.core.model.dict.En_DevUnitType;
import ru.protei.portal.core.model.struct.AuditableObject;
import ru.protei.portal.core.model.struct.ProductDirectionInfo;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.core.model.view.ProductShortViewSupport;
import ru.protei.winter.jdbc.annotations.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Created by michael on 23.05.16.
 */
@JdbcEntity(table = "dev_unit")
public class DevUnit extends AuditableObject implements ProductShortViewSupport {

    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name="UTYPE_ID")
    private int typeId;

    @JdbcColumn(name="CREATED")
    private Date created;

    @JdbcColumn(name="UNIT_NAME")
    private String name;

    @JdbcColumn(name="UNIT_INFO")
    private String info;

    @JdbcColumn(name="LAST_UPDATE")
    private Date lastUpdate;

    @JdbcColumn(name="CREATOR_ID")
    private Long creatorId;

    @JdbcColumn(name="UNIT_STATE")
    private int stateId;

    @JdbcColumn(name = "old_id")
    private Long oldId;

    @JdbcOneToMany(table = "DevUnitSubscription", localColumn = "id", remoteColumn = "dev_unit_id" )
    private List<DevUnitSubscription> subscriptions;

    @JdbcManyToMany(localLinkColumn = "CHILD_ID", linkTable = "dev_unit_children", remoteLinkColumn = "DUNIT_ID")
    private List<DevUnit> parents;

    @JdbcManyToMany(localLinkColumn = "DUNIT_ID", linkTable = "dev_unit_children", remoteLinkColumn = "CHILD_ID")
    private List<DevUnit> children;


    public static DevUnit fromProductShortView(ProductShortView productShortView){
        if(productShortView == null)
            return null;

        DevUnit product = new DevUnit();
        product.setId(productShortView.getId());
        product.setName(productShortView.getName());
        product.setStateId(productShortView.getStateId());
        return product;
    }

    public static DevUnit fromEntityOption(EntityOption entityOption){
        if(entityOption == null)
            return null;

        DevUnit product = new DevUnit(entityOption.getId());
        product.setName(entityOption.getDisplayText());
        return product;
    }


    public DevUnit () {}

    public DevUnit (Long id) {
        this.id = id;
    }

    public DevUnit (En_DevUnitType type, String name, String info) {
        this (type.getId(), name, info);
    }

    public DevUnit(int typeId, String name, String info) {
        this.typeId = typeId;
        this.name = name;
        this.info = info;
        this.created = new Date();
        this.stateId = En_DevUnitState.ACTIVE.getId();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
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

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public int getStateId() {
        return stateId;
    }

    public void setStateId(int stateId) {
        this.stateId = stateId;
    }

    public Long getOldId() {
        return oldId;
    }

    public void setOldId(Long oldId) {
        this.oldId = oldId;
    }

    public En_DevUnitState getState () {
        return En_DevUnitState.forId(this.stateId);
    }

    public boolean isActiveUnit () {
        return getState() == En_DevUnitState.ACTIVE;
    }

    public En_DevUnitType getType () {
        return En_DevUnitType.forId(this.typeId);
    }

    public DevUnit getParent() {
        return parents == null || parents.size() == 0 ? null : parents.get(0);
    }

    public void setParent(DevUnit parent) {
        if (parents == null) {
            parents = new ArrayList<>();
        } else {
            parents.clear();
        }
        if (parent != null) {
            parents.add(parent);
        }
    }

    public List<DevUnit> getChildren() {
        return children;
    }

    public void setChildren(List<DevUnit> children) {
        if (this.children == null) {
            this.children = new ArrayList<>();
        }
        if (children == null) {
            this.children.clear();
        } else {
            this.children = children;
        }
    }

    @Override
    public String getAuditType() {
        return "DevUnit";
    }

    @Override
    public ProductShortView toProductShortView() {
        return new ProductShortView(this.id, this.name, this.stateId);
    }

    public ProductDirectionInfo toProductDirectionInfo() {
        return new ProductDirectionInfo( this.id, this.name );
    }

    public List<DevUnitSubscription> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(List<DevUnitSubscription> subscriptions) {
        this.subscriptions = subscriptions;
    }

    public boolean isProduct() {
        return En_DevUnitType.PRODUCT.equals(getType());
    }

    public boolean isComponent() {
        return En_DevUnitType.COMPONENT.equals(getType());
    }

    @Override
    public String toString() {
        return "DevUnit{" +
                "id=" + id +
                ", typeId=" + typeId +
                ", created=" + created +
                ", name='" + name + '\'' +
                ", info='" + info + '\'' +
                ", lastUpdate=" + lastUpdate +
                ", creatorId=" + creatorId +
                ", stateId=" + stateId +
                ", oldId=" + oldId +
                ", subscriptions=" + subscriptions +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DevUnit devUnit = (DevUnit) o;
        return Objects.equals(id, devUnit.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
