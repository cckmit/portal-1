package ru.protei.portal.ui.issue.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.struct.CaseObjectMetaJira;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.core.service.CaseLinkService;
import ru.protei.portal.core.service.CaseService;
import ru.protei.portal.ui.common.client.service.IssueController;
import ru.protei.portal.ui.common.server.ServiceUtils;
import ru.protei.portal.ui.common.server.service.SessionService;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.winter.core.utils.beans.SearchResult;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static ru.protei.portal.core.model.helper.CollectionUtils.*;
import static ru.protei.portal.ui.common.server.ServiceUtils.*;

/**
 * Реализация сервиса по работе с обращениями
 */
@Service( "IssueController" )
public class IssueControllerImpl implements IssueController {

    @Override
    public SearchResult<CaseShortView> getIssues(CaseQuery query) throws RequestFailedException {
        log.info("getIssues(): caseNo={} | companyId={} | productId={} | managerId={} | searchPattern={} | " +
                        "state={} | importance={} | sortField={} | sortDir={} | caseService={}",
                query.getCaseNumbers(), query.getCompanyIds(), query.getProductIds(), query.getManagerIds(), query.getSearchString(),
                query.getStateIds(), query.getImportanceIds(), query.getSortField(), query.getSortDir(), caseService);
        AuthToken token = getAuthToken(sessionService, httpServletRequest);
        Result<SearchResult<CaseShortView>> result = caseService.getCaseObjects(token, query);
        return checkResultAndGetData(result);
    }

    @Override
    public CaseObject getIssue( long number ) throws RequestFailedException {
        log.info("getIssue(): number: {}", number);

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        Result<CaseObject> response = caseService.getCaseObjectByNumber( descriptor.makeAuthToken(), number );
        log.info("getIssue(), number: {} -> {} ", number, response.isError() ? "error" : response.getData().getCaseNumber());

        if (response.isError()) {
            throw new RequestFailedException( response.getStatus() );
        }

        return response.getData();
    }

    private CaseObject createIssue( CaseObject caseObject ) throws RequestFailedException{
        log.info( "saveIssue(): case={}", caseObject );
        if(caseObject == null || caseObject.getId() != null){
           throw new RequestFailedException(En_ResultStatus.INCORRECT_PARAMS);
        }

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        caseObject.setTypeId( En_CaseType.CRM_SUPPORT.getId() );
        caseObject.setCreatorId( getCurrentPerson().getId() );

        Result< CaseObject >  response = caseService.createCaseObject( descriptor.makeAuthToken(), caseObject, getCurrentPerson() );

        log.info( "saveIssue(): response.isOk()={}", response.isOk() );
        if ( response.isError() ) throw new RequestFailedException(response.getStatus());
        log.info( "saveIssue(): id={}", response.getData().getId() );
        return response.getData();
    }

    @Deprecated
    @Override
    public Long saveIssue( CaseObject caseObject ) throws RequestFailedException {
        log.info("saveIssue(): caseNo={} | case={}", caseObject.getCaseNumber(), caseObject);
        AuthToken token = getAuthToken(sessionService, httpServletRequest);
        if (caseObject.getId() == null) {
            CaseObject saved = createIssue(caseObject);
            return saved.getId();
        }
        Result<CaseObject> response = caseService.updateCaseObject(token, caseObject, getCurrentPerson());
        log.info("saveIssue(): caseNo={}", caseObject.getCaseNumber());
        return checkResultAndGetData(response).getId();
    }

    @Override
    public CaseObjectMeta updateIssueMeta(CaseObjectMeta caseMeta) throws RequestFailedException {
        log.info("updateIssueMeta(): caseId={} | caseMeta={}", caseMeta.getId(), caseMeta);
        AuthToken token = getAuthToken(sessionService, httpServletRequest);
        Result<CaseObjectMeta> result = caseService.updateCaseObjectMeta(token, caseMeta, getCurrentPerson());
        log.info("updateIssueMeta(): caseId={} | status={}", caseMeta.getId(), result.getStatus());
        return checkResultAndGetData(result);
    }

    @Override
    public CaseObjectMetaNotifiers updateIssueMetaNotifiers(CaseObjectMetaNotifiers caseMetaNotifiers) throws RequestFailedException {
        log.info("updateIssueMetaNotifiers(): caseId={} | caseMetaNotifiers={}", caseMetaNotifiers.getId(), caseMetaNotifiers);
        AuthToken token = getAuthToken(sessionService, httpServletRequest);
        Result<CaseObjectMetaNotifiers> result = caseService.updateCaseObjectMetaNotifiers(token, caseMetaNotifiers, getCurrentPerson());
        log.info("updateIssueMetaNotifiers(): caseId={} | status={}", caseMetaNotifiers.getId(), result.getStatus());
        return checkResultAndGetData(result);
    }

    @Override
    public CaseObjectMetaJira updateIssueMetaJira(CaseObjectMetaJira caseMetaJira) throws RequestFailedException {
        log.info("updateIssueMetaJira(): caseId={} | caseMetaJira={}", caseMetaJira.getId(), caseMetaJira);
        AuthToken token = getAuthToken(sessionService, httpServletRequest);
        Result<CaseObjectMetaJira> result = caseService.updateCaseObjectMetaJira(token, caseMetaJira, getCurrentPerson());
        log.info("updateIssueMetaJira(): caseId={} | status={}", caseMetaJira.getId(), result.getStatus());
        return checkResultAndGetData(result);
    }

    @Override
    public CaseInfo getIssueShortInfo(Long caseNumber) throws RequestFailedException {
        log.info("getIssueShortInfo(): number: {}", caseNumber);
        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        Result<CaseInfo> response = caseService.getCaseShortInfo( descriptor.makeAuthToken(), caseNumber );
        log.info("getIssueShortInfo(), number: {} -> {} ", caseNumber, response.isError() ? "error" : response.getData().getCaseNumber());

        if (response.isError()) {
            throw new RequestFailedException( response.getStatus() );
        }

        return response.getData();
    }

    private Person getCurrentPerson(){
        return ServiceUtils.getCurrentPerson( sessionService, httpServletRequest );
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
    CaseLinkService linkService;

    @Autowired
    SessionService sessionService;

    @Autowired
    HttpServletRequest httpServletRequest;

    @Autowired
    HttpServletRequest request;

    private static final Logger log = LoggerFactory.getLogger(IssueControllerImpl.class);

}
