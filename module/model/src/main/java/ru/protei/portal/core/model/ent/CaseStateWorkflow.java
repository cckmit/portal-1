package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.dict.En_CaseStateWorkflow;
import ru.protei.winter.jdbc.annotations.IdInsertMode;
import ru.protei.winter.jdbc.annotations.JdbcEntity;
import ru.protei.winter.jdbc.annotations.JdbcId;
import ru.protei.winter.jdbc.annotations.JdbcOneToMany;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@JdbcEntity(table = "case_state_workflow")
public class CaseStateWorkflow implements Serializable {

    @JdbcId(name = "id", idInsertMode = IdInsertMode.EXPLICIT)
    private Long id; // -> En_CaseStateWorkflow.id

    @JdbcOneToMany(localColumn = "id", remoteColumn = "workflow_id")
    private List<CaseStateWorkflowLink> caseStateWorkflowLinks;

    public CaseStateWorkflow() {}

    public Long getId() {
        return id;
    }

    public List<CaseStateWorkflowLink> getCaseStateWorkflowLinks() {
        return caseStateWorkflowLinks;
    }

    public boolean matches(En_CaseStateWorkflow workflow) {
        return id == workflow.getId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CaseStateWorkflow that = (CaseStateWorkflow) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "CaseStateWorkflow{" +
                "id=" + id +
                ", caseStateWorkflowLinks=" + caseStateWorkflowLinks +
                '}';
    }
}
