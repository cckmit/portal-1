package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.view.EntityOption;

/**
 * Created by Mike on 02.11.2016.
 */
public class ContactQuery extends BaseQuery {

    private Long companyId;
    private Boolean fired;
    private Boolean deleted;

    public ContactQuery() {
        fired = false;
    }

    public ContactQuery(EntityOption company, Boolean fired, String searchString, En_SortField sortField, En_SortDir sortDir) {
        this (company == null ? null : company.getId(), fired, searchString, sortField, sortDir);
    }

    public ContactQuery(EntityOption company, Boolean fired, Boolean deleted, String searchString, En_SortField sortField, En_SortDir sortDir) {
        this (company == null ? null : company.getId(), fired, deleted, searchString, sortField, sortDir);
    }

    public ContactQuery(Long companyId, Boolean fired, String searchString, En_SortField sortField, En_SortDir sortDir) {
        this (companyId, fired, null, searchString, sortField, sortDir);
    }

    public ContactQuery(Long companyId, Boolean fired, Boolean deleted, String searchString, En_SortField sortField, En_SortDir sortDir) {
        super(searchString, sortField, sortDir);
        this.companyId = companyId;
        this.fired = fired;
        this.deleted = deleted;
        this.limit = 1000;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
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

    @Override
    public String toString() {
        return "ContactQuery{" +
                "companyId=" + companyId +
                ", fired=" + fired +
                ", deleted=" + deleted +
                '}';
    }
}
