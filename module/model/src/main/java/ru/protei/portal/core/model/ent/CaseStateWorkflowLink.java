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

    @JdbcColumn(name = "state_from")
    private long caseStateFromId;

    @JdbcColumn(name = "state_to")
    private long caseStateToId;

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

    public long getCaseStateFromId() {
        return caseStateFromId;
    }

    public void setCaseStateFromId(long caseStateFromId) {
        this.caseStateFromId = caseStateFromId;
    }

    public long getCaseStateToId() {
        return caseStateToId;
    }

    public void setCaseStateToId(long caseStateToId) {
        this.caseStateToId = caseStateToId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CaseStateWorkflowLink that = (CaseStateWorkflowLink) o;
        return caseStateWorkflow == that.caseStateWorkflow &&
                caseStateFromId == that.caseStateFromId &&
                caseStateToId == that.caseStateToId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(caseStateWorkflow, caseStateFromId, caseStateToId);
    }

    @Override
    public String toString() {
        return "CaseStateWorkflowLink{" +
                "id=" + id +
                ", caseStateWorkflow=" + caseStateWorkflow +
                ", caseStateFrom=" + caseStateFromId +
                ", caseStateTo=" + caseStateToId +
                '}';
    }
}
