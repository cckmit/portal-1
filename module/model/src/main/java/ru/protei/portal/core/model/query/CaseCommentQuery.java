package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;

public class CaseCommentQuery extends BaseQuery {

    private Long caseId;

    public CaseCommentQuery() {
        this(null, null, En_SortField.creation_date, En_SortDir.ASC);
    }

    public CaseCommentQuery(Long id) {
        this(id, null, En_SortField.creation_date, En_SortDir.ASC);
    }

    public CaseCommentQuery(Long id, String searchString, En_SortField sortField, En_SortDir sortDir) {
        super(searchString, sortField, sortDir);
        setCaseId(id);
    }

    public Long getCaseId() {
        return caseId;
    }

    public void setCaseId(Long caseId) {
        this.caseId = caseId;
    }
}
