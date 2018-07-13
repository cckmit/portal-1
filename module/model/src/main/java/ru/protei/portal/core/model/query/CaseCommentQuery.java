package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

public class CaseCommentQuery extends BaseQuery {

    private Collection<Long> caseIds;

    public CaseCommentQuery() {
        this(null, null, En_SortField.creation_date, En_SortDir.ASC);
    }

    public CaseCommentQuery(Long caseId) {
        this(caseId, null, En_SortField.creation_date, En_SortDir.ASC);
    }

    public CaseCommentQuery(Long caseId, String searchString, En_SortField sortField, En_SortDir sortDir) {
        super(searchString, sortField, sortDir);
        setCaseIds(Arrays.asList(caseId));
    }

    public Collection<Long> getCaseIds() {
        return caseIds;
    }

    public void setCaseIds(Collection<Long> caseIds) {
        this.caseIds = caseIds;
    }
}
