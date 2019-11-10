package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.dict.En_CaseLink;
import ru.protei.portal.core.model.ent.YouTrackIssueInfo;

import java.util.Map;

public interface CaseLinkControllerAsync {

    void getLinkMap(AsyncCallback<Map<En_CaseLink, String>> async);

    void getYtLinkInfo( String ytId, AsyncCallback<YouTrackIssueInfo> async );
}
