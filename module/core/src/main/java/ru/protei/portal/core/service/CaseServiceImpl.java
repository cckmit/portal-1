package ru.protei.portal.core.service;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.CasePrivilegeValidator;
import ru.protei.portal.core.CaseStateWorkflowValidator;
import ru.protei.portal.core.event.CaseObjectEvent;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.util.CaseStateWorkflowUtil;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.core.service.user.AuthService;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.util.*;
import java.util.stream.Collectors;

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
    CaseShortViewDAO caseShortViewDAO;

    @Autowired
    CaseStateMatrixDAO caseStateMatrixDAO;

    @Autowired
    CaseCommentDAO caseCommentDAO;

    @Autowired
    PersonDAO personDAO;

    @Autowired
    EventAssemblerService publisherService;

    @Autowired
    CaseAttachmentDAO caseAttachmentDAO;

    @Autowired
    CaseNotifierDAO caseNotifierDAO;

    @Autowired
    PolicyService policyService;

    @Autowired
    AuthService authService;

    @Autowired
    CaseLinkService caseLinkService;

    @Autowired
    CaseCommentService caseCommentService;

    @Autowired
    CasePrivilegeValidator casePrivilegeValidator;

    @Autowired
    CaseStateWorkflowValidator caseStateWorkflowValidator;

    @Override
    public CoreResponse<List<CaseShortView>> caseObjectList( AuthToken token, CaseQuery query ) {

        applyFilterByScope( token, query );

        List<CaseShortView> list = caseShortViewDAO.getCases( query );

        if ( list == null )
            return new CoreResponse<List<CaseShortView>>().error(En_ResultStatus.GET_DATA_ERROR);

        return new CoreResponse<List<CaseShortView>>().success(list);
    }

    @Override
    public CoreResponse<CaseObject> getCaseObject( AuthToken token, long number ) {

        CaseObject caseObject = caseObjectDAO.getCase( En_CaseType.CRM_SUPPORT, number );

        if ( !hasAccessForCaseObject( token, En_Privilege.ISSUE_VIEW, caseObject ) ) {
            return new CoreResponse<CaseObject>().error(En_ResultStatus.PERMISSION_DENIED);
        }

        if(caseObject == null)
            return new CoreResponse().error(En_ResultStatus.NOT_FOUND);

        jdbcManyRelationsHelper.fillAll( caseObject.getInitiatorCompany() );
        jdbcManyRelationsHelper.fill( caseObject, "attachments");
        jdbcManyRelationsHelper.fill( caseObject, "notifiers");

        CoreResponse<List<CaseLink>> caseLinks = caseLinkService.getLinks(token, caseObject.getId());
        if (caseLinks.isOk()) {
            caseObject.setLinks(caseLinks.getData());
        }


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
        if ( CollectionUtils.isNotEmpty(caseObject.getNotifiers())) {
            caseObject.getNotifiers().forEach(Person::resetPrivacyInfo);
        }

        return new CoreResponse<CaseObject>().success(caseObject);
    }

    @Override
    @Transactional
    public CoreResponse< CaseObject > saveCaseObject( AuthToken token, CaseObject caseObject, Person initiator ) {

        if (caseObject == null)
            return new CoreResponse().error(En_ResultStatus.INCORRECT_PARAMS);

        applyCaseByScope( token, caseObject );
        if ( !hasAccessForCaseObject( token, En_Privilege.ISSUE_EDIT, caseObject ) ) {
            return new CoreResponse<CaseObject>().error( En_ResultStatus.PERMISSION_DENIED );
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
            return new CoreResponse().error(En_ResultStatus.NOT_CREATED);
        else
            caseObject.setId(caseId);

        Long stateMessageId = createAndPersistStateMessage(initiator, caseId, caseObject.getState(), caseObject.getTimeElapsed(), caseObject.getTimeElapsedType());
        if(stateMessageId == null)
            log.error("State message for the issue %d not saved!", caseId);

        Long impMessageId = createAndPersistImportanceMessage(initiator, caseId, caseObject.getImpLevel());
        if (impMessageId == null) {
            log.error("Importance level message for the issue %d not saved!", caseId);
        }

        if(CollectionUtils.isNotEmpty(caseObject.getAttachments())){
            caseAttachmentDAO.persistBatch(
                    caseObject.getAttachments()
                            .stream()
                            .map(a -> new CaseAttachment(caseId, a.getId()))
                            .collect(Collectors.toList())
            );
        }

        if(CollectionUtils.isNotEmpty(caseObject.getNotifiers())){
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

        if (CollectionUtils.isNotEmpty(caseObject.getLinks())) {
            caseLinkService.mergeLinks(token, caseObject.getId(), caseObject.getCaseNumber(), caseObject.getLinks());
        }

        // From GWT-side we get partially filled object, that's why we need to refresh state from db
        CaseObject newState = caseObjectDAO.get(caseId);
        newState.setAttachments(caseObject.getAttachments());
        newState.setNotifiers(caseObject.getNotifiers());
        publisherService.publishEvent(new CaseObjectEvent(this, newState, initiator));

        return new CoreResponse<CaseObject>().success( newState );
    }


    @Override
    @Transactional
    public CoreResponse<CaseObject> updateCaseObject( CaseObject caseObject, Person initiator ) {
        if (caseObject == null)
            return new CoreResponse().error(En_ResultStatus.INCORRECT_PARAMS);

        jdbcManyRelationsHelper.persist(caseObject, "notifiers");

        CaseObject oldState = caseObjectDAO.get(caseObject.getId());

        applyStateBasedOnManager(caseObject);

        if(isCaseHasNoChanges(caseObject, oldState))
            return new CoreResponse<CaseObject>().success( caseObject ); //ignore

        En_CaseStateWorkflow workflow = CaseStateWorkflowUtil.recognizeWorkflow(caseObject);
        boolean isStateTransitionValid = caseStateWorkflowValidator.isCaseStateTransitionValid(workflow, oldState.getState(), caseObject.getState());
        if (!isStateTransitionValid) {
            return new CoreResponse<CaseObject>().error(En_ResultStatus.INCORRECT_PARAMS);
        }

        caseObject.setModified(new Date());
        caseObject.setTimeElapsed(caseCommentService.getTimeElapsed(caseObject.getId()).getData());

        if (CollectionUtils.isNotEmpty(caseObject.getNotifiers())) {
            // update partially filled objects
            caseObject.setNotifiers(new HashSet<>(
                    personDAO.partialGetListByKeys(
                            caseObject.getNotifiers().stream().map(Person::getId).collect(Collectors.toList()),
                            "id", "contactInfo")
            ));
        }

        boolean isUpdated = caseObjectDAO.merge(caseObject);
        if (!isUpdated)
            return new CoreResponse().error(En_ResultStatus.NOT_UPDATED);

        if(oldState.getState() != caseObject.getState()){
            Long messageId = createAndPersistStateMessage(initiator, caseObject.getId(), caseObject.getState(), null, null);
            if(messageId == null)
                log.error("State message for the issue %d isn't saved!", caseObject.getId());
        }

        if (!oldState.getImpLevel().equals(caseObject.getImpLevel())) {
            Long messageId = createAndPersistImportanceMessage(initiator, caseObject.getId(), caseObject.getImpLevel());
            if (messageId == null) {
                log.error("Importance level message for the issue %d isn't saved!", caseObject.getId());
            }
        }

        // From GWT-side we get partially filled object, that's why we need to refresh state from db
        CaseObject newState = caseObjectDAO.get(caseObject.getId());
        newState.setAttachments(caseObject.getAttachments());
        newState.setNotifiers(caseObject.getNotifiers());
        jdbcManyRelationsHelper.fill( oldState, "attachments");

        publisherService.publishEvent(new CaseObjectEvent(this, newState, oldState, initiator));

        return new CoreResponse<CaseObject>().success( caseObject );
    }

    @Override
    @Transactional
    public CoreResponse< CaseObject > updateCaseObject( AuthToken token, CaseObject caseObject ) {
        if ( !hasAccessForCaseObject( token, En_Privilege.ISSUE_EDIT, caseObject ) ) {
            return new CoreResponse<CaseObject>().error( En_ResultStatus.PERMISSION_DENIED );
        }
        UserSessionDescriptor descriptor = authService.findSession( token );

        caseLinkService.mergeLinks(token, caseObject.getId(), caseObject.getCaseNumber(), caseObject.getLinks());

        return updateCaseObject (caseObject, descriptor.getPerson());
    }

    @Override
    public CoreResponse<List<En_CaseState>> stateList( En_CaseType caseType ) {
        List<En_CaseState> states = caseStateMatrixDAO.getStatesByCaseType(caseType);

        if (states == null)
            return new CoreResponse<List<En_CaseState>>().error(En_ResultStatus.GET_DATA_ERROR);

        return new CoreResponse<List<En_CaseState>>().success(states);
    }

    @Override
    public CoreResponse<Long> count( AuthToken token, CaseQuery query ) {

        applyFilterByScope( token, query );

        Long count = caseShortViewDAO.count(query);

        if (count == null)
            return new CoreResponse<Long>().error(En_ResultStatus.GET_DATA_ERROR, 0L);

        return new CoreResponse<Long>().success(count);
    }

    @Override
    public CoreResponse<Boolean> updateCaseModified( AuthToken token, Long caseId, Date modified) {
        if(caseId == null || !caseObjectDAO.checkExistsByKey(caseId))
            return new CoreResponse<Boolean>().error(En_ResultStatus.INCORRECT_PARAMS);

        CaseObject caseObject = new CaseObject(caseId);
        caseObject.setModified(modified == null? new Date(): modified);

        boolean isUpdated = caseObjectDAO.partialMerge(caseObject, "MODIFIED");

        return new CoreResponse<Boolean>().success(isUpdated);
    }

    @Override
    public CoreResponse<Boolean> updateExistsAttachmentsFlag(Long caseId, boolean flag){
        if(caseId == null)
            return new CoreResponse<Boolean>().error(En_ResultStatus.INCORRECT_PARAMS);

        CaseObject caseObject = new CaseObject(caseId);
        caseObject.setAttachmentExists(flag);
        boolean result = caseObjectDAO.partialMerge(caseObject, "ATTACHMENT_EXISTS");

        if(!result)
            return new CoreResponse<Boolean>().error(En_ResultStatus.NOT_UPDATED);
        return new CoreResponse<Boolean>().success(true);
    }

    @Override
    public CoreResponse<Boolean> updateExistsAttachmentsFlag(Long caseId){
        return updateExistsAttachmentsFlag(caseId, isExistsAttachments(caseId));
    }

    @Override
    public CoreResponse<Long> getEmailLastId(Long caseId) {
        if (caseId == null) {
            return new CoreResponse<Long>().error(En_ResultStatus.INCORRECT_PARAMS);
        }

        Long lastMessageId = caseObjectDAO.getEmailLastId(caseId);
        if (lastMessageId == null) {
            lastMessageId = 0L;
        }

        return new CoreResponse<Long>().success(lastMessageId);
    }

    @Override
    public CoreResponse<Boolean> updateEmailLastId(Long caseId, Long emailLastId) {
        if (caseId == null) {
            return new CoreResponse<Boolean>().error(En_ResultStatus.INCORRECT_PARAMS);
        }

        boolean result = caseObjectDAO.updateEmailLastId(caseId, emailLastId);

        return new CoreResponse<Boolean>().success(result);
    }

    @Override
    public CoreResponse<CaseInfo> getCaseShortInfo(AuthToken token, Long caseNumber) {
        if ( !hasAccessForCaseObject( token, En_Privilege.ISSUE_VIEW, caseObjectDAO.getCase(En_CaseType.CRM_SUPPORT, caseNumber) ) ) {
            return new CoreResponse<CaseInfo>().error( En_ResultStatus.PERMISSION_DENIED );
        }

        CaseShortView caseObject = caseShortViewDAO.getCase( caseNumber );

        if(caseObject == null)
            return new CoreResponse().error(En_ResultStatus.NOT_FOUND);

        CaseInfo info = new CaseInfo();
        info.setId(caseObject.getId());
        info.setCaseNumber(caseObject.getCaseNumber());
        info.setPrivateCase(caseObject.isPrivateCase());
        info.setName(caseObject.getName());
        info.setImpLevel(caseObject.getImpLevel());
        info.setStateId(caseObject.getStateId());
        info.setInfo(caseObject.getInfo());

        return new CoreResponse<CaseInfo>().success(info);
    }

    @Override
    @Transactional
    public CoreResponse<Long> bindAttachmentToCaseNumber(AuthToken token, En_CaseType caseType, Attachment attachment, long caseNumber) {
        casePrivilegeValidator.checkPrivilegesModify(token, caseType);

        CaseObject caseObject = caseObjectDAO.getCase(caseType, caseNumber);
        if ( !hasAccessForCaseObject( token, En_Privilege.ISSUE_EDIT, caseObject ) ) {
            return new CoreResponse<Long>().error( En_ResultStatus.PERMISSION_DENIED );
        }
        return attachToCaseId( attachment, caseObject.getId() );
    }

    @Override
    @Transactional
    public CoreResponse<Long> attachToCaseId(Attachment attachment, long caseId) {
        CaseAttachment caseAttachment = new CaseAttachment(caseId, attachment.getId());
        Long caseAttachId = caseAttachmentDAO.persist(caseAttachment);

        if(caseAttachId == null)
            return new CoreResponse<Long>().error(En_ResultStatus.NOT_CREATED);

        CaseObject caseObject = new CaseObject(caseId);
        caseObject.setAttachmentExists(true);
        caseObject.setModified(new Date());
        boolean isCaseUpdated = caseObjectDAO.partialMerge(caseObject, "ATTACHMENT_EXISTS", "MODIFIED");

        if(!isCaseUpdated)
            throw new RuntimeException("failed to update case object");

        return new CoreResponse<Long>().success(caseAttachId);
    }

    @Override
    public boolean isExistsAttachments(Long caseId) {
        return caseAttachmentDAO.checkExistsByCondition("case_id = ?", caseId);
    }

    private Long createAndPersistStateMessage(Person author, Long caseId, En_CaseState state, Long timeElapsed, En_TimeElapsedType timeElapsedType){
        CaseComment stateChangeMessage = new CaseComment();
        stateChangeMessage.setAuthor(author);
        stateChangeMessage.setCreated(new Date());
        stateChangeMessage.setCaseId(caseId);
        stateChangeMessage.setCaseStateId((long)state.getId());
        if (timeElapsed != null && timeElapsed > 0L) {
            stateChangeMessage.setTimeElapsed(timeElapsed);
            stateChangeMessage.setTimeElapsedType(timeElapsedType!=null?timeElapsedType:En_TimeElapsedType.NONE);
        }
        return caseCommentDAO.persist(stateChangeMessage);
    }

    private Long createAndPersistImportanceMessage(Person author, Long caseId, En_ImportanceLevel importance) {
        return createAndPersistImportanceMessage(author, caseId, importance.getId());
    }

    private Long createAndPersistImportanceMessage(Person author, Long caseId, Integer importance) {//int -> Integer т.к. падает unit test с NPE, неясно почему
        CaseComment stateChangeMessage = new CaseComment();
        stateChangeMessage.setAuthor(author);
        stateChangeMessage.setCreated(new Date());
        stateChangeMessage.setCaseId(caseId);
        stateChangeMessage.setCaseImpLevel(importance);
        return caseCommentDAO.persist(stateChangeMessage);
    }

    private void applyFilterByScope( AuthToken token, CaseQuery query ) {
        UserSessionDescriptor descriptor = authService.findSession( token );
        Set< UserRole > roles = descriptor.getLogin().getRoles();
        if ( !policyService.hasGrantAccessFor( roles, En_Privilege.ISSUE_VIEW ) ) {
            query.setCompanyIds( acceptAllowedCompanies(query.getCompanyIds(), descriptor.getAllowedCompaniesIds() ) );
            query.setAllowViewPrivate( false );
        }
    }

    private List<Long> acceptAllowedCompanies( List<Long> companyIds, Collection<Long> allowedCompaniesIds ) {
        if(companyIds==null) return new ArrayList<Long>(allowedCompaniesIds);
        ArrayList allowedCompanies = new ArrayList( companyIds );
        allowedCompanies.retainAll( allowedCompaniesIds );
        return allowedCompanies;
    }

    private void applyCaseByScope( AuthToken token, CaseObject caseObject ) {
        UserSessionDescriptor descriptor = authService.findSession( token );
        Set< UserRole > roles = descriptor.getLogin().getRoles();
        if ( !policyService.hasGrantAccessFor( roles, En_Privilege.ISSUE_CREATE ) && policyService.hasScopeForPrivilege( roles, En_Privilege.ISSUE_CREATE, En_Scope.COMPANY ) ) {
            caseObject.setPrivateCase( false );
            if(!descriptor.getAllowedCompaniesIds().contains( caseObject.getInitiatorCompanyId() )) {
                caseObject.setInitiatorCompany( descriptor.getCompany() );
            }
            caseObject.setManagerId( null );
        }
    }

    private boolean isCaseHasNoChanges(CaseObject co1, CaseObject co2){
        // without notifiers
        // without links
        return
                Objects.equals(co1.getName(), co2.getName())
                && Objects.equals(co1.getInfo(), co2.getInfo())
                && Objects.equals(co1.isPrivateCase(), co2.isPrivateCase())
                && Objects.equals(co1.getState(), co2.getState())
                && Objects.equals(co1.getImpLevel(), co2.getImpLevel())
                && Objects.equals(co1.getInitiatorCompanyId(), co2.getInitiatorCompanyId())
                && Objects.equals(co1.getInitiatorId(), co2.getInitiatorId())
                && Objects.equals(co1.getProductId(), co2.getProductId())
                && Objects.equals(co1.getManagerId(), co2.getManagerId());
    }

    @Override
    public boolean hasAccessForCaseObject(AuthToken token, En_Privilege privilege, CaseObject caseObject ) {
        UserSessionDescriptor descriptor = authService.findSession( token );
        Set< UserRole > roles = descriptor.getLogin().getRoles();
        if ( !policyService.hasGrantAccessFor( roles, privilege ) && policyService.hasScopeForPrivilege( roles, privilege, En_Scope.COMPANY ) ) {
            if ( caseObject == null ) {
                return false;
            }

            Collection<Long> companyIds = descriptor.getAllowedCompaniesIds();
            if ( !companyIds.contains( caseObject.getInitiatorCompanyId() ) ) {
                return false;
            }

            if ( caseObject.isPrivateCase() ) {
                return false;
            }
        }
        return true;
    }

    private Set<UserRole> getRoles(AuthToken token) {
        return Optional.ofNullable(authService.findSession(token))
                .map(d -> d.getLogin().getRoles())
                .orElse(null);
    }

    private boolean personBelongsToHomeCompany(AuthToken token) {

        UserSessionDescriptor descriptor = authService.findSession(token);

        if (descriptor == null || descriptor.getCompany() == null || descriptor.getCompany().getCategory() == null) {
            return false;
        }

        return Objects.equals(En_CompanyCategory.HOME.getId(), descriptor.getCompany().getCategory().getId());
    }

    private void applyStateBasedOnManager(CaseObject caseObject) {
        if (caseObject.getState() == En_CaseState.CREATED && caseObject.getManager() != null) {
            caseObject.setState(En_CaseState.OPENED);
        }
    }
}
