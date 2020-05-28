package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.dict.En_CaseLink;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.CaseLink;
import ru.protei.portal.core.model.ent.YouTrackIssueInfo;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface CaseLinkControllerAsync {

    void getLinkMap(AsyncCallback<Map<En_CaseLink, String>> async);

    void getYtLinkInfo( String ytId, AsyncCallback<YouTrackIssueInfo> async );

    void getCaseLinks( Long caseId, AsyncCallback<List<CaseLink>> async );

    void createLink(CaseLink value, boolean createCrossLinks, AsyncCallback<Long> async);

    void createLinkWithPublish(CaseLink value, En_CaseType caseType, boolean createCrossLinks, AsyncCallback<Long> async);

    void deleteLink(Long id, AsyncCallback<Void> async);

    void deleteLinkWithPublish(Long id, En_CaseType caseType, AsyncCallback<Void> async);
}
