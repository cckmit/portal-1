package ru.protei.portal.core.model.dto;

import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.core.model.struct.AuditableObject;
import ru.protei.winter.jdbc.annotations.IdInsertMode;
import ru.protei.winter.jdbc.annotations.JdbcColumn;
import ru.protei.winter.jdbc.annotations.JdbcEntity;
import ru.protei.winter.jdbc.annotations.JdbcId;

@JdbcEntity(table = "dev_unit")
public class Product extends AuditableObject {

    @JdbcId(name = DevUnit.Columns.ID, idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name= DevUnit.Columns.UNIT_INFO)
    private String description;

    @JdbcColumn(name = DevUnit.Columns.CONFIGURATION)
    private String configuration;

    @JdbcColumn(name = DevUnit.Columns.CDR_DESCRIPTION)
    private String cdrDescription;

    @JdbcColumn(name = DevUnit.Columns.HISTORY_VERSION)
    private String historyVersion;

    @Override
    public String getAuditType() {
        return PRODUCT;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId( Long id ) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription( String description ) {
        this.description = description;
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

    public static final String PRODUCT = "Product";

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
