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
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.query.PersonQuery;
import ru.protei.portal.core.model.struct.*;
import ru.protei.portal.core.model.util.CaseStateWorkflowUtil;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.util.DiffCollectionResult;
import ru.protei.portal.core.model.util.DiffResult;
import ru.protei.portal.core.model.view.CaseShortView;
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

import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;
import static ru.protei.portal.api.struct.Result.*;
import static ru.protei.portal.core.model.dict.En_CaseLink.YT;
import static ru.protei.portal.core.model.helper.CollectionUtils.*;
import static ru.protei.portal.core.model.util.CrmConstants.SOME_LINKS_NOT_SAVED;

/**
 * Реализация сервиса управления обращениями
 */
public class CaseServiceImpl implements CaseService {

    @Override
    public Result<SearchResult<CaseShortView>> getCaseObjects( AuthToken token, CaseQuery query) {
        applyFilterByScope(token, query);

        SearchResult<CaseShortView> sr = caseShortViewDAO.getSearchResult(query);

        return ok(sr);
    }

    @Override
    public Result<CaseObject> getCaseObjectById( AuthToken token, Long caseID ) {
        CaseObject caseObject = caseObjectDAO.get( caseID );

        return fillCaseObject( token, caseObject );
    }

    @Override
    public Result<CaseObject> getCaseObjectByNumber( AuthToken token, long number ) {

        CaseObject caseObject = caseObjectDAO.getCase( En_CaseType.CRM_SUPPORT, number );

        return fillCaseObject( token, caseObject );
    }

    private Result<CaseObject> fillCaseObject( AuthToken token, CaseObject caseObject ) {
        if ( !hasAccessForCaseObject( token, En_Privilege.ISSUE_VIEW, caseObject ) ) {
            return error(En_ResultStatus.PERMISSION_DENIED);
        }

        if(caseObject == null)
            return error(En_ResultStatus.NOT_FOUND);

        jdbcManyRelationsHelper.fillAll( caseObject.getInitiatorCompany() );
        jdbcManyRelationsHelper.fill( caseObject, "attachments");
        jdbcManyRelationsHelper.fill( caseObject, "notifiers");

        withJiraSLAInformation(caseObject);

        // RESET PRIVACY INFO
        if ( caseObject.getInitiator() != null ) {
            caseObject.getInitiator().resetPrivacyInfo();
        }
        if ( caseObject.getCreator() != null ) {
            caseObject.getCreator().resetPrivacyInfo();
        }
        if ( caseObject.getManager() != null ) {
            caseObject.getManager().resetPrivacyInfo();
        }
        if ( isNotEmpty(caseObject.getNotifiers())) {
            caseObject.getNotifiers().forEach( Person::resetPrivacyInfo);
        }

        return ok(caseObject);
    }

    @Override
    @Transactional
    public Result< CaseObject > createCaseObject( AuthToken token, CaseObjectCreateRequest caseObjectCreateRequest) {

        CaseObject caseObject = caseObjectCreateRequest.getCaseObject();

        if (!validateFieldsOfNew(caseObject)) {
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
        }

        if(isNotEmpty(caseObject.getNotifiers())){
            caseNotifierDAO.persistBatch(
                    caseObject.getNotifiers()
                            .stream()
                            .map(person -> new CaseNotifier(caseId, person.getId()))
                            .collect(Collectors.toList()));

            // update partially filled objects
            caseObject.setNotifiers(new HashSet<>(personDAO.partialGetListByKeys(
                            caseObject.getNotifiers()
                                    .stream()
                                    .map(person ->  person.getId())
                                    .collect(Collectors.toList()), "id", "contactInfo"))
            );
        }

        if (isNotEmpty(caseObjectCreateRequest.getTags())) {
            caseObjectTagDAO.persistBatch(
                    caseObjectCreateRequest.getTags()
                            .stream()
                            .map(tag -> new CaseObjectTag(caseId, tag.getId()))
                            .collect(Collectors.toList())
            );
        }

        Result addLinksResult = ok();

        for (CaseLink caseLink : CollectionUtils.emptyIfNull(caseObjectCreateRequest.getLinks())) {
            caseLink.setCaseId(caseObject.getId());
            Result currentResult = caseLinkService.createLink(token, caseLink, true);
            if (currentResult.isError()) addLinksResult = currentResult;
        }

        autoOpenCaseService.processNewCreatedCaseToAutoOpen(caseId, caseObject.getInitiatorCompany().getId());

        // From GWT-side we get partially filled object, that's why we need to refresh state from db
        CaseObject newState = caseObjectDAO.get(caseId);
        newState.setAttachments(caseObject.getAttachments());
        newState.setNotifiers(caseObject.getNotifiers());
        CaseObjectCreateEvent caseObjectCreateEvent = new CaseObjectCreateEvent(this, ServiceModule.GENERAL, token.getPersonId(), newState);

        return new Result<>(En_ResultStatus.OK, newState, (addLinksResult.isOk() ? null : SOME_LINKS_NOT_SAVED), Collections.singletonList(caseObjectCreateEvent));
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
                caseObjectDAO.partialMerge(caseObject, "ATTACHMENT_EXISTS");

                caseAttachmentDAO.persistBatch(
                        changeRequest.getAttachments()
                                .stream()
                                .map(a -> new CaseAttachment(changeRequest.getId(), a.getId()))
                                .collect(Collectors.toList())
                );
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

        if (!validateMetaFields(caseMeta)) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        if (!isCaseMetaChanged(caseMeta, oldCaseMeta)) {
            return ok(caseMeta);
        }

        En_CaseStateWorkflow workflow = CaseStateWorkflowUtil.recognizeWorkflow(oldState.getExtAppType());
        boolean isStateTransitionValidByWorkflow = isCaseStateTransitionValid(workflow, oldCaseMeta.getStateId(), caseMeta.getStateId());
        if (!isStateTransitionValidByWorkflow) {
            log.info("Wrong state transition for the issue {}: {} -> {}, workflow={}",
                    caseMeta.getId(), oldCaseMeta.getStateId(), caseMeta.getStateId(), workflow);
            throw new ResultStatusException(En_ResultStatus.VALIDATION_ERROR);
        }

        boolean isStateTransitionValidNoWorkflow = workflow != En_CaseStateWorkflow.NO_WORKFLOW || !isStateReopenNotAllowed(token, oldCaseMeta, caseMeta);
        if (!isStateTransitionValidNoWorkflow) {
            log.info("Wrong state transition for the issue {}: {} -> {}",
                    caseMeta.getId(), oldCaseMeta.getStateId(), caseMeta.getStateId());
            throw new ResultStatusException(En_ResultStatus.INVALID_CASE_UPDATE_CASE_IS_CLOSED);
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

        // From GWT-side we get partially filled object, that's why we need to refresh state from db
        CaseObjectMeta newCaseMeta = caseObjectMetaDAO.get(caseMeta.getId());
        return ok(newCaseMeta)
                .publishEvent( new CaseObjectMetaEvent(
                this,
                ServiceModule.GENERAL,
                token.getPersonId(),
                En_ExtAppType.forCode(oldState.getExtAppType()),
                oldCaseMeta,
                newCaseMeta
        ) );
    }

    @Override
    public Result<CaseObjectMeta> getIssueMeta( AuthToken token, Long issueId ) {
        CaseObjectMeta caseObjectMeta = caseObjectMetaDAO.get( issueId );

        return ok(caseObjectMeta);
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
                    "id", "contactInfo", "displayShortName")
            ));
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
    public Result<List<CaseState>> stateListWithViewOrder(En_CaseType caseType) {
        List<CaseState> states = caseStateMatrixDAO.getStatesByCaseType(caseType);

        if (states == null)
            return error(En_ResultStatus.GET_DATA_ERROR);

        return ok(states);
    }

    @Override
    public Result<Boolean> updateCaseModified( AuthToken token, Long caseId, Date modified) {
        if(caseId == null || !caseObjectDAO.checkExistsByKey(caseId))
            return error(En_ResultStatus.INCORRECT_PARAMS);

        CaseObject caseObject = new CaseObject(caseId);
        caseObject.setModified(modified == null? new Date(): modified);

        boolean isUpdated = caseObjectDAO.partialMerge(caseObject, "MODIFIED");

        return ok(isUpdated);
    }

    @Override
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
    public Result<CaseInfo> getCaseShortInfo( AuthToken token, Long caseNumber) {
        if ( !hasAccessForCaseObject( token, En_Privilege.ISSUE_VIEW, caseObjectDAO.getCase(En_CaseType.CRM_SUPPORT, caseNumber) ) ) {
            return error(En_ResultStatus.PERMISSION_DENIED );
        }

        CaseShortView caseObject = caseShortViewDAO.getCase( caseNumber );

        if(caseObject == null)
            return error(En_ResultStatus.NOT_FOUND);

        CaseInfo info = new CaseInfo();
        info.setId(caseObject.getId());
        info.setCaseNumber(caseObject.getCaseNumber());
        info.setPrivateCase(caseObject.isPrivateCase());
        info.setName(caseObject.getName());
        info.setImpLevel(caseObject.getImpLevel());
        info.setStateId(caseObject.getStateId());
        info.setInfo(caseObject.getInfo());

        return ok(info);
    }

    @Override
    @Transactional
    public Result<Long> bindAttachmentToCaseNumber( AuthToken token, En_CaseType caseType, Attachment attachment, long caseNumber) {
        CaseObject caseObject = caseObjectDAO.getCase(caseType, caseNumber);
        if ( !hasAccessForCaseObject( token, En_Privilege.ISSUE_EDIT, caseObject ) ) {
            return error(En_ResultStatus.PERMISSION_DENIED );
        }
        return attachToCaseId( attachment, caseObject.getId() );
    }

    @Override
    @Transactional
    public Result<Long> attachToCaseId( Attachment attachment, long caseId) {
        CaseAttachment caseAttachment = new CaseAttachment(caseId, attachment.getId());
        Long caseAttachId = caseAttachmentDAO.persist(caseAttachment);

        if(caseAttachId == null)
            return error(En_ResultStatus.NOT_CREATED);

        CaseObject caseObject = new CaseObject(caseId);
        caseObject.setAttachmentExists(true);
        caseObject.setModified(new Date());
        boolean isCaseUpdated = caseObjectDAO.partialMerge(caseObject, "ATTACHMENT_EXISTS", "MODIFIED");

        if(!isCaseUpdated)
            throw new RuntimeException("failed to update case object");

        return ok(caseAttachId);
    }

    @Override
    public Result<Boolean> isExistsAttachments(Long caseId) {
        return ok(caseAttachmentDAO.checkExistsByCondition("case_id = ?", caseId));
    }

    @Override
    public Result<List<CaseLink>> getCaseLinks( AuthToken token, Long caseId ) {
        return caseLinkService.getLinks( token, caseId)
                .map( this::fillYouTrackInfo );
    }


    @Override
    public Result<Long> getCaseIdByNumber( AuthToken token, Long caseNumber ) {
        Long caseId = caseObjectDAO.getCaseIdByNumber( caseNumber );
        if(caseId==null) error( En_ResultStatus.NOT_FOUND );
        return ok(caseId);
    }

    @Override
    public Result<Long> getCaseNumberById( AuthToken token, Long caseId ) {
        Long caseNumber = caseObjectDAO.getCaseNumberById( caseId );
        if(caseNumber==null) error( En_ResultStatus.NOT_FOUND );
        return ok(caseNumber);
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

    private void applyFilterByScope( AuthToken token, CaseQuery query ) {
        Set< UserRole > roles = token.getRoles();
        if ( !policyService.hasGrantAccessFor( roles, En_Privilege.ISSUE_VIEW ) ) {
            query.setCompanyIds( acceptAllowedCompanies( query.getCompanyIds(), token.getCompanyAndChildIds() ) );
            query.setManagerCompanyIds(acceptAllowedCompanies(query.getManagerCompanyIds(), token.getCompanyAndChildIds()));
            query.setManagerOrInitiatorCondition(true);
            query.setAllowViewPrivate( false );
            query.setCustomerSearch( true );
        }
    }

    private List<Long> acceptAllowedCompanies( List<Long> companyIds, Collection<Long> allowedCompaniesIds ) {
        if( companyIds == null ) return new ArrayList<>( allowedCompaniesIds );
        ArrayList allowedCompanies = new ArrayList( companyIds );
        allowedCompanies.retainAll( allowedCompaniesIds );
        return allowedCompanies.isEmpty() ? new ArrayList<>( allowedCompaniesIds ) : allowedCompanies;
    }

    private boolean isCaseChanged(CaseObject co1, CaseObject co2){
        // without links
        return     !Objects.equals(co1.getName(), co2.getName())
                || !Objects.equals(co1.getInfo(), co2.getInfo())
                || !Objects.equals(co1.isPrivateCase(), co2.isPrivateCase());
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
                || !Objects.equals(co1.getPlatformId(), co2.getPlatformId());
    }

    private boolean isLinksChanged( DiffCollectionResult<CaseLink> mergeLinks ){
        if(mergeLinks == null) return false;
        if(!isEmpty(mergeLinks.getAddedEntries())) return true;
        if(!isEmpty(mergeLinks.getRemovedEntries())) return true;
        return false;
    }

    private void applyCaseByScope( AuthToken token, CaseObject caseObject ) {
        Set< UserRole > roles = token.getRoles();
        if ( !policyService.hasGrantAccessFor( roles, En_Privilege.ISSUE_CREATE ) && policyService.hasScopeForPrivilege( roles, En_Privilege.ISSUE_CREATE, En_Scope.COMPANY ) ) {
            caseObject.setPrivateCase( false );
            if( !token.getCompanyAndChildIds().contains( caseObject.getInitiatorCompanyId() ) ) {
                Company company = companyService.getCompanyUnsafe(token, token.getCompanyId()).getData();
                caseObject.setInitiatorCompany( company );
            }
            caseObject.setManagerId( null );
        }
    }

    private boolean hasAccessForCaseObject( AuthToken token, En_Privilege privilege, CaseObject caseObject ) {
        return policyService.hasAccessForCaseObject( token, privilege, caseObject );
    }


    private boolean isStateReopenNotAllowed(AuthToken token, CaseObjectMeta oldMeta, CaseObjectMeta newMeta) {
        return CrmConstants.State.VERIFIED == oldMeta.getStateId() &&
                CrmConstants.State.VERIFIED != newMeta.getStateId()  &&
                !isPersonHasGrantAccess(token, En_Privilege.ISSUE_EDIT);
    }

    private boolean isPersonHasGrantAccess(AuthToken token, En_Privilege privilege) {
        Set<UserRole> roles = getRoles(token);
        return policyService.hasGrantAccessFor(roles, privilege);
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

        Result<Company> result = companyService.getCompanyUnsafe(token, token.getCompanyId());
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

    private boolean isCaseStateTransitionValid(En_CaseStateWorkflow workflow, long caseStateFromId, long caseStateToId) {
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

    private boolean validateFieldsOfNew(CaseObject caseObject) {
        if (!validateFields( caseObject )) return false;
        CaseObjectMeta caseObjectMeta = new CaseObjectMeta( caseObject );
        if (!validateMetaFields( caseObjectMeta )) return false;
        return true;
    }

    private boolean validateFields(CaseObject caseObject) {
        if(caseObject == null) return false;
        if(caseObject.getName() == null) return false;
        if(caseObject.getName().isEmpty()) return false;
        if(caseObject.getType() == null) return false;
        return true;
    }

    private boolean validateMetaFields(CaseObjectMeta caseMeta) {
        if (caseMeta == null) return false;
        if (caseMeta.getImpLevel() == null) return false;
        if (En_ImportanceLevel.find(caseMeta.getImpLevel()) == null) return false;
        if (!isStateValid(caseMeta.getStateId(), caseMeta.getManagerId(), caseMeta.getPauseDate())) return false;
        if (caseMeta.getManagerCompanyId() == null) return false;
        if (caseMeta.getManagerId() != null && !personBelongsToCompany(caseMeta.getManagerId(), caseMeta.getManagerCompanyId())) return false;
        if (caseMeta.getInitiatorCompanyId() == null) return false;
        if (caseMeta.getInitiatorId() != null && !personBelongsToCompany( caseMeta.getInitiatorId(), caseMeta.getInitiatorCompanyId() ))
            return false;
        return true;
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
            return false;
        }

        if (CrmConstants.State.PAUSED == caseStateId) {
            return pauseDate != null && (System.currentTimeMillis() < pauseDate);
        }

        return true;
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

    @Autowired
    JdbcManyRelationsHelper jdbcManyRelationsHelper;

    @Autowired
    CaseObjectDAO caseObjectDAO;

    @Autowired
    CaseShortViewDAO caseShortViewDAO;

    @Autowired
    CaseObjectMetaDAO caseObjectMetaDAO;

    @Autowired
    CaseObjectMetaNotifiersDAO caseObjectMetaNotifiersDAO;

    @Autowired
    CaseStateMatrixDAO caseStateMatrixDAO;

    @Autowired
    CaseCommentDAO caseCommentDAO;

    @Autowired
    PersonDAO personDAO;

    @Autowired
    CaseAttachmentDAO caseAttachmentDAO;

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

    private static Logger log = LoggerFactory.getLogger(CaseServiceImpl.class);
}
