package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.En_CaseState;

import java.util.Collection;
import java.util.Date;
import java.util.Set;

public class EmployeeRegistrationQuery extends BaseQuery {
    private Set<En_CaseState> states;
    private Date createdFrom, createdTo;
    private Collection<String> linkedIssueIds;

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

    public Collection<String> getLinkedIssueIds() {
        return linkedIssueIds;
    }

    public void setLinkedIssueIds(Collection<String> linkedIssueIds) {
        this.linkedIssueIds = linkedIssueIds;
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
