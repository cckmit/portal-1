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
import ru.protei.portal.core.exception.ResultStatusException;
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
import ru.protei.portal.core.model.util.CaseStateWorkflowUtil;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.util.DiffCollectionResult;
import ru.protei.portal.core.model.util.DiffResult;
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
import static ru.protei.portal.core.model.dict.En_CaseLink.YT;
import static ru.protei.portal.core.model.dict.En_CaseType.CRM_SUPPORT;
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

    @Override
    public Result<SearchResult<CaseShortView>> getCaseObjects(AuthToken token, CaseQuery query) {

        applyFilterByScope(token, query);

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

        if (!validateFieldsOfNew(token, caseObject)) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        applyCaseByScope( token, caseObject );
        if ( !hasAccessForCaseObject( token, En_Privilege.ISSUE_EDIT, caseObject ) ) {
            return error(En_ResultStatus.PERMISSION_DENIED );
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
            caseObject.setTimeElapsed(null);
        }

        applyStateBasedOnManager(caseObject);

        Long caseId = caseObjectDAO.insertCase(caseObject);

        if (caseId == null)
            return error(En_ResultStatus.NOT_CREATED);
        else
            caseObject.setId(caseId);

        Long stateMessageId = createAndPersistStateMessage(token.getPersonId(), caseId, caseObject.getStateId());
        if (stateMessageId == null) {
            log.error("State message for the issue {} not saved!", caseId);
        }

        Long impMessageId = createAndPersistImportanceMessage(token.getPersonId(), caseId, caseObject.getImpLevel());
        if (impMessageId == null) {
            log.error("Importance level message for the issue {} not saved!", caseId);
        }

        if (caseObject.getManager() != null && caseObject.getManager().getId() != null) {
            Long messageId = createAndPersistManagerMessage(token.getPersonId(), caseObject.getId(), caseObject.getManager().getId());
            if (messageId == null) {
                log.error("Manager message for the issue {} not saved!", caseObject.getId());
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
                    throw new ResultStatusException(
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

        autoOpenCaseService.processNewCreatedCaseToAutoOpen(caseId, caseObject.getInitiatorCompanyId());

        // From GWT-side we get partially filled object, that's why we need to refresh state from db
        CaseObject newState = caseObjectDAO.get(caseId);
        newState.setAttachments(caseObject.getAttachments());
        newState.setNotifiers(caseObject.getNotifiers());
        CaseObjectCreateEvent caseObjectCreateEvent = new CaseObjectCreateEvent(this, ServiceModule.GENERAL, token.getPersonId(), newState);

        return new Result<>(En_ResultStatus.OK, newState, createLinksResult.getMessage(), listOf(caseObjectCreateEvent));
    }

    @Override
    @Transactional
    public Result<CaseNameAndDescriptionChangeRequest> updateCaseNameAndDescription(AuthToken token, CaseNameAndDescriptionChangeRequest changeRequest) {
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

            if(isNotEmpty(changeRequest.getAttachments())){
                caseObject.setAttachmentExists(true);
                boolean isAttachmentsExistUpdated
                        = caseObjectDAO.partialMerge(caseObject, "ATTACHMENT_EXISTS");

                if (!isAttachmentsExistUpdated) {
                    throw new ResultStatusException(En_ResultStatus.NOT_UPDATED, "Attachment exists flag was not updated");
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

            return ok(changeRequest)
                    .publishEvent( new CaseNameAndDescriptionEvent(
                    this,
                    changeRequest.getId(),
                    nameDiff,
                    infoDiff,
                    token.getPersonId(),
                    ServiceModule.GENERAL,
                    En_ExtAppType.forCode(oldCaseObject.getExtAppType())) );
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

        if (!validateMetaFields(token, caseMeta)) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        if (!isCaseMetaChanged(caseMeta, oldCaseMeta)) {
            return ok(caseMeta);
        }

        En_CaseStateWorkflow workflow = CaseStateWorkflowUtil.recognizeWorkflow(oldState.getExtAppType());
        boolean isStateTransitionValidByWorkflow = isStateTransitionValid(workflow, oldCaseMeta.getStateId(), caseMeta.getStateId());
        if (!isStateTransitionValidByWorkflow) {
            log.info("Wrong state transition for the issue {}: {} -> {}, workflow={}",
                    caseMeta.getId(), oldCaseMeta.getStateId(), caseMeta.getStateId(), workflow);
            throw new ResultStatusException(En_ResultStatus.VALIDATION_ERROR);
        }

        boolean isStateTransitionValidNoWorkflow = workflow != En_CaseStateWorkflow.NO_WORKFLOW || !isStateReopenNotAllowed(oldCaseMeta, caseMeta);
        if (!isStateTransitionValidNoWorkflow) {
            log.info("Wrong state transition for the issue {}: {} -> {}",
                    caseMeta.getId(), oldCaseMeta.getStateId(), caseMeta.getStateId());
            throw new ResultStatusException(En_ResultStatus.INVALID_CASE_UPDATE_CASE_IS_CLOSED);
        }

        boolean isStateTerminalValid = !isTerminalState(caseMeta.getStateId()) || isStateTerminalValid(caseMeta.getId());
        if (!isStateTerminalValid) {
            log.info("Impossible to terminate the issue {}: {} -> {}",
                    caseMeta.getId(), oldCaseMeta.getStateId(), caseMeta.getStateId());
            throw new ResultStatusException(En_ResultStatus.INVALID_CASE_UPDATE_SUBTASK_NOT_CLOSED);
        }

        caseMeta.setModified(new Date());
        caseMeta.setTimeElapsed(caseCommentService.getTimeElapsed(caseMeta.getId()).getData());

        boolean isUpdated = caseObjectMetaDAO.merge(caseMeta);
        if (!isUpdated) {
            log.info("Failed to update issue meta data {} at db", caseMeta.getId());
            throw new ResultStatusException(En_ResultStatus.NOT_UPDATED);
        }

        if (oldCaseMeta.getStateId() != caseMeta.getStateId()) {
            Long messageId = createAndPersistStateMessage(token.getPersonId(), caseMeta.getId(), caseMeta.getStateId());
            if (messageId == null) {
                log.error("State message for the issue {} isn't saved!", caseMeta.getId());
            }
        }

        if (!Objects.equals(oldCaseMeta.getImpLevel(), caseMeta.getImpLevel())) {
            Long messageId = createAndPersistImportanceMessage(token.getPersonId(), caseMeta.getId(), caseMeta.getImpLevel());
            if (messageId == null) {
                log.error("Importance level message for the issue {} isn't saved!", caseMeta.getId());
            }
        }

        if (!Objects.equals(oldCaseMeta.getManagerId(), caseMeta.getManagerId())) {
            Long messageId = createAndPersistManagerMessage(token.getPersonId(), caseMeta.getId(), caseMeta.getManagerId());
            if (messageId == null) {
                log.error("Manager message for the issue {} isn't saved!", caseMeta.getId());
            }
        }

        Result<Long> openedParentsResult = ok(caseMeta.getId());
        if (oldCaseMeta.getStateId() != caseMeta.getStateId() && isTerminalState(caseMeta.getStateId())) {
            openedParentsResult = openParentIssuesIfAllLinksInTerminalState(token, caseMeta.getId());
            if (openedParentsResult.isError()) {
                log.error("Failed to open parent issue | message = '{}'", openedParentsResult.getMessage());
                throw new ResultStatusException(openedParentsResult.getStatus());
            }
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
    public Result<CaseObjectMetaNotifiers> updateCaseObjectMetaNotifiers(AuthToken token, CaseObjectMetaNotifiers caseMetaNotifiers) {

        if (caseMetaNotifiers.getId() == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        CaseObject oldState = caseObjectDAO.get(caseMetaNotifiers.getId());
        if (oldState == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        if (!hasAccessForCaseObject(token, En_Privilege.ISSUE_EDIT, oldState)) {
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
            throw new ResultStatusException(En_ResultStatus.NOT_UPDATED);
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
            throw new ResultStatusException(En_ResultStatus.NOT_UPDATED, "failed to update case object");
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
                .map( this::fillYouTrackInfo );
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
        caseObject.setModified(new Date());

        if (!caseObjectDAO.partialMerge(caseObject, "MODIFIED")) {
            return error(En_ResultStatus.NOT_UPDATED, "Modified column was not added");
        }

        PlanQuery planQuery = new PlanQuery();
        planQuery.setIssueId(caseId);
        planQuery.setCreatorId(token.getPersonId());

        Result<List<PlanOption>> oldPlansResult = planService.listPlanOptions(token, planQuery);

        if (oldPlansResult.isError()) {
            throw new ResultStatusException(oldPlansResult.getStatus());
        }

        En_ResultStatus resultStatus = updatePlans(token, caseId, new HashSet<>(oldPlansResult.getData()), plans);

        if (!En_ResultStatus.OK.equals(resultStatus)) {
            throw new ResultStatusException(resultStatus);
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

        if (isParentStateNotAllowed(parentCaseObject)) {
            return error(En_ResultStatus.NOT_ALLOWED_PARENT_STATE);
        }

        fillCaseCreateRequest(caseObjectCreateRequest, parentCaseObject);

        Result<CaseObject> result = createCaseObject(token, caseObjectCreateRequest);
        if (result.isError()) {
            log.error("createSubtask(): parent-id = {} | failed to save subtask to db with result = {}", parentCaseObjectId, result);
            throw new ResultStatusException(result.getStatus());
        }

        parentCaseObject.setStateId(CrmConstants.State.BLOCKED);
        Result<CaseObjectMeta> parentUpdate = updateCaseObjectMeta(token, new CaseObjectMeta(parentCaseObject));
        if (parentUpdate.isError()) {
            log.error("createSubtask(): parent-id = {} | failed to save parent issue to db with result = {}", parentCaseObjectId, result);
            throw new ResultStatusException(parentUpdate.getStatus());
        }

        return result.publishEvents(parentUpdate.getEvents());
    }

    @Override
    @Transactional
    public Result<CaseObjectMetaNotifiers> addNotifierToCaseObject(AuthToken authToken, Long caseId, PersonShortView personShortView) {
        CaseObjectMetaNotifiers caseObjectMetaNotifiers = caseObjectMetaNotifiersDAO.get(caseId);
        jdbcManyRelationsHelper.fill(caseObjectMetaNotifiers, "notifiers");

        caseObjectMetaNotifiers.getNotifiers().add(Person.fromPersonShortView(personShortView));
        return updateCaseObjectMetaNotifiers(authToken, caseObjectMetaNotifiers);
    }

    private Result<Long> openParentIssuesIfAllLinksInTerminalState(AuthToken token, long caseObjectId) {

        List<CaseLink> caseLinks = caseLinkDAO.getListByQuery(new CaseLinkQuery(caseObjectId, En_BundleType.SUBTASK));
        Result<Long> result = ok(caseObjectId);

        for(CaseLink caseLink : caseLinks) {
            Long parentId = NumberUtils.parseLong(caseLink.getRemoteId());
            boolean isAllLinksInTerminalState = isAllLinksInTerminalState(parentId);
            if (isAllLinksInTerminalState) {
                CaseObjectMeta caseObjectMeta = caseObjectMetaDAO.get(caseObjectId);
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
        stateChangeMessage.setPrivateComment(true);
        stateChangeMessage.setText(CrmConstants.Comment.TIME_ELAPSED_DEFAULT_COMMENT);
        return caseCommentDAO.persist(stateChangeMessage);
    }

    private Long createAndPersistStateMessage(Long authorId, long caseId, long stateId) {
        CaseComment stateChangeMessage = new CaseComment();
        stateChangeMessage.setAuthorId(authorId);
        stateChangeMessage.setCreated(new Date());
        stateChangeMessage.setCaseId(caseId);
        stateChangeMessage.setCaseStateId(stateId);
        return caseCommentDAO.persist(stateChangeMessage);
    }

    private Long createAndPersistImportanceMessage(Long authorId, Long caseId, Integer importance) {//int -> Integer т.к. падает unit test с NPE, неясно почему
        CaseComment stateChangeMessage = new CaseComment();
        stateChangeMessage.setAuthorId(authorId);
        stateChangeMessage.setCreated(new Date());
        stateChangeMessage.setCaseId(caseId);
        stateChangeMessage.setCaseImpLevel(importance);
        return caseCommentDAO.persist(stateChangeMessage);
    }

    private Long createAndPersistManagerMessage(Long authorId, Long caseId, Long managerId) {
        CaseComment managerChangeMessage = new CaseComment();
        managerChangeMessage.setAuthorId(authorId);
        managerChangeMessage.setCreated(new Date());
        managerChangeMessage.setCaseId(caseId);
        managerChangeMessage.setCaseManagerId(managerId);
        return caseCommentDAO.persist(managerChangeMessage);
    }

    private void applyFilterByScope(AuthToken token, CaseQuery query) {
        Set<UserRole> roles = token.getRoles();
        if (!policyService.hasGrantAccessFor(roles, En_Privilege.ISSUE_VIEW)) {
            Company company = companyService.getCompanyOmitPrivileges(token, token.getCompanyId()).getData();
            query.setCompanyIds(
                    acceptAllowedCompanies(
                            query.getCompanyIds(),
                            getCompaniesBySubcontractorIds(company.getCategory(), token.getCompanyAndChildIds())));
            query.setManagerCompanyIds(
                    acceptAllowedCompanies(
                            query.getManagerCompanyIds(),
                            getSubcontractorsByCompanyIds(company.getCategory(), token.getCompanyAndChildIds())));
            query.setAllowViewPrivate(false);
            query.setCustomerSearch(true);
        }
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
                || !Objects.equals(co1.getDeadline(), co2.getDeadline())
                || !Objects.equals(co1.getWorkTrigger(), co2.getWorkTrigger());
    }

    private void applyCaseByScope( AuthToken token, CaseObject caseObject ) {
        Set< UserRole > roles = token.getRoles();
        if ( !policyService.hasGrantAccessFor( roles, En_Privilege.ISSUE_CREATE ) && policyService.hasScopeForPrivilege( roles, En_Privilege.ISSUE_CREATE, En_Scope.COMPANY ) ) {
            caseObject.setPrivateCase( false );
            if( !token.getCompanyAndChildIds().contains( caseObject.getInitiatorCompanyId() ) ) {
                Company company = companyService.getCompanyOmitPrivileges(token, token.getCompanyId()).getData();
                caseObject.setInitiatorCompany( company );
            }
            caseObject.setManagerId( null );
        }
    }

    private boolean hasAccessForCaseObject( AuthToken token, En_Privilege privilege, CaseObject caseObject ) {
        return policyService.hasAccessForCaseObject( token, privilege, caseObject );
    }


    private boolean isStateReopenNotAllowed(CaseObjectMeta oldMeta, CaseObjectMeta newMeta) {
        return isTerminalState(oldMeta.getStateId()) &&
              !isTerminalState(newMeta.getStateId());
    }

    private boolean isParentStateNotAllowed(CaseObject caseObject) {
        return isTerminalState(caseObject.getStateId()) ||
                CrmConstants.State.CREATED == caseObject.getStateId();
    }

    private Set<UserRole> getRoles(AuthToken token) {
        return Optional.ofNullable(token)
                .map(d -> token.getRoles())
                .orElse(new HashSet<>());
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
        if (CrmConstants.State.CREATED == caseMeta.getStateId() && caseMeta.getManager() != null) {
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

    private boolean validateFieldsOfNew(AuthToken token, CaseObject caseObject) {
        if (!validateFields( caseObject )) {
            return false;
        }
        CaseObjectMeta caseObjectMeta = new CaseObjectMeta( caseObject );
        if (!validateMetaFields(token, caseObjectMeta)) {
            return false;
        }
        return true;
    }

    private boolean validateFields(CaseObject caseObject) {
        if (caseObject == null) {
            log.warn("Case object cannot be null");
            return false;
        }
        if (StringUtils.isEmpty(caseObject.getName())) {
            log.warn("Name must be specified. caseId={}", caseObject.getId());
            return false;
        }
        if (caseObject.getType() == null) {
            log.warn("Type must be specified. caseId={}", caseObject.getId());
            return false;
        }
        return true;
    }

    private boolean validateMetaFields(AuthToken token, CaseObjectMeta caseMeta) {
        if (caseMeta == null) {
            log.warn("Case meta cannot be null");
            return false;
        }
        if (caseMeta.getImpLevel() == null) {
            log.warn("Importance level must be specified. caseId={}", caseMeta.getId());
            return false;
        }
        if (En_ImportanceLevel.find(caseMeta.getImpLevel()) == null) {
            log.warn("Unknown importance level. caseId={}, importance={}", caseMeta.getId(), caseMeta.getImpLevel());
            return false;
        }
        if (!isStateValid(caseMeta.getStateId(), caseMeta.getManagerId(), caseMeta.getPauseDate())) {
            log.warn("State is not valid. caseId={}", caseMeta.getId());
            return false;
        }
        if (caseMeta.getManagerCompanyId() == null) {
            log.warn("Manager company must be specified. caseId={}", caseMeta.getId());
            return false;
        }
        if (caseMeta.getManagerId() != null && !personBelongsToCompany(caseMeta.getManagerId(), caseMeta.getManagerCompanyId())) {
            log.warn("Manager doesn't belong to company. caseId={}, managerId={}, managerCompanyId={}",
                    caseMeta.getId(), caseMeta.getManagerId(), caseMeta.getManagerCompanyId());
            return false;
        }
        if (caseMeta.getManagerId() != null && caseMeta.getProductId() == null) {
            log.warn("Manager must be specified with product. caseId={}", caseMeta.getId());
            return false;
        }
        if (caseMeta.getInitiatorCompanyId() == null) {
            log.warn("Initiator company must be specified. caseId={}", caseMeta.getId());
            return false;
        }
        if (caseMeta.getInitiatorId() != null && !personBelongsToCompany( caseMeta.getInitiatorId(), caseMeta.getInitiatorCompanyId() )) {
            log.warn("Initiator doesn't belong to company. caseId={}, initiatorId={}, initiatorCompanyId={}",
                    caseMeta.getId(), caseMeta.getInitiatorId(), caseMeta.getInitiatorCompanyId());
            return false;
        }
        if (caseMeta.getPlatformId() != null && !platformBelongsToCompany(token, caseMeta.getPlatformId(), caseMeta.getInitiatorCompanyId())) {
            log.warn("Platform doesn't belong to initiator company. caseId={}, platformId={}, initiatorCompanyId={}",
                    caseMeta.getId(), caseMeta.getPlatformId(), caseMeta.getInitiatorCompanyId());
            return false;
        }
        if (!isProductValid(token, caseMeta.getProductId(), caseMeta.getPlatformId(), caseMeta.getInitiatorCompanyId())) {
            log.warn("Product is not valid. caseId={}", caseMeta.getId());
            return false;
        }
        if (!isDeadlineValid(caseMeta.getDeadline())) {
            log.warn("Deadline has passed. caseId={}", caseMeta.getId());
            return false;
        }
        return true;
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

    private boolean isStateValid(long caseStateId, Long managerId, Long pauseDate) {
        if (!(listOf(CrmConstants.State.CREATED, CrmConstants.State.CANCELED)
                .contains(caseStateId)) && managerId == null) {

            log.warn("State must be CREATED or CANCELED without manager");
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

    private List<CaseLink> fillYouTrackInfo( List<CaseLink> caseLinks ) {
        for (CaseLink link : emptyIfNull( caseLinks )) {
            if (!YT.equals( link.getType() ) || link.getRemoteId() == null) continue;
            youtrackService.getIssueInfo( link.getRemoteId() )
                    .ifError(e -> log.warn( "fillYouTrackInfo(): case link with id={}, caseId={}, linkType={}, remoteId={} not found! ", link.getId(), link.getCaseId(), link.getType(), link.getRemoteId()))
                    .ifOk(link::setYouTrackIssueInfo);
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
        if ( isNotEmpty(caseObject.getNotifiers())) {
            caseObject.getNotifiers().forEach( Person::resetPrivacyInfo);
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

        CaseLink caseLink = new CaseLink();
        caseLink.setType(En_CaseLink.CRM);
        caseLink.setBundleType(En_BundleType.SUBTASK);
        caseLink.setRemoteId(parent.getId().toString());
        caseLink.setWithCrosslink(true);
        createRequest.addLink(caseLink);
    }

    private Collection<Long> getSubcontractorsByCompanyIds(En_CompanyCategory category, Collection<Long> companyIds) {
        if (category == En_CompanyCategory.SUBCONTRACTOR) {
            return companyIds;
        }

        Result<List<EntityOption>> result = companyService.subcontractorOptionListByCompanyIds(companyIds);
        if (result.isError()) {
            throw new RuntimeException("Failed to get subcontractors by companies");
        }
        return result.getData().stream().map(EntityOption::getId).collect(Collectors.toList());
    }

    private Collection<Long> getCompaniesBySubcontractorIds(En_CompanyCategory category, Collection<Long> subcontractorIds) {
        if (category != En_CompanyCategory.SUBCONTRACTOR) {
            return subcontractorIds;
        }

        Result<List<EntityOption>> result = companyService.companyOptionListBySubcontractorIds(subcontractorIds);
        if (result.isError()) {
            throw new RuntimeException("Failed to get companies by subcontractors");
        }
        return result.getData().stream().map(EntityOption::getId).collect(Collectors.toList());
    }
}
