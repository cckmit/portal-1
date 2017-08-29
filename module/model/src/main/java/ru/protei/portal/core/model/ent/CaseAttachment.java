package ru.protei.portal.core.model.ent;

import ru.protei.winter.jdbc.annotations.IdInsertMode;
import ru.protei.winter.jdbc.annotations.JdbcColumn;
import ru.protei.winter.jdbc.annotations.JdbcEntity;
import ru.protei.winter.jdbc.annotations.JdbcId;

import java.io.Serializable;
import java.util.Objects;

/**
 * Created by michael on 24.05.16.
 */
@JdbcEntity(table = "case_attachment")
public class CaseAttachment implements Serializable{

    @JdbcId(name = "id",idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name = "case_id")
    private Long caseId;

    @JdbcColumn(name = "att_id")
    private Long attachmentId;

    @JdbcColumn(name = "ccomment_id")
    private Long commentId;

    public CaseAttachment() {
    }

    public CaseAttachment(Long caseId, Long attachmentId) {
        this(caseId, attachmentId, null, null);
    }

    public CaseAttachment(Long caseId, Long attachmentId, Long commentId) {
        this(caseId, attachmentId, commentId, null);
    }

    public CaseAttachment(Long caseId, Long attachmentId, Long commentId, Long id) {
        this.id = id;
        this.caseId = caseId;
        this.attachmentId = attachmentId;
        this.commentId = commentId;
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

    public Long getCommentId() {
        return commentId;
    }

    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof CaseAttachment))
            return false;

        CaseAttachment ca = (CaseAttachment) obj;

        return Objects.equals(id, ca.getId())
                || (Objects.equals(caseId, ca.getCaseId()) && Objects.equals(attachmentId, ca.getAttachmentId()));
    }
}
