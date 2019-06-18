package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;

import java.util.List;

public class CaseTagQuery extends BaseQuery {

    private List<En_CaseType> caseTypes;
    private Long companyId;
    private List<Long> ids;
    private Long caseId;

    public Long getCaseId() {
        return caseId;
    }

    public void setCaseId(Long caseId) {
        this.caseId = caseId;
    }

    public CaseTagQuery() {}

    public CaseTagQuery(String name, En_SortField sortField, En_SortDir sortDir) {
        super(name, sortField, sortDir);
    }

    public String getName() {
        return searchString;
    }

    public void setName(String name) {
        this.searchString = name;
    }

    public List<En_CaseType> getCaseTypes() {
        return caseTypes;
    }

    public void setCaseTypes(List<En_CaseType> caseTypes) {
        this.caseTypes = caseTypes;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }
}
