package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.ServiceModule;
import ru.protei.portal.core.event.CaseObjectEvent;
import ru.protei.portal.core.event.CaseObjectMetaEvent;
import ru.protei.portal.core.exception.ResultStatusException;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.utils.JiraUtils;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.query.CaseTagQuery;
import ru.protei.portal.core.model.query.PersonQuery;
import ru.protei.portal.core.model.struct.*;
import ru.protei.portal.core.model.util.CaseStateWorkflowUtil;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.core.service.policy.PolicyService;
import ru.protei.portal.core.service.auth.AuthService;
import ru.protei.winter.core.utils.beans.SearchResult;
import ru.protei.portal.core.model.util.DiffCollectionResult;

import ru.protei.winter.jdbc.JdbcManyRelationsHelper;


import java.util.*;
import java.util.stream.Collectors;

import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;
import static ru.protei.portal.core.model.dict.En_CaseLink.YT;
import static ru.protei.portal.core.model.helper.CollectionUtils.*;
import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;

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

        Result<List<CaseTag>> caseTags = caseTagService.getTagsByCaseId(token, caseObject.getId());
        if (caseTags.isOk()) {
            caseObject.setTags(new HashSet<>(caseTags.getData()));
        }

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
    public Result< CaseObject > createCaseObject( AuthToken token, CaseObject caseObject, Person initiator ) {

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
            caseObject.setState(En_CaseState.CREATED);
            caseObject.setTimeElapsed(null);
        }

        applyStateBasedOnManager(caseObject);

        Long caseId = caseObjectDAO.insertCase(caseObject);

        if (caseId == null)
            return error(En_ResultStatus.NOT_CREATED);
        else
            caseObject.setId(caseId);

        Long stateMessageId = createAndPersistStateMessage(initiator, caseId, caseObject.getState(), caseObject.getTimeElapsed(), caseObject.getTimeElapsedType());
        if (stateMessageId == null) {
            log.error("State message for the issue {} not saved!", caseId);
        }

        Long impMessageId = createAndPersistImportanceMessage(initiator, caseId, caseObject.getImpLevel());
        if (impMessageId == null) {
            log.error("Importance level message for the issue {} not saved!", caseId);
        }

        if (caseObject.getManager() != null && caseObject.getManager().getId() != null) {
            Long messageId = createAndPersistManagerMessage(initiator, caseObject.getId(), caseObject.getManager().getId());
            if (messageId == null) {
                log.error("Manager message for the issue {} not saved!", caseObject.getId());
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

        if (isNotEmpty(caseObject.getTags())) {
            caseObjectTagDAO.persistBatch(
                    caseObject.getTags()
                            .stream()
                            .map(tag -> new CaseObjectTag(caseId, tag.getId()))
                            .collect(Collectors.toList())
            );
        }

        // From GWT-side we get partially filled object, that's why we need to refresh state from db
        CaseObject newState = caseObjectDAO.get(caseId);
        newState.setAttachments(caseObject.getAttachments());
        newState.setNotifiers(caseObject.getNotifiers());
        newState.setTags(caseObject.getTags());
        publisherService.publishEvent( new CaseObjectEvent(this, ServiceModule.GENERAL, initiator, null, newState ));

        return ok(newState);
    }

    @Deprecated
    @Override
    @Transactional
    public Result< CaseObject > updateCaseObject( AuthToken token, CaseObject caseObject, Person initiator ) {

        CaseObject oldState = caseObjectDAO.get(caseObject.getId());
        if (oldState == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        UpdateResult<CaseObject> objectResultData = performUpdateCaseObject(token, caseObject, oldState, initiator);

        if (objectResultData.isUpdated()) {
            // From GWT-side we get partially filled object, that's why we need to refresh state from db
            CaseObject newState = caseObjectDAO.get(objectResultData.getObject().getId());
            newState.setAttachments(objectResultData.getObject().getAttachments());
            newState.setNotifiers(objectResultData.getObject().getNotifiers());
            jdbcManyRelationsHelper.fill(oldState, "attachments");
            publisherService.publishEvent( new CaseObjectEvent(this, ServiceModule.GENERAL, initiator, oldState, newState));
        }

        return ok(objectResultData.getObject());
    }

    @Override
    @Transactional
    public Result<CaseObjectMeta> updateCaseObjectMeta(AuthToken token, CaseObjectMeta caseMeta, Person initiator) {

        CaseObject oldState = caseObjectDAO.get(caseMeta.getId());
        if (oldState == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }
        CaseObjectMeta oldCaseMeta = new CaseObjectMeta(oldState);

        if (!hasAccessForCaseObject(token, En_Privilege.ISSUE_EDIT, oldState)) {
            return error(En_ResultStatus.PERMISSION_DENIED);
        }

        if (!validateMetaFields(caseMeta)) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        applyStateBasedOnManager(caseMeta);

        if (!isCaseMetaChanged(caseMeta, oldCaseMeta)) {
            return ok(caseMeta);
        }

        En_CaseStateWorkflow workflow = CaseStateWorkflowUtil.recognizeWorkflow(oldState.getExtAppType());
        boolean isStateTransitionValidByWorkflow = isCaseStateTransitionValid(workflow, oldCaseMeta.getState(), caseMeta.getState());
        if (!isStateTransitionValidByWorkflow) {
            log.info("Wrong state transition for the issue {}: {} -> {}, workflow={}",
                    caseMeta.getId(), oldCaseMeta.getState(), caseMeta.getState(), workflow);
            throw new ResultStatusException(En_ResultStatus.VALIDATION_ERROR);
        }

        boolean isStateTransitionValidNoWorkflow = workflow != En_CaseStateWorkflow.NO_WORKFLOW || !isStateReopenNotAllowed(token, oldCaseMeta, caseMeta);
        if (!isStateTransitionValidNoWorkflow) {
            log.info("Wrong state transition for the issue {}: {} -> {}",
                    caseMeta.getId(), oldCaseMeta.getState(), caseMeta.getState());
            throw new ResultStatusException(En_ResultStatus.INVALID_CASE_UPDATE_CASE_IS_CLOSED);
        }

        caseMeta.setModified(new Date());
        caseMeta.setTimeElapsed(caseCommentService.getTimeElapsed(caseMeta.getId()).getData());

        boolean isUpdated = caseObjectMetaDAO.merge(caseMeta);
        if (!isUpdated) {
            log.info("Failed to update issue meta data {} at db", caseMeta.getId());
            throw new ResultStatusException(En_ResultStatus.NOT_UPDATED);
        }

        if (!Objects.equals(oldCaseMeta.getState(), caseMeta.getState())) {
            Long messageId = createAndPersistStateMessage(initiator, caseMeta.getId(), caseMeta.getState(), null, null);
            if (messageId == null) {
                log.error("State message for the issue {} isn't saved!", caseMeta.getId());
            }
        }

        if (!Objects.equals(oldCaseMeta.getImpLevel(), caseMeta.getImpLevel())) {
            Long messageId = createAndPersistImportanceMessage(initiator, caseMeta.getId(), caseMeta.getImpLevel());
            if (messageId == null) {
                log.error("Importance level message for the issue {} isn't saved!", caseMeta.getId());
            }
        }

        if (oldCaseMeta.getManager() != null && caseMeta.getManager() != null &&
            !Objects.equals(oldCaseMeta.getManager().getId(), caseMeta.getManager().getId())) {
            Long messageId = createAndPersistManagerMessage(initiator, caseMeta.getId(), caseMeta.getManager().getId());
            if (messageId == null) {
                log.error("Manager message for the issue {} isn't saved!", caseMeta.getId());
            }
        }

        // From GWT-side we get partially filled object, that's why we need to refresh state from db
        CaseObject newState = caseObjectDAO.get(caseMeta.getId());
        CaseObjectMeta newCaseMeta = new CaseObjectMeta(newState);
        publisherService.publishEvent(new CaseObjectMetaEvent(
                this,
                ServiceModule.GENERAL,
                initiator,
                En_ExtAppType.forCode(newState.getExtAppType()),
                oldCaseMeta,
                newCaseMeta
        ));

        return ok(newCaseMeta);
    }

    @Override
    @Transactional
    public Result<CaseObjectMetaNotifiers> updateCaseObjectMetaNotifiers(AuthToken token, CaseObjectMetaNotifiers caseMetaNotifiers, Person initiator) {

        CaseObject oldState = caseObjectDAO.get(caseMetaNotifiers.getId());
        if (oldState == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        if (!hasAccessForCaseObject(token, En_Privilege.ISSUE_EDIT, oldState)) {
            return error(En_ResultStatus.PERMISSION_DENIED);
        }

        if (!isPersonHasGrantAccess(token, En_Privilege.ISSUE_FILTER_MANAGER_VIEW)) {
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
                    "id", "contactInfo")
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
    public Result<CaseObjectMetaJira> updateCaseObjectMetaJira(AuthToken token, CaseObjectMetaJira caseMetaJira, Person initiator) {

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

    @Deprecated
    private UpdateResult<CaseObject> performUpdateCaseObject(AuthToken token, CaseObject caseObject, CaseObject oldState, Person initiator ) {

        if (caseObject == null) {
            throw new ResultStatusException(En_ResultStatus.INCORRECT_PARAMS);
        }

        caseObject.setCreated(oldState.getCreated());
        caseObject.setCaseNumber(oldState.getCaseNumber());

        if (!hasAccessForCaseObject(token, En_Privilege.ISSUE_EDIT, caseObject)) {
            throw new ResultStatusException(En_ResultStatus.PERMISSION_DENIED);
        }

        if (!validateFields(caseObject)) {
            throw new ResultStatusException(En_ResultStatus.INCORRECT_PARAMS);
        }

        synchronizeTags(caseObject, authService.findSession(token));
        jdbcManyRelationsHelper.persist(caseObject, "tags");

        if (!isCaseChanged(caseObject, oldState)) {
            return new UpdateResult<>(caseObject, false);
        }

        boolean isSelfCase = Objects.equals(initiator.getId(), oldState.getCreator().getId());
        boolean isChangedNameOrDescription = !Objects.equals(oldState.getName(), caseObject.getName()) || !Objects.equals(oldState.getInfo(), caseObject.getInfo());
        if ( !isSelfCase && isChangedNameOrDescription ) {
            log.info("Trying edit not self name or description for the issue {}", caseObject.getId());
            throw new ResultStatusException(En_ResultStatus.NOT_ALLOWED_CHANGE_ISSUE_NAME_OR_DESCRIPTION);
        }

        caseObject.setModified(new Date());
        caseObject.setTimeElapsed(caseCommentService.getTimeElapsed(caseObject.getId()).getData());

        boolean isUpdated = caseObjectDAO.merge(caseObject);
        if (!isUpdated) {
            log.info("Failed to update issue {} at db", caseObject.getId());
            throw new ResultStatusException(En_ResultStatus.NOT_UPDATED);
        }

        return new UpdateResult<>(caseObject, true);
    }

    @Override
    public Result<List<En_CaseState>> stateList( En_CaseType caseType ) {
        List<CaseState> states = caseStateMatrixDAO.getStatesByCaseType(caseType);

        if (states == null)
            return error(En_ResultStatus.GET_DATA_ERROR);

        return ok(states.stream().map(caseState -> En_CaseState.getById(caseState.getId())).collect( Collectors.toList()));
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

    private void synchronizeTags(CaseObject caseObject, UserSessionDescriptor descriptor) {
        if (caseObject == null || descriptor == null || caseObject.getTags() == null) {
            return;
        }

        Set<UserRole> roles = descriptor.getLogin().getRoles();
        if (policyService.hasGrantAccessFor(roles, En_Privilege.ISSUE_VIEW)) {
            return;
        }

        Set<CaseTag> tags = caseObject.getTags();
        List<CaseTag> allTags = caseTagDAO.getListByQuery(new CaseTagQuery(caseObject.getId()));
        List<CaseTag> existingTags = allTags.stream().filter(tag -> tag.getCompanyId().equals(descriptor.getCompany().getId())).collect(Collectors.toList());

        List<CaseTag> deletedTags = existingTags.stream().filter(tag -> !tags.contains(tag)).collect(Collectors.toList());
        List<CaseTag> addedTags = tags.stream().filter(tag -> !existingTags.contains(tag)).collect(Collectors.toList());

        allTags.removeAll(deletedTags);
        allTags.addAll(addedTags);

        caseObject.setTags(allTags.stream().collect(Collectors.toSet()));
    }

    private Long createAndPersistStateMessage(Person author, Long caseId, En_CaseState state, Long timeElapsed, En_TimeElapsedType timeElapsedType){
        CaseComment stateChangeMessage = new CaseComment();
        stateChangeMessage.setAuthor(author);
        stateChangeMessage.setCreated(new Date());
        stateChangeMessage.setCaseId(caseId);
        stateChangeMessage.setCaseStateId((long)state.getId());
        if (timeElapsed != null && timeElapsed > 0L) {
            stateChangeMessage.setTimeElapsed(timeElapsed);
            stateChangeMessage.setTimeElapsedType(timeElapsedType != null ? timeElapsedType : En_TimeElapsedType.NONE);
        }
        return caseCommentDAO.persist(stateChangeMessage);
    }

    private Long createAndPersistImportanceMessage(Person author, Long caseId, Integer importance) {//int -> Integer т.к. падает unit test с NPE, неясно почему
        CaseComment stateChangeMessage = new CaseComment();
        stateChangeMessage.setAuthor(author);
        stateChangeMessage.setCreated(new Date());
        stateChangeMessage.setCaseId(caseId);
        stateChangeMessage.setCaseImpLevel(importance);
        return caseCommentDAO.persist(stateChangeMessage);
    }

    private Long createAndPersistManagerMessage(Person author, Long caseId, Long managerId) {
        CaseComment managerChangeMessage = new CaseComment();
        managerChangeMessage.setAuthor(author);
        managerChangeMessage.setCreated(new Date());
        managerChangeMessage.setCaseId(caseId);
        managerChangeMessage.setCaseManagerId(managerId);
        return caseCommentDAO.persist(managerChangeMessage);
    }

    private void applyFilterByScope( AuthToken token, CaseQuery query ) {
        UserSessionDescriptor descriptor = authService.findSession( token );
        Set< UserRole > roles = descriptor.getLogin().getRoles();
        if ( !policyService.hasGrantAccessFor( roles, En_Privilege.ISSUE_VIEW ) ) {
            query.setCompanyIds( acceptAllowedCompanies( query.getCompanyIds(), descriptor.getAllowedCompaniesIds() ) );
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
                || !Objects.equals(co1.getState(), co2.getState())
                || !Objects.equals(co1.getImpLevel(), co2.getImpLevel())
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
        UserSessionDescriptor descriptor = authService.findSession( token );
        Set< UserRole > roles = descriptor.getLogin().getRoles();
        if ( !policyService.hasGrantAccessFor( roles, En_Privilege.ISSUE_CREATE ) && policyService.hasScopeForPrivilege( roles, En_Privilege.ISSUE_CREATE, En_Scope.COMPANY ) ) {
            caseObject.setPrivateCase( false );
            if( !descriptor.getAllowedCompaniesIds().contains( caseObject.getInitiatorCompanyId() ) ) {
                caseObject.setInitiatorCompany( descriptor.getCompany() );
            }
            caseObject.setManagerId( null );
        }
    }

    private boolean hasAccessForCaseObject( AuthToken token, En_Privilege privilege, CaseObject caseObject ) {
        UserSessionDescriptor descriptor = authService.findSession( token );
        return policyService.hasAccessForCaseObject( descriptor, privilege, caseObject );
    }


    private boolean isStateReopenNotAllowed(AuthToken token, CaseObjectMeta oldMeta, CaseObjectMeta newMeta) {
        return oldMeta.getState() == En_CaseState.VERIFIED &&
                newMeta.getState() != En_CaseState.VERIFIED &&
                !isPersonHasGrantAccess(token, En_Privilege.ISSUE_EDIT);
    }

    private boolean isPersonHasGrantAccess(AuthToken token, En_Privilege privilege) {
        Set<UserRole> roles = getRoles(token);
        return policyService.hasGrantAccessFor(roles, privilege);
    }

    private Set<UserRole> getRoles(AuthToken token) {
        return Optional.ofNullable(authService.findSession(token))
                .map(d -> d.getLogin().getRoles())
                .orElse(new HashSet<>());
    }

    private boolean personBelongsToHomeCompany(AuthToken token) {

        UserSessionDescriptor descriptor = authService.findSession(token);

        if (descriptor == null || descriptor.getCompany() == null || descriptor.getCompany().getCategory() == null) {
            return false;
        }

        return Objects.equals(En_CompanyCategory.HOME.getId(), descriptor.getCompany().getCategory().getId());
    }

    private void applyStateBasedOnManager(CaseObject caseObject) {
        CaseObjectMeta caseMeta = new CaseObjectMeta(caseObject);
        applyStateBasedOnManager(caseMeta);
        caseObject.setState(caseMeta.getState());
    }

    private void applyStateBasedOnManager(CaseObjectMeta caseMeta) {
        if (caseMeta.getState() == En_CaseState.CREATED && caseMeta.getManager() != null) {
            caseMeta.setState(En_CaseState.OPENED);
        }
    }

    private boolean isCaseStateTransitionValid(En_CaseStateWorkflow workflow, En_CaseState caseStateFrom, En_CaseState caseStateTo) {
        if (caseStateFrom == caseStateTo) {
            return true;
        }
        Result<CaseStateWorkflow> response = caseStateWorkflowService.getWorkflow(workflow);
        if (response.isError()) {
            log.error("Failed to get case state workflow, status={}", response.getStatus());
            return false;
        }
        return CaseStateWorkflowUtil.isCaseStateTransitionValid(response.getData(), caseStateFrom, caseStateTo);
    }

    private boolean validateFieldsOfNew(CaseObject caseObject) {
        return validateFields(caseObject)
            && validateMetaFields(new CaseObjectMeta(caseObject));
    }

    private boolean validateFields(CaseObject caseObject) {
        return caseObject != null
                && caseObject.getName() != null
                && !caseObject.getName().isEmpty()
                && En_CaseType.find(caseObject.getTypeId()) != null;
    }

    private boolean validateMetaFields(CaseObjectMeta caseMeta) {
        return caseMeta != null
                && caseMeta.getImpLevel() != null
                && En_ImportanceLevel.find(caseMeta.getImpLevel()) != null
                && En_CaseState.getById(caseMeta.getStateId()) != null
                && (caseMeta.getState().getId() == En_CaseState.CREATED.getId()
                    || caseMeta.getState().getId() == En_CaseState.CANCELED.getId()
                    || caseMeta.getManagerId() != null
                )
                && (caseMeta.getInitiatorCompanyId() != null)
                && (caseMeta.getInitiatorId() == null || personBelongsToCompany(caseMeta.getInitiatorId(), caseMeta.getInitiatorCompanyId()));
    }

    private boolean personBelongsToCompany(Long personId, Long companyId) {
        PersonQuery personQuery = new PersonQuery();
        personQuery.setCompanyIds(Collections.singleton(companyId));
        return personDAO.getPersons(personQuery).stream().anyMatch(person -> personId.equals(person.getId()));
    }

    private List<CaseLink> fillYouTrackInfo( List<CaseLink> caseLinks ) {
        for (CaseLink link : emptyIfNull( caseLinks )) {
            if (!YT.equals( link.getType() ) || link.getRemoteId() == null) continue;
            youtrackService.getIssueInfo( link.getRemoteId() )
                    .ifOk( info -> link.setYouTrackIssueInfo( info ) );
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
    EventPublisherService publisherService;

    @Autowired
    CaseAttachmentDAO caseAttachmentDAO;

    @Autowired
    CaseNotifierDAO caseNotifierDAO;

    @Autowired
    CaseObjectTagDAO caseObjectTagDAO;

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

    private static Logger log = LoggerFactory.getLogger(CaseServiceImpl.class);
}
