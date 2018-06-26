package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.view.EntityOption;

/**
 * Created by turik on 08.06.17.
 */
public class PersonQuery extends BaseQuery {
    private Long companyId;

    private Boolean onlyPeople;

    private Boolean fired;

    private Boolean deleted;

    public PersonQuery() {
        super( "", En_SortField.person_full_name, En_SortDir.ASC );
    }

    public PersonQuery( EntityOption company, Boolean onlyPeople, Boolean fired, String searchString, En_SortField sortField, En_SortDir sortDir ) {
        this ( company == null ? null : company.getId(), onlyPeople, fired, searchString, sortField, sortDir );
    }

    public PersonQuery( Long companyId, Boolean onlyPeople, Boolean fired, String searchString, En_SortField sortField, En_SortDir sortDir ) {
        this(companyId, onlyPeople, fired, null, searchString, sortField, sortDir);
    }

    public PersonQuery( Long companyId, Boolean onlyPeople, Boolean fired, Boolean deleted, String searchString, En_SortField sortField, En_SortDir sortDir ) {
        super(searchString, sortField, sortDir);
        this.companyId = companyId;
        this.onlyPeople = onlyPeople;
        this.limit = 1000;
        this.fired = fired;
        this.deleted = deleted;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId( Long companyId ) {
        this.companyId = companyId;
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

    @Override
    public String toString() {
        return "PersonQuery{" +
                "companyId=" + companyId +
                "onlyPeople=" + onlyPeople +
                "fired=" + fired +
                "deleted=" + deleted +
                '}';
    }
}
