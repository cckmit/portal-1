package ru.protei.portal.ui.issue.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.core.service.CaseService;
import ru.protei.portal.ui.common.client.service.IssueController;
import ru.protei.portal.ui.common.server.service.SessionService;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Реализация сервиса по работе с обращениями
 */
@Service( "IssueController" )
public class IssueControllerImpl implements IssueController {

    @Override
    public List<CaseShortView> getIssues( CaseQuery query ) throws RequestFailedException {
        log.debug( "getIssues(): caseNo={} | companyId={} | productId={} | managerId={} | searchPattern={} | state={} | importance={} | sortField={} | sortDir={} | caseService={}",
                query.getCaseNo(), query.getCompanyIds(), query.getProductIds(), query.getManagerIds(), query.getSearchString(), query.getStateIds(), query.getImportanceIds(), query.getSortField(), query.getSortDir(), caseService );

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        CoreResponse<List<CaseShortView>> response = caseService.caseObjectList( descriptor.makeAuthToken(), query );
        if (response.isError()) {
            throw new RequestFailedException( response.getStatus() );
        }
        return response.getData();
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
            response = caseService.updateCaseObject( descriptor.makeAuthToken(), caseObject );

        log.debug( "saveIssue(): response.isOk()={}", response.isOk() );
        if ( response.isError() ) throw new RequestFailedException(response.getStatus());
        log.debug( "saveIssue(): id", response.getData().getId() );
        return response.getData();
    }

    @Override
    public long getIssuesCount( CaseQuery query ) throws RequestFailedException {

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        log.debug( "getIssuesCount(): query={}", query );
        CoreResponse<Long> result = caseService.count( descriptor.makeAuthToken(), query );
        return result.isOk() ? result.getData() : 0L;
    }

    @Override
    public List<CaseComment> getIssueComments( Long caseId ) throws RequestFailedException {
        log.debug( "getIssueComments(): issueId={}", caseId );

        CoreResponse<List<CaseComment>> response = caseService.getCaseCommentList( getDescriptorAndCheckSession().makeAuthToken(), caseId );
        if (response.isError()) {
            throw new RequestFailedException( response.getStatus() );
        }

        return response.getData();
    }

    @Override
    public void removeIssueComment( CaseComment value ) throws RequestFailedException {
        log.debug( "removeIssueComment(): value={}", value );

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        CoreResponse<List<CaseComment>> response = caseService.removeCaseComment( descriptor.makeAuthToken(), value, getCurrentPerson().getId() );
        if (response.isError()) {
            throw new RequestFailedException( response.getStatus() );
        }
    }

    @Override
    @Transactional
    public CaseComment editIssueComment(CaseComment comment ) throws RequestFailedException {
        log.debug( "editIssueComment(): comment={}", comment );

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        CoreResponse<CaseComment> response;
        if ( comment.getId() == null ) {
            response = caseService.addCaseComment( descriptor.makeAuthToken(), comment, getCurrentPerson() );
        } else {
            response = caseService.updateCaseComment( descriptor.makeAuthToken(), comment, getCurrentPerson() );
        }
        if (response.isError()) {
            throw new RequestFailedException( response.getStatus() );
        }

        return response.getData();
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

    private static final Logger log = LoggerFactory.getLogger( "web" );

}
