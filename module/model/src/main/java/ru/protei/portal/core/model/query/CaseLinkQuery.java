package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.En_CaseLink;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;

public class CaseLinkQuery extends BaseQuery {

    private Long caseId;
    private Boolean showOnlyPublic;
    private String remoteId;
    private En_CaseLink type;
    private Boolean withCrosslink;

    public CaseLinkQuery() {}

    public CaseLinkQuery(Long caseId, Boolean showOnlyPublic, String remoteId) {
        this(null, null, null, caseId, showOnlyPublic, remoteId);
    }

    public CaseLinkQuery(Long caseId, Boolean showOnlyPublic) {
        this(null, null, null, caseId, showOnlyPublic, null);
    }

    public CaseLinkQuery(String searchString, En_SortField sortField, En_SortDir sortDir, Long caseId, Boolean showOnlyPublic, String remoteId) {
        super(searchString, sortField, sortDir);
        this.caseId = caseId;
        this.showOnlyPublic = showOnlyPublic;
        this.remoteId = remoteId;
    }

    public En_CaseLink getType() {
        return type;
    }

    public void setType( En_CaseLink type ) {
        this.type = type;
    }

    public Long getCaseId() {
        return caseId;
    }

    public void setCaseId(Long caseId) {
        this.caseId = caseId;
    }

    public Boolean isShowOnlyPublic() {
        return showOnlyPublic;
    }

    public void setShowOnlyPublic(Boolean showPrivate) {
        this.showOnlyPublic = showPrivate;
    }

    public String getRemoteId() {
        return remoteId;
    }

    public void setRemoteId(String remoteId) {
        this.remoteId = remoteId;
    }

    public Boolean getWithCrosslink() {
        return withCrosslink;
    }

    public void setWithCrosslink(Boolean withCrosslink) {
        this.withCrosslink = withCrosslink;
    }
}
