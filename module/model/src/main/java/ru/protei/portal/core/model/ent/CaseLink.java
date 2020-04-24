package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.dict.En_CaseLink;
import ru.protei.portal.core.model.struct.AuditableObject;
import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;

@JdbcEntity(table = "case_link")
public class CaseLink extends AuditableObject {
    public static final String AUDIT_TYPE = "CaseLink";

    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name="case_id")
    private Long caseId;

    @JdbcColumn(name = "link_type")
    @JdbcEnumerated(EnumType.STRING)
    private En_CaseLink type;

    @JdbcColumn(name="remote_id")
    private String remoteId;

    @JdbcJoinedObject( sqlTableAlias = "case_object", joinData = {
            @JdbcJoinData(localColumn = "link_type", value = "'CRM'"),
            @JdbcJoinData(remoteColumn = "id", value = "(SELECT CAST(case_link.remote_id AS UNSIGNED INTEGER))")
    })
    private CaseInfo caseInfo;

    private YouTrackIssueInfo youTrackIssueInfo;

    public CaseLink() {}

    public CaseLink(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCaseId() {
        return caseId;
    }

    public void setCaseId(Long caseId) {
        this.caseId = caseId;
    }

    public En_CaseLink getType() {
        return type;
    }

    public void setType(En_CaseLink type) {
        this.type = type;
    }

    public String getRemoteId() {
        return remoteId;
    }

    public void setRemoteId(String remoteId) {
        this.remoteId = remoteId;
    }

    public CaseInfo getCaseInfo() {
        return caseInfo;
    }

    public void setCaseInfo(CaseInfo caseInfo) {
        this.caseInfo = caseInfo;
    }

    public YouTrackIssueInfo getYouTrackInfo() {
        return youTrackIssueInfo;
    }

    public void setYouTrackIssueInfo( YouTrackIssueInfo youTrackIssueInfo ) {
        this.youTrackIssueInfo = youTrackIssueInfo;
    }

    @Override
    public String getAuditType() {
        return AUDIT_TYPE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CaseLink)) return false;

        CaseLink caseLink = (CaseLink) o;

        if (getCaseId() != null ? !getCaseId().equals(caseLink.getCaseId()) : caseLink.getCaseId() != null)
            return false;
        if (getType() != caseLink.getType()) return false;
        return getRemoteId() != null ? getRemoteId().equals(caseLink.getRemoteId()) : caseLink.getRemoteId() == null;
    }

    @Override
    public int hashCode() {
        int result = getCaseId() != null ? getCaseId().hashCode() : 0;
        result = 31 * result + (getType() != null ? getType().hashCode() : 0);
        result = 31 * result + (getRemoteId() != null ? getRemoteId().hashCode() : 0);
        return result;
    }

    public boolean isPrivate() {
        return (type != null && type.isForcePrivacy()) || ( caseInfo != null && caseInfo.isPrivateCase() );
    }

    @Override
    public String toString() {
        return "CaseMember{" +
                "id=" + id +
                ", caseId=" + caseId +
                ", type=" + type +
                ", remoteId=" + remoteId +
                '}';
    }
}
