package ru.protei.portal.jira.service;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.Attachment;
import com.atlassian.jira.rest.client.api.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamSource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.FileStorage;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.ServiceModule;
import ru.protei.portal.core.event.AssembledCaseEvent;
import ru.protei.portal.core.event.CaseNameAndDescriptionEvent;
import ru.protei.portal.core.event.CaseObjectCreateEvent;
import ru.protei.portal.core.event.CaseObjectMetaEvent;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.query.PlatformQuery;
import ru.protei.portal.core.model.struct.FileStream;
import ru.protei.portal.core.model.struct.JiraExtAppData;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.util.DiffResult;
import ru.protei.portal.core.service.AttachmentService;
import ru.protei.portal.core.service.CaseService;
import ru.protei.portal.core.model.util.DateUtils;
import ru.protei.portal.core.utils.EntityCache;
import ru.protei.portal.core.utils.JiraUtils;
import ru.protei.portal.jira.dto.JiraHookEventData;
import ru.protei.portal.jira.factory.JiraClientFactory;
import ru.protei.portal.jira.mapper.CachedPersonMapper;
import ru.protei.portal.jira.mapper.PersonMapper;
import ru.protei.portal.jira.utils.CustomJiraIssueParser;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static com.atlassian.jira.rest.client.api.domain.Visibility.Type.ROLE;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;
import static ru.protei.portal.core.model.helper.CollectionUtils.isEmpty;
import static ru.protei.portal.core.model.helper.CollectionUtils.stream;
import static ru.protei.portal.core.utils.JiraUtils.getDescriptionWithReplacedImagesFromJira;
import static ru.protei.portal.core.utils.JiraUtils.setTextWithReplacedImagesFromJira;
import static ru.protei.portal.jira.config.JiraConfigurationContext.JIRA_INTEGRATION_SINGLE_TASK_QUEUE;

public class JiraIntegrationServiceImpl implements JiraIntegrationService {

    private static Logger logger = LoggerFactory.getLogger(JiraIntegrationServiceImpl.class);

    @Autowired
    CaseService caseService;
    @Autowired
    PersonDAO personDAO;
    @Autowired
    private ContactItemDAO contactItemDAO;
    @Autowired
    private JdbcManyRelationsHelper jdbcManyRelationsHelper;
    @Autowired
    JiraEndpointDAO jiraEndpointDAO;
    @Autowired
    JiraCompanyGroupDAO jiraCompanyGroupDAO;

    @Autowired
    private ExternalCaseAppDAO externalCaseAppDAO;
    @Autowired
    private CaseObjectDAO caseObjectDAO;
    @Autowired
    private CaseCommentDAO commentDAO;
    @Autowired
    private CaseAttachmentDAO caseAttachmentDAO;
    @Autowired
    private JiraStatusMapEntryDAO jiraStatusMapEntryDAO;
    @Autowired
    private JiraPriorityMapEntryDAO jiraPriorityMapEntryDAO;
    @Autowired
    JiraClientFactory clientFactory;
    @Autowired
    FileStorage fileStorage;
    @Autowired
    AttachmentService attachmentService;
    @Autowired
    AttachmentDAO attachmentDAO;
    @Autowired
    PlatformDAO platformDAO;
    @Autowired
    HistoryDAO historyDAO;
    @Autowired
    CaseStateDAO caseStateDAO;

    private EntityCache<JiraEndpoint> jiraEndpointCache;
    private EntityCache<JiraEndpoint> jiraEndpointCache() {
        if (jiraEndpointCache == null) {
            jiraEndpointCache = new EntityCache<>(jiraEndpointDAO, TimeUnit.MINUTES.toMillis(10));
        }
        return jiraEndpointCache;
    }

    @Override
    public Result<JiraEndpoint> selectEndpoint( Issue issue, Long originalCompanyId ) {
        JiraEndpoint endpoint = jiraEndpointCache().findFirst( ep ->
                Objects.equals(ep.getCompanyId(), originalCompanyId) &&
                        Objects.equals(ep.getProjectId(), String.valueOf(issue.getProject().getId()))
        );

        if (endpoint == null) return error( En_ResultStatus.NOT_FOUND );

        CaseObject caseObj = caseObjectDAO.getByExternalAppCaseId(makeExternalIssueID(endpoint.getId(), issue));
        if (caseObj != null) return ok(endpoint);

        Long endpointCompanyId = selectEndpointCompanyId(issue, originalCompanyId);
        logger.info("jiraWebhook() map company id and issue field 'companygroup' in endpoint companyId: endpointCompanyId={}", endpointCompanyId);
        endpoint = jiraEndpointCache().findFirst( ep ->
                Objects.equals( ep.getCompanyId(), endpointCompanyId ) &&
                        Objects.equals( ep.getProjectId(), String.valueOf( issue.getProject().getId() ) ) );

        return ok(endpoint);
    }

    @Async(JIRA_INTEGRATION_SINGLE_TASK_QUEUE)
    @Override
    @Transactional
    public CompletableFuture<AssembledCaseEvent> create( JiraEndpoint endpoint, JiraHookEventData event) {
        final Issue issue = event.getIssue();
        IssueField projectIdField = getProjectId(issue);
        if (projectIdField != null && caseObjectDAO.isJiraDuplicateByProjectId(projectIdField.getValue().toString())) {
            logger.info( "issue is duplicate by projectId" );
            return null;
        }
        CachedPersonMapper personMapper = new CachedPersonMapper( personDAO, contactItemDAO, jdbcManyRelationsHelper, endpoint, personDAO.get( endpoint.getPersonId() ));
        Long authorId = personMapper.toProteiPerson( event.getUser() ).getId();
        Person initiator = personMapper.toProteiPerson( issue.getReporter() );
        Date createDate = event.getIssue().getCreationDate().toDate();
        return completedFuture( createCaseObject( initiator, authorId, issue, endpoint, personMapper, createDate));
    }

    @Async(JIRA_INTEGRATION_SINGLE_TASK_QUEUE)
    @Override
    @Transactional
    public CompletableFuture<AssembledCaseEvent> updateOrCreate(JiraEndpoint endpoint, JiraHookEventData event) {
        final Issue issue = event.getIssue();
        final Person defaultPerson = personDAO.get(endpoint.getPersonId());
        jdbcManyRelationsHelper.fill(defaultPerson, Person.Fields.CONTACT_ITEMS);
        final PersonMapper personMapper = new CachedPersonMapper(personDAO, contactItemDAO, jdbcManyRelationsHelper, endpoint, defaultPerson);

        User user = event.getUser();
        Long authorId = personMapper.toProteiPerson( event.getUser() ).getId();

        CaseObject caseObj = caseObjectDAO.getByExternalAppCaseId(makeExternalIssueID(endpoint.getId(), issue));
        if (caseObj == null) {
            IssueField projectIdField = getProjectId(issue);
            if (projectIdField != null && caseObjectDAO.isJiraDuplicateByProjectId(projectIdField.getValue().toString())) {
                logger.info( "issue is duplicate by projectId" );
                return null;
            }
            Person initiator = personMapper.toProteiPerson( issue.getReporter() );
            return completedFuture(createCaseObject( initiator, authorId, issue, endpoint, personMapper, new Date(event.getTimestamp())));
        }

        if (isTechUser( endpoint.getServerLogin(), user )) {
            logger.info( "skip event to prevent recursion, author is tech-login" );
            return null;
        }

        return completedFuture(updateCaseObject( authorId, caseObj, event, endpoint, personMapper));
    }

    private AssembledCaseEvent updateCaseObject(Long authorId, CaseObject caseObj, JiraHookEventData event, JiraEndpoint endpoint, PersonMapper personMapper) {
        if (caseObj.isDeleted()) {
            logger.debug("our case {} is marked as deleted, skip event", caseObj.defGUID());
            return null;
        }

        if (caseObj.isPrivateCase()) {
            logger.debug("our case {} is marked as private, skip event", caseObj.defGUID());
            return null;
        }

        final Issue issue = event.getIssue();
        caseObj.setModified(DateUtils.max(issue.getUpdateDate().toDate(), caseObj.getModified()));
        caseObj.setExtAppType(En_ExtAppType.JIRA.getCode());

        //обновлять статус caseObj нужно в случае если новое событие позже последнего по истории
        History lastHistory = historyDAO.getLastHistory(caseObj.getId(), En_HistoryType.CASE_STATE);
        JiraStatusMapEntry newState = getNewCaseState(endpoint.getStatusMapId(), issue.getStatus().getName());
        Date updateDate = event.getIssue().getUpdateDate().toDate();
        if (newState != null && lastHistory.getDate().before(updateDate)){
            caseObj.setStateId(newState.getLocalStatusId());
            caseObj.setStateName(newState.getLocalStatusName());
        }

        ImportanceLevel oldImportance = new ImportanceLevel(caseObj.getImpLevel(), caseObj.getImportanceCode());
        ImportanceLevel newImportance = getNewImportanceLevel(endpoint.getPriorityMapId(), getIssueSeverity(issue));

        if (newImportance == null) {
            newImportance = oldImportance;
        }

        caseObj.setImportanceLevel(newImportance);

        caseObj.setName(getNewName(issue, caseObj.getCaseNumber(), getProjectId(issue)));

        ExternalCaseAppData appData = externalCaseAppDAO.get(caseObj.getId());
        JiraExtAppData jiraExtAppData = JiraExtAppData.fromJSON(appData.getExtAppData());

        List<ru.protei.portal.core.model.ent.Attachment> processedAttachments = processAttachments(endpoint, issue.getAttachments(), caseObj, jiraExtAppData, personMapper);
        List<ru.protei.portal.core.model.ent.Attachment> caseAttachments = attachmentDAO.getAttachmentsByCaseId(caseObj.getId());
        List<CaseAttachment> caseToAttachments = caseAttachmentDAO.getListByCaseId(caseObj.getId());
        caseObj.setInfo(convertDescription(issue.getDescription(), caseAttachments));

        CaseObject oldCase = caseObjectDAO.get(caseObj.getId());
        logger.debug("get case external data, ext-id = {}, case-id = {}, sync-state = {}", appData.getExtAppCaseId(), appData.getId(), appData.getExtAppData());

        caseObjectDAO.merge(caseObj);

        Optional<ChangelogItem> statusChangelogItem = getStatusChangelogItem(event.getChangelogItems());

        if (statusChangelogItem.isPresent()){

            JiraStatusMapEntry fromState = getNewCaseState(endpoint.getStatusMapId(), statusChangelogItem.get().getFromString());
            JiraStatusMapEntry toState = getNewCaseState(endpoint.getStatusMapId(), statusChangelogItem.get().getToString());

            changeStateHistory(updateDate, authorId, caseObj.getId(),
                    fromState.getLocalStatusId(), caseStateDAO.get(fromState.getLocalStatusId()).getState(),
                    toState.getLocalStatusId(), caseStateDAO.get(toState.getLocalStatusId()).getState());
        }

        if (!newImportance.equals(oldImportance)) {
            changeImportanceHistory(new Date(), authorId, caseObj.getId(), oldImportance, newImportance);
        }

        AssembledCaseEvent caseEvent = generateUpdateEvent(oldCase, caseObj, authorId);
        caseEvent.putAddedAttachments(processedAttachments);

        caseEvent.putAddedComments(processComments(endpoint.getServerLogin(),
                issue.getComments(), caseObj, personMapper, jiraExtAppData, caseAttachments, caseToAttachments));

        jiraExtAppData = addIssueTypeAndSeverity(jiraExtAppData, issue.getIssueType().getName(), getIssueSeverity(issue));

        appData.setExtAppData(jiraExtAppData.toString());
        logger.debug("save case external data, ext-id = {}, case-id = {}, sync-state = {}", appData.getExtAppCaseId(), appData.getId(), appData.getExtAppData());
        externalCaseAppDAO.merge(appData);

        return caseEvent;
    }

    private Optional<ChangelogItem> getStatusChangelogItem(Collection<ChangelogItem> changelogItems) {
        return stream(changelogItems).filter(item -> item.getFieldType().equals(FieldType.JIRA)
                && item.getField().equalsIgnoreCase("status")).findFirst();
    }

    private AssembledCaseEvent createCaseObject(Person initiator, Long authorId, Issue issue, JiraEndpoint endpoint, PersonMapper personMapper, Date createDate) {
        CaseObject caseObj = makeCaseObject( issue, initiator );
        caseObj.setInitiatorCompanyId(endpoint.getCompanyId());
        caseObj.setManagerCompanyId(CrmConstants.Company.HOME_COMPANY_ID);

        JiraStatusMapEntry newState = getNewCaseState(endpoint.getStatusMapId(), issue.getStatus().getName());
        if (newState != null) {
            caseObj.setStateId(newState.getLocalStatusId());
            caseObj.setStateName(newState.getLocalStatusName());
        } else {
            caseObj.setStateId(CrmConstants.State.CREATED);
            caseObj.setStateName(CrmConstants.State.CREATED_NAME);
        }
        logger.info("issue {}, case-state new={}", issue.getKey(), caseObj.getStateId());

        ImportanceLevel newImportance = getNewImportanceLevel(endpoint.getPriorityMapId(), getIssueSeverity(issue));

        if (newImportance == null) {
            newImportance = new ImportanceLevel(CrmConstants.ImportanceLevel.BASIC, CrmConstants.ImportanceLevel.BASIC_NAME);
        }

        logger.debug("issue {}, case-priority old={}, new={}", issue.getKey(), caseObj.getImportanceCode(), newImportance);
        caseObj.setImportanceLevel(newImportance);

        IssueField projectId = getProjectId(issue);
        caseObj.setName(getNewName(issue, caseObj.getCaseNumber(), projectId));

        List<Platform> platforms = getPlatforms(caseObj.getInitiatorCompanyId());
        if (platforms != null && platforms.size() == 1) {
            caseObj.setPlatformId(platforms.get(0).getId());
        }

        caseObjectDAO.insertCase(caseObj);

        JiraExtAppData jiraExtAppData = new JiraExtAppData();
        List<ru.protei.portal.core.model.ent.Attachment> addedAttachments =
                processAttachments( endpoint, issue.getAttachments(), caseObj, jiraExtAppData, personMapper );
        List<CaseAttachment> caseToAttachments = caseAttachmentDAO.getListByCaseId(caseObj.getId());
        caseObj.setInfo(convertDescription(issue.getDescription(), addedAttachments));
        caseObjectDAO.merge(caseObj);

        addStateHistory(createDate, authorId, caseObj.getId(), caseObj.getStateId(), caseStateDAO.get(caseObj.getStateId()).getState());
        addImportanceHistory(caseObj.getCreated(), authorId, caseObj.getId(), newImportance);

        List<CaseComment> caseComments = processComments( endpoint.getServerLogin(), issue.getComments(), caseObj,
                personMapper, jiraExtAppData, addedAttachments, caseToAttachments );

        jiraExtAppData = addIssueTypeAndSeverity(jiraExtAppData, issue.getIssueType().getName(), getIssueSeverity(issue));
        if (projectId != null) {
            jiraExtAppData.setProjectId(projectId.getValue().toString());
        }

        final ExternalCaseAppData appData = new ExternalCaseAppData(caseObj);
        appData.setExtAppCaseId(makeExternalIssueID(endpoint.getId(), issue));
        appData.setId(caseObj.getId());
        appData.setExtAppData(jiraExtAppData.toString());

        logger.debug("save case external data, ext-id = {}, case-id = {}, sync-state = {}", appData.getExtAppCaseId(), appData.getId(), appData.getExtAppData());

        externalCaseAppDAO.merge(appData);

        CaseObjectCreateEvent caseObjectCreateEvent = new CaseObjectCreateEvent( this, ServiceModule.JIRA, authorId, caseObj );
        final AssembledCaseEvent caseEvent = new AssembledCaseEvent( caseObjectCreateEvent );
        caseEvent.attachCaseObjectCreateEvent( caseObjectCreateEvent );
        caseEvent.putAddedComments(caseComments);
        caseEvent.putAddedAttachments(addedAttachments);

        return caseEvent;
    }

    private CaseObject makeCaseObject( Issue issue, Person initiator ) {
        final CaseObject caseObj = new CaseObject();

        caseObj.setType(En_CaseType.CRM_SUPPORT);
        caseObj.setCreated(issue.getCreationDate().toDate());
        caseObj.setModified(issue.getUpdateDate().toDate());
        caseObj.setExtAppType(En_ExtAppType.JIRA.getCode());
        caseObj.setInitiator( initiator );
        caseObj.setCreator( initiator );
        caseObj.setCreatorInfo("jira");
        return caseObj;
    }

    private List<CaseComment> processComments(String serverLogin, Iterable<Comment> comments, CaseObject caseObject, PersonMapper personMapper, JiraExtAppData state,
                                              List<ru.protei.portal.core.model.ent.Attachment> attachments, List<CaseAttachment> caseToAttachment) {
        logger.debug("process comments on caseObject.id={}", caseObject.getId());

        if (comments == null) {
            logger.debug("no comments in caseObject.id={}", caseObject.getId());
            return Collections.emptyList();
        }

        List<CaseComment> ourCaseComments = new ArrayList<>();
        for(Comment comment : comments){
            if (state.hasComment(comment.getId())) {
                logger.info("skip already merged comment id = {}", comment.getId());
                continue;
            }

            if (isTechUser(serverLogin, comment.getUpdateAuthor()) ||
                    isTechUser(serverLogin, comment.getAuthor())) {
                logger.info("skip our comment {}, it's by tech-login", comment.getId());
                continue;
            }

            CaseComment caseComment = convertComment(caseObject, personMapper, comment, attachments, caseToAttachment);

            logger.debug("add new comment, id = {}", comment.getId());
            state.appendComment(comment.getId());
            logger.debug("convert jira-comment {} with text {}", comment.getId(), comment.getBody());
            ourCaseComments.add(caseComment);
        };

        if (!ourCaseComments.isEmpty()) {
            logger.debug("store case comments, size = {}", ourCaseComments.size());
            commentDAO.persistBatch(ourCaseComments);
            ourCaseComments.forEach(caseComment -> {
                if (!isEmpty(caseComment.getCaseAttachments())) {
                    caseComment.getCaseAttachments().forEach(caseAttachment -> {
                        caseAttachment.setCommentId(caseComment.getId());
                        caseAttachmentDAO.saveOrUpdate(caseAttachment);
                    });
                }
            });
        }

        return ourCaseComments;
    }

    private List<ru.protei.portal.core.model.ent.Attachment> processAttachments (JiraEndpoint endpoint,
                                                    Iterable<Attachment> attachments,
                                                    CaseObject caseObject,
                                                    JiraExtAppData state,
                                                    PersonMapper personMapper) {
        if (attachments == null) {
            logger.debug("issue caseObjectId={} has no attachments", caseObject.getId());
            return Collections.emptyList();
        }

        List<Attachment> jiraAttachments = new ArrayList<>();

        for (Attachment attachment : attachments) {
            if (state.hasAttachment(attachment.getSelf().toString())) {
                logger.debug("skip attachment {} (exists)", attachment.getSelf());
                continue;
            }

            if (isTechUser(endpoint.getServerLogin(), attachment.getAuthor())) {
                logger.debug("skip our attachment {}, it's by tech-login", attachment.getSelf());
                continue;
            }

            jiraAttachments.add(attachment);
            state.appendAttachment(attachment.getSelf().toString());
        }

        if (jiraAttachments.isEmpty()) {
            return Collections.emptyList();
        }

        List<ru.protei.portal.core.model.ent.Attachment> addedAttachments = new ArrayList<>();

        logger.debug("load attachments for caseObjectId {}, size={}", caseObject.getId(), jiraAttachments.size());

        clientFactory.forEach(endpoint, jiraAttachments, (client, jiraAttachment) -> {
            try {
                logger.debug("load attachment caseObjectId={}, uri={}", caseObject.getId(), jiraAttachment.getContentUri());

                ru.protei.portal.core.model.ent.Attachment a = convertAttachment(personMapper, jiraAttachment);

                storeAttachment(a, new JiraAttachmentSource(client, jiraAttachment), caseObject);

                addedAttachments.add(a);
            }
            catch (Throwable e) {
                logger.error("unable to store attachment {}", jiraAttachment.getContentUri(), e);
            }
        });

        return addedAttachments;
    }

    private String generateCloudFileName(Long id, String filename){
        int i = filename.lastIndexOf(".");
        if (i <= 0)
            return String.valueOf(id);
        return id + filename.substring(i);
    }

    private void storeAttachment (ru.protei.portal.core.model.ent.Attachment attachment, InputStreamSource content, CaseObject caseObject) throws Exception {

        if(attachmentService.saveAttachment(attachment).isError()) {
            throw new SQLException("attachment not saved");
        }

        try (InputStream contentStream = content.getInputStream()) {
            String filePath =  fileStorage.save(
                    generateCloudFileName(attachment.getId(), attachment.getFileName()),
                    new FileStream(contentStream, attachment.getDataSize(), attachment.getMimeType())
            );

            attachment.setExtLink(filePath);
        }

        if(attachmentService.saveAttachment(attachment).isError()) {
            fileStorage.deleteFile(attachment.getExtLink());
            throw new SQLException("unable to save link to file");
        }

        Result<Long> caseAttachId = caseService.attachToCaseId(attachment, caseObject.getId(), caseObject.isPrivateCase());
        if(caseAttachId.isError())
            throw new SQLException("unable to bind attachment to case");

        logger.debug("new attachment id {}", caseAttachId.getData());
    }

    private ru.protei.portal.core.model.ent.Attachment convertAttachment(PersonMapper personMapper, Attachment jiraAttachment) {
        ru.protei.portal.core.model.ent.Attachment a = new ru.protei.portal.core.model.ent.Attachment();
        a.setCreated(jiraAttachment.getCreationDate().toDate());
        a.setCreatorId(personMapper.toProteiPerson(fromBasicUserInfo(jiraAttachment.getAuthor())).getId());
        a.setDataSize((long)jiraAttachment.getSize());
        a.setFileName(jiraAttachment.getFilename());
        a.setMimeType(jiraAttachment.getMimeType());
        a.setLabelText(jiraAttachment.getFilename());
        return a;
    }

    class JiraAttachmentSource implements InputStreamSource {

        JiraRestClient client;
        URI contentURI;

        public JiraAttachmentSource(JiraRestClient client, Attachment attachment) {
            this.client = client;
            this.contentURI = attachment.getContentUri();
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return client.getIssueClient().getAttachment(contentURI).claim();
        }
    }

    private CaseComment convertComment(CaseObject caseObject, PersonMapper personMapper, Comment comment,
                                       List<ru.protei.portal.core.model.ent.Attachment> attachments,
                                       List<CaseAttachment> caseToAttachment) {
        CaseComment our = new CaseComment();
        our.setCaseId(caseObject.getId());
        our.setAuthor(personMapper.toProteiPerson(fromBasicUserInfo(comment.getAuthor())));
        our.setCreated(comment.getCreationDate().toDate());
        our.setOriginalAuthorFullName(comment.getAuthor().getDisplayName());
        our.setOriginalAuthorName(comment.getAuthor().getDisplayName());
        our.setText(comment.getBody());
        our.setPrivacyType(makePrivacyType(comment));

        if (attachments != null) {
            replaceImageLink(our, attachments, caseToAttachment);
        }

        return our;
    }

    private String convertDescription(String originalDescription, List<ru.protei.portal.core.model.ent.Attachment> attachments) {
        String description = originalDescription;
        if (!StringUtils.isEmpty(description) && !attachments.isEmpty()) {
            description = getDescriptionWithReplacedImagesFromJira(originalDescription, attachments);
        }

        return description;
    }

    private void replaceImageLink(CaseComment caseComment, List<ru.protei.portal.core.model.ent.Attachment> attachments, Collection<CaseAttachment> caseToAttachment) {
        setTextWithReplacedImagesFromJira(caseComment, attachments, caseToAttachment);
    }

    private JiraStatusMapEntry getNewCaseState(Long statusMapId, String issueStatusName) {

        JiraStatusMapEntry state = jiraStatusMapEntryDAO.getByJiraStatus(statusMapId, issueStatusName);
        if (state == null){
            logger.error("unable to map jira-status " + issueStatusName + " to portal case-state");
        }

        return state;
    }

    private String getNewName(Issue issue, Long caseNumber, IssueField issueProjectId){
        logger.debug("update case name, issue={}, case={}", issue.getKey(), caseNumber);
        return (issueProjectId == null ? "" : issueProjectId.getValue() + " | ") + issue.getSummary();
    }

    private IssueField getProjectId(Issue issue) {
        return issue.getFieldByName(CustomJiraIssueParser.CUSTOM_FIELD_PROJECT_ID);
    }

    private ImportanceLevel getNewImportanceLevel(Long priorityMapId, String severityName) {
        JiraPriorityMapEntry jiraPriorityEntry = severityName != null ? jiraPriorityMapEntryDAO.getByJiraPriorityName(priorityMapId, severityName) : null;

        if (jiraPriorityEntry == null) {
            logger.warn("unable to map jira-priority level : priorityMapId={}, severityName={}",  priorityMapId,  severityName);
            return null;
        }

        return new ImportanceLevel(jiraPriorityEntry.getLocalPriorityId(), jiraPriorityEntry.getLocalPriorityName());
    }

    private Long addStateHistory(Date date, Long personId, Long caseId, Long stateId, String stateName) {
        return createHistory(date, personId, caseId, En_HistoryAction.ADD, En_HistoryType.CASE_STATE, null, null, stateId, stateName);
    }

    private Long changeStateHistory(Date date, Long personId, Long caseId, Long oldStateId, String oldStateName, Long newStateId, String newStateName) {
        return createHistory(date, personId, caseId, En_HistoryAction.CHANGE, En_HistoryType.CASE_STATE, oldStateId, oldStateName, newStateId, newStateName);
    }

    private Long addImportanceHistory(Date date, Long personId, Long caseId, ImportanceLevel newImportance) {
        return createHistory(date, personId, caseId, En_HistoryAction.ADD, En_HistoryType.CASE_IMPORTANCE, null, null,
                newImportance.getId().longValue(), newImportance.getCode());
    }

    private Long changeImportanceHistory(Date date, Long personId, Long caseId, ImportanceLevel oldImportance, ImportanceLevel newImportance) {
        return createHistory(date, personId, caseId, En_HistoryAction.CHANGE, En_HistoryType.CASE_IMPORTANCE,
                (long)oldImportance.getId(), oldImportance.getCode(), (long)newImportance.getId(), newImportance.getCode());
    }

    private Long createHistory(Date date, Long personId, Long caseObjectId, En_HistoryAction action,
                               En_HistoryType type, Long oldId, String oldValue, Long newId, String newValue) {
        History history = new History();
        history.setInitiatorId(personId);
        history.setDate(date);
        history.setCaseObjectId(caseObjectId);
        history.setAction(action);
        history.setType(type);
        history.setOldId(oldId);
        history.setOldValue(oldValue);
        history.setNewId(newId);
        history.setNewValue(newValue);

        return historyDAO.persist(history);
    }

    private JiraExtAppData addIssueTypeAndSeverity(JiraExtAppData jiraExtAppData, String issueType, String severity) {
        jiraExtAppData.setIssueType(issueType);
        boolean isSeverityShouldBeSaved = En_JiraSLAIssueType.byJira().contains(En_JiraSLAIssueType.forIssueType(issueType));
        if (isSeverityShouldBeSaved) {
            jiraExtAppData.setSlaSeverity(severity);
        }
        return jiraExtAppData;
    }

    private AssembledCaseEvent generateUpdateEvent(CaseObject oldCase, CaseObject newCase, Long authorId) {

        CaseNameAndDescriptionEvent caseNameAndDescriptionEvent = new CaseNameAndDescriptionEvent(
                this,
                newCase.getId(),
                new DiffResult<>(oldCase.getName(), newCase.getName()),
                new DiffResult<>(oldCase.getInfo(), newCase.getInfo()),
                authorId,
                ServiceModule.JIRA,
                En_ExtAppType.JIRA
        );

        CaseObjectMetaEvent caseObjectMetaEvent = new CaseObjectMetaEvent(
                this,
                ServiceModule.JIRA,
                authorId,
                En_ExtAppType.JIRA,
                new CaseObjectMeta(oldCase),
                new CaseObjectMeta(newCase)
        );

        AssembledCaseEvent caseEvent = new AssembledCaseEvent(caseNameAndDescriptionEvent);
        caseEvent.attachCaseNameAndDescriptionEvent(caseNameAndDescriptionEvent);
        caseEvent.attachCaseObjectMetaEvent(caseObjectMetaEvent);
        caseEvent.setLastCaseObject(newCase);

        return caseEvent;
    }

    public static boolean isTechUser (String serverLogin, BasicUser user) {
        return user != null && user.getName().equals(serverLogin);
    }

    public static String getIssueSeverity (Issue issue) {
        IssueField field = issue.getFieldByName(CustomJiraIssueParser.SEVERITY_CODE_NAME);
        return field == null ? null : field.getValue().toString();
    }

    private static Map<String,URI> fakeAvatarURI_map = Collections.singletonMap(User.S48_48, safeURI("https://atlassian.com/"));

    private static URI safeURI (String uri) {
        try {
            return new URI(uri);
        }
        catch (Throwable e) {
            return null;
        }
    }

    private static User fromBasicUserInfo (BasicUser basicUser) {
        return new User(basicUser.getSelf(), basicUser.getDisplayName(), basicUser.getDisplayName(), null, true,
                null, fakeAvatarURI_map, null);
    }

    private static En_CaseCommentPrivacyType makePrivacyType(Comment comment) {
        Visibility visibility = comment.getVisibility();
        if (visibility != null &&
                (visibility.getType() == ROLE && !visibility.getValue().equals(JiraUtils.PROJECT_CUSTOMER_ROLE))) {
            return En_CaseCommentPrivacyType.PRIVATE_CUSTOMERS;
        }
        return En_CaseCommentPrivacyType.PUBLIC;
    }

    private static String makeExternalIssueID (Long endpointId, Issue issue) {
        return endpointId+ "_" + issue.getKey();
    }

    private Long selectEndpointCompanyId(Issue issue, Long originalCompanyId) {
        return Optional.ofNullable(issue.getFieldByName(CustomJiraIssueParser.COMPANY_GROUP_CODE_NAME))
                .map(IssueField::getValue)
                .map(Object::toString)
                .map(jiraCompanyGroupDAO::getByName)
                .map(JiraCompanyGroup::getCompany)
                .map(Company::getId)
                .orElse(originalCompanyId);
    }

    private List<Platform> getPlatforms(Long companyId) {
        PlatformQuery query = new PlatformQuery();
        query.setCompanyId(companyId);
        return platformDAO.listByQuery(query);
    }
}
