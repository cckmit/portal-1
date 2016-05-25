package ru.protei.portal.core.model.ent;

import ru.protei.winter.jdbc.annotations.IdInsertMode;
import ru.protei.winter.jdbc.annotations.JdbcColumn;
import ru.protei.winter.jdbc.annotations.JdbcEntity;
import ru.protei.winter.jdbc.annotations.JdbcId;

import java.util.Date;

/**
 * Created by michael on 19.05.16.
 */
@JdbcEntity(table = "case_document")
public class CaseDocument {

    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name = "CASE_ID")
    private Long caseId;

    @JdbcColumn(name = "created")
    private Date created;

    @JdbcColumn(name = "DOC_TYPE")
    private int typeId;

    @JdbcColumn(name = "REVISION")
    private int revision;

    @JdbcColumn(name = "AUTHOR")
    private Long authorId;

    @JdbcColumn(name = "DOC_BODY")
    private String docBody;


    @JdbcColumn(name = "old_id")
    private Long oldId;

    public CaseDocument() {
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

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public int getRevision() {
        return revision;
    }

    public void setRevision(int revision) {
        this.revision = revision;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public String getDocBody() {
        return docBody;
    }

    public void setDocBody(String docBody) {
        this.docBody = docBody;
    }

    public Long getOldId() {
        return oldId;
    }

    public void setOldId(Long oldId) {
        this.oldId = oldId;
    }
}
