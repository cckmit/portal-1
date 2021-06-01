package ru.protei.portal.ui.issue.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.struct.CaseNameAndDescriptionChangeRequest;
import ru.protei.portal.core.model.struct.CaseObjectMetaJira;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.core.model.view.PlanOption;
import ru.protei.portal.core.service.CaseLinkService;
import ru.protei.portal.core.service.CaseService;
import ru.protei.portal.core.service.session.SessionService;
import ru.protei.portal.core.model.util.UiResult;
import ru.protei.portal.ui.common.client.service.IssueController;
import ru.protei.portal.ui.common.server.ServiceUtils;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.winter.core.utils.beans.SearchResult;

import javax.servlet.http.HttpServletRequest;

import java.util.Set;

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
        log.info("getIssue(): number={}", number);

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);

        Result<CaseObject> response = caseService.getCaseObjectByNumber( token, number );
        log.info("getIssue(), number: {} -> {} ", number, response.isError() ? "error" : response.getData().getCaseNumber());

        if (response.isError()) {
            throw new RequestFailedException( response.getStatus() );
        }

        return response.getData();
    }

    @Override
    public CaseObjectMetaNotifiers getIssueMetaNotifiers(long id) throws RequestFailedException {
        log.info("getIssueMetaNotifiers(): id={}", id);
        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        Result<CaseObjectMetaNotifiers> response = caseService.getCaseObjectMetaNotifiers(token, id);
        return checkResultAndGetData(response);
    }

    @Override
    public UiResult<Long> createIssue(CaseObjectCreateRequest caseObjectCreateRequest) throws RequestFailedException {
        log.info("createIssue(): caseObjectCreateRequest={}", caseObjectCreateRequest);

        if (caseObjectCreateRequest == null || caseObjectCreateRequest.getCaseId() != null) {
            throw new RequestFailedException(En_ResultStatus.INCORRECT_PARAMS);
        }

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);

        caseObjectCreateRequest.getCaseObject().setType(En_CaseType.CRM_SUPPORT);
        caseObjectCreateRequest.getCaseObject().setCreatorId(token.getPersonId());

        Result<CaseObject> response = caseService.createCaseObject(token, caseObjectCreateRequest);

        if (response.isError()) {
            log.info("createIssue(): status={}", response.getStatus());
            throw new RequestFailedException(response.getStatus());
        }

        if (response.getMessage() != null) {
            log.info("createIssue(): message={}", response.getMessage());
        }

        log.info("createIssue(): id={}", response.getData().getId());

        return new UiResult<>(response.getData().getId(), response.getMessage());
    }

    @Override
    public void saveIssueNameAndDescription(CaseNameAndDescriptionChangeRequest changeRequest) throws RequestFailedException {
        log.info("saveIssueNameAndDescription(): id={}| name={}, description={}", changeRequest.getId(), changeRequest.getName(), changeRequest.getInfo());
        AuthToken token = getAuthToken(sessionService, httpServletRequest);
        Result response = caseService.updateCaseNameAndDescription(token, changeRequest, En_CaseType.CRM_SUPPORT);
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
        Result<CaseObjectMetaNotifiers> result = caseService.updateCaseObjectMetaNotifiers(token, En_CaseType.CRM_SUPPORT, caseMetaNotifiers);
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

        Result<CaseInfo> response = caseService.getCaseInfo( token, caseNumber );
        log.info("getIssueShortInfo(), number: {} -> {} ", caseNumber, response.isError() ? "error" : response.getData().getCaseNumber());

        if (response.isError()) {
            throw new RequestFailedException( response.getStatus() );
        }

        return response.getData();
    }

    @Override
    public void updateManagerOfIssue(long issueId, long personId) throws RequestFailedException {
        log.info("updateManagerOfIssue(): issueId={} | personId={}", issueId, personId);
        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        CaseObjectMeta meta = ServiceUtils.checkResultAndGetData(caseService.getIssueMeta(token, issueId));
        meta.setManagerId(personId);
        ServiceUtils.checkResult(caseService.updateCaseObjectMeta(token, meta));
    }

    @Override
    public Set<PlanOption> updatePlans(Set<PlanOption> plans, Long caseId) throws RequestFailedException {
        log.info("updateManagerOfIssue(): plans={}, caseId={}", plans, caseId);

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);

        Result<Set<PlanOption>> updatedPlansResult = caseService.updateCasePlans(token, plans, caseId);

        if (updatedPlansResult.isError()) {
            throw new RequestFailedException(updatedPlansResult.getStatus());
        }

        return updatedPlansResult.getData();
    }

    @Override
    public Long removeFavoriteState(Long personId, Long issueId) throws RequestFailedException {
        log.info("removeFavoriteState(): personId={}, issueId={}", personId, issueId);

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);

        return ServiceUtils.checkResultAndGetData(caseService.removeFavoriteState(token, personId, issueId));
    }

    @Override
    public Long addFavoriteState(Long personId, Long issueId) throws RequestFailedException {
        log.info("addFavoriteState(): personId={}, issueId={}", personId, issueId);

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);

        return ServiceUtils.checkResultAndGetData(caseService.addFavoriteState(token, personId, issueId));
    }

    @Override
    public UiResult<Long> createSubtask(CaseObjectCreateRequest createRequest, Long parentCaseId) throws RequestFailedException {
        log.info("createSubtask(): createRequest={}, parentCaseId={}", createRequest, parentCaseId);

        if (createRequest == null || createRequest.getCaseId() != null || parentCaseId == null) {
            throw new RequestFailedException(En_ResultStatus.INCORRECT_PARAMS);
        }

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);

        createRequest.getCaseObject().setType(En_CaseType.CRM_SUPPORT);
        createRequest.getCaseObject().setCreatorId(token.getPersonId());

        Result<CaseObject> response = caseService.createSubtask(token, createRequest, parentCaseId);

        if (response.isError()) {
            log.info("createSubtask(): status={}", response.getStatus());
            throw new RequestFailedException(response.getStatus());
        }

        if (response.getMessage() != null) {
            log.info("createSubtask(): message={}", response.getMessage());
        }

        log.info("createSubtask(): id={}", response.getData().getId());

        return new UiResult<>(response.getData().getId(), response.getMessage());
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
