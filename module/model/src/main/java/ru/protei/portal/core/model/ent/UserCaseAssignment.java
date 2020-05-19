package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.dict.En_TableEntity;
import ru.protei.portal.core.model.view.EntityOption;
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
    private List<Long> states;

    @JdbcColumnCollection(name = "persons", separator = ",")
    private List<Long> persons;

    // not db column, sync with 'persons'
    private List<PersonShortView> personShortViews;

    // not db column, sync with 'states'
    private List<EntityOption> stateEntityOptions;

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

    public List<Long> getStates() {
        return states;
    }

    public void setStates(List<Long> states) {
        this.states = states;
        this.stateEntityOptions = states == null
                ? new ArrayList<>()
                : states.stream().map(EntityOption::new).collect(Collectors.toList());
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

    public List<EntityOption> getStateEntityOptions() {
        return stateEntityOptions;
    }

    public void setStateEntityOptions(List<EntityOption> stateEntityOptions) {
        this.stateEntityOptions = stateEntityOptions;
        this.states = stateEntityOptions == null
                ? new ArrayList<>()
                : stateEntityOptions.stream().map(EntityOption::getId).collect(Collectors.toList());
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
