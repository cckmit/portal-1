package ru.protei.portal.core.model.ent;

import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;
import java.util.Objects;

@JdbcEntity(table = "case_object_tag")
public class CaseObjectTag implements Serializable {

    @JdbcId(name = "id",idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name = "case_id")
    private Long caseId;

    @JdbcJoinedObject(localColumn = "tag_id")
    private CaseTag tag;

    public CaseObjectTag() {}

    public CaseObjectTag(Long caseId, Long tagId) {
        this.caseId = caseId;
        this.tag = new CaseTag(tagId);
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

    public void setTag(CaseTag tag) {
        this.tag = tag;
    }

    public CaseTag getTag() {
        return tag;
    }

    @Override
    public String toString() {
        return "CaseObjectTag{" +
                "id=" + id +
                ", caseId=" + caseId +
                ", tag=" + tag +
                '}';
    }
}
