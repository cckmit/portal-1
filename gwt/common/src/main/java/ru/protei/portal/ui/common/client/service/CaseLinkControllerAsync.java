package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.dict.En_CaseLink;

import java.util.Map;

public interface CaseLinkControllerAsync {

    void getLinkMap(AsyncCallback<Map<En_CaseLink, String>> async);
}
