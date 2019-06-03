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

    @JdbcJoinedObject( localColumn = "company_id", table = "company" )
    private Company company;

    private boolean showCompanyInView = false;

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

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public boolean isShowCompanyInView() {
        return showCompanyInView;
    }

    public void setShowCompanyInView(boolean showCompanyInView) {
        this.showCompanyInView = showCompanyInView;
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
                ", company='" + company + '\'' +
                '}';
    }

    @Override
    public EntityOption toEntityOption() {
        return new EntityOption(getViewName(), getId());
    }

    public String getViewName() {
        return getName() + (showCompanyInView ? " (" + company.getCname() + ")" : "");
    }
}
