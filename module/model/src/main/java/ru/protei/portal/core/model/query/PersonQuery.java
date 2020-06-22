package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by turik on 08.06.17.
 */
public class PersonQuery extends BaseQuery {
    private Set<Long> companyIds;

    private Boolean people;

    private Boolean fired;

    private Boolean deleted;

    private Boolean hasCaseFilter;

    public PersonQuery() {
        super( "", En_SortField.person_full_name, En_SortDir.ASC );
    }

    public PersonQuery(Long companyId, Boolean people, Boolean fired, String searchString, En_SortField sortField, En_SortDir sortDir ) {
        this ( toSet(companyId), people, fired, searchString, sortField, sortDir );
    }

    public PersonQuery(Set<Long> companyIds, Boolean people, Boolean fired, String searchString, En_SortField sortField, En_SortDir sortDir ) {
        this(companyIds, people, fired, null, searchString, sortField, sortDir, null);
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

    public Set<Long> getCompanyIds() {
        return companyIds;
    }

    public void setCompanyIds(Set<Long> companyIds) {
        this.companyIds = companyIds;
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

    public void setFired( Boolean fired ) {
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

    private static Set<Long> toSet(Long companyId) {
        if (companyId == null) {
            return null;
        }
        Set<Long> companyIds = new HashSet<>();
        companyIds.add(companyId);
        return companyIds;
    }

    @Override
    public String toString() {
        return "PersonQuery{" +
                "companyIds=" + companyIds +
                ", people=" + people +
                ", fired=" + fired +
                ", deleted=" + deleted +
                ", hasCaseFilter=" + hasCaseFilter +
                ", searchString='" + searchString + '\'' +
                ", sortField=" + sortField +
                ", sortDir=" + sortDir +
                ", limit=" + limit +
                ", offset=" + offset +
                '}';
    }
}
