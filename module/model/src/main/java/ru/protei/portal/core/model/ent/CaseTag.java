package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;
import java.util.Objects;

@JdbcEntity(table = "case_tag")
public class CaseTag implements Serializable {

    @JdbcId(name="id" , idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcEnumerated(EnumType.ID)
    @JdbcColumn(name = "case_type")
    private En_CaseType caseType;

    @JdbcColumn(name = "name")
    private String name;

    @JdbcColumn(name = "color")
    private String color;

    @JdbcColumn(name = "company_id")
    private Long companyId;

    @JdbcJoinedColumn(localColumn = "company_id", table = "company", remoteColumn = "id", mappedColumn = "cname")
    private String companyName;

    @JdbcColumn(name = "person_id")
    private Long personId;

    @JdbcJoinedColumn(localColumn = "person_id", table = "person", remoteColumn = "id", mappedColumn = "displayname")
    private String personName;

    public CaseTag() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public En_CaseType getCaseType() {
        return caseType;
    }

    public void setCaseType(En_CaseType caseType) {
        this.caseType = caseType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CaseTag caseTag = (CaseTag) o;
        return Objects.equals(id, caseTag.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "CaseTag{" +
                "id=" + id +
                ", caseType=" + caseType +
                ", name='" + name + '\'' +
                ", color='" + color + '\'' +
                ", companyId='" + companyId + '\'' +
                ", companyName='" + companyName + '\'' +
                '}';
    }
}
