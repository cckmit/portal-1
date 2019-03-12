package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;

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

    @Override
    public String toString() {
        return "CaseTag{" +
                "id=" + id +
                ", caseType=" + caseType +
                ", name='" + name + '\'' +
                ", color='" + color + '\'' +
                '}';
    }
}
