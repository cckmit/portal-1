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
import ru.protei.portal.core.model.struct.CaseNameAndDescriptionChangeRequest;
import ru.protei.portal.core.model.struct.CaseObjectMetaJira;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.core.service.CaseLinkService;
import ru.protei.portal.core.service.CaseService;
import ru.protei.portal.core.service.session.SessionService;
import ru.protei.portal.ui.common.client.service.IssueController;
import ru.protei.portal.ui.common.server.ServiceUtils;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.winter.core.utils.beans.SearchResult;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static ru.protei.portal.core.model.helper.CollectionUtils.size;
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

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);

        Result<CaseObject> response = caseService.getCaseObjectByNumber( token, number );
        log.info("getIssue(), number: {} -> {} ", number, response.isError() ? "error" : response.getData().getCaseNumber());

        if (response.isError()) {
            throw new RequestFailedException( response.getStatus() );
        }

        return response.getData();
    }

    @Deprecated
    @Override
    public Long createIssue(IssueCreateRequest issueCreateRequest ) throws RequestFailedException {
        log.info("saveIssue(): case={}", issueCreateRequest);

        if (issueCreateRequest == null || issueCreateRequest.getCaseId() != null) {
            throw new RequestFailedException(En_ResultStatus.INCORRECT_PARAMS);
        }

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);

        issueCreateRequest.getCaseObject().setTypeId(En_CaseType.CRM_SUPPORT.getId());
        issueCreateRequest.getCaseObject().setCreatorId(token.getPersonId());

        Result<CaseObject> response = caseService.createCaseObject(token, issueCreateRequest);

        log.info("saveIssue(): response.isOk()={}", response.isOk());
        if (response.isError()) throw new RequestFailedException(response.getStatus());
        log.info("saveIssue(): id={}", response.getData().getId());
        return response.getData().getId();
    }

    @Override
    public void saveIssueNameAndDescription(CaseNameAndDescriptionChangeRequest changeRequest) throws RequestFailedException {
        log.info("saveIssueNameAndDescription(): id={}| name={}, description={}", changeRequest.getId(), changeRequest.getName(), changeRequest.getInfo());
        AuthToken token = getAuthToken(sessionService, httpServletRequest);
        Result response = caseService.updateCaseObject(token, changeRequest);
        log.info("saveIssueNameAndDescription(): response.isOk()={}", response.isOk());

        checkResult(response);
    }

    @Override
    public CaseObjectMeta updateIssueMeta(CaseObjectMeta caseMeta) throws RequestFailedException {
        log.info("updateIssueMeta(): caseId={} | caseMeta={}", caseMeta.getId(), caseMeta);
        AuthToken token = getAuthToken(sessionService, httpServletRequest);
        Result<CaseObjectMeta> result = caseService.updateCaseObjectMeta(token, caseMeta);
        log.info("updateIssueMeta(): caseId={} | status={}", caseMeta.getId(), result.getStatus());
        return checkResultAndGetData(result);
    }

    @Override
    public CaseObjectMetaNotifiers updateIssueMetaNotifiers(CaseObjectMetaNotifiers caseMetaNotifiers) throws RequestFailedException {
        log.info("updateIssueMetaNotifiers(): caseId={} | caseMetaNotifiers={}", caseMetaNotifiers.getId(), caseMetaNotifiers);
        AuthToken token = getAuthToken(sessionService, httpServletRequest);
        Result<CaseObjectMetaNotifiers> result = caseService.updateCaseObjectMetaNotifiers(token, caseMetaNotifiers);
        log.info("updateIssueMetaNotifiers(): caseId={} | status={}", caseMetaNotifiers.getId(), result.getStatus());
        return checkResultAndGetData(result);
    }

    @Override
    public CaseObjectMetaJira updateIssueMetaJira(CaseObjectMetaJira caseMetaJira) throws RequestFailedException {
        log.info("updateIssueMetaJira(): caseId={} | caseMetaJira={}", caseMetaJira.getId(), caseMetaJira);
        AuthToken token = getAuthToken(sessionService, httpServletRequest);
        Result<CaseObjectMetaJira> result = caseService.updateCaseObjectMetaJira(token, caseMetaJira);
        log.info("updateIssueMetaJira(): caseId={} | status={}", caseMetaJira.getId(), result.getStatus());
        return checkResultAndGetData(result);
    }

    @Override
    public CaseInfo getIssueShortInfo(Long caseNumber) throws RequestFailedException {
        log.info("getIssueShortInfo(): number: {}", caseNumber);
        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);

        Result<CaseInfo> response = caseService.getCaseShortInfo( token, caseNumber );
        log.info("getIssueShortInfo(), number: {} -> {} ", caseNumber, response.isError() ? "error" : response.getData().getCaseNumber());

        if (response.isError()) {
            throw new RequestFailedException( response.getStatus() );
        }

        return response.getData();
    }

    @Override
    public List<En_CaseState> getStateList() throws RequestFailedException {

        ServiceUtils.getAuthToken(sessionService, httpServletRequest);

        En_CaseType type = En_CaseType.CRM_SUPPORT;

        log.info( "getStatesByCaseType: caseType={} ", type );

        Result< List<En_CaseState> > result = caseService.stateList( type );

        log.info("result status: {}, data-amount: {}", result.getStatus(), size(result.getData()));

        if (result.isError())
            throw new RequestFailedException(result.getStatus());

        return result.getData();
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
