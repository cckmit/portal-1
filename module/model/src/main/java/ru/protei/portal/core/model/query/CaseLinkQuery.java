package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;

public class CaseLinkQuery extends BaseQuery {

    private Long caseId;
    private Boolean showPrivate;

    public CaseLinkQuery() {}

    public CaseLinkQuery(Long caseId, Boolean showPrivate) {
        this(null, null, null, caseId, showPrivate);
    }

    public CaseLinkQuery(String searchString, En_SortField sortField, En_SortDir sortDir, Long caseId, Boolean showPrivate) {
        super(searchString, sortField, sortDir);
        this.caseId = caseId;
        this.showPrivate = showPrivate;
    }

    public Long getCaseId() {
        return caseId;
    }

    public void setCaseId(Long caseId) {
        this.caseId = caseId;
    }

    public Boolean isShowPrivate() {
        return showPrivate;
    }

    public void setShowPrivate(Boolean showPrivate) {
        this.showPrivate = showPrivate;
    }
}
