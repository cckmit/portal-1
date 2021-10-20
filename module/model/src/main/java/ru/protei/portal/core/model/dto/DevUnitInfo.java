package ru.protei.portal.core.model.dto;

import ru.protei.portal.core.model.dict.En_DevUnitType;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.core.model.struct.AuditableObject;

import java.util.Objects;

public class DevUnitInfo extends AuditableObject {

    private Long id;

    private En_DevUnitType type;

    private String name;

    private String description;

    private String internalDocLink;

    private String externalDocLink;

    private String configuration;

    private String cdrDescription;

    private String historyVersion;

    private Long commonManagerId;

    @Override
    public String getAuditType() {
        return DEV_UNIT_INFO;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId( Long id ) {
        this.id = id;
    }

    public String getCdrDescription() {
        return cdrDescription;
    }

    public void setCdrDescription( String cdrDescription ) {
        this.cdrDescription = cdrDescription;
    }

    public String getHistoryVersion() {
        return historyVersion;
    }

    public void setHistoryVersion( String historyVersion ) {
        this.historyVersion = historyVersion;
    }

    public String getConfiguration() {
        return configuration;
    }

    public void setConfiguration( String configuration ) {
        this.configuration = configuration;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription( String description ) {
        this.description = description;
    }

    public En_DevUnitType getType() {
        return type;
    }

    public void setType( En_DevUnitType type) {
        this.type = type;
    }

    /**Используется в API
     * https://wiki.protei.ru/doku.php?id=protei:om:acs:portalv4_config
     * */
    public int getTypeId() {
        return type!=null?type.getId():0;
    }
    /**Используется в API
     * https://wiki.protei.ru/doku.php?id=protei:om:acs:portalv4_config
     * */
    public void setTypeId(int typeId) {
        type = En_DevUnitType.forId( typeId );
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInternalDocLink() {
        return internalDocLink;
    }

    public void setInternalDocLink(String internalDocLink) {
        this.internalDocLink = internalDocLink;
    }

    public String getExternalDocLink() {
        return externalDocLink;
    }

    public void setExternalDocLink(String externalDocLink) {
        this.externalDocLink = externalDocLink;
    }

    public Long getCommonManagerId() {
        return commonManagerId;
    }

    public void setCommonManagerId(Long commonManagerId) {
        this.commonManagerId = commonManagerId;
    }

    public static DevUnitInfo toInfo( DevUnit devUnit ) {
        if (devUnit == null) {
            return null;
        }

        DevUnitInfo info = new DevUnitInfo();
        info.setId( devUnit.getId() );
        info.setConfiguration( devUnit.getConfiguration() );
        info.setCdrDescription( devUnit.getCdrDescription() );
        info.setHistoryVersion( devUnit.getHistoryVersion() );
        info.setDescription( devUnit.getInfo() );
        info.setName( devUnit.getName() );
        info.setType( devUnit.getType() );
        info.setInternalDocLink( devUnit.getInternalDocLink() );
        info.setExternalDocLink( devUnit.getExternalDocLink() );
        info.setCommonManagerId( devUnit.getCommonManagerId() );
        return info;
    }

    public static DevUnit fromInfo(DevUnitInfo info) {
        if (info == null) {
            return null;
        }

        DevUnit devUnit = new DevUnit();
        devUnit.setId(info.getId());
        devUnit.setName(info.getName());
        devUnit.setInfo(info.getDescription());
        devUnit.setType( info.getType() );
        devUnit.setCdrDescription(info.getCdrDescription());
        devUnit.setConfiguration(info.getConfiguration());
        devUnit.setHistoryVersion(info.getHistoryVersion());
        devUnit.setInternalDocLink(info.getInternalDocLink());
        devUnit.setExternalDocLink(info.getExternalDocLink());
        devUnit.setCommonManagerId(info.getCommonManagerId());

        return devUnit;
    }

    public static final String DEV_UNIT_INFO = "DevUnitInfo";

    @Override
    public boolean equals( Object o ) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DevUnitInfo product = (DevUnitInfo) o;
        return Objects.equals( id, product.id );
    }

    @Override
    public int hashCode() {
        return Objects.hash( id );
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", cdrDescription='" + cdrDescription + '\'' +
                ", historyVersion='" + historyVersion + '\'' +
                ", configuration='" + configuration + '\'' +
                '}';
    }
}
