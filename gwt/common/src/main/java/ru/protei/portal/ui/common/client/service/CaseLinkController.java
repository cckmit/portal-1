package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.dict.En_CaseLink;
import ru.protei.portal.core.model.ent.YouTrackIssueInfo;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.Map;

@RemoteServiceRelativePath( "springGwtServices/CaseLinkController" )
public interface CaseLinkController extends RemoteService {

    Map<En_CaseLink, String> getLinkMap() throws RequestFailedException;

    YouTrackIssueInfo getYtLinkInfo( String ytId ) throws RequestFailedException;
}
