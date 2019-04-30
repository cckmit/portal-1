package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;

import java.util.List;

public class CaseTagQuery extends BaseQuery {

    private List<En_CaseType> caseTypes;

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
}
