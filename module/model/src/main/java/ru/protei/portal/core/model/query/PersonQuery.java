package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;

import java.util.Collection;
import java.util.Set;

/**
 * Created by turik on 08.06.17.
 */
public class PersonQuery extends BaseQuery {

    private Set<Long> companyIds;

    private Collection<Long> personIds;

    private Boolean people;

    private Boolean fired;

    private Boolean deleted;

    private Boolean hasCaseFilter;

    private String email;

    public PersonQuery() {
        super(null, En_SortField.person_full_name, En_SortDir.ASC);
    }

    public PersonQuery(Set<Long> companyIds, Boolean people, Boolean fired, Boolean deleted, String searchString, En_SortField sortField, En_SortDir sortDir, Boolean hasCaseFilter) {
        super(searchString, sortField, sortDir);
        this.companyIds = companyIds;
        this.people = people;
        this.limit = 1000;
        this.fired = fired;
        this.deleted = deleted;
        this.hasCaseFilter = hasCaseFilter;
    }

    public PersonQuery(Collection<Long> personIds) {
        this.personIds = personIds;
    }

    public Set<Long> getCompanyIds() {
        return companyIds;
    }

    public void setCompanyIds(Set<Long> companyIds) {
        this.companyIds = companyIds;
    }

    public Collection<Long> getPersonIds() {
        return personIds;
    }

    public void setPersonIds(Collection<Long> personIds) {
        this.personIds = personIds;
    }

    public Boolean getPeople() {
        return people;
    }

    public void setPeople(Boolean people) {
        this.people = people;
    }

    public Boolean getFired() {
        return fired;
    }

    public void setFired(Boolean fired) {
        this.fired = fired;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Boolean getHasCaseFilter() {
        return hasCaseFilter;
    }

    public void setHasCaseFilter(Boolean hasCaseFilter) {
        this.hasCaseFilter = hasCaseFilter;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "PersonQuery{" +
                ", limit=" + limit +
                ", offset=" + offset +
                ", people=" + people +
                ", fired=" + fired +
                ", deleted=" + deleted +
                ", hasCaseFilter=" + hasCaseFilter +
                ", searchString='" + searchString + '\'' +
                ", sortField=" + sortField +
                ", sortDir=" + sortDir +
                ", companyIds=" + companyIds +
                ", inIds=" + personIds +
                ", email=" + email +
                '}';
    }
}
