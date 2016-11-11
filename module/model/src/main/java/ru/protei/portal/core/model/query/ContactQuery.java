package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.view.EntityOption;

import java.io.Serializable;

/**
 * Created by Mike on 02.11.2016.
 */
public class ContactQuery extends BaseQuery {

    private Long companyId;
    private Boolean fired;

    public ContactQuery() {
        fired = false;
    }

    public ContactQuery(EntityOption company, Boolean fired, String searchString, En_SortField sortField, En_SortDir sortDir) {
        this (company == null ? null : company.getId(), fired, searchString, sortField, sortDir);
    }

    public ContactQuery(Long companyId, Boolean fired, String searchString, En_SortField sortField, En_SortDir sortDir) {
        super(searchString, sortField, sortDir);
        this.companyId = companyId;
        this.fired = fired;
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
}
