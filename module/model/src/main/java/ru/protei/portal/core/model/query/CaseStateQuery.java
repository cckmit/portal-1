package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;

import java.util.List;

public class CaseStateQuery extends BaseQuery {
    private En_CaseType type;
    private List<Long> ids;
    private Long companyId;

    public CaseStateQuery() {
        sortField = En_SortField.id;
        sortDir = En_SortDir.ASC;
    }

    public CaseStateQuery(En_CaseType type) {
        this.type = type;
        this.sortField = En_SortField.state_order;
        this.sortDir = En_SortDir.ASC;
    }

    public CaseStateQuery(En_CaseType type, List<Long> ids) {
        this(type);
        this.ids = ids;
    }

    public CaseStateQuery(Long companyId) {
        this.companyId = companyId;
        sortField = En_SortField.id;
        sortDir = En_SortDir.ASC;
    }

    public En_CaseType getType() {
        return type;
    }

    public void setType(En_CaseType type) {
        this.type = type;
    }

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }
}
