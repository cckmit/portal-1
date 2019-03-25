package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.dict.En_CaseStateWorkflow;
import ru.protei.portal.core.model.ent.CaseStateWorkflow;

import java.util.List;

public interface CaseStateWorkflowControllerAsync {

    void getWorkflowList(AsyncCallback<List<CaseStateWorkflow>> async);

    void getWorkflow(En_CaseStateWorkflow caseStateWorkflow, AsyncCallback<CaseStateWorkflow> async);
}
