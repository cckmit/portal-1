package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;

public class CaseLinkQuery extends BaseQuery {

    private Long caseId;
    private Boolean showOnlyPrivate;

    public CaseLinkQuery() {}

    public CaseLinkQuery(Long caseId, Boolean showOnlyPrivate) {
        this(null, null, null, caseId, showOnlyPrivate);
    }

    public CaseLinkQuery(String searchString, En_SortField sortField, En_SortDir sortDir, Long caseId, Boolean showOnlyPrivate) {
        super(searchString, sortField, sortDir);
        this.caseId = caseId;
        this.showOnlyPrivate = showOnlyPrivate;
    }

    public Long getCaseId() {
        return caseId;
    }

    public void setCaseId(Long caseId) {
        this.caseId = caseId;
    }

    public Boolean isShowOnlyPrivate() {
        return showOnlyPrivate;
    }

    public void setShowOnlyPrivate(Boolean showPrivate) {
        this.showOnlyPrivate = showPrivate;
    }
}
