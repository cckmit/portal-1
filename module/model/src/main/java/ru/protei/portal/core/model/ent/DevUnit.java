package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.dict.En_DevUnitState;
import ru.protei.portal.core.model.dict.En_DevUnitType;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.struct.AuditableObject;
import ru.protei.portal.core.model.struct.ProductDirectionInfo;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.core.model.view.ProductShortViewSupport;
import ru.protei.winter.jdbc.annotations.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by michael on 23.05.16.
 */
@JdbcEntity(table = "dev_unit")
public class DevUnit extends AuditableObject implements ProductShortViewSupport {

    @JdbcId(name = Columns.ID, idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name="UTYPE_ID")
    @JdbcEnumerated( EnumType.ID )
    private En_DevUnitType devUnitType;

    @JdbcColumn(name="CREATED")
    private Date created;

    @JdbcColumn(name="UNIT_NAME")
    private String name;

    @JdbcColumn(name=Columns.UNIT_INFO)
    private String info;

    @JdbcColumn(name="LAST_UPDATE")
    private Date lastUpdate;

    @JdbcColumn(name="CREATOR_ID")
    private Long creatorId;

    @JdbcColumn(name="UNIT_STATE")
    private int stateId;

    @JdbcColumn(name = "old_id")
    private Long oldId;

    @JdbcOneToMany(table = "dev_unit_subscription", localColumn = "id", remoteColumn = "dev_unit_id" )
    private List<DevUnitSubscription> subscriptions;

    @JdbcColumn(name = "wiki_link")
    private String wikiLink;

    @JdbcColumn(name = Columns.CONFIGURATION)
    private String configuration;

    @JdbcColumn(name = Columns.CDR_DESCRIPTION)
    private String cdrDescription;

    @JdbcColumn(name = Columns.HISTORY_VERSION)
    private String historyVersion;

    private List<DevUnit> parents;

    private List<DevUnit> children;

    private DevUnit productDirection;

    /**
     * Псевдонимы для поиска
     */
    @JdbcColumnCollection(name = "aliases", separator = ",")
    private List<String> aliases;

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

    public static DevUnit fromProductDirectionInfo(ProductDirectionInfo directionInfo) {
        if (directionInfo == null) {
            return null;
        }

        DevUnit direction = new DevUnit(directionInfo.id);
        direction.setName(directionInfo.name);
        return direction;
    }

    public DevUnit () {}

    public DevUnit (Long id) {
        this.id = id;
    }

    public DevUnit (En_DevUnitType type, String name, String info) {
        this.devUnitType = type;
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
        return devUnitType.getId();
    }

    public void setType(En_DevUnitType devUnitType) {
        this.devUnitType = devUnitType;
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

    public boolean isDeprecatedUnit() {
        return getState() == En_DevUnitState.DEPRECATED;
    }

    public En_DevUnitType getType () {
        return devUnitType;
    }

    public List<DevUnit> getParents() {
        return parents;
    }

    public void setParents(List<DevUnit> parents) {
        this.parents = parents;
    }

    public List<DevUnit> getChildren() {
        return children;
    }

    public void setChildren(List<DevUnit> children) {
        this.children = children;
    }

    @Override
    public String getAuditType() {
        return "DevUnit";
    }

    @Override
    public ProductShortView toProductShortView() {
        return new ProductShortView(this.id, this.name, this.stateId, CollectionUtils.isEmpty(this.aliases) ? "" : this.aliases.stream().collect(Collectors.joining(", ")));
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

    public boolean isComplex() {
        return En_DevUnitType.COMPLEX.equals(getType());
    }

    public boolean isDirection() {
        return En_DevUnitType.DIRECTION.equals(getType());
    }

    public String getWikiLink() {
        return wikiLink;
    }

    public void setWikiLink(String wikiLink) {
        this.wikiLink = wikiLink;
    }

    public String getConfiguration() {
        return configuration;
    }

    public void setConfiguration(String configuration) {
        this.configuration = configuration;
    }

    public String getCdrDescription() {
        return cdrDescription;
    }

    public void setCdrDescription(String cdrDescription) {
        this.cdrDescription = cdrDescription;
    }

    public String getHistoryVersion() {
        return historyVersion;
    }

    public void setHistoryVersion(String historyVersion) {
        this.historyVersion = historyVersion;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public void setAliases(List<String> aliases) {
        this.aliases = aliases;
    }

    public DevUnit getProductDirection() {
        return productDirection;
    }

    public void setProductDirection(DevUnit productDirection) {
        this.productDirection = productDirection;
    }

    @Override
    public String toString() {
        return "DevUnit{" +
                "id=" + id +
                ", devUnitType=" + devUnitType +
                ", created=" + created +
                ", name='" + name + '\'' +
                ", info='" + info + '\'' +
                ", lastUpdate=" + lastUpdate +
                ", creatorId=" + creatorId +
                ", stateId=" + stateId +
                ", oldId=" + oldId +
                ", subscriptions=" + subscriptions +
                ", parents=" + parents +
                ", children=" + children +
                ", wikiLink='" + wikiLink + '\'' +
                ", configuration='" + configuration + '\'' +
                ", cdrDescription='" + cdrDescription + '\'' +
                ", historyVersion='" + historyVersion + '\'' +
                ", productDirection=" + productDirection +
                ", aliases=" + aliases +
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

    public interface Columns {
        String ID = "id";
        String UNIT_INFO = "UNIT_INFO";
        String HISTORY_VERSION = "history_version";
        String CONFIGURATION = "configuration";
        String CDR_DESCRIPTION = "cdr_description";
    }

}
