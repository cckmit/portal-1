package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.ServiceModule;
import ru.protei.portal.core.event.CaseNameAndDescriptionEvent;
import ru.protei.portal.core.event.CaseObjectCreateEvent;
import ru.protei.portal.core.event.CaseObjectMetaEvent;
import ru.protei.portal.core.event.DeliveryNameAndDescriptionEvent;
import ru.protei.portal.core.exception.RollbackTransactionException;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.NumberUtils;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.query.*;
import ru.protei.portal.core.model.struct.CaseNameAndDescriptionChangeRequest;
import ru.protei.portal.core.model.struct.CaseObjectMetaJira;
import ru.protei.portal.core.model.struct.JiraExtAppData;
import ru.protei.portal.core.model.util.*;
import ru.protei.portal.core.model.view.*;
import ru.protei.portal.core.service.auth.AuthService;
import ru.protei.portal.core.service.autoopencase.AutoOpenCaseService;
import ru.protei.portal.core.service.policy.PolicyService;
import ru.protei.portal.core.utils.JiraUtils;
import ru.protei.winter.core.utils.beans.SearchResult;
import ru.protei.winter.core.utils.services.lock.LockService;
import ru.protei.winter.core.utils.services.lock.LockStrategy;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;
import static ru.protei.portal.core.model.dict.En_CaseLink.UITS;
import static ru.protei.portal.core.model.dict.En_CaseLink.YT;
import static ru.protei.portal.core.model.dict.En_CaseType.CRM_SUPPORT;
import static ru.protei.portal.core.model.dict.En_CaseType.DELIVERY;
import static ru.protei.portal.core.model.helper.CollectionUtils.*;
import static ru.protei.portal.core.model.util.CaseStateUtil.isTerminalState;

/**
 * Реализация сервиса управления обращениями
 */
public class CaseServiceImpl implements CaseService {
    private static Logger log = LoggerFactory.getLogger(CaseServiceImpl.class);

    @Autowired
    JdbcManyRelationsHelper jdbcManyRelationsHelper;

    @Autowired
    CaseObjectDAO caseObjectDAO;

    @Autowired
    CompanyDAO companyDAO;

    @Autowired
    PlatformDAO platformDAO;

    @Autowired
    SiteFolderService siteFolderService;

    @Autowired
    ProductService productService;

    @Autowired
    CaseShortViewDAO caseShortViewDAO;

    @Autowired
    CaseObjectMetaDAO caseObjectMetaDAO;

    @Autowired
    CaseObjectMetaNotifiersDAO caseObjectMetaNotifiersDAO;

    @Autowired
    CaseCommentDAO caseCommentDAO;

    @Autowired
    PersonDAO personDAO;

    @Autowired
    PersonShortViewDAO personShortViewDAO;

    @Autowired
    DevUnitDAO devUnitDAO;

    @Autowired
    CaseAttachmentDAO caseAttachmentDAO;

    @Autowired
    AttachmentDAO attachmentDAO;

    @Autowired
    CaseNotifierDAO caseNotifierDAO;

    @Autowired
    ExternalCaseAppDAO externalCaseAppDAO;

    @Autowired
    CaseTagDAO caseTagDAO;

    @Autowired
    JiraEndpointDAO jiraEndpointDAO;

    @Autowired
    JiraSLAMapEntryDAO jiraSLAMapEntryDAO;

    @Autowired
    CaseStateDAO caseStateDAO;

    @Autowired
    HistoryService historyService;

    @Autowired
    PolicyService policyService;

    @Autowired
    AuthService authService;

    @Autowired
    CaseLinkService caseLinkService;

    @Autowired
    CaseCommentService caseCommentService;

    @Autowired
    CaseStateWorkflowService caseStateWorkflowService;

    @Autowired
    CaseTagService caseTagService;

    @Autowired
    YoutrackService youtrackService;

    @Autowired
    UitsService uitsService;

    @Autowired
    PortalConfig portalConfig;

    @Autowired
    LockService lockService;

    @Autowired
    CompanyService companyService;

    @Autowired
    CaseObjectTagDAO caseObjectTagDAO;

    @Autowired
    AutoOpenCaseService autoOpenCaseService;

    @Autowired
    PlanService planService;

    @Autowired
    PersonFavoriteIssuesDAO personFavoriteIssuesDAO;

    @Autowired
    private CaseLinkDAO caseLinkDAO;

    @Autowired
    private CompanyImportanceItemDAO companyImportanceItemDAO;

    @Autowired
    private ImportanceLevelDAO importanceLevelDAO;

    @Override
    public Result<SearchResult<CaseShortView>> getCaseObjects(AuthToken token, CaseQuery query) {

        query = applyFilterByScope(token, query);

        SearchResult<CaseShortView> sr = caseShortViewDAO.getSearchResult(query);

        List<Long> personFavoriteIssueIds = getPersonFavoriteIssueIds(token.getPersonId());

        List<Long> caseIds = toList( sr.getResults(), CaseShortView::getId );
        List<Long> hasPublicAttachments = attachmentDAO.findCasesIdsWithPublicAttachments(caseIds);

        sr.getResults().forEach(caseShortView -> {
            caseShortView.setFavorite(personFavoriteIssueIds.contains(caseShortView.getId()));
            caseShortView.setPublicAttachmentsExist(hasPublicAttachments.contains(caseShortView.getId()));
        });

        return ok(sr);
    }

    @Override
    public Result<CaseObject> getCaseObjectById( AuthToken token, Long caseID ) {
        CaseObject caseObject = caseObjectDAO.get( caseID );

        return fillCaseObject( token, caseObject );
    }

    @Override
    public Result<CaseObject> getCaseObjectByNumber( AuthToken token, long number ) {

        CaseObject caseObject = caseObjectDAO.getCaseByNumber( CRM_SUPPORT, number );

        return fillCaseObject( token, caseObject );
    }

    @Override
    @Transactional
    public Result< CaseObject > createCaseObject(AuthToken token, CaseObjectCreateRequest caseObjectCreateRequest) {

        CaseObject caseObject = caseObjectCreateRequest.getCaseObject();

        En_IssueValidationResult validationResult = validateFieldsOfNew(token, caseObject);
        if (En_IssueValidationResult.OK != validationResult) {
            return error(En_ResultStatus.VALIDATION_ERROR, validationResult.name());
        }

        Result<CaseObject> fillCaseObjectByScopeResult = fillCaseObjectByScope(token, caseObject);
        if (fillCaseObjectByScopeResult.isError()) {
            return error(En_ResultStatus.INTERNAL_ERROR);
        }

        caseObject = fillCaseObjectByScopeResult.getData();

        if (!hasAccessForCaseObject(token, En_Privilege.ISSUE_EDIT, caseObject)) {
            return error(En_ResultStatus.PERMISSION_DENIED);
        }

        Date now = new Date();
        caseObject.setCreated(now);
        caseObject.setModified(now);

        if (personBelongsToHomeCompany(token)) {
            if (!hasAccessForCaseObject(token, En_Privilege.ISSUE_WORK_TIME_VIEW, caseObject)) {
                caseObject.setTimeElapsed(null);
            }
        } else {
            caseObject.setStateId(CrmConstants.State.CREATED);
            caseObject.setStateName(CrmConstants.State.CREATED_NAME);
            caseObject.setTimeElapsed(null);
        }

        applyStateBasedOnManager(caseObject);

        Long caseId = caseObjectDAO.insertCase(caseObject);

        if (caseId == null)
            return error(En_ResultStatus.NOT_CREATED);
        else
            caseObject.setId(caseId);

        Result<Long> resultState = addStateHistory(token, caseId, caseObject.getStateId(), caseStateDAO.get(caseObject.getStateId()).getState());
        if (resultState.isError()) {
            log.error("State history for the issue {} not saved!", caseId);
        }

        Result<Long> importanceResult = addImportanceHistory(token, caseId, caseObject.getImpLevel().longValue(), importanceLevelDAO.get(caseObject.getImpLevel()).getCode());
        if (importanceResult.isError()) {
            log.error("Importance level history for the issue {} not saved!", caseId);
        }

        if (StringUtils.isNotEmpty(caseObject.getName())) {
            Result<Long> resultManager = addNameHistory(token, caseObject.getId(), caseObject.getName());
            if (resultManager.isError()) {
                log.error("Case name history for the issue {} not saved!", caseObject.getId());
            }
        }

        //описание обращения в истории будет сделано в отдельной YT задаче
//        if (StringUtils.isNotEmpty(caseObject.getInfo())) {
//            Result<Long> resultManager = addInfoHistory(token, caseObject.getId(), "Issue info changed");
//            if (resultManager.isError()) {
//                log.error("Case info history for the issue {} not saved!", caseObject.getId());
//            }
//        }

        if (caseObject.getManager() != null && caseObject.getManager().getId() != null) {
            Result<Long> resultManager = addManagerHistory(token, caseObject.getId(), caseObject.getManager().getId(), makeManagerName(caseObject));
            if (resultManager.isError()) {
                log.error("Manager history for the issue {} not saved!", caseObject.getId());
            }
        }

        if (caseObject.getPauseDate() != null) {
            Result<Long> resultPauseDate = addPauseDateHistory(token, caseObject.getId(), String.valueOf(caseObject.getPauseDate()));
            if (resultPauseDate.isError()) {
                log.error("Pause date history for the issue {} not saved!", caseObject.getId());
            }
        }

        if (caseObject.getProductId() != null) {
            Result<Long> resultProduct = addProductHistory(token, caseObject.getId(), caseObject.getProductId(), makeProductName(caseObject));
            if (resultProduct.isError()) {
                log.error("Product history for the issue {} not saved!", caseObject.getId());
            }
        }

        if (caseObject.getDeadline() != null) {
            Result<Long> resultDeadline = addDeadlineHistory(token, caseObject.getId(), String.valueOf(caseObject.getDeadline()));
            if (resultDeadline.isError()) {
                log.error("Deadline history for the issue {} not saved!", caseObject.getId());
            }
        }

        if (caseObject.getWorkTrigger() != null) {
            Result<Long> resultWorkTrigger = addWorkTriggerHistory(token, caseObject.getId(),
                    (long)caseObject.getWorkTrigger().getId(), caseObject.getWorkTrigger().name());
            if (resultWorkTrigger.isError()) {
                log.error("Work trigger history for the issue {} not saved!", caseObject.getId());
            }
        }

        if (caseObject.getManagerCompanyId() != null) {
            Result<Long> result = addManagerCompanyHistory(token, caseObject.getId(), caseObject.getManagerCompanyId(),
                    makeManagerCompanyName(caseObject.getManagerCompanyName(), caseObject.getManagerCompanyId()));
            if (result.isError()) {
                log.error("Manager company history for the issue {} not saved!", caseObject.getId());
            }
        }

        if (caseObject.getInitiatorCompanyId() != null) {
            Result<Long> result = addInitiatorCompanyHistory(token, caseObject.getId(), caseObject.getInitiatorCompanyId(),
                    makeInitiatorCompanyName(caseObject.getInitiatorCompany(), caseObject.getInitiatorCompanyId()));
            if (result.isError()) {
                log.error("Initiator company history for the issue {} not saved!", caseObject.getId());
            }
        }

        if (caseObject.getInitiatorId() != null) {
            Result<Long> result = addInitiatorHistory(token, caseObject.getId(), caseObject.getInitiatorId(), makeInitiatorName(caseObject));
            if (result.isError()) {
                log.error("Initiator history for the issue {} not saved!", caseObject.getId());
            }
        }

        if (caseObject.getPlatformId() != null) {
            Result<Long> result = addPlatformHistory(token, caseObject.getId(), caseObject.getPlatformId(), makePlatformName(caseObject));
            if (result.isError()) {
                log.error("Platform history for the issue {} not saved!", caseObject.getId());
            }
        }

        if (caseObject.getTimeElapsed() != null && caseObject.getTimeElapsed() > 0L) {
            Long timeElapsedMessage = createAndPersistTimeElapsedMessage(token.getPersonId(), caseId, caseObject.getTimeElapsed(), caseObject.getTimeElapsedType());

            if (timeElapsedMessage == null) {
                log.error("Time elapsed message for the issue {} not saved!", caseId);
            }
        }

        if(isNotEmpty(caseObject.getAttachments())){
            caseAttachmentDAO.persistBatch(
                    caseObject.getAttachments()
                            .stream()
                            .map(a -> new CaseAttachment(caseId, a.getId()))
                            .collect(Collectors.toList())
            );

            if (caseObject.isPrivateCase()) {
                attachmentDAO.saveOrUpdateBatch(
                        caseObject.getAttachments().stream()
                                .map(a -> {
                                    a.setPrivate(true);
                                    return a;
                                }).collect(Collectors.toList()));
            }
        }

        if(isNotEmpty(caseObject.getNotifiers())){
            caseNotifierDAO.persistBatch(
                    caseObject.getNotifiers()
                            .stream()
                            .map(person -> new CaseNotifier(caseId, person.getId()))
                            .collect(Collectors.toList()));

            // update partially filled objects
            jdbcManyRelationsHelper.fill(caseObject.getNotifiers(), Person.Fields.CONTACT_ITEMS);
        }

        if (isNotEmpty(caseObjectCreateRequest.getTags())) {
            caseObjectTagDAO.persistBatch(
                    caseObjectCreateRequest.getTags()
                            .stream()
                            .map(tag -> new CaseObjectTag(caseId, tag.getId()))
                            .collect(Collectors.toList())
            );
            caseTagService.addItemsToHistory(token, caseId, caseObjectCreateRequest.getTags());
        }

        if (Boolean.TRUE.equals(caseObject.isFavorite())) {
            addFavoriteState(token, token.getPersonId(), caseId);
        }

        Set<PlanOption> plans = caseObjectCreateRequest.getPlans();

        if (isNotEmpty(plans)) {
            for (PlanOption planOption : plans) {
                Result<Plan> planResult = planService.addIssueToPlan(token, planOption.getId(), caseId);

                if (planResult.isError()) {
                    throw new RollbackTransactionException(
                            planResult.getStatus(),
                            String.format("Issue was not added to plan. planId=%d", planOption.getId())
                    );
                }
            }
        }

        List<CaseLink> links = emptyIfNull(caseObjectCreateRequest.getLinks());

        links.forEach(link -> link.setCaseId(caseId));

        Result<List<CaseLink>> createLinksResult =
                caseLinkService.createLinks(token, links, CRM_SUPPORT);

        // From GWT-side we get partially filled object, that's why we need to refresh state from db
        CaseObject newState = caseObjectDAO.get(caseId);
        newState.setAttachments(caseObject.getAttachments());
        newState.setNotifiers(caseObject.getNotifiers());
        CaseObjectCreateEvent caseObjectCreateEvent = new CaseObjectCreateEvent(this, ServiceModule.GENERAL, token.getPersonId(), newState);

        return new Result<>(En_ResultStatus.OK, newState, createLinksResult.getMessage(), listOf(caseObjectCreateEvent));
    }

    @Override
    @Transactional
    public Result<CaseNameAndDescriptionChangeRequest> updateCaseNameAndDescription(AuthToken token, CaseNameAndDescriptionChangeRequest changeRequest, En_CaseType caseType) {
        return lockService.doWithLock( CaseObject.class, changeRequest.getId(), LockStrategy.TRANSACTION, TimeUnit.SECONDS, 5, () -> {
            CaseObject oldCaseObject = caseObjectDAO.get(changeRequest.getId());
            if(oldCaseObject == null) {
                return error(En_ResultStatus.NOT_FOUND);
            }

            DiffResult<String> nameDiff = new DiffResult<>(oldCaseObject.getName(), changeRequest.getName());
            DiffResult<String> infoDiff = new DiffResult<>(oldCaseObject.getInfo(), changeRequest.getInfo());

            if(!nameDiff.hasDifferences() && !infoDiff.hasDifferences()) {
                return ok();
            }

            CaseObject caseObject = new CaseObject(changeRequest.getId());
            caseObject.setName(changeRequest.getName());
            caseObject.setInfo(changeRequest.getInfo());

            boolean isUpdated = caseObjectDAO.partialMerge(caseObject, "CASE_NAME", "INFO");

            if (!isUpdated) {
                log.info("Failed to update issue {} at db", caseObject.getId());
                return error(En_ResultStatus.NOT_UPDATED);
            }

            if (!Objects.equals(oldCaseObject.getName(), changeRequest.getName())) {
                updateNameHistory(token, changeRequest.getId(), oldCaseObject.getName(), changeRequest.getName());
            }

            //описание обращения в истории будет сделано в отдельной YT задаче
//            if (!Objects.equals(oldCaseObject.getInfo(), changeRequest.getInfo())) {
//                updateInfoHistory(token, changeRequest.getId(), "Issue info changed", "Issue info changed");
//            }

            if(isNotEmpty(changeRequest.getAttachments())){
                caseObject.setAttachmentExists(true);
                boolean isAttachmentsExistUpdated
                        = caseObjectDAO.partialMerge(caseObject, "ATTACHMENT_EXISTS");

                if (!isAttachmentsExistUpdated) {
                    throw new RollbackTransactionException(En_ResultStatus.NOT_UPDATED, "Attachment exists flag was not updated");
                }

                caseAttachmentDAO.persistBatch(
                        changeRequest.getAttachments()
                                .stream()
                                .map(a -> new CaseAttachment(changeRequest.getId(), a.getId()))
                                .collect(Collectors.toList())
                );

                if (caseObject.isPrivateCase()) {
                    attachmentDAO.saveOrUpdateBatch(
                            changeRequest.getAttachments().stream()
                                    .map(a -> {
                                        a.setPrivate(true);
                                        return a;
                                    }).collect(Collectors.toList()));
                }
            }

            Result<CaseNameAndDescriptionChangeRequest> result = ok(changeRequest);
            if (caseType == CRM_SUPPORT) {
                result.publishEvent( new CaseNameAndDescriptionEvent(
                                this,
                                changeRequest.getId(),
                                nameDiff,
                                infoDiff,
                                token.getPersonId(),
                                ServiceModule.GENERAL,
                                En_ExtAppType.forCode(oldCaseObject.getExtAppType())) );
            }
            if (caseType == DELIVERY) {
                result.publishEvent( new DeliveryNameAndDescriptionEvent(
                        this,
                        changeRequest.getId(),
                        nameDiff,
                        infoDiff,
                        token.getPersonId()) );
            }
            return result;
        });
    }

    @Override
    @Transactional
    public Result<CaseObjectMeta> updateCaseObjectMeta(AuthToken token, CaseObjectMeta caseMeta) {

        if (caseMeta.getId() == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        CaseObject oldState = caseObjectDAO.get(caseMeta.getId());
        if (oldState == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        CaseObjectMeta oldCaseMeta = new CaseObjectMeta(oldState);

        if (!hasAccessForCaseObject(token, En_Privilege.ISSUE_EDIT, oldState)) {
            return error(En_ResultStatus.PERMISSION_DENIED);
        }

        applyStateBasedOnManager(caseMeta);

        En_IssueValidationResult validationResult = validateMetaFields(token, oldCaseMeta, caseMeta);
        if (En_IssueValidationResult.OK != validationResult) {
            return error(En_ResultStatus.VALIDATION_ERROR, validationResult.name());
        }

        if (!isCaseMetaChanged(caseMeta, oldCaseMeta)) {
            return ok(caseMeta);
        }

        En_CaseStateWorkflow workflow = CaseStateWorkflowUtil.recognizeWorkflow(oldState.getExtAppType());
        boolean isStateTransitionValidByWorkflow = isStateTransitionValid(workflow, oldCaseMeta.getStateId(), caseMeta.getStateId());
        if (!isStateTransitionValidByWorkflow) {
            log.info("Wrong state transition for the issue {}: {} -> {}, workflow={}",
                    caseMeta.getId(), oldCaseMeta.getStateId(), caseMeta.getStateId(), workflow);
            throw new RollbackTransactionException(En_ResultStatus.VALIDATION_ERROR);
        }

        boolean isStateTransitionValidNoWorkflow = workflow != En_CaseStateWorkflow.NO_WORKFLOW || !isStateReopenNotAllowed(oldCaseMeta, caseMeta);
        if (!isStateTransitionValidNoWorkflow) {
            log.info("Wrong state transition for the issue {}: {} -> {}",
                    caseMeta.getId(), oldCaseMeta.getStateId(), caseMeta.getStateId());
            throw new RollbackTransactionException(En_ResultStatus.INVALID_CASE_UPDATE_CASE_IS_CLOSED);
        }

        boolean isStateTerminalValid = !isTerminalState(caseMeta.getStateId()) || isStateTerminalValid(caseMeta.getId());
        if (!isStateTerminalValid) {
            log.info("Impossible to terminate the issue {}: {} -> {}",
                    caseMeta.getId(), oldCaseMeta.getStateId(), caseMeta.getStateId());
            throw new RollbackTransactionException(En_ResultStatus.INVALID_CASE_UPDATE_SUBTASK_NOT_CLOSED);
        }

        caseMeta.setModified(new Date());
        caseMeta.setTimeElapsed(caseCommentService.getTimeElapsed(caseMeta.getId()).getData());

        boolean isUpdated = caseObjectMetaDAO.merge(caseMeta);
        if (!isUpdated) {
            log.info("Failed to update issue meta data {} at db", caseMeta.getId());
            throw new RollbackTransactionException(En_ResultStatus.NOT_UPDATED);
        }

        if (oldCaseMeta.getStateId() != caseMeta.getStateId()) {
            Result<Long> resultState = changeStateHistory(token, caseMeta.getId(),
                    oldCaseMeta.getStateId(), caseStateDAO.get(oldCaseMeta.getStateId()).getState(),
                    caseMeta.getStateId(), caseStateDAO.get(caseMeta.getStateId()).getState());
            if (resultState.isError()) {
                log.error("State history for the issue {} isn't saved!", caseMeta.getId());
            }
        }

        if (!Objects.equals(oldCaseMeta.getImpLevel(), caseMeta.getImpLevel())) {
            Result<Long> resultImportance = changeImportanceHistory(token, caseMeta.getId(),
                    oldCaseMeta.getImpLevel().longValue(), importanceLevelDAO.get(oldCaseMeta.getImpLevel()).getCode(),
                    caseMeta.getImpLevel().longValue(), importanceLevelDAO.get(caseMeta.getImpLevel()).getCode());
            if (resultImportance.isError()) {
                log.error("Importance level history for the issue {} isn't saved!", caseMeta.getId());
            }
        }

        if (!Objects.equals(oldCaseMeta.getManagerId(), caseMeta.getManagerId())) {
            updateManagerHistory(token, caseMeta, oldCaseMeta);
        }

        if (!Objects.equals(oldCaseMeta.getPauseDate(), caseMeta.getPauseDate())) {
            updatePauseDateHistory(token, caseMeta, oldCaseMeta);
        }

        if (!Objects.equals(oldCaseMeta.getProductId(), caseMeta.getProductId())) {
            updateProductHistory(token, caseMeta, oldCaseMeta);
        }

        if (!Objects.equals(oldCaseMeta.getManagerCompanyId(), caseMeta.getManagerCompanyId())) {
            updateManagerCompanyHistory(token, caseMeta, oldCaseMeta);
        }

        if (!Objects.equals(oldCaseMeta.getInitiatorCompanyId(), caseMeta.getInitiatorCompanyId())) {
            updateInitiatorCompanyHistory(token, caseMeta, oldCaseMeta);
        }

        if (!Objects.equals(oldCaseMeta.getInitiatorId(), caseMeta.getInitiatorId())) {
            updateInitiatorHistory(token, caseMeta, oldCaseMeta);
        }

        if (!Objects.equals(oldCaseMeta.getPlatformId(), caseMeta.getPlatformId())) {
            updatePlatformHistory(token, caseMeta, oldCaseMeta);
        }

        if (!Objects.equals(oldCaseMeta.getAutoClose(), caseMeta.getAutoClose())) {
            updateAutoCloseHistory(token, caseMeta, oldCaseMeta);
        }

        if (!Objects.equals(oldCaseMeta.getDeadline(), caseMeta.getDeadline())) {
            updateDeadlineHistory(token, caseMeta, oldCaseMeta);
        }

        if (!Objects.equals(oldCaseMeta.getWorkTrigger(), caseMeta.getWorkTrigger())) {
            updateWorkTriggerHistory(token, caseMeta, oldCaseMeta);
        }

        Result<Long> openedParentsResult = ok(caseMeta.getId());
        if (oldCaseMeta.getStateId() != caseMeta.getStateId() && isTerminalState(caseMeta.getStateId())) {
            openedParentsResult = openParentIssuesIfAllLinksInTerminalState(token, caseMeta.getId());
            if (openedParentsResult.isError()) {
                log.error("Failed to open parent issue | message = '{}'", openedParentsResult.getMessage());
                throw new RollbackTransactionException(openedParentsResult.getStatus());
            }
        }

        if (!oldCaseMeta.getAutoClose() && caseMeta.getAutoClose()) {
            String langString = "issue_will_be_closed";
            createAndPersistAutoCloseMessage(caseMeta.getId(), langString);
        }

        // From GWT-side we get partially filled object, that's why we need to refresh state from db
        CaseObjectMeta newCaseMeta = caseObjectMetaDAO.get(caseMeta.getId());

        return ok(newCaseMeta)
                .publishEvent(new CaseObjectMetaEvent(
                this,
                ServiceModule.GENERAL,
                token.getPersonId(),
                En_ExtAppType.forCode(oldState.getExtAppType()),
                oldCaseMeta,
                newCaseMeta))
                .publishEvents(openedParentsResult.getEvents());
    }

    @Override
    public Result<CaseObjectMeta> getIssueMeta( AuthToken token, Long issueId ) {
        CaseObjectMeta caseObjectMeta = caseObjectMetaDAO.get( issueId );

        return ok(caseObjectMeta);
    }

    @Override
    public Result<CaseObjectMetaNotifiers> getCaseObjectMetaNotifiers(AuthToken token, Long issueId) {
        CaseObjectMetaNotifiers caseObjectMetaNotifiers = caseObjectMetaNotifiersDAO.get(issueId);
        jdbcManyRelationsHelper.fill(caseObjectMetaNotifiers, "notifiers");
        return ok(caseObjectMetaNotifiers);
    }

    @Override
    @Transactional
    public Result<CaseObjectMetaNotifiers> updateCaseObjectMetaNotifiers(AuthToken token, En_CaseType caseType,
                                                                         CaseObjectMetaNotifiers caseMetaNotifiers) {

        if (caseMetaNotifiers.getId() == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        CaseObject oldState = caseObjectDAO.get(caseMetaNotifiers.getId());
        if (oldState == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        if (caseType == CRM_SUPPORT && !hasAccessForCaseObject(token, En_Privilege.ISSUE_EDIT, oldState)) {
            return error(En_ResultStatus.PERMISSION_DENIED);
        }

        jdbcManyRelationsHelper.persist(caseMetaNotifiers, "notifiers");
        if (isNotEmpty(caseMetaNotifiers.getNotifiers())) {
            // update partially filled objects
            caseMetaNotifiers.setNotifiers(new HashSet<>(
                personDAO.partialGetListByKeys(
                    caseMetaNotifiers.getNotifiers().stream()
                        .map(Person::getId)
                        .collect(Collectors.toList()),
                    "id", "displayShortName")
            ));
            jdbcManyRelationsHelper.fill(caseMetaNotifiers.getNotifiers(), Person.Fields.CONTACT_ITEMS);
        }
        caseMetaNotifiers.setModified(new Date());

        boolean isUpdated = caseObjectMetaNotifiersDAO.merge(caseMetaNotifiers);
        if (!isUpdated) {
            log.info("Failed to update issue meta notifiers data {} at db", caseMetaNotifiers.getId());
            throw new RollbackTransactionException(En_ResultStatus.NOT_UPDATED);
        }

        // Event not needed

        return ok(caseMetaNotifiers);
    }

    @Override
    @Transactional
    public Result<CaseObjectMetaJira> updateCaseObjectMetaJira(AuthToken token, CaseObjectMetaJira caseMetaJira) {

        if (caseMetaJira.getId() == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        CaseObject oldState = caseObjectDAO.get(caseMetaJira.getId());
        if (oldState == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        if (!hasAccessForCaseObject(token, En_Privilege.ISSUE_EDIT, oldState)) {
            return error(En_ResultStatus.PERMISSION_DENIED);
        }

        if (!En_ExtAppType.JIRA.getCode().equals(oldState.getExtAppType())) {
            return error(En_ResultStatus.NOT_AVAILABLE);
        }

        if (caseMetaJira.getSlaMapId() == null || StringUtils.isEmpty(caseMetaJira.getIssueType())) {
            log.warn("Got caseObjectMetaJira with 'jira' extAppType and empty jiraMetaData field(s): {}", caseMetaJira);
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        En_JiraSLAIssueType jiraSLAIssueType = En_JiraSLAIssueType.forIssueType(caseMetaJira.getIssueType());
        boolean isSeverityCanBeChanged = En_JiraSLAIssueType.byPortal().contains(jiraSLAIssueType);
        if (!isSeverityCanBeChanged) {
            log.info("Got caseObjectMetaJira with jiraSLAIssueType that cannot be changed by portal: {}", caseMetaJira);
            return error(En_ResultStatus.NOT_UPDATED);
        }

        try {
            JiraSLAMapEntry slaMapEntry = jiraSLAMapEntryDAO.getByIssueTypeAndSeverity(
                caseMetaJira.getSlaMapId(),
                caseMetaJira.getIssueType(),
                caseMetaJira.getSeverity()
            );
            if (slaMapEntry == null) {
                log.warn("Got caseObjectMetaJira with 'jira' extAppType and invalid issueType/severity: {}", caseMetaJira);
                return error(En_ResultStatus.INCORRECT_PARAMS);
            }

            ExternalCaseAppData appData = externalCaseAppDAO.get(caseMetaJira.getId());
            JiraExtAppData extAppData = JiraExtAppData.fromJSON(appData.getExtAppData());
            extAppData.setSlaSeverity(caseMetaJira.getSeverity());
            appData.setExtAppData(JiraExtAppData.toJSON(extAppData));
            if (!externalCaseAppDAO.saveExtAppData(appData)) {
                log.warn("Failed to save extAppData with jira SLA information");
                return error(En_ResultStatus.INTERNAL_ERROR);
            }
        } catch (Exception e) {
            log.warn("Failed to persist jira SLA information", e);
            throw e;
        }

        // Event not needed

        return ok(caseMetaJira);
    }

    @Override
    @Transactional
    public Result<Boolean> updateCaseModified( AuthToken token, Long caseId, Date modified) {
        if(caseId == null || !caseObjectDAO.checkExistsByKey(caseId))
            return error(En_ResultStatus.INCORRECT_PARAMS);

        CaseObject caseObject = new CaseObject(caseId);
        caseObject.setModified(modified == null? new Date(): modified);

        boolean isUpdated = caseObjectDAO.partialMerge(caseObject, "MODIFIED");

        return ok(isUpdated);
    }

    @Override
    @Transactional
    public Result<Boolean> updateExistsAttachmentsFlag( Long caseId, boolean flag){
        if(caseId == null)
            return error(En_ResultStatus.INCORRECT_PARAMS);

        CaseObject caseObject = new CaseObject(caseId);
        caseObject.setAttachmentExists(flag);
        boolean result = caseObjectDAO.partialMerge(caseObject, "ATTACHMENT_EXISTS");

        if(!result)
            return error(En_ResultStatus.NOT_UPDATED);
        return ok(true);
    }

    @Override
    @Transactional
    public Result<Boolean> updateExistsAttachmentsFlag( Long caseId){
        return isExistsAttachments(caseId).flatMap( isExists ->
                updateExistsAttachmentsFlag(caseId, isExists));
    }

    @Override
    public Result<Long> getAndIncrementEmailLastId( Long caseId ) {
        if (caseId == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        Long previousId = caseObjectDAO.getAndIncrementEmailLastId(caseId);
        if(previousId==null) previousId = 0L;

        return ok(previousId);
    }

    @Override
    public Result<CaseInfo> getCaseInfo(AuthToken token, Long caseNumber) {
        if ( !hasAccessForCaseObject( token, En_Privilege.ISSUE_VIEW, caseObjectDAO.getCaseByNumber(CRM_SUPPORT, caseNumber) ) ) {
            return error(En_ResultStatus.PERMISSION_DENIED );
        }

        CaseShortView caseObject = caseShortViewDAO.getCaseByNumber( CRM_SUPPORT, caseNumber );

        if(caseObject == null)
            return error(En_ResultStatus.NOT_FOUND);

        CaseInfo info = new CaseInfo();
        info.setId(caseObject.getId());
        info.setCaseNumber(caseObject.getCaseNumber());
        info.setPrivateCase(caseObject.isPrivateCase());
        info.setName(caseObject.getName());
        info.setImpLevel(caseObject.getImpLevel());
        info.setInfo(caseObject.getInfo());
        info.setStateId(caseObject.getStateId());
        info.setState(caseStateDAO.get(caseObject.getStateId()));

        return ok(info);
    }

    @Override
    @Transactional
    public Result<Long> bindAttachmentToCaseNumber( AuthToken token, En_CaseType caseType, Attachment attachment, long caseNumber) {
        CaseObject caseObject = caseObjectDAO.getCaseByNumber(caseType, caseNumber);
        if ( !hasAccessForCaseObject( token, En_Privilege.ISSUE_EDIT, caseObject ) ) {
            return error(En_ResultStatus.PERMISSION_DENIED );
        }
        return attachToCaseId( attachment, caseObject.getId(), caseObject.isPrivateCase());
    }

    @Override
    @Transactional
    public Result<Long> attachToCaseId( Attachment attachment, long caseId, boolean isPrivateCase) {
        CaseAttachment caseAttachment = new CaseAttachment(caseId, attachment.getId());
        Long caseAttachId = caseAttachmentDAO.persist(caseAttachment);

        if(caseAttachId == null)
            return error(En_ResultStatus.NOT_CREATED);

        CaseObject caseObject = new CaseObject(caseId);
        caseObject.setAttachmentExists(true);
        caseObject.setModified(new Date());
        boolean isCaseUpdated = caseObjectDAO.partialMerge(caseObject, "ATTACHMENT_EXISTS", "MODIFIED");
        if (isPrivateCase) {
            attachment.setPrivate(true);
            attachmentDAO.partialMerge(attachment, "private_flag");
        }

        if (!isCaseUpdated) {
            throw new RollbackTransactionException(En_ResultStatus.NOT_UPDATED, "failed to update case object");
        }

        return ok(caseAttachId);
    }

    @Override
    public Result<Boolean> isExistsAttachments(Long caseId) {
        return ok(caseAttachmentDAO.checkExistsByCondition("case_attachment.case_id = ?", caseId));
    }

    @Override
    public Result<Boolean> isExistAnyAttachments(List<Long> attachmentIds) {
        if (isEmpty(attachmentIds)) {
            return ok(false);
        }

        return ok(caseAttachmentDAO.checkExistAnyAttachments(attachmentIds));
    }

    @Override
    public Result<List<CaseLink>> getCaseLinks( AuthToken token, Long caseId ) {
        return caseLinkService.getLinks( token, caseId )
                .map( this::fillLinkedEntryInfo);
    }


    @Override
    public Result<Long> getCaseId(AuthToken token, Long caseNumber, En_CaseType type ) {
        Long caseId = caseObjectDAO.getCaseIdByNumber( type, caseNumber );
        if(caseId==null) error( En_ResultStatus.NOT_FOUND );
        return ok(caseId);
    }

    @Override
    public Result<Long> getCaseNumberById( AuthToken token, Long caseId ) {
        Long caseNumber = caseObjectDAO.getCaseNumberById( caseId );
        if(caseNumber==null) error( En_ResultStatus.NOT_FOUND );
        return ok(caseNumber);
    }

    @Override
    @Transactional
    public Result<Set<PlanOption>> updateCasePlans(AuthToken token, Set<PlanOption> plans, Long caseId) {
        log.info("CaseServiceImpl#updatePlans : plans={}, caseId={}", plans, caseId);

        CaseObject caseObject = caseObjectDAO.partialGet(caseId, "MODIFIED");
        caseObject.setId(caseId);
        caseObject.setModified(new Date());

        if (!caseObjectDAO.partialMerge(caseObject, "MODIFIED")) {
            return error(En_ResultStatus.NOT_UPDATED, "Modified column was not added");
        }

        PlanQuery planQuery = new PlanQuery();
        planQuery.setIssueId(caseId);
        planQuery.setCreatorId(token.getPersonId());

        Result<List<PlanOption>> oldPlansResult = planService.listPlanOptions(token, planQuery);

        if (oldPlansResult.isError()) {
            throw new RollbackTransactionException(oldPlansResult.getStatus());
        }

        En_ResultStatus resultStatus = updatePlans(token, caseId, new HashSet<>(oldPlansResult.getData()), plans);

        if (!En_ResultStatus.OK.equals(resultStatus)) {
            throw new RollbackTransactionException(resultStatus);
        }

        return ok(plans);
    }

    @Override
    @Transactional
    public Result<Long> removeFavoriteState(AuthToken token, Long personId, Long issueId) {
        if (personId == null || issueId == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        personFavoriteIssuesDAO.removeState(personId, issueId);

        return ok(issueId);
    }

    @Override
    @Transactional
    public Result<Long> addFavoriteState(AuthToken token, Long personId, Long issueId) {
        if (personId == null || issueId == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        Long personFavoriteIssuesId = personFavoriteIssuesDAO.persist(new PersonFavoriteIssues(personId, issueId));
        return ok(personFavoriteIssuesId);
    }

    private List<Long> getPersonFavoriteIssueIds(Long personId) {
        return personFavoriteIssuesDAO.getIssueIdListByPersonId(personId);
    }

    private En_ResultStatus updatePlans(AuthToken token, Long caseId, Set<PlanOption> oldPlans, Set<PlanOption> plans) {
        DiffCollectionResult<PlanOption> planDiffs = diffCollection(oldPlans, plans);

        for (PlanOption planOption : emptyIfNull(planDiffs.getAddedEntries())) {
            Result<Plan> planResult = planService.addIssueToPlan(token, planOption.getId(), caseId);

            if (planResult.isError()) {
                return planResult.getStatus();
            }
        }

        for (PlanOption planOption : emptyIfNull(planDiffs.getRemovedEntries())) {
            Result<Long> planResult = planService.removeIssueFromPlan(token, planOption.getId(), caseId);

            if (planResult.isError()) {
                return planResult.getStatus();
            }
        }

        return En_ResultStatus.OK;
    }

    @Override
    @Transactional
    public Result<CaseObject> createSubtask(AuthToken token, CaseObjectCreateRequest caseObjectCreateRequest, Long parentCaseObjectId) {

        if (parentCaseObjectId == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        CaseObject parentCaseObject = caseObjectDAO.get(parentCaseObjectId);
        if (parentCaseObject == null) {
            return error(En_ResultStatus.NOT_FOUND_PARENT);
        }

        if (parentCaseObject.getInitiatorCompany().getAutoOpenIssue()) {
            return error(En_ResultStatus.NOT_ALLOWED_AUTOOPEN_ISSUE);
        }

        if (isIntegrationIssue(parentCaseObject.getExtAppType())) {
            return error(En_ResultStatus.NOT_ALLOWED_INTEGRATION_ISSUE);
        }

        if (isParentStateNotAllowed(parentCaseObject.getStateId())) {
            return error(En_ResultStatus.NOT_ALLOWED_PARENT_STATE);
        }

        fillCaseCreateRequest(caseObjectCreateRequest, parentCaseObject);

        Result<CaseObject> result = createCaseObject(token, caseObjectCreateRequest);
        if (result.isError()) {
            log.error("createSubtask(): parent-id = {} | failed to save subtask to db with result = {}", parentCaseObjectId, result);
            throw new RollbackTransactionException(result.getStatus());
        }

        parentCaseObject.setStateId(CrmConstants.State.BLOCKED);
        Result<CaseObjectMeta> parentUpdateResult = updateCaseObjectMeta(token, new CaseObjectMeta(parentCaseObject));
        if (parentUpdateResult.isError()) {
            log.error("createSubtask(): parent-id = {} | failed to save parent issue to db with result = {}", parentCaseObjectId, result);
            throw new RollbackTransactionException(parentUpdateResult.getStatus());
        }

        return result.publishEvents(parentUpdateResult.getEvents());
    }

    @Override
    @Transactional
    public Result<CaseObjectMetaNotifiers> addNotifierToCaseObject(AuthToken authToken, Long caseId, PersonShortView personShortView) {
        CaseObjectMetaNotifiers caseObjectMetaNotifiers = caseObjectMetaNotifiersDAO.get(caseId);
        jdbcManyRelationsHelper.fill(caseObjectMetaNotifiers, "notifiers");

        caseObjectMetaNotifiers.getNotifiers().add(Person.fromPersonShortView(personShortView));
        return updateCaseObjectMetaNotifiers(authToken, CRM_SUPPORT, caseObjectMetaNotifiers);
    }

    private Result<Long> openParentIssuesIfAllLinksInTerminalState(AuthToken token, long caseObjectId) {

        List<CaseLink> caseLinks = caseLinkDAO.getListByQuery(new CaseLinkQuery(caseObjectId, En_BundleType.SUBTASK));
        Result<Long> result = ok(caseObjectId);

        for(CaseLink caseLink : caseLinks) {
            Long parentId = NumberUtils.parseLong(caseLink.getRemoteId());
            boolean isAllLinksInTerminalState = isAllLinksInTerminalState(parentId);
            if (isAllLinksInTerminalState) {
                CaseObjectMeta caseObjectMeta = caseObjectMetaDAO.get(parentId);
                caseObjectMeta.setStateId(CrmConstants.State.OPENED);
                Result<CaseObjectMeta> openedIssueResult = updateCaseObjectMeta(token, caseObjectMeta);
                if (openedIssueResult.isError()) {
                    return error(openedIssueResult.getStatus(), "issueId = " + parentId);
                }
                result.publishEvents(openedIssueResult.getEvents());
            }
        }

        return result;
    }

    private boolean isAllLinksInTerminalState(Long caseObjectId) {
        List<CaseLink> caseLinks = caseLinkDAO.getListByQuery(new CaseLinkQuery(caseObjectId, En_BundleType.PARENT_FOR));

        if (CollectionUtils.isNotEmpty(caseLinks) &&
                caseLinks.stream()
                        .allMatch(caseLink -> isTerminalState(caseLink.getCaseInfo().getState().getId()))) {
            return true;
        }
        return false;
    }

    private Long createAndPersistTimeElapsedMessage(Long authorId, Long caseId, Long timeElapsed, En_TimeElapsedType timeElapsedType) {
        CaseComment stateChangeMessage = new CaseComment();
        stateChangeMessage.setAuthorId(authorId);
        stateChangeMessage.setCreated(new Date());
        stateChangeMessage.setCaseId(caseId);
        stateChangeMessage.setTimeElapsed(timeElapsed);
        stateChangeMessage.setTimeElapsedType(timeElapsedType != null ? timeElapsedType : En_TimeElapsedType.NONE);
        stateChangeMessage.setText(CrmConstants.Comment.TIME_ELAPSED_DEFAULT_COMMENT);
        stateChangeMessage.setPrivacyType(En_CaseCommentPrivacyType.PRIVATE);
        return caseCommentDAO.persist(stateChangeMessage);
    }

    private Long createAndPersistAutoCloseMessage(Long caseId, String message) {
        ResourceBundle langRu = ResourceBundle.getBundle("Lang", new Locale( "ru", "RU"));
        CaseComment autoCloseComment = new CaseComment(message);
        autoCloseComment.setCreated( new Date() );
        autoCloseComment.setAuthorId(portalConfig.data().getCommonConfig().getSystemUserId());
        autoCloseComment.setCaseId(caseId);
        autoCloseComment.setText(langRu.getString(message));
        autoCloseComment.setPrivacyType( En_CaseCommentPrivacyType.PUBLIC );
        return caseCommentDAO.persist(autoCloseComment);
    }

    private CaseQuery applyFilterByScope(AuthToken token, CaseQuery caseQuery) {
        Set<UserRole> roles = token.getRoles();
        if (policyService.hasGrantAccessFor(roles, En_Privilege.ISSUE_VIEW)) {
            return caseQuery;
        }

        Company company = companyService.getCompanyOmitPrivileges(token, token.getCompanyId()).getData();
        if (company.getCategory() == En_CompanyCategory.SUBCONTRACTOR) {
            caseQuery.setManagerCompanyIds(
                    acceptAllowedCompanies(caseQuery.getManagerCompanyIds(), token.getCompanyAndChildIds()));
        } else {
            caseQuery.setCompanyIds(
                    acceptAllowedCompanies(caseQuery.getCompanyIds(), token.getCompanyAndChildIds()));
        }
        caseQuery.setAllowViewPrivate(false);
        caseQuery.setCustomerSearch(true);

        log.info("applyFilterByScope(): CaseQuery modified: {}", caseQuery);
        return caseQuery;
    }

    private List<Long> acceptAllowedCompanies( List<Long> companyIds, Collection<Long> allowedCompaniesIds ) {
        if( companyIds == null ) return new ArrayList<>( allowedCompaniesIds );
        ArrayList<Long> allowedCompanies = new ArrayList<>( companyIds );
        allowedCompanies.retainAll( allowedCompaniesIds );
        return allowedCompanies.isEmpty() ? new ArrayList<>( allowedCompaniesIds ) : allowedCompanies;
    }

    private boolean isCaseMetaChanged(CaseObjectMeta co1, CaseObjectMeta co2){
        // without state
        // without imp level
        // without manager
        return     !Objects.equals(co1.getInitiatorCompanyId(), co2.getInitiatorCompanyId())
                || !Objects.equals(co1.getInitiatorId(), co2.getInitiatorId())
                || !Objects.equals(co1.getProductId(), co2.getProductId())
                || !Objects.equals(co1.getStateId(), co2.getStateId())
                || !Objects.equals(co1.getPauseDate(), co2.getPauseDate())
                || !Objects.equals(co1.getImpLevel(), co2.getImpLevel())
                || !Objects.equals(co1.getManagerCompanyId(), co2.getManagerCompanyId())
                || !Objects.equals(co1.getManagerId(), co2.getManagerId())
                || !Objects.equals(co1.getPlatformId(), co2.getPlatformId())
                || !Objects.equals(co1.getAutoClose(), co2.getAutoClose())
                || !Objects.equals(co1.getDeadline(), co2.getDeadline())
                || !Objects.equals(co1.getWorkTrigger(), co2.getWorkTrigger());
    }

    private Result<CaseObject> fillCaseObjectByScope(AuthToken token, CaseObject caseObject) {
        Set< UserRole > roles = token.getRoles();
        if (policyService.hasGrantAccessFor(roles, En_Privilege.ISSUE_CREATE)) {
            return ok(caseObject);
        }

        caseObject.setPrivateCase(false);

        Company company = companyService.getCompanyOmitPrivileges(token, token.getCompanyId()).getData();

        List<Long> initiatorAllowedCompanies = new ArrayList<>();
        if (company.getCategory() == En_CompanyCategory.SUBCONTRACTOR) {
            Result<List<EntityOption>> result = companyService.companyOptionListBySubcontractorIds(token, token.getCompanyAndChildIds(), true);
            if (result.isError()) {
                log.error("fillCaseObjectByScope(): failed to get companies by subcontractors with result {}", result);
                return error(result.getStatus());
            }
            initiatorAllowedCompanies.addAll(result.getData().stream().map(EntityOption::getId).collect(Collectors.toList()));
        } else {
            initiatorAllowedCompanies.addAll(token.getCompanyAndChildIds());
        }
        if(!initiatorAllowedCompanies.contains(caseObject.getInitiatorCompanyId())) {
            caseObject.setInitiatorCompany(company);
            caseObject.setInitiatorId(null);
            log.info("fillCaseObjectByScope(): CaseObject modified: {}", caseObject);
        }

        List<Long> managerAllowedCompanies = new ArrayList<>();
        if (company.getCategory() == En_CompanyCategory.SUBCONTRACTOR) {
            managerAllowedCompanies.addAll(token.getCompanyAndChildIds());
        } else {
            Result<List<EntityOption>> result = companyService.subcontractorOptionListByCompanyIds(token, token.getCompanyAndChildIds(), true);
            if (result.isError()) {
                log.error("fillCaseObjectByScope(): failed to get subcontractors by companies with result {}", result);
                return error(result.getStatus());
            }
            managerAllowedCompanies.addAll(result.getData().stream().map(EntityOption::getId).collect(Collectors.toList()));
        }
        if(!managerAllowedCompanies.contains(caseObject.getManagerCompanyId())) {
            caseObject.setManagerCompanyId(CrmConstants.Company.HOME_COMPANY_ID);
            caseObject.setManagerId(null);
            log.info("fillCaseObjectByScope(): CaseObject modified: {}", caseObject);
        }

        return ok(caseObject);
    }

    private boolean hasAccessForCaseObject( AuthToken token, En_Privilege privilege, CaseObject caseObject ) {
        return policyService.hasAccessForCaseObject( token, privilege, caseObject );
    }

    private boolean isStateReopenNotAllowed(CaseObjectMeta oldMeta, CaseObjectMeta newMeta) {
        return isTerminalState(oldMeta.getStateId()) &&
              !isTerminalState(newMeta.getStateId());
    }

    private boolean isParentStateNotAllowed(Long stateId) {
        return isTerminalState(stateId) ||
                CrmConstants.State.CREATED == stateId;
    }

    private boolean personBelongsToHomeCompany(AuthToken token) {
        if (token == null || token.getCompanyId() == null) {
            return false;
        }

        Result<Company> result = companyService.getCompanyOmitPrivileges(token, token.getCompanyId());
        if (result.isError()) {
            return false;
        }

        Company company = result.getData();
        if (company == null || company.getCategory() == null) {
            return false;
        }

        return (En_CompanyCategory.HOME == company.getCategory());
    }

    private void applyStateBasedOnManager(CaseObject caseObject) {
        CaseObjectMeta caseMeta = new CaseObjectMeta(caseObject);
        applyStateBasedOnManager(caseMeta);
        caseObject.setStateId(caseMeta.getStateId());
    }

    private void applyStateBasedOnManager(CaseObjectMeta caseMeta) {
        if (CrmConstants.State.CREATED == caseMeta.getStateId() && caseMeta.getManagerId() != null) {
            caseMeta.setStateId(CrmConstants.State.OPENED);
        }
    }

    private boolean isStateTransitionValid(En_CaseStateWorkflow workflow, long caseStateFromId, long caseStateToId) {
        if (caseStateFromId == caseStateToId) {
            return true;
        }
        Result<CaseStateWorkflow> response = caseStateWorkflowService.getWorkflow(workflow);
        if (response.isError()) {
            log.error("Failed to get case state workflow, status={}", response.getStatus());
            return false;
        }
        return CaseStateWorkflowUtil.isCaseStateTransitionValid(response.getData(), caseStateFromId, caseStateToId);
    }

    private boolean isStateTerminalValid(long caseObjectId) {
        List<CaseLink> caseLinks = caseLinkDAO.getListByQuery(new CaseLinkQuery(caseObjectId, En_BundleType.PARENT_FOR));

        return caseLinks.stream()
                .allMatch(caseLink -> isTerminalState(caseLink.getCaseInfo().getStateId()));
    }

    private En_IssueValidationResult validateFieldsOfNew(AuthToken token, CaseObject caseObject) {
        En_IssueValidationResult result = validateFields(caseObject);
        if (En_IssueValidationResult.OK != result) {
            return result;
        }
        CaseObjectMeta caseObjectMeta = new CaseObjectMeta( caseObject );
        return validateMetaFields(token, caseObjectMeta);
    }

    private En_IssueValidationResult validateFields(CaseObject caseObject) {
        if (caseObject == null) {
            log.warn("Case object cannot be null");
            return En_IssueValidationResult.NULL;
        }
        if (StringUtils.isEmpty(caseObject.getName())) {
            log.warn("Name must be specified. caseId={}", caseObject.getId());
            return En_IssueValidationResult.NAME_EMPTY;
        }
        if (caseObject.getType() == null) {
            log.warn("Type must be specified. caseId={}", caseObject.getId());
            return En_IssueValidationResult.TYPE_EMPTY;
        }
        if (caseObject.getCreatorId() == null) {
            log.warn("Required creator id. caseId={}", caseObject.getId());
            return En_IssueValidationResult.CREATOR_EMPTY;
        }
        return En_IssueValidationResult.OK;
    }

    private En_IssueValidationResult validateMetaFields(AuthToken token, CaseObjectMeta caseMeta) {
        return validateMetaFields(token, null, caseMeta);
    }

    private En_IssueValidationResult validateMetaFields(AuthToken token, CaseObjectMeta oldCaseMeta, CaseObjectMeta caseMeta) {
        if (caseMeta == null) {
            log.warn("Case meta cannot be null");
            return En_IssueValidationResult.NULL;
        }
        if (caseMeta.getImpLevel() == null) {
            log.warn("Importance level must be specified. caseId={}", caseMeta.getId());
            return En_IssueValidationResult.IMPORTANCE_EMPTY;
        }
        if (caseMeta.getManagerCompanyId() == null) {
            log.warn("Manager company must be specified. caseId={}", caseMeta.getId());
            return En_IssueValidationResult.MANAGER_EMPTY;
        }
        if (caseMeta.getManagerId() != null && !personBelongsToCompany(caseMeta.getManagerId(), caseMeta.getManagerCompanyId())) {
            log.warn("Manager doesn't belong to company. caseId={}, managerId={}, managerCompanyId={}",
                    caseMeta.getId(), caseMeta.getManagerId(), caseMeta.getManagerCompanyId());
            return En_IssueValidationResult.MANAGER_OTHER_COMPANY;
        }
        if (caseMeta.getManagerId() != null && caseMeta.getProductId() == null) {
            log.warn("Manager must be specified with product. caseId={}", caseMeta.getId());
            return En_IssueValidationResult.MANAGER_WITHOUT_PRODUCT;
        }
        if (caseMeta.getInitiatorCompanyId() == null) {
            log.warn("Initiator company must be specified. caseId={}", caseMeta.getId());
            return En_IssueValidationResult.INITIATOR_EMPTY;
        }
        if (!isStateValid(caseMeta.getStateId(), caseMeta.getManagerId(), caseMeta.getInitiatorCompanyId(), caseMeta.getPauseDate())) {
            log.warn("State is not valid. caseId={}", caseMeta.getId());
            return En_IssueValidationResult.STATUS_INVALID;
        }
        if (!importanceBelongsToCompany(caseMeta.getImpLevel(), caseMeta.getInitiatorCompanyId())) {
            log.warn("Importance level doesn't belong to company. caseId={}, importance={}, companyId={}", caseMeta.getId(), caseMeta.getImpLevel(), caseMeta.getInitiatorCompanyId());
            return En_IssueValidationResult.IMPORTANCE_OTHER_COMPANY;
        }
        if (caseMeta.getInitiatorId() != null && !personBelongsToCompany( caseMeta.getInitiatorId(), caseMeta.getInitiatorCompanyId() )) {
            log.warn("Initiator doesn't belong to company. caseId={}, initiatorId={}, initiatorCompanyId={}",
                    caseMeta.getId(), caseMeta.getInitiatorId(), caseMeta.getInitiatorCompanyId());
            return En_IssueValidationResult.INITIATOR_OTHER_COMPANY;
        }
        if (caseMeta.getPlatformId() != null && !platformBelongsToCompany(token, caseMeta.getPlatformId(), caseMeta.getInitiatorCompanyId())) {
            log.warn("Platform doesn't belong to initiator company. caseId={}, platformId={}, initiatorCompanyId={}",
                    caseMeta.getId(), caseMeta.getPlatformId(), caseMeta.getInitiatorCompanyId());
            return En_IssueValidationResult.PLATFORM_OTHER_COMPANY;
        }
        if (!isProductValid(token, caseMeta.getProductId(), caseMeta.getPlatformId(), caseMeta.getInitiatorCompanyId())) {
            log.warn("Product is not valid. caseId={}", caseMeta.getId());
            return En_IssueValidationResult.PRODUCT_INVALID;
        }

        Long oldDeadline = oldCaseMeta == null ? null : oldCaseMeta.getDeadline();

        if (!Objects.equals(oldDeadline, caseMeta.getDeadline()) && !isDeadlineValid(caseMeta.getDeadline())) {
            log.warn("Deadline has passed. caseId={}", caseMeta.getId());
            return En_IssueValidationResult.DEADLINE_PASSED;
        }

        if (caseMeta.getAutoClose() && !isDeadLineValidOnAutoClose(caseMeta.getDeadline())) {
            log.warn("A valid deadline must be specified on auto close. caseId={}", caseMeta.getId());
            return En_IssueValidationResult.AUTO_CLOSE_DEADLINE_INVALID;
        }

        return En_IssueValidationResult.OK;
    }

    private boolean importanceBelongsToCompany(Integer importanceLevelId, Long companyId) {
        return stream(companyImportanceItemDAO.getSortedImportanceLevels(companyId))
                .anyMatch(companyImportanceItem -> importanceLevelId.equals(companyImportanceItem.getImportanceLevelId()));
    }

    private boolean isProductValid(AuthToken token, Long productId, Long platformId, Long companyId) {
        Company company = companyDAO.get(companyId);

        if (!Boolean.TRUE.equals(company.getAutoOpenIssue())) {
            return true;
        }

        if (productId == null) {
            log.warn("Company with auto open issues must be specified with product");
            return false;
        }

        if (!isProductContainsInPlatformsProducts(token, productId, platformId, companyId)) {
            if (platformId != null) {
                log.warn("Product must be present in specified platform. platformId={}", platformId);
            } else {
                log.warn("Product must be present at least in one company platform. companyId={}", companyId);
            }
            return false;
        }

        return true;
    }

    private boolean isProductContainsInPlatformsProducts(AuthToken token, Long productId, Long platformId, Long companyId) {
        ProductQuery productQuery = new ProductQuery();
        productQuery.setState(En_DevUnitState.ACTIVE);
        productQuery.setTypes(new HashSet<>(Arrays.asList(En_DevUnitType.COMPLEX, En_DevUnitType.PRODUCT)));

        Set<Long> platformIds = new HashSet<>();

        if (platformId != null) {
            platformIds.add(platformId);
        } else {
            PlatformQuery platformQuery = new PlatformQuery();
            platformQuery.setCompanyId(companyId);

            Result<List<PlatformOption>> platformsResult = siteFolderService.listPlatformsOptionList(token, platformQuery);

            platformIds.addAll(toSet(emptyIfNull(platformsResult.getData()), PlatformOption::getId));
        }

        if (platformIds.isEmpty()) {
            return false;
        }

        productQuery.setPlatformIds(platformIds);

        Result<List<ProductShortView>> productsResult = productService.productsShortViewListWithChildren(token, productQuery);

        return toList(emptyIfNull(productsResult.getData()), ProductShortView::getId).contains(productId);
    }

    private boolean platformBelongsToCompany(AuthToken token, Long platformId, Long companyId) {
        PlatformQuery platformQuery = new PlatformQuery();
        platformQuery.setCompanyId(companyId);

        Result<List<PlatformOption>> listResult = siteFolderService.listPlatformsOptionList(token, platformQuery);

        if (isEmpty(listResult.getData())) {
            return false;
        }

        return toList(listResult.getData(), PlatformOption::getId).contains(platformId);
    }

    private boolean personBelongsToCompany(Long personId, Long companyId) {
        PersonQuery personQuery = new PersonQuery();
        personQuery.setCompanyIds(Collections.singleton(companyId));
        List<Person> persons = personDAO.getPersons( personQuery );
        log.info( "personBelongsToCompany(): companyId={} personId={} in {}", companyId, personId, toList( persons, Person::getId ) );
        return persons.stream().anyMatch( person -> personId.equals( person.getId() ) );
    }

    private boolean isStateValid(long caseStateId, Long managerId, Long initiatorCompanyId, Long pauseDate) {
        List<CaseState> crmSupportStates = caseStateDAO.getAllByCaseType(CRM_SUPPORT);

        if (stream(crmSupportStates).noneMatch(caseState -> caseState.getId().equals(caseStateId))) {
            log.warn("Not crm state");
            return false;
        }

        CaseState caseState = caseStateDAO.get(caseStateId);
        jdbcManyRelationsHelper.fillAll(caseState);

        if (caseState.getUsageInCompanies().equals(En_CaseStateUsageInCompanies.NONE)) {
            log.warn("The state must be used for some companies");
            return false;
        }

        if (caseState.getUsageInCompanies().equals(En_CaseStateUsageInCompanies.SELECTED) &&
                caseState.getCompanies().stream().noneMatch(company -> company.getId().equals(initiatorCompanyId))) {
            log.warn("The state can't be used with specified company. companyId={}", initiatorCompanyId);
            return false;
        }

        if (!(listOf(CrmConstants.State.CREATED, CrmConstants.State.CANCELED)
                .contains(caseStateId)) && managerId == null) {

            log.warn("The state must be CREATED or CANCELED without manager");
            return false;
        }

        if (CrmConstants.State.PAUSED == caseStateId) {
            boolean isPauseDateValid = pauseDate != null && (System.currentTimeMillis() < pauseDate);

            if (!isPauseDateValid) {
                log.warn("Pause date has passed");
            }

            return isPauseDateValid;
        }

        return true;
    }

    private boolean isDeadlineValid(Long date) {
        return date == null || date > System.currentTimeMillis();
    }

    private boolean isDeadLineValidOnAutoClose(Long date) {
        return date != null && date > System.currentTimeMillis();
    }

    private List<CaseLink> fillLinkedEntryInfo(List<CaseLink> caseLinks ) {
        for (CaseLink link : emptyIfNull( caseLinks )) {
            if (link.getRemoteId() == null) continue;
            if (YT.equals( link.getType() )){
                youtrackService.getIssueInfo( link.getRemoteId() )
                        .ifError(e -> log.warn( "fillLinkedEntryInfo(): YouTrack case link with id={}, caseId={}, linkType={}, remoteId={} not found! ", link.getId(), link.getCaseId(), link.getType(), link.getRemoteId()))
                        .ifOk(link::setYouTrackIssueInfo);
            } else if (UITS.equals( link.getType() )) {
                uitsService.getIssueInfo( Long.valueOf(link.getRemoteId()) )
                        .ifError(e -> log.warn( "fillLinkedEntryInfo(): UITS case link with id={}, caseId={}, linkType={}, remoteId={} not found! ", link.getId(), link.getCaseId(), link.getType(), link.getRemoteId()))
                        .ifOk(link::setUitsIssueInfo);
            }
        }
        return caseLinks;
    }

    private CaseObject withJiraSLAInformation(CaseObject caseObject) {

        if (!En_ExtAppType.JIRA.getCode().equals(caseObject.getExtAppType())) {
            return caseObject;
        }

        try {
            ExternalCaseAppData appData = externalCaseAppDAO.get(caseObject.getId());
            JiraExtAppData extAppData = JiraExtAppData.fromJSON(appData.getExtAppData());
            JiraUtils.JiraIssueData issueData = JiraUtils.convert(appData);
            JiraEndpoint endpoint = jiraEndpointDAO.get(issueData.endpointId);
            caseObject.setCaseObjectMetaJira(new CaseObjectMetaJira(
                extAppData.issueType(),
                extAppData.slaSeverity(),
                endpoint.getSlaMapId()
            ));
            caseObject.setJiraUrl(portalConfig.data().jiraConfig().getJiraUrl());
            caseObject.setJiraProjects(portalConfig.data().jiraConfig().getJiraProjects());
        } catch (Exception e) {
            log.warn("Failed to fill jira SLA information", e);
            caseObject.setCaseObjectMetaJira(new CaseObjectMetaJira());
            return caseObject;
        }

        return caseObject;
    }

    private Result<CaseObject> fillCaseObject( AuthToken token, CaseObject caseObject ) {
        if ( !hasAccessForCaseObject( token, En_Privilege.ISSUE_VIEW, caseObject ) ) {
            return error(En_ResultStatus.PERMISSION_DENIED);
        }

        if(caseObject == null)
            return error(En_ResultStatus.NOT_FOUND);

        jdbcManyRelationsHelper.fillAll( caseObject.getInitiatorCompany() );
        jdbcManyRelationsHelper.fill( caseObject, "notifiers");
        jdbcManyRelationsHelper.fill(caseObject, "plans");
        fillAttachments(token, caseObject);

        withJiraSLAInformation(caseObject);

        caseObject.setFavorite(getPersonFavoriteIssueIds(token.getPersonId()).contains(caseObject.getId()));

        // RESET PRIVACY INFO
        if ( caseObject.getInitiator() != null ) {
            caseObject.getInitiator().resetPrivacyInfo();
        }
        if ( caseObject.getCreator() != null ) {
            caseObject.getCreator().resetPrivacyInfo();
        }
        if ( isNotEmpty(caseObject.getNotifiers()) ) {
            caseObject.getNotifiers().forEach( Person::resetPrivacyInfo );
        }

        return ok(caseObject);
    }

    private void fillAttachments(AuthToken token, CaseObject caseObject) {
        jdbcManyRelationsHelper.fill(caseObject, "attachments");

        if (!policyService.hasGrantAccessFor(token.getRoles(), En_Privilege.ISSUE_VIEW)) {
            caseObject.setAttachments(stream(caseObject.getAttachments()).filter(not(Attachment::isPrivate)).collect(Collectors.toList()));
        }
    }

    private void fillCaseCreateRequest(CaseObjectCreateRequest createRequest, CaseObject parent) {

        CaseObject subtask = createRequest.getCaseObject();
        subtask.setPrivateCase(parent.isPrivateCase());
        subtask.setImpLevel(parent.getImpLevel());
        subtask.setInitiatorCompanyId(parent.getInitiatorCompanyId());
        subtask.setInitiatorId(parent.getInitiatorId());
        subtask.setProductId(parent.getProductId());
        subtask.setPlatformId(parent.getPlatformId());
        subtask.setNotifiers(setOf(Person.fromPersonShortView(parent.getManager())));
        subtask.setWorkTrigger(En_WorkTrigger.NONE);

        CaseLink caseLink = new CaseLink();
        caseLink.setType(En_CaseLink.CRM);
        caseLink.setBundleType(En_BundleType.SUBTASK);
        caseLink.setRemoteId(parent.getId().toString());
        caseLink.setWithCrosslink(true);
        createRequest.addLink(caseLink);
    }

    private boolean isIntegrationIssue(String extAppType) {
        En_ExtAppType type = En_ExtAppType.forCode(extAppType);
        if (type == null) {
            return false;
        }
        return true;
    }

    private void updateNameHistory(AuthToken token, Long caseId, String oldCaseName, String newCaseName) {
        Result<Long> resultName = ok();
        if (StringUtils.isEmpty(oldCaseName) && StringUtils.isNotEmpty(newCaseName)) {
            resultName = addNameHistory(token, caseId, newCaseName);
        } else if (StringUtils.isNotEmpty(oldCaseName) && StringUtils.isNotEmpty(newCaseName)) {
            resultName = changeNameHistory(token, caseId, oldCaseName, newCaseName);
        } else if (StringUtils.isNotEmpty(oldCaseName) && StringUtils.isEmpty(newCaseName)) {
            resultName = removeNameHistory(token, caseId, oldCaseName);
        }

        if (resultName.isError()) {
            log.error("Case name history for the issue {} isn't saved!", caseId);
        }
    }

    //описание обращения в истории будет сделано в отдельной YT задаче
    private void updateInfoHistory(AuthToken token, Long caseId, String oldCaseName, String newCaseName) {
        Result<Long> resultName = ok();
        if (StringUtils.isEmpty(oldCaseName) && StringUtils.isNotEmpty(newCaseName)) {
            resultName = addInfoHistory(token, caseId, newCaseName);
        } else if (StringUtils.isNotEmpty(oldCaseName) && StringUtils.isNotEmpty(newCaseName)) {
            resultName = changeInfoHistory(token, caseId, oldCaseName, newCaseName);
        } else if (StringUtils.isNotEmpty(oldCaseName) && StringUtils.isEmpty(newCaseName)) {
            resultName = removeInfoHistory(token, caseId, oldCaseName);
        }

        if (resultName.isError()) {
            log.error("Case info history for the issue {} isn't saved!", caseId);
        }
    }

    private void updateManagerHistory(AuthToken token, CaseObjectMeta caseMeta, CaseObjectMeta oldCaseMeta) {
        Result<Long> resultManager = ok();
        if (oldCaseMeta.getManagerId() == null && caseMeta.getManagerId() != null) {
            resultManager = addManagerHistory(token, caseMeta.getId(),
                    caseMeta.getManagerId(), makeManagerName(caseMeta));
        } else if (oldCaseMeta.getManagerId() != null && caseMeta.getManagerId() != null) {
            resultManager = changeManagerHistory(token, caseMeta.getId(),
                    oldCaseMeta.getManagerId(), makeManagerName(oldCaseMeta),
                    caseMeta.getManagerId(), makeManagerName(caseMeta));
        } else if (oldCaseMeta.getManagerId() != null && caseMeta.getManagerId() == null) {
            resultManager = removeManagerHistory(token, caseMeta.getId(),
                    oldCaseMeta.getManagerId(), makeManagerName(oldCaseMeta));
        }

        if (resultManager.isError()) {
            log.error("Manager history for the issue {} isn't saved!", caseMeta.getId());
        }
    }

    private void updatePauseDateHistory(AuthToken token, CaseObjectMeta caseMeta, CaseObjectMeta oldCaseMeta) {
        Result<Long> resultPauseDate = ok();
        if (oldCaseMeta.getPauseDate() == null && caseMeta.getPauseDate() != null) {
            resultPauseDate = addPauseDateHistory(token, caseMeta.getId(), String.valueOf(caseMeta.getPauseDate()));
        } else if (oldCaseMeta.getPauseDate() != null && caseMeta.getPauseDate() != null) {
            resultPauseDate = changePauseDateHistory(token, caseMeta.getId(),
                    String.valueOf(oldCaseMeta.getPauseDate()), String.valueOf(caseMeta.getPauseDate()));
        } else if (oldCaseMeta.getPauseDate() != null && caseMeta.getPauseDate() == null) {
            resultPauseDate = removePauseDateHistory(token, caseMeta.getId(),
                    String.valueOf(oldCaseMeta.getPauseDate()));
        }

        if (resultPauseDate.isError()) {
            log.error("Pause date history for the issue {} isn't saved!", caseMeta.getId());
        }
    }

    private void updateProductHistory(AuthToken token, CaseObjectMeta caseMeta, CaseObjectMeta oldCaseMeta) {
        Result<Long> resultProduct = ok();
        if (oldCaseMeta.getProductId() == null && caseMeta.getProductId() != null) {
            resultProduct = addProductHistory(token, caseMeta.getId(),
                    caseMeta.getProductId(), makeProductName(caseMeta));
        } else if (oldCaseMeta.getProductId() != null && caseMeta.getProductId() != null) {
            resultProduct = changeProductHistory(token, caseMeta.getId(),
                    oldCaseMeta.getProductId(), makeProductName(oldCaseMeta),
                    caseMeta.getProductId(), makeProductName(caseMeta));
        } else if (oldCaseMeta.getProductId() != null && caseMeta.getProductId() == null) {
            resultProduct = removeProductHistory(token, caseMeta.getId(),
                    oldCaseMeta.getProductId(), makeProductName(oldCaseMeta));
        }

        if (resultProduct.isError()) {
            log.error("Product history for the issue {} isn't saved!", caseMeta.getId());
        }
    }

    private void updateWorkTriggerHistory(AuthToken token, CaseObjectMeta caseMeta, CaseObjectMeta oldCaseMeta) {
        Result<Long> resultWorkTrigger = ok();
        if (oldCaseMeta.getWorkTrigger() == null && caseMeta.getWorkTrigger() != null) {
            resultWorkTrigger = addWorkTriggerHistory(token, caseMeta.getId(), (long)caseMeta.getWorkTrigger().getId(), caseMeta.getWorkTrigger().name());
        } else if (oldCaseMeta.getWorkTrigger() != null && caseMeta.getWorkTrigger() != null) {
            resultWorkTrigger = changeWorkTriggerHistory(token, caseMeta.getId(),
                    (long)oldCaseMeta.getWorkTrigger().getId(), oldCaseMeta.getWorkTrigger().name(),
                    (long)caseMeta.getWorkTrigger().getId(), caseMeta.getWorkTrigger().name() );
        } else if (oldCaseMeta.getWorkTrigger() != null && caseMeta.getWorkTrigger() == null) {
            resultWorkTrigger = removeWorkTriggerHistory(token, caseMeta.getId(),
                    (long)oldCaseMeta.getWorkTrigger().getId(), oldCaseMeta.getWorkTrigger().name());
        }

        if (resultWorkTrigger.isError()) {
            log.error("Work trigger history for the issue {} isn't saved!", caseMeta.getId());
        }
    }

    private void updateAutoCloseHistory(AuthToken token, CaseObjectMeta caseMeta, CaseObjectMeta oldCaseMeta) {
        Result<Long> resultAutoClose = ok();
        if (oldCaseMeta.getAutoClose() == null && caseMeta.getAutoClose() != null) {
            resultAutoClose = addAutoCloseHistory(token, caseMeta.getId(), String.valueOf(caseMeta.getAutoClose()));
        } else if (oldCaseMeta.getAutoClose() != null && caseMeta.getAutoClose() != null) {
            resultAutoClose = changeAutoCloseHistory(token, caseMeta.getId(),
                    String.valueOf(oldCaseMeta.getAutoClose()), String.valueOf(caseMeta.getAutoClose()));
        } else if (oldCaseMeta.getAutoClose() != null && caseMeta.getAutoClose() == null) {
            resultAutoClose = removeAutoCloseHistory(token, caseMeta.getId(),
                    String.valueOf(oldCaseMeta.getAutoClose()));
        }

        if (resultAutoClose.isError()) {
            log.error("Auto close history for the issue {} isn't saved!", caseMeta.getId());
        }
    }

    private void updateDeadlineHistory(AuthToken token, CaseObjectMeta caseMeta, CaseObjectMeta oldCaseMeta) {
        Result<Long> resultDeadline = ok();
        if (oldCaseMeta.getDeadline() == null && caseMeta.getDeadline() != null) {
            resultDeadline = addDeadlineHistory(token, caseMeta.getId(), String.valueOf(caseMeta.getDeadline()));
        } else if (oldCaseMeta.getDeadline() != null && caseMeta.getDeadline() != null) {
            resultDeadline = changeDeadlineHistory(token, caseMeta.getId(),
                    String.valueOf(oldCaseMeta.getDeadline()), String.valueOf(caseMeta.getDeadline()));
        } else if (oldCaseMeta.getDeadline() != null && caseMeta.getDeadline() == null) {
            resultDeadline = removeDeadlineHistory(token, caseMeta.getId(),
                    String.valueOf(oldCaseMeta.getDeadline()));
        }

        if (resultDeadline.isError()) {
            log.error("Deadline history for the issue {} isn't saved!", caseMeta.getId());
        }
    }

    private void updatePlatformHistory(AuthToken token, CaseObjectMeta caseMeta, CaseObjectMeta oldCaseMeta) {
        Result<Long> result = ok();
        if (oldCaseMeta.getPlatformId() == null && caseMeta.getPlatformId() != null) {
            result = addPlatformHistory(token, caseMeta.getId(),
                    caseMeta.getPlatformId(), makePlatformName(caseMeta));
        } else if (oldCaseMeta.getPlatformId() != null && caseMeta.getPlatformId() != null) {
            result = changePlatformHistory(token, caseMeta.getId(),
                    oldCaseMeta.getPlatformId(), makePlatformName(oldCaseMeta),
                    caseMeta.getPlatformId(), makePlatformName(caseMeta));
        } else if (oldCaseMeta.getPlatformId() != null && caseMeta.getPlatformId() == null) {
            result = removePlatformHistory(token, caseMeta.getId(),
                    oldCaseMeta.getPlatformId(), makePlatformName(oldCaseMeta));
        }

        if (result.isError()) {
            log.error("Platform history for the issue {} isn't saved!", caseMeta.getId());
        }
    }

    private void updateInitiatorHistory(AuthToken token, CaseObjectMeta caseMeta, CaseObjectMeta oldCaseMeta) {
        Result<Long> result = ok();
        if (oldCaseMeta.getInitiatorId() == null && caseMeta.getInitiatorId() != null) {
            result = addInitiatorHistory(token, caseMeta.getId(),
                    caseMeta.getInitiatorId(), makeInitiatorName(caseMeta));
        } else if (oldCaseMeta.getInitiatorId() != null && caseMeta.getInitiatorId() != null) {
            result = changeInitiatorHistory(token, caseMeta.getId(),
                    oldCaseMeta.getInitiatorId(), makeInitiatorName(oldCaseMeta),
                    caseMeta.getInitiatorId(), makeInitiatorName(caseMeta));
        } else if (oldCaseMeta.getInitiatorId() != null && caseMeta.getInitiatorId() == null) {
            result = removeInitiatorHistory(token, caseMeta.getId(),
                    oldCaseMeta.getInitiatorId(), makeInitiatorName(oldCaseMeta));
        }

        if (result.isError()) {
            log.error("Initiator history for the issue {} isn't saved!", caseMeta.getId());
        }
    }

    private void updateInitiatorCompanyHistory(AuthToken token, CaseObjectMeta caseMeta, CaseObjectMeta oldCaseMeta) {
        Result<Long> resultCompany = ok();
        if (oldCaseMeta.getInitiatorCompanyId() == null && caseMeta.getInitiatorCompanyId() != null) {
            resultCompany = addInitiatorCompanyHistory(token, caseMeta.getId(),
                    caseMeta.getInitiatorCompanyId(), makeInitiatorCompanyName(caseMeta.getInitiatorCompany(), caseMeta.getInitiatorCompanyId()));
        } else if (oldCaseMeta.getInitiatorCompanyId() != null && caseMeta.getInitiatorCompanyId() != null) {
            resultCompany = changeInitiatorCompanyHistory(token, caseMeta.getId(),
                    oldCaseMeta.getInitiatorCompanyId(), makeInitiatorCompanyName(oldCaseMeta.getInitiatorCompany(), oldCaseMeta.getInitiatorCompanyId()),
                    caseMeta.getInitiatorCompanyId(), makeInitiatorCompanyName(caseMeta.getInitiatorCompany(), caseMeta.getInitiatorCompanyId()));
        } else if (oldCaseMeta.getInitiatorCompanyId() != null && caseMeta.getInitiatorCompanyId() == null) {
            resultCompany = removeInitiatorCompanyHistory(token, caseMeta.getId(),
                    oldCaseMeta.getInitiatorCompanyId(), makeInitiatorCompanyName(oldCaseMeta.getInitiatorCompany(), oldCaseMeta.getInitiatorCompanyId()));
        }

        if (resultCompany.isError()) {
            log.error("Initiator company history for the issue {} isn't saved!", caseMeta.getId());
        }
    }

    private void updateManagerCompanyHistory(AuthToken token, CaseObjectMeta caseMeta, CaseObjectMeta oldCaseMeta) {
        Result<Long> resultCompany = ok();
        if (oldCaseMeta.getManagerCompanyId() == null && caseMeta.getManagerCompanyId() != null) {
            resultCompany = addManagerCompanyHistory(token, caseMeta.getId(),
                    caseMeta.getManagerCompanyId(), makeManagerCompanyName(caseMeta.getManagerCompanyName(), caseMeta.getManagerCompanyId()));
        } else if (oldCaseMeta.getManagerCompanyId() != null && caseMeta.getManagerCompanyId() != null) {
            resultCompany = changeManagerCompanyHistory(token, caseMeta.getId(),
                    oldCaseMeta.getManagerCompanyId(), makeManagerCompanyName(oldCaseMeta.getManagerCompanyName(), oldCaseMeta.getManagerCompanyId()),
                    caseMeta.getManagerCompanyId(), makeManagerCompanyName(caseMeta.getManagerCompanyName(), caseMeta.getManagerCompanyId()));
        } else if (oldCaseMeta.getManagerCompanyId() != null && caseMeta.getManagerCompanyId() == null) {
            resultCompany = removeManagerCompanyHistory(token, caseMeta.getId(),
                    oldCaseMeta.getManagerCompanyId(), makeManagerCompanyName(oldCaseMeta.getManagerCompanyName(), oldCaseMeta.getManagerCompanyId()));
        }

        if (resultCompany.isError()) {
            log.error("Manager company history for the issue {} isn't saved!", caseMeta.getId());
        }
    }

    private Result<Long> addStateHistory(AuthToken authToken, Long caseId, Long stateId, String stateName) {
        return historyService.createHistory(authToken, caseId, En_HistoryAction.ADD, En_HistoryType.CASE_STATE, null, null, stateId, stateName);
    }

    private Result<Long> changeStateHistory(AuthToken authToken, Long caseId, Long oldStateId, String oldStateName, Long newStateId, String newStateName) {
        return historyService.createHistory(authToken, caseId, En_HistoryAction.CHANGE, En_HistoryType.CASE_STATE, oldStateId, oldStateName, newStateId, newStateName);
    }

    private Result<Long> addPauseDateHistory(AuthToken authToken, Long caseId, String pauseDate) {
        return historyService.createHistory(authToken, caseId, En_HistoryAction.ADD, En_HistoryType.CASE_PAUSE_DATE,null, null, null, pauseDate);
    }

    private Result<Long> changePauseDateHistory(AuthToken authToken, Long caseId, String oldPauseDate, String newPauseDate) {
        return historyService.createHistory(authToken, caseId, En_HistoryAction.CHANGE, En_HistoryType.CASE_PAUSE_DATE, null, oldPauseDate, null, newPauseDate);
    }

    private Result<Long> removePauseDateHistory(AuthToken authToken, Long caseId, String oldPauseDate) {
        return historyService.createHistory(authToken, caseId, En_HistoryAction.REMOVE, En_HistoryType.CASE_PAUSE_DATE, null, oldPauseDate, null, null);
    }

    private Result<Long> addWorkTriggerHistory(AuthToken authToken, Long caseId, Long workTriggerId, String workTriggerName) {
        return historyService.createHistory(authToken, caseId, En_HistoryAction.ADD, En_HistoryType.CASE_WORK_TRIGGER,null, null, workTriggerId, workTriggerName);
    }

    private Result<Long> changeWorkTriggerHistory(AuthToken authToken, Long caseId, Long oldWorkTriggerId, String oldWorkTriggerName, Long newWorkTriggerId, String newWorkTriggerName) {
        return historyService.createHistory(authToken, caseId, En_HistoryAction.CHANGE, En_HistoryType.CASE_WORK_TRIGGER, oldWorkTriggerId, oldWorkTriggerName, newWorkTriggerId, newWorkTriggerName);
    }

    private Result<Long> removeWorkTriggerHistory(AuthToken authToken, Long caseId, Long oldWorkTriggerId, String oldWorkTriggerName) {
        return historyService.createHistory(authToken, caseId, En_HistoryAction.REMOVE, En_HistoryType.CASE_WORK_TRIGGER, oldWorkTriggerId, oldWorkTriggerName, null, null);
    }

    private Result<Long> addAutoCloseHistory(AuthToken authToken, Long caseId, String autoClose) {
        return historyService.createHistory(authToken, caseId, En_HistoryAction.ADD, En_HistoryType.CASE_AUTO_CLOSE,null, null, null, autoClose);
    }

    private Result<Long> changeAutoCloseHistory(AuthToken authToken, Long caseId, String oldAutoClose, String newAutoClose) {
        return historyService.createHistory(authToken, caseId, En_HistoryAction.CHANGE, En_HistoryType.CASE_AUTO_CLOSE, null, oldAutoClose, null, newAutoClose);
    }

    private Result<Long> removeAutoCloseHistory(AuthToken authToken, Long caseId, String oldAutoClose) {
        return historyService.createHistory(authToken, caseId, En_HistoryAction.REMOVE, En_HistoryType.CASE_AUTO_CLOSE, null, oldAutoClose, null, null);
    }

    private Result<Long> addDeadlineHistory(AuthToken authToken, Long caseId, String deadline) {
        return historyService.createHistory(authToken, caseId, En_HistoryAction.ADD, En_HistoryType.CASE_DEADLINE,null, null, null, deadline);
    }

    private Result<Long> changeDeadlineHistory(AuthToken authToken, Long caseId, String oldDeadline, String newDeadline) {
        return historyService.createHistory(authToken, caseId, En_HistoryAction.CHANGE, En_HistoryType.CASE_DEADLINE, null, oldDeadline, null, newDeadline);
    }

    private Result<Long> removeDeadlineHistory(AuthToken authToken, Long caseId, String oldDeadline) {
        return historyService.createHistory(authToken, caseId, En_HistoryAction.REMOVE, En_HistoryType.CASE_DEADLINE, null, oldDeadline, null, null);
    }

    private Result<Long> addManagerCompanyHistory(AuthToken authToken, Long caseId, Long companyId, String companyName) {
        return historyService.createHistory(authToken, caseId, En_HistoryAction.ADD, En_HistoryType.CASE_MANAGER_COMPANY,null, null, companyId, companyName);
    }

    private Result<Long> changeManagerCompanyHistory(AuthToken authToken, Long caseId, Long oldCompanyId, String oldCompanyName, Long newCompanyId, String newCompanyName) {
        return historyService.createHistory(authToken, caseId, En_HistoryAction.CHANGE, En_HistoryType.CASE_MANAGER_COMPANY, oldCompanyId, oldCompanyName, newCompanyId, newCompanyName);
    }

    private Result<Long> removeManagerCompanyHistory(AuthToken authToken, Long caseId, Long oldCompanyId, String oldCompanyName) {
        return historyService.createHistory(authToken, caseId, En_HistoryAction.REMOVE, En_HistoryType.CASE_MANAGER_COMPANY, oldCompanyId, oldCompanyName, null, null);
    }

    private Result<Long> addInitiatorCompanyHistory(AuthToken authToken, Long caseId, Long companyId, String companyName) {
        return historyService.createHistory(authToken, caseId, En_HistoryAction.ADD, En_HistoryType.CASE_INITIATOR_COMPANY,null, null, companyId, companyName);
    }

    private Result<Long> changeInitiatorCompanyHistory(AuthToken authToken, Long caseId, Long oldCompanyId, String oldCompanyName, Long newCompanyId, String newCompanyName) {
        return historyService.createHistory(authToken, caseId, En_HistoryAction.CHANGE, En_HistoryType.CASE_INITIATOR_COMPANY, oldCompanyId, oldCompanyName, newCompanyId, newCompanyName);
    }

    private Result<Long> removeInitiatorCompanyHistory(AuthToken authToken, Long caseId, Long oldCompanyId, String oldCompanyName) {
        return historyService.createHistory(authToken, caseId, En_HistoryAction.REMOVE, En_HistoryType.CASE_INITIATOR_COMPANY, oldCompanyId, oldCompanyName, null, null);
    }

    private Result<Long> addInitiatorHistory(AuthToken authToken, Long caseId, Long initiatorId, String initiatorName) {
        return historyService.createHistory(authToken, caseId, En_HistoryAction.ADD, En_HistoryType.CASE_INITIATOR,null, null, initiatorId, initiatorName);
    }

    private Result<Long> changeInitiatorHistory(AuthToken authToken, Long caseId, Long oldInitiatorId, String oldInitiatorName, Long newInitiatorId, String newInitiatorName) {
        return historyService.createHistory(authToken, caseId, En_HistoryAction.CHANGE, En_HistoryType.CASE_INITIATOR, oldInitiatorId, oldInitiatorName, newInitiatorId, newInitiatorName);
    }

    private Result<Long> removeInitiatorHistory(AuthToken authToken, Long caseId, Long oldInitiatorId, String oldInitiatorName) {
        return historyService.createHistory(authToken, caseId, En_HistoryAction.REMOVE, En_HistoryType.CASE_INITIATOR, oldInitiatorId, oldInitiatorName, null, null);
    }

    private Result<Long> addPlatformHistory(AuthToken authToken, Long caseId, Long platformId, String platformName) {
        return historyService.createHistory(authToken, caseId, En_HistoryAction.ADD, En_HistoryType.CASE_PLATFORM,null, null, platformId, platformName);
    }

    private Result<Long> changePlatformHistory(AuthToken authToken, Long caseId, Long oldPlatformId, String oldPlatformName, Long newPlatformId, String newPlatformName) {
        return historyService.createHistory(authToken, caseId, En_HistoryAction.CHANGE, En_HistoryType.CASE_PLATFORM, oldPlatformId, oldPlatformName, newPlatformId, newPlatformName);
    }

    private Result<Long> removePlatformHistory(AuthToken authToken, Long caseId, Long oldPlatformId, String oldPlatformName) {
        return historyService.createHistory(authToken, caseId, En_HistoryAction.REMOVE, En_HistoryType.CASE_PLATFORM, oldPlatformId, oldPlatformName, null, null);
    }

    private Result<Long> addProductHistory(AuthToken authToken, Long caseId, Long productId, String productName) {
        return historyService.createHistory(authToken, caseId, En_HistoryAction.ADD, En_HistoryType.CASE_PRODUCT,null, null, productId, productName);
    }

    private Result<Long> changeProductHistory(AuthToken authToken, Long caseId, Long oldProductId, String oldProductName, Long newProductId, String newProductName) {
        return historyService.createHistory(authToken, caseId, En_HistoryAction.CHANGE, En_HistoryType.CASE_PRODUCT, oldProductId, oldProductName, newProductId, newProductName);
    }

    private Result<Long> removeProductHistory(AuthToken authToken, Long caseId, Long oldProductId, String oldProductName) {
        return historyService.createHistory(authToken, caseId, En_HistoryAction.REMOVE, En_HistoryType.CASE_PRODUCT, oldProductId, oldProductName, null, null);
    }

    private Result<Long> addNameHistory(AuthToken authToken, Long caseId, String caseName) {
        return historyService.createHistory(authToken, caseId, En_HistoryAction.ADD, En_HistoryType.CASE_NAME,null, null, null, caseName);
    }

    private Result<Long> changeNameHistory(AuthToken authToken, Long caseId, String oldCaseName, String newCaseName) {
        return historyService.createHistory(authToken, caseId, En_HistoryAction.CHANGE, En_HistoryType.CASE_NAME, null, oldCaseName, null, newCaseName);
    }

    private Result<Long> removeNameHistory(AuthToken authToken, Long caseId, String oldCaseName) {
        return historyService.createHistory(authToken, caseId, En_HistoryAction.REMOVE, En_HistoryType.CASE_NAME, null, oldCaseName, null, null);
    }

    private Result<Long> addInfoHistory(AuthToken authToken, Long caseId, String caseInfo) {
        return historyService.createHistory(authToken, caseId, En_HistoryAction.ADD, En_HistoryType.CASE_INFO,null, null, null, caseInfo);
    }

    private Result<Long> changeInfoHistory(AuthToken authToken, Long caseId, String oldCaseInfo, String newCaseInfo) {
        return historyService.createHistory(authToken, caseId, En_HistoryAction.CHANGE, En_HistoryType.CASE_INFO, null, oldCaseInfo, null, newCaseInfo);
    }

    private Result<Long> removeInfoHistory(AuthToken authToken, Long caseId, String oldCaseInfo) {
        return historyService.createHistory(authToken, caseId, En_HistoryAction.REMOVE, En_HistoryType.CASE_INFO, null, oldCaseInfo, null, null);
    }

    private Result<Long> addManagerHistory(AuthToken authToken, Long caseId, Long managerId, String managerName) {
        return historyService.createHistory(authToken, caseId, En_HistoryAction.ADD, En_HistoryType.CASE_MANAGER,null, null, managerId, managerName);
    }

    private Result<Long> changeManagerHistory(AuthToken authToken, Long caseId, Long oldManagerId, String oldManagerName, Long newManagerId, String newManagerName) {
        return historyService.createHistory(authToken, caseId, En_HistoryAction.CHANGE, En_HistoryType.CASE_MANAGER, oldManagerId, oldManagerName, newManagerId, newManagerName);
    }

    private Result<Long> removeManagerHistory(AuthToken authToken, Long caseId, Long oldManagerId, String oldManagerName) {
        return historyService.createHistory(authToken, caseId, En_HistoryAction.REMOVE, En_HistoryType.CASE_MANAGER, oldManagerId, oldManagerName, null, null);
    }

    private Result<Long> addImportanceHistory(AuthToken authToken, Long caseId, Long importanceId, String importanceName) {
        return historyService.createHistory(authToken, caseId, En_HistoryAction.ADD, En_HistoryType.CASE_IMPORTANCE, null, null, importanceId, importanceName);
    }

    private Result<Long> changeImportanceHistory(AuthToken authToken, Long caseId, Long oldImportanceId, String oldImportanceName, Long newImportanceId, String newImportanceName) {
        return historyService.createHistory(authToken, caseId, En_HistoryAction.CHANGE, En_HistoryType.CASE_IMPORTANCE, oldImportanceId, oldImportanceName, newImportanceId, newImportanceName);
    }

    private String makeManagerName(CaseObjectMeta meta) {
        if (meta.getManager() == null){
            return personShortViewDAO.get(meta.getManagerId()).getDisplayShortName();
        }
        return meta.getManager().getDisplayShortName();
    }

    private String makeManagerName(CaseObject caseObject) {
        if (caseObject.getManager().getDisplayShortName() == null){
            return personShortViewDAO.get(caseObject.getManagerId()).getDisplayShortName();
        }
        return caseObject.getManager().getDisplayShortName();
    }

    private String makeInitiatorName(CaseObjectMeta meta) {
        if (meta.getInitiator() == null || meta.getInitiator().getDisplayShortName() == null){
            return personShortViewDAO.get(meta.getInitiatorId()).getDisplayShortName();
        }
        return meta.getInitiator().getDisplayShortName();
    }

    private String makeInitiatorName(CaseObject caseObject) {
        if (caseObject.getInitiator() == null || caseObject.getInitiator().getDisplayShortName() == null){
            return personShortViewDAO.get(caseObject.getInitiatorId()).getDisplayShortName();
        }
        return caseObject.getInitiator().getDisplayShortName();
    }

    private String makeProductName(CaseObjectMeta meta) {
        if (meta.getProduct() == null){
            return devUnitDAO.get(meta.getProductId()).getName();
        }
        return meta.getProduct().getName();
    }

    private String makeProductName(CaseObject caseObject) {
        if (caseObject.getProduct() == null || caseObject.getProduct().getName() == null){
            return devUnitDAO.get(caseObject.getProductId()).getName();
        }
        return caseObject.getProduct().getName();
    }

    private String makeManagerCompanyName(String companyName, Long companyId) {
        if (companyName == null){
            return companyDAO.get(companyId).getCname();
        }
        return companyName;
    }

    private String makeInitiatorCompanyName(Company company, Long companyId) {
        if (company == null) {
            return companyDAO.get(companyId).getCname();
        }
        return company.getCname();
    }

    private String makePlatformName(CaseObjectMeta meta) {
        if (meta.getPlatformName() == null) {
            return platformDAO.get(meta.getPlatformId()).getName();
        }
        return meta.getPlatformName();
    }

    private String makePlatformName(CaseObject caseObject) {
        if (caseObject.getPlatformName() == null) {
            return platformDAO.get(caseObject.getPlatformId()).getName();
        }
        return caseObject.getPlatformName();
    }
}
