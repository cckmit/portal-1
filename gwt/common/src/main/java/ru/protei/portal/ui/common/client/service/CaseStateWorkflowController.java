package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.dict.En_CaseStateWorkflow;
import ru.protei.portal.core.model.ent.CaseStateWorkflow;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.List;

@RemoteServiceRelativePath("springGwtServices/CaseStateWorkflowController")
public interface CaseStateWorkflowController extends RemoteService {

    List<CaseStateWorkflow> getWorkflowList() throws RequestFailedException;

    CaseStateWorkflow getWorkflow(En_CaseStateWorkflow caseStateWorkflow) throws RequestFailedException;
}
