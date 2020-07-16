package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.dict.En_CaseLink;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.CaseLink;
import ru.protei.portal.core.model.ent.YouTrackIssueInfo;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.List;
import java.util.Map;

@RemoteServiceRelativePath( "springGwtServices/CaseLinkController" )
public interface CaseLinkController extends RemoteService {

    Map<En_CaseLink, String> getLinkMap() throws RequestFailedException;

    YouTrackIssueInfo getYtLinkInfo( String ytId ) throws RequestFailedException;

    List<CaseLink> getCaseLinks( Long caseId ) throws RequestFailedException;

    CaseLink createLink(CaseLink value, boolean createCrossLinks) throws RequestFailedException;

    CaseLink createLinkWithPublish(CaseLink value, En_CaseType caseType, boolean createCrossLinks) throws RequestFailedException;

    void deleteLink(Long id) throws RequestFailedException;

    void deleteLinkWithPublish(Long id, En_CaseType caseType) throws RequestFailedException;
}
