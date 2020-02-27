package ru.protei.portal.core.model.dto;

import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.core.model.struct.AuditableObject;

import java.util.Objects;

public class DevUnitInfo extends AuditableObject {

    private Long id;

    private int typeId;

    private String name;

    private String description;

    private String wikiLink;

    private String configuration;

    private String cdrDescription;

    private String historyVersion;

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

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWikiLink() {
        return wikiLink;
    }

    public void setWikiLink(String wikiLink) {
        this.wikiLink = wikiLink;
    }

    public static DevUnitInfo toInfo( DevUnit devUnit ) {
        DevUnitInfo info = new DevUnitInfo();
        info.setId( devUnit.getId() );
        info.setConfiguration( devUnit.getConfiguration() );
        info.setCdrDescription( devUnit.getCdrDescription() );
        info.setHistoryVersion( devUnit.getHistoryVersion() );
        info.setDescription( devUnit.getInfo() );
        info.setName( devUnit.getName() );
        info.setTypeId( devUnit.getTypeId() );
        info.setWikiLink( devUnit.getWikiLink() );
        return info;
    }

    public static DevUnit fromInfo(DevUnitInfo info) {
        if (info == null) {
            return null;
        }

        DevUnit devUnit = new DevUnit();
        devUnit.setName(info.getName());
        devUnit.setInfo(info.getDescription());
        devUnit.setTypeId(info.getTypeId());
        devUnit.setCdrDescription(info.getCdrDescription());
        devUnit.setConfiguration(info.getConfiguration());
        devUnit.setHistoryVersion(info.getHistoryVersion());
        devUnit.setWikiLink(info.getWikiLink());

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
