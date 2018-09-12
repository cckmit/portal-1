package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.En_CaseLink;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;

public class CaseLinkQuery extends BaseQuery {

    private Long caseId;
    private Boolean showOnlyPrivate;
    private String remoteId;
    private En_CaseLink linkType;
    private En_CaseType caseType;

    public CaseLinkQuery(En_CaseLink linkType, String remoteId, En_CaseType caseType) {
        this.linkType = linkType;
        this.remoteId = remoteId;
        this.caseType = caseType;
    }

    public CaseLinkQuery(Long caseId, En_CaseLink linkType) {
        this.caseId = caseId;
        this.linkType = linkType;
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

    public En_CaseLink getLinkType() {
        return linkType;
    }

    public void setLinkType(En_CaseLink linkType) {
        this.linkType = linkType;
    }

    public En_CaseType getCaseType() {
        return caseType;
    }

    public void setCaseType(En_CaseType caseType) {
        this.caseType = caseType;
    }
}
