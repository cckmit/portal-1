package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.ent.History;

import java.util.List;

public interface CaseHistoryControllerAsync {
    void getHistoryListByCaseId(Long caseId, AsyncCallback<List<History>> async);
}
