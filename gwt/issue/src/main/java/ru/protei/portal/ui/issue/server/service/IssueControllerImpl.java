package ru.protei.portal.ui.issue.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.struct.CaseObjectWithCaseComment;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.core.service.CaseService;
import ru.protei.portal.ui.common.client.service.IssueController;
import ru.protei.portal.ui.common.server.service.SessionService;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.winter.core.utils.beans.SearchResult;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static ru.protei.portal.ui.common.server.ServiceUtils.checkResultAndGetData;
import static ru.protei.portal.ui.common.server.ServiceUtils.getAuthToken;

/**
 * Реализация сервиса по работе с обращениями
 */
@Service( "IssueController" )
public class IssueControllerImpl implements IssueController {

    @Override
    public SearchResult<CaseShortView> getIssues(CaseQuery query) throws RequestFailedException {
        log.debug("getIssues(): caseNo={} | companyId={} | productId={} | managerId={} | searchPattern={} | " +
                        "state={} | importance={} | sortField={} | sortDir={} | caseService={}",
                query.getCaseNumbers(), query.getCompanyIds(), query.getProductIds(), query.getManagerIds(), query.getSearchString(),
                query.getStateIds(), query.getImportanceIds(), query.getSortField(), query.getSortDir(), caseService);
        AuthToken token = getAuthToken(sessionService, httpServletRequest);
        CoreResponse<SearchResult<CaseShortView>> result = caseService.getCaseObjects(token, query);
        return checkResultAndGetData(result);
    }

    @Override
    public CaseObject getIssue( long number ) throws RequestFailedException {
        log.debug("getIssue(): number: {}", number);

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        CoreResponse<CaseObject> response = caseService.getCaseObject( descriptor.makeAuthToken(), number );
        log.debug("getIssue(), number: {} -> {} ", number, response.isError() ? "error" : response.getData().getCaseNumber());

        if (response.isError()) {
            throw new RequestFailedException( response.getStatus() );
        }

        return response.getData();
    }

    @Override
    public CaseObject saveIssue( CaseObject caseObject ) throws RequestFailedException{
        log.debug( "saveIssue(): case={}", caseObject );

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        CoreResponse< CaseObject > response;
        if ( caseObject.getId() == null ) {
            caseObject.setTypeId(En_CaseType.CRM_SUPPORT.getId());
            caseObject.setCreatorId(getCurrentPerson().getId());

            response = caseService.saveCaseObject( descriptor.makeAuthToken(), caseObject, getCurrentPerson() );
        }
        else
            response = caseService.updateCaseObject( descriptor.makeAuthToken(), caseObject, getCurrentPerson() );

        log.debug( "saveIssue(): response.isOk()={}", response.isOk() );
        if ( response.isError() ) throw new RequestFailedException(response.getStatus());
        log.debug( "saveIssue(): id={}", response.getData().getId() );
        return response.getData();
    }

    @Override
    public CaseObjectWithCaseComment saveIssueAndComment(CaseObject caseObject, CaseComment caseComment) throws RequestFailedException {
        log.debug("saveIssueAndComment(): caseNo={} | case={} | comment={}", caseObject.getCaseNumber(), caseObject, caseComment);
        AuthToken token = getAuthToken(sessionService, httpServletRequest);
        if (caseObject.getId() == null) {
            CaseObject saved = saveIssue(caseObject);
            return new CaseObjectWithCaseComment(saved, null);
        }
        CoreResponse<CaseObjectWithCaseComment> response = caseService.updateCaseObjectAndSaveComment(token, caseObject, caseComment, getCurrentPerson());
        log.debug("saveIssueAndComment(): caseNo={}", caseObject.getCaseNumber());
        return checkResultAndGetData(response);
    }

    @Override
    public CaseInfo getIssueShortInfo(Long caseNumber) throws RequestFailedException {
        log.debug("getIssueShortInfo(): number: {}", caseNumber);
        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        CoreResponse<CaseInfo> response = caseService.getCaseShortInfo( descriptor.makeAuthToken(), caseNumber );
        log.debug("getIssueShortInfo(), number: {} -> {} ", caseNumber, response.isError() ? "error" : response.getData().getCaseNumber());

        if (response.isError()) {
            throw new RequestFailedException( response.getStatus() );
        }

        return response.getData();
    }

    @Override
    public List<CaseLink> getCaseLinks( Long caseId ) throws RequestFailedException {
        AuthToken authToken = getAuthToken( sessionService, httpServletRequest );
        return checkResultAndGetData( caseService.getCaseLinks(authToken, caseId ) );
    }

    @Override
    public List<En_CaseState> getStateList() throws RequestFailedException {

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        En_CaseType type = En_CaseType.CRM_SUPPORT;

        log.debug( "getStatesByCaseType: caseType={} ", type );

        CoreResponse< List<En_CaseState> > result = caseService.stateList( type );

        log.debug("result status: {}, data-amount: {}", result.getStatus(), result.isOk() ? result.getDataAmountTotal() : 0);

        if (result.isError())
            throw new RequestFailedException(result.getStatus());

        return result.getData();
    }

    private Person getCurrentPerson(){
        return sessionService.getUserSessionDescriptor(request).getPerson();
    }

    private UserSessionDescriptor getDescriptorAndCheckSession() throws RequestFailedException {
        UserSessionDescriptor descriptor = sessionService.getUserSessionDescriptor( httpServletRequest );
        log.info( "userSessionDescriptor={}", descriptor );
        if ( descriptor == null ) {
            throw new RequestFailedException( En_ResultStatus.SESSION_NOT_FOUND );
        }

        return descriptor;
    }

    @Autowired
    CaseService caseService;

    @Autowired
    SessionService sessionService;

    @Autowired
    HttpServletRequest httpServletRequest;

    @Autowired
    HttpServletRequest request;

    private static final Logger log = LoggerFactory.getLogger(IssueControllerImpl.class);

}
