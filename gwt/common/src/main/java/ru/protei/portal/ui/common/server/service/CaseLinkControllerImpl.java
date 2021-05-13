package ru.protei.portal.ui.common.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dict.En_CaseLink;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.service.CaseLinkService;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.service.CaseService;
import ru.protei.portal.core.service.session.SessionService;
import ru.protei.portal.ui.common.client.service.CaseLinkController;
import ru.protei.portal.ui.common.server.ServiceUtils;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

import static ru.protei.portal.ui.common.server.ServiceUtils.*;

@Service("CaseLinkController")
public class CaseLinkControllerImpl implements CaseLinkController {

    @Override
    public Map<En_CaseLink, String> getLinkMap() throws RequestFailedException {
        log.info("get case link map");

        Result<Map<En_CaseLink, String>> response = caseLinkService.getLinkMap();

        log.info("get case link map -> {} ", response.isOk() ? "ok" : response.getStatus());

        if (response.isError()) {
            throw new RequestFailedException(response.getStatus());
        }

        return response.isOk() ? response.getData() : null;
    }

    @Override
    public YouTrackIssueInfo getYtLinkInfo( String ytId ) throws RequestFailedException {
        AuthToken authToken = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        return checkResultAndGetData(caseLinkService.getYoutrackIssueInfo(authToken, ytId));
    }

    @Override
    public UitsIssueInfo getUitsLinkInfo(Long uitsId) throws RequestFailedException {
        AuthToken authToken = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        return checkResultAndGetData(caseLinkService.getUitsIssueInfo(authToken, uitsId));
    }

    @Override
    public List<CaseLink> getCaseLinks( Long caseId ) throws RequestFailedException {
        AuthToken token = getAuthToken( sessionService, httpServletRequest );
        return checkResultAndGetData( caseService.getCaseLinks(token, caseId ) );
    }

    @Override
    public CaseLink createLinkWithPublish(CaseLink value, En_CaseType caseType) throws RequestFailedException {
        AuthToken authToken = getAuthToken( sessionService, httpServletRequest );
        return checkResultAndGetData( linkService.createLinkWithPublish( authToken, value, caseType) );
    }

    @Override
    public CaseLink deleteLinkWithPublish(Long id, En_CaseType caseType) throws RequestFailedException {
        AuthToken authToken = getAuthToken( sessionService, httpServletRequest );
        return checkResultAndGetData( linkService.deleteLinkWithPublish( authToken, id, caseType) );
    }

    @Autowired
    CaseService caseService;
    @Autowired
    CaseLinkService linkService;
    @Autowired
    SessionService sessionService;
    @Autowired
    HttpServletRequest httpServletRequest;
    @Autowired
    CaseLinkService caseLinkService;
    @Autowired
    HttpServletRequest request;


    private static final Logger log = LoggerFactory.getLogger(CaseLinkControllerImpl.class);
}
