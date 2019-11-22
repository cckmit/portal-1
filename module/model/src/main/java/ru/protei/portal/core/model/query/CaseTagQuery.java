package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;

import java.util.List;

public class CaseTagQuery extends BaseQuery {

    private En_CaseType caseType;
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

    public CaseTagQuery(Long caseId) {
        this.caseId = caseId;
    }

    public CaseTagQuery(String name, En_SortField sortField, En_SortDir sortDir) {
        super(name, sortField, sortDir);
    }

    public String getName() {
        return searchString;
    }

    public void setName(String name) {
        this.searchString = name;
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

    public En_CaseType getCaseType() {
        return caseType;
    }

    public void setCaseType(En_CaseType caseType) {
        this.caseType = caseType;
    }
}
