package ru.protei.portal.core.model.struct;

import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.ent.CaseStateWorkflow;

import java.io.Serializable;
import java.util.List;

public class CaseStateAndWorkflowList implements Serializable {

    private List<CaseState> caseStatesList;
    private List<CaseStateWorkflow> caseStateWorkflowList;

    public CaseStateAndWorkflowList() {}

    public CaseStateAndWorkflowList( List<CaseState> caseStatesList, List<CaseStateWorkflow> caseStateWorkflowList) {
        this.caseStatesList = caseStatesList;
        this.caseStateWorkflowList = caseStateWorkflowList;
    }

    public List<CaseState> getCaseStatesList() {
        return caseStatesList;
    }

    public void setCaseStatesList(List<CaseState> caseStatesList) {
        this.caseStatesList = caseStatesList;
    }

    public List<CaseStateWorkflow> getCaseStateWorkflowList() {
        return caseStateWorkflowList;
    }

    public void setCaseStateWorkflowList(List<CaseStateWorkflow> caseStateWorkflowList) {
        this.caseStateWorkflowList = caseStateWorkflowList;
    }
}
