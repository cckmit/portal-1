package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.view.EntityOption;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by turik on 08.06.17.
 */
public class PersonQuery extends BaseQuery {
    private Set<Long> companyIds;

    private Boolean onlyPeople;

    private Boolean fired;

    private Boolean deleted;

    public PersonQuery() {
        super( "", En_SortField.person_full_name, En_SortDir.ASC );
    }

    public PersonQuery( Long companyId, Boolean onlyPeople, Boolean fired, String searchString, En_SortField sortField, En_SortDir sortDir ) {
        this ( makeCompanyIds(companyId), onlyPeople, fired, searchString, sortField, sortDir );
    }

    public PersonQuery( Set<Long> companyIds, Boolean onlyPeople, Boolean fired, String searchString, En_SortField sortField, En_SortDir sortDir ) {
        this(companyIds, onlyPeople, fired, null, searchString, sortField, sortDir);
    }

    public PersonQuery( Set<Long> companyIds, Boolean onlyPeople, Boolean fired, Boolean deleted, String searchString, En_SortField sortField, En_SortDir sortDir ) {
        super(searchString, sortField, sortDir);
        this.companyIds = companyIds;
        this.onlyPeople = onlyPeople;
        this.limit = 1000;
        this.fired = fired;
        this.deleted = deleted;
    }

    public Set<Long> getCompanyIds() {
        return companyIds;
    }

    public void setCompanyIds(Set<Long> companyIds) {
        this.companyIds = companyIds;
    }

    public Boolean getOnlyPeople() {
        return onlyPeople;
    }

    public void setOnlyPeople( Boolean onlyPeople ) {
        this.onlyPeople = onlyPeople;
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

    public static Set<Long> makeCompanyIds(Long companyId) {
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
                "companyId=" + companyIds +
                "onlyPeople=" + onlyPeople +
                "fired=" + fired +
                "deleted=" + deleted +
                '}';
    }
}
