package ru.protei.portal.core.model.ent;

import ru.protei.winter.jdbc.annotations.IdInsertMode;
import ru.protei.winter.jdbc.annotations.JdbcColumn;
import ru.protei.winter.jdbc.annotations.JdbcEntity;
import ru.protei.winter.jdbc.annotations.JdbcId;

@JdbcEntity(table = "person_to_case_filter")
public class PersonToCaseFilter {
    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name="person_id")
    private Long personId;

    @JdbcColumn(name="case_filter_id")
    private Long caseFilterId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public Long getCaseFilterId() {
        return caseFilterId;
    }

    public void setCaseFilterId(Long caseFilterId) {
        this.caseFilterId = caseFilterId;
    }

    @Override
    public String toString() {
        return "PersonToCaseFilter{" +
                "id=" + id +
                ", personId=" + personId +
                ", caseFilterId=" + caseFilterId +
                '}';
    }
}
