package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.dict.En_CaseLink;
import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;

@JdbcEntity(table = "case_link")
public class CaseLink implements Serializable {

    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name="case_id")
    private Long caseId;

    @JdbcColumn(name = "link_type")
    @JdbcEnumerated(EnumType.STRING)
    private En_CaseLink type;

    @JdbcColumn(name="remote_id")
    private String remoteId;

    // not db column
    private String link = "";

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

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public boolean equals(CaseLink caseLink) {
        return  getCaseId().equals(caseLink.getCaseId()) &&
                getRemoteId().equals(caseLink.getRemoteId()) &&
                getType().equals(caseLink.getType());
    }

    @Override
    public String toString() {
        return "CaseMember{" +
                "id=" + id +
                ", caseId=" + caseId +
                ", type=" + type +
                ", remoteId=" + remoteId +
                ", link='" + link + '\'' +
                '}';
    }
}
