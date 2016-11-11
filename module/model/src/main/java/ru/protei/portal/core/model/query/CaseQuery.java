package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.view.EntityOption;

/**
 * Created by Mike on 02.11.2016.
 */
public class CaseQuery extends BaseQuery {

    private Long companyId;
    private En_CaseType type;

    public CaseQuery() {};

    public CaseQuery( En_CaseType type, EntityOption company, String searchString, En_SortField sortField, En_SortDir sortDir ) {
        this (type, company == null ? null : company.getId(), searchString, sortField, sortDir);
    }

    public CaseQuery( En_CaseType type, Long companyId, String searchString, En_SortField sortField, En_SortDir sortDir ) {
        super(searchString, sortField, sortDir);
        this.type = type;
        this.companyId = companyId;
        this.limit = 1000;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public En_CaseType getType() {
        return type;
    }

    public void setType( En_CaseType type ) {
        this.type = type;
    }
}
