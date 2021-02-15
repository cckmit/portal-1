package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.dict.En_CaseLink;
import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;

@JdbcEntity(table = "employee_registration_history")
public class EmployeeRegistrationHistory implements Serializable {
    @JdbcId(idInsertMode = IdInsertMode.AUTOINCREMENT)
    private Long id;

    @JdbcColumn(name = "history_id")
    private Long historyId;

    @JdbcColumn(name = "remote_link_id")
    private Long remoteLinkId;

    @JdbcColumn(name = "original_author_name")
    private String originalAuthorName;

    @JdbcJoinedColumn(localColumn = "remote_link_id", remoteColumn = "id", table = "case_link", mappedColumn = "link_type")
    @JdbcEnumerated(EnumType.STRING)
    private En_CaseLink type;

    @JdbcJoinedColumn(localColumn = "remote_link_id", remoteColumn = "id", table = "case_link", mappedColumn = "remote_id")
    private String remoteId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getHistoryId() {
        return historyId;
    }

    public void setHistoryId(Long historyId) {
        this.historyId = historyId;
    }

    public Long getRemoteLinkId() {
        return remoteLinkId;
    }

    public void setRemoteLinkId(Long remoteLinkId) {
        this.remoteLinkId = remoteLinkId;
    }

    public String getOriginalAuthorName() {
        return originalAuthorName;
    }

    public void setOriginalAuthorName(String originalAuthorName) {
        this.originalAuthorName = originalAuthorName;
    }

    public En_CaseLink getType() {
        return type;
    }

    public String getRemoteId() {
        return remoteId;
    }

    @Override
    public String toString() {
        return "EmployeeRegistrationHistory{" +
                "id=" + id +
                ", historyId=" + historyId +
                ", remoteLinkId=" + remoteLinkId +
                ", originalAuthorName='" + originalAuthorName + '\'' +
                '}';
    }
}
