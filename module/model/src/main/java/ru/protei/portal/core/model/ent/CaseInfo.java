package ru.protei.portal.core.model.ent;

import ru.protei.winter.jdbc.annotations.JdbcColumn;
import ru.protei.winter.jdbc.annotations.JdbcEntity;
import ru.protei.winter.jdbc.annotations.JdbcId;

import java.io.Serializable;

@JdbcEntity(table = "case_object")
public class CaseInfo implements Serializable {

    @JdbcId
    private Long id;

    @JdbcColumn(name = "CASENO")
    private Long caseNumber;

    @JdbcColumn(name = "CASE_NAME")
    private String name;

    @JdbcColumn(name = "INFO")
    private String info;

    @JdbcColumn(name = "STATE")
    private long stateId;

    @JdbcColumn(name = "IMPORTANCE")
    private Integer impLevel;

    @JdbcColumn(name = "private_flag")
    private boolean privateCase;

    public Long getCaseNumber() {
        return caseNumber;
    }

    public void setCaseNumber(Long caseNumber) {
        this.caseNumber = caseNumber;
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

    public long getStateId() {
        return stateId;
    }

    public void setStateId(long stateId) {
        this.stateId = stateId;
    }

    public Integer getImpLevel() {
        return impLevel;
    }

    public void setImpLevel(Integer impLevel) {
        this.impLevel = impLevel;
    }

    public boolean isPrivateCase() {
        return privateCase;
    }

    public void setPrivateCase(boolean privateCase) {
        this.privateCase = privateCase;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
