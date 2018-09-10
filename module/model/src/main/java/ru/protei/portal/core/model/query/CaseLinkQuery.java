package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.En_CaseLink;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;

public class CaseLinkQuery extends BaseQuery {

    private Long caseId;
    private Boolean showOnlyPrivate;
    private String remoteId;
    private En_CaseLink caseLink;

    public CaseLinkQuery() {}

    public CaseLinkQuery(Long caseId, En_CaseLink caseLink) {
        this.caseId = caseId;
        this.caseLink = caseLink;
    }

    public CaseLinkQuery(Long caseId, Boolean showOnlyPrivate, String remoteId) {
        this(null, null, null, caseId, showOnlyPrivate, remoteId);
    }

    public CaseLinkQuery(Long caseId, Boolean showOnlyPrivate) {
        this(null, null, null, caseId, showOnlyPrivate, null);
    }

    public CaseLinkQuery(String searchString, En_SortField sortField, En_SortDir sortDir, Long caseId, Boolean showOnlyPrivate, String remoteId) {
        super(searchString, sortField, sortDir);
        this.caseId = caseId;
        this.showOnlyPrivate = showOnlyPrivate;
        this.remoteId = remoteId;
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

    public String getRemoteId() {
        return remoteId;
    }

    public void setRemoteId(String remoteId) {
        this.remoteId = remoteId;
    }

    public En_CaseLink getCaseLink() {
        return caseLink;
    }

    public void setCaseLink(En_CaseLink caseLink) {
        this.caseLink = caseLink;
    }
}
