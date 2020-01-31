package ru.protei.portal.core.model.dto;

import ru.protei.portal.core.model.struct.AuditableObject;

import java.util.Objects;

public class DevUnitInfo extends AuditableObject {

    private Long id;

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
                ", cdrDescription='" + cdrDescription + '\'' +
                ", historyVersion='" + historyVersion + '\'' +
                ", configuration='" + configuration + '\'' +
                '}';
    }
}
