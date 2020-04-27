package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.dict.En_CaseStateWorkflow;
import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;
import java.util.Objects;

@JdbcEntity(table = "case_state_workflow_link")
public class CaseStateWorkflowLink implements Serializable {

    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name = "workflow_id")
    @JdbcEnumerated(EnumType.ID)
    private En_CaseStateWorkflow caseStateWorkflow;

    @JdbcJoinedObject(localColumn = "state_from", table = "case_state", remoteColumn = "id")
    private CaseState caseStateFrom;

    @JdbcJoinedObject(localColumn = "state_to", table = "case_state", remoteColumn = "id")
    private CaseState caseStateTo;

    public CaseStateWorkflowLink() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public En_CaseStateWorkflow getCaseStateWorkflow() {
        return caseStateWorkflow;
    }

    public void setCaseStateWorkflow(En_CaseStateWorkflow caseStateWorkflow) {
        this.caseStateWorkflow = caseStateWorkflow;
    }

    public CaseState getCaseStateFrom() {
        return caseStateFrom;
    }

    public void setCaseStateFrom(CaseState caseStateFrom) {
        this.caseStateFrom = caseStateFrom;
    }

    public CaseState getCaseStateTo() {
        return caseStateTo;
    }

    public void setCaseStateTo(CaseState caseStateTo) {
        this.caseStateTo = caseStateTo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CaseStateWorkflowLink that = (CaseStateWorkflowLink) o;
        return caseStateWorkflow == that.caseStateWorkflow &&
                caseStateFrom.equals(that.caseStateFrom) &&
                caseStateTo.equals(that.caseStateTo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(caseStateWorkflow, caseStateFrom, caseStateTo);
    }

    @Override
    public String toString() {
        return "CaseStateWorkflowLink{" +
                "id=" + id +
                ", caseStateWorkflow=" + caseStateWorkflow +
                ", caseStateFrom=" + caseStateFrom +
                ", caseStateTo=" + caseStateTo +
                '}';
    }
}
