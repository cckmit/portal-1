package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.ent.History;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.List;

@RemoteServiceRelativePath("springGwtServices/CaseHistoryController")
public interface CaseHistoryController extends RemoteService {
    List<History> getHistoryListByCaseId(Long caseId) throws RequestFailedException;
}
