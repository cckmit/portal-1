package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;

/**
 * Created by Mike on 02.11.2016.
 */
public class ContactQuery extends BaseQuery {

    private Long companyId;

    public ContactQuery() {
    }

    public ContactQuery(String searchString, En_SortField sortField, En_SortDir sortDir) {
        super(searchString, sortField, sortDir);
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }
}
