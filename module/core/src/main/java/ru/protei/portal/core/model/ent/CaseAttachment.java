package ru.protei.portal.core.model.ent;

import ru.protei.winter.jdbc.annotations.IdInsertMode;
import ru.protei.winter.jdbc.annotations.JdbcColumn;
import ru.protei.winter.jdbc.annotations.JdbcEntity;
import ru.protei.winter.jdbc.annotations.JdbcId;

/**
 * Created by michael on 24.05.16.
 */
@JdbcEntity(table = "case_attachment")
public class CaseAttachment {

    @JdbcId(name = "id",idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name = "case_id")
    private Long caseId;

    @JdbcColumn(name = "att_id")
    private Long attachmentId;

    public CaseAttachment() {
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

    public Long getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(Long attachmentId) {
        this.attachmentId = attachmentId;
    }
}
