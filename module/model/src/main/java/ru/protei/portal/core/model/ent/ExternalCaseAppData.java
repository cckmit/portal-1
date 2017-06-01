package ru.protei.portal.core.model.ent;

import ru.protei.winter.jdbc.annotations.IdInsertMode;
import ru.protei.winter.jdbc.annotations.JdbcColumn;
import ru.protei.winter.jdbc.annotations.JdbcEntity;
import ru.protei.winter.jdbc.annotations.JdbcId;

import java.io.Serializable;

/**
 * Created by michael on 19.05.16.
 */
@JdbcEntity(table = "case_object")
public class ExternalCaseAppData implements Serializable {

    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name = "EXT_ID")
    private String extId;

    @JdbcColumn(name = "EXT_APP")
    private String extAppType;

    @JdbcColumn(name = "EXT_APP_ID")
    private String extAppCaseId;

    @JdbcColumn(name = "EXT_APP_DATA")
    private String extAppData;

    public ExternalCaseAppData() {

    }

    public ExternalCaseAppData(Long id) {
        this.id = id;
    }

    public ExternalCaseAppData (CaseObject obj) {
        this.id = obj.getId();
        this.extId = obj.getExtId();
        this.extAppType = obj.getExtAppType();
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getExtId() {
        return extId;
    }

    public void setExtId(String extId) {
        this.extId = extId;
    }


    public String getExtAppCaseId() {
        return extAppCaseId;
    }

    public void setExtAppCaseId(String extAppCaseId) {
        this.extAppCaseId = extAppCaseId;
    }

    public String getExtAppData() {
        return extAppData;
    }

    public void setExtAppData(String extAppData) {
        this.extAppData = extAppData;
    }

    public String getExtAppType() {
        return extAppType;
    }

    public void setExtAppType(String extAppType) {
        this.extAppType = extAppType;
    }


    @Override
    public String toString() {
        return "ExternalCaseAppData{" +
                "id=" + id +
                ", extId=" + extId +
                ", extApp=" + extAppType +
                ", extAppCaseId=" + extAppCaseId +
                '}';
    }
}
