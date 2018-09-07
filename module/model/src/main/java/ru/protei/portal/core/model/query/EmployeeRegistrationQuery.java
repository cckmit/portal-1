package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.En_CaseState;

import java.util.Date;
import java.util.Set;

public class EmployeeRegistrationQuery extends BaseQuery {
    private Set<En_CaseState> states;
    private Date createdFrom, createdTo;

    public Set<En_CaseState> getStates() {
        return states;
    }

    public void setStates(Set<En_CaseState> states) {
        this.states = states;
    }

    public Date getCreatedFrom() {
        return createdFrom;
    }

    public void setCreatedFrom(Date createdFrom) {
        this.createdFrom = createdFrom;
    }

    public Date getCreatedTo() {
        return createdTo;
    }

    public void setCreatedTo(Date createdTo) {
        this.createdTo = createdTo;
    }

    @Override
    public String toString() {
        return "EmployeeRegistrationQuery{" +
                "states=" + states +
                ", createdFrom=" + createdFrom +
                ", createdTo=" + createdTo +
                ", searchString='" + searchString + '\'' +
                ", sortField=" + sortField +
                ", sortDir=" + sortDir +
                ", limit=" + limit +
                ", offset=" + offset +
                '}';
    }
}
