package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.dict.En_CaseLink;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.CaseLink;
import ru.protei.portal.core.model.ent.UitsIssueInfo;
import ru.protei.portal.core.model.ent.YouTrackIssueInfo;

import java.util.List;
import java.util.Map;

public interface CaseLinkControllerAsync {

    void getLinkMap(AsyncCallback<Map<En_CaseLink, String>> async);

    void getYtLinkInfo( String ytId, AsyncCallback<YouTrackIssueInfo> async );

    void getUitsLinkInfo( Long uitsId, AsyncCallback<UitsIssueInfo> async );

    void getCaseLinks( Long caseId, AsyncCallback<List<CaseLink>> async );

    void createLinkWithPublish(CaseLink value, En_CaseType caseType, AsyncCallback<CaseLink> async);

    void deleteLinkWithPublish(Long id, En_CaseType caseType, AsyncCallback<CaseLink> async);
}
