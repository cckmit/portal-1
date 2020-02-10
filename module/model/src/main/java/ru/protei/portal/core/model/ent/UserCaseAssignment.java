package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_TableEntity;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@JdbcEntity(table = "user_case_assignment")
public class UserCaseAssignment implements Serializable {

    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name = "login_id")
    private Long loginId;

    @JdbcColumn(name = "table_entity")
    @JdbcEnumerated(EnumType.ID)
    private En_TableEntity tableEntity;

    @JdbcColumnCollection(name = "states", separator = ",")
    @JdbcEnumerated(EnumType.ID)
    private List<En_CaseState> states;

    @JdbcColumnCollection(name = "persons", separator = ",")
    private List<Long> persons;

    // not db column, sync with 'persons'
    private List<PersonShortView> personShortViews;

    public UserCaseAssignment() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getLoginId() {
        return loginId;
    }

    public void setLoginId(Long loginId) {
        this.loginId = loginId;
    }

    public En_TableEntity getTableEntity() {
        return tableEntity;
    }

    public void setTableEntity(En_TableEntity tableEntity) {
        this.tableEntity = tableEntity;
    }

    public List<En_CaseState> getStates() {
        return states;
    }

    public void setStates(List<En_CaseState> states) {
        this.states = states;
    }

    public List<Long> getPersons() {
        return persons;
    }

    public void setPersons(List<Long> persons) {
        this.persons = persons;
        this.personShortViews = persons == null
                ? new ArrayList<>()
                : persons.stream().map(PersonShortView::new).collect(Collectors.toList());
    }

    public List<PersonShortView> getPersonShortViews() {
        return personShortViews;
    }

    public void setPersonShortViews(List<PersonShortView> personShortViews) {
        this.personShortViews = personShortViews;
        this.persons = personShortViews == null
                ? new ArrayList<>()
                : personShortViews.stream().map(PersonShortView::getId).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "UserCaseAssignment{" +
                "id=" + id +
                ", loginId=" + loginId +
                ", tableEntity=" + tableEntity +
                ", states=" + states +
                ", persons=" + persons +
                ", personShortViews=" + personShortViews +
                '}';
    }
}
