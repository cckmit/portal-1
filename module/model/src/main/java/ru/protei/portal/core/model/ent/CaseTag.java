package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.EntityOptionSupport;
import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;
import java.util.Objects;

@JdbcEntity(table = "case_tag")
public class CaseTag implements Serializable, EntityOptionSupport {

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
                '}';
    }

    @Override
    public EntityOption toEntityOption() {
        return new EntityOption(getName(), getId());
    }

    public static CaseTag fromEntityOption(EntityOption entityOption) {
        CaseTag caseTag = new CaseTag();
        caseTag.setId(entityOption.getId());
        caseTag.setName(entityOption.getDisplayText());
        return caseTag;
    }
}
