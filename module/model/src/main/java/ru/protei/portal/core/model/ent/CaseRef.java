package ru.protei.portal.core.model.ent;

import ru.protei.winter.jdbc.annotations.IdInsertMode;
import ru.protei.winter.jdbc.annotations.JdbcColumn;
import ru.protei.winter.jdbc.annotations.JdbcEntity;
import ru.protei.winter.jdbc.annotations.JdbcId;

/**
 * Created by michael on 24.05.16.
 */
@JdbcEntity(table = "case_refs")
public class CaseRef {

    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name = "case_id")
    private Long caseId;

    @JdbcColumn(name = "case_ref")
    private Long caseRefId;

    @JdbcColumn(name = "case_stage")
    private Long caseStageId;


    public CaseRef() {
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

    public Long getCaseRefId() {
        return caseRefId;
    }

    public void setCaseRefId(Long caseRefId) {
        this.caseRefId = caseRefId;
    }

    public Long getCaseStageId() {
        return caseStageId;
    }

    public void setCaseStageId(Long caseStageId) {
        this.caseStageId = caseStageId;
    }
}
