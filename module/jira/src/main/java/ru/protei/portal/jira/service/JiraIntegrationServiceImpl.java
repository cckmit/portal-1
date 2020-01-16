package ru.protei.portal.jira.service;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.Attachment;
import com.atlassian.jira.rest.client.api.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamSource;
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
import ru.protei.portal.core.model.helper.DateUtils;
import ru.protei.portal.core.model.struct.FileStream;
import ru.protei.portal.core.model.struct.JiraExtAppData;
import ru.protei.portal.core.model.util.DiffResult;
import ru.protei.portal.core.service.AttachmentService;
import ru.protei.portal.core.service.CaseService;
import ru.protei.portal.jira.factory.JiraClientFactory;
import ru.protei.portal.jira.mapper.CachedPersonMapper;
import ru.protei.portal.jira.mapper.PersonMapper;
import ru.protei.portal.jira.utils.CommonUtils;
import ru.protei.portal.jira.utils.CustomJiraIssueParser;
import ru.protei.portal.jira.dto.JiraHookEventData;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JiraIntegrationServiceImpl implements JiraIntegrationService {

    private static Logger logger = LoggerFactory.getLogger(JiraIntegrationServiceImpl.class);

    @Autowired
    CaseService caseService;
    @Autowired
    CompanyDAO companyDAO;
    @Autowired
    PersonDAO personDAO;
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
    private JiraStatusMapEntryDAO jiraStatusMapEntryDAO;
    @Autowired
    private JiraPriorityMapEntryDAO jiraPriorityMapEntryDAO;
    @Autowired
    JiraClientFactory clientFactory;
    @Autowired
    FileStorage fileStorage;
    @Autowired
    AttachmentService attachmentService;

    @Override
    public AssembledCaseEvent create(JiraEndpoint endpoint, JiraHookEventData event) {
        return createCaseObject(event.getUser(),
                getInitiatorCompany(endpoint, event),
                event.getIssue(),
                endpoint,
                new CachedPersonMapper(personDAO, endpoint, personDAO.get(endpoint.getPersonId()))
        );
    }

    @Override
    public AssembledCaseEvent updateOrCreate(JiraEndpoint endpoint, JiraHookEventData event) {
        final Issue issue = event.getIssue();
        final Person defaultPerson = personDAO.get(endpoint.getPersonId());
        final PersonMapper personMapper = new CachedPersonMapper(personDAO, endpoint, defaultPerson);

        CaseObject caseObj = caseObjectDAO.getByExternalAppCaseId(CommonUtils.makeExternalIssueID(endpoint, issue));
        if (caseObj == null) {
            return createCaseObject(event.getUser(), getInitiatorCompany(endpoint, event), issue, endpoint, personMapper);
        } else {
            if (CommonUtils.isTechUser(endpoint, event.getUser())) {
                logger.info("skip event to prevent recursion, author is tech-login");
                return null;
            }

            if (caseObj.isDeleted()) {
                logger.debug("our case {} is marked as deleted, skip event", caseObj.defGUID());
                return null;
            }

            if (caseObj.isPrivateCase()) {
                logger.debug("our case {} is marked as private, skip event", caseObj.defGUID());
                return null;
            }

            CaseObject oldCase = caseObjectDAO.get(caseObj.getId());
            CaseObject newCase = caseObj;

            ExternalCaseAppData appData = externalCaseAppDAO.get(caseObj.getId());
            logger.debug("get case external data, ext-id = {}, case-id = {}, sync-state = {}", appData.getExtAppCaseId(), appData.getId(), appData.getExtAppData());

            caseObj.setModified(DateUtils.max(issue.getUpdateDate().toDate(), caseObj.getModified()));
            caseObj.setExtAppType(En_ExtAppType.JIRA.getCode());
            caseObj.setLocal(0);

            updateCaseState(endpoint, issue, caseObj);
            updatePriorityAndInfo(endpoint, issue, caseObj);

            caseObjectDAO.saveOrUpdate(caseObj);

            AssembledCaseEvent caseEvent = generateUpdateEvent(oldCase, newCase, personMapper.toProteiPerson(event.getUser()));

            JiraExtAppData jiraExtAppData = JiraExtAppData.fromJSON(appData.getExtAppData());

            caseEvent.includeCaseComments(processComments(endpoint, issue, caseObj, personMapper, jiraExtAppData));
            caseEvent.includeCaseAttachments(processAttachments(endpoint, issue, caseObj, jiraExtAppData, personMapper));

            addIssueTypeAndSeverity(jiraExtAppData, issue);

            appData.setExtAppData(jiraExtAppData.toString());
            logger.debug("save case external data, ext-id = {}, case-id = {}, sync-state = {}", appData.getExtAppCaseId(), appData.getId(), appData.getExtAppData());
            externalCaseAppDAO.merge(appData);

            return caseEvent;
        }
    }

    private AssembledCaseEvent createCaseObject(User initiator, Company companyInitiator, Issue issue, JiraEndpoint endpoint, PersonMapper personMapper) {
        final CaseObject caseObj = new CaseObject();
        CaseObjectCreateEvent caseObjectCreateEvent = new CaseObjectCreateEvent( this, ServiceModule.JIRA, personMapper.toProteiPerson( initiator ).getId(), caseObj );
        final AssembledCaseEvent caseEvent = new AssembledCaseEvent(caseObjectCreateEvent);
        caseEvent.attachCaseObjectCreateEvent(caseObjectCreateEvent);

        caseObj.setCaseType(En_CaseType.CRM_SUPPORT);
        caseObj.setCreated(issue.getCreationDate().toDate());
        caseObj.setModified(issue.getUpdateDate().toDate());

        // TODO for what? initiator is null at the moment
        caseObj.setCreatorId(caseObj.getInitiatorId());

        caseObj.setExtAppType(En_ExtAppType.JIRA.getCode());
        caseObj.setLocal(0);
        caseObj.setInitiator(personMapper.toProteiPerson(issue.getReporter()));
        caseObj.setInitiatorCompany(companyInitiator);
        caseObj.setCreator (caseObj.getInitiator());
        caseObj.setCreatorInfo("jira");

        updateCaseState(endpoint, issue, caseObj);
        updatePriorityAndInfo(endpoint, issue, caseObj);

        caseObjectDAO.insertCase(caseObj);

        JiraExtAppData jiraExtAppData = new JiraExtAppData();

        caseEvent.includeCaseComments(processComments(endpoint, issue, caseObj, personMapper, jiraExtAppData));
        caseEvent.includeCaseAttachments(processAttachments(endpoint, issue, caseObj, jiraExtAppData, personMapper));

        addIssueTypeAndSeverity(jiraExtAppData, issue);

        final ExternalCaseAppData appData = new ExternalCaseAppData(caseObj);
        appData.setExtAppCaseId(CommonUtils.makeExternalIssueID(endpoint, issue));
        appData.setId(caseObj.getId());
        appData.setExtAppData(jiraExtAppData.toString());

        logger.debug("save case external data, ext-id = {}, case-id = {}, sync-state = {}", appData.getExtAppCaseId(), appData.getId(), appData.getExtAppData());

        externalCaseAppDAO.merge(appData);

        return caseEvent;
    }

    private Company getInitiatorCompany(JiraEndpoint endpoint, JiraHookEventData issue) {
        IssueField field =  issue.getIssue().getFieldByName(CustomJiraIssueParser.COMPANY_GROUP_CODE_NAME);
        String companyGroup = (field == null) ? null : field.getValue().toString();
        if (companyGroup != null) {
            JiraCompanyGroup jiraCompanyGroup = jiraCompanyGroupDAO.getByName(companyGroup);
            if (jiraCompanyGroup != null) {
                return jiraCompanyGroup.getCompany();
            }
        }

        return companyDAO.get(endpoint.getCompanyId());
    }

    private List<CaseComment> processComments(JiraEndpoint endpoint, Issue issue, CaseObject caseObj, PersonMapper personMapper, JiraExtAppData state) {
        logger.debug("process comments on {}", issue.getKey());

        if (issue.getComments() == null) {
            logger.debug("no comments in issue {}", issue.getKey());
            return Collections.emptyList();
        }

        List<CaseComment> ourCaseComments = new ArrayList<>();
        issue.getComments().forEach(comment -> {
            if (state.hasComment(comment.getId())) {
                logger.debug("skip already merged comment id = {}", comment.getId());
                return;
            }

            if (CommonUtils.isTechUser(endpoint, comment.getUpdateAuthor()) ||
                    CommonUtils.isTechUser(endpoint, comment.getAuthor())) {
                logger.debug("skip our comment {}, it's by tech-login", comment.getId());
                return;
            }

            CaseComment caseComment = convertComment(caseObj, personMapper, comment);

            logger.debug("add new comment, id = {}", comment.getId());
            state.appendComment(comment.getId());
            logger.debug("convert jira-comment {} with text {}", comment.getId(), comment.getBody());
            ourCaseComments.add(caseComment);
        });

        if (!ourCaseComments.isEmpty()) {
            logger.debug("store case comments, size = {}", ourCaseComments.size());
            commentDAO.persistBatch(ourCaseComments);
        }

        return ourCaseComments;
    }


    private List<ru.protei.portal.core.model.ent.Attachment> processAttachments (JiraEndpoint endpoint,
                                                                                 Issue issue,
                                                                                 CaseObject caseObject,
                                                                                 JiraExtAppData state,
                                                                                 PersonMapper personMapper) {
        if (issue.getAttachments() == null) {
            logger.debug("issue {} has no attachments", issue.getKey());
            return Collections.emptyList();
        }

        List<Attachment> jiraAttachments = new ArrayList<>();

        issue.getAttachments().forEach(attachment -> {
            if (state.hasAttachment(attachment.getSelf().toString())) {
                logger.debug("skip attachment {} (exists)", attachment.getSelf());
                return;
            }

            if (CommonUtils.isTechUser(endpoint, attachment.getAuthor())) {
                logger.debug("skip our attachment {}, it's by tech-login", attachment.getSelf());
                return;
            }

            jiraAttachments.add(attachment);
            state.appendAttachment(attachment.getSelf().toString());
        });

        if (jiraAttachments.isEmpty())
            return Collections.emptyList();


        List<ru.protei.portal.core.model.ent.Attachment> addedAttachments = new ArrayList<>();

        logger.debug("load attachments for issue {}, size={}", issue.getKey(), jiraAttachments.size());

        clientFactory.forEach(endpoint, jiraAttachments, (client, jiraAttachment) -> {
            try {
                logger.debug("load attachment issue={}, uri={}", issue.getKey(), jiraAttachment.getContentUri());

                ru.protei.portal.core.model.ent.Attachment a = convertAttachment(personMapper, jiraAttachment);

                storeAttachment(a, new JiraAttachmentSource(client, jiraAttachment), caseObject.getId());

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

    private void storeAttachment (ru.protei.portal.core.model.ent.Attachment attachment, InputStreamSource content, long caseId) throws Exception {

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

        Result<Long> caseAttachId = caseService.attachToCaseId(attachment, caseId);
        if(caseAttachId.isError())
            throw new SQLException("unable to bind attachment to case");

        logger.debug("new attachment id {}", caseAttachId.getData());
    }

    private ru.protei.portal.core.model.ent.Attachment convertAttachment(PersonMapper personMapper, Attachment jiraAttachment) {
        ru.protei.portal.core.model.ent.Attachment a = new ru.protei.portal.core.model.ent.Attachment();
        a.setCreated(jiraAttachment.getCreationDate().toDate());
        a.setCreatorId(personMapper.toProteiPerson(CommonUtils.fromBasicUserInfo(jiraAttachment.getAuthor())).getId());
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

    private CaseComment convertComment(CaseObject caseObj, PersonMapper personMapper, Comment comment) {
        CaseComment our = new CaseComment();
        our.setCaseId(caseObj.getId());
        our.setAuthor(personMapper.toProteiPerson(CommonUtils.fromBasicUserInfo(comment.getAuthor())));
        our.setCreated(comment.getCreationDate().toDate());
        our.setOriginalAuthorFullName(comment.getAuthor().getDisplayName());
        our.setOriginalAuthorName(comment.getAuthor().getDisplayName());
        our.setText(comment.getBody());
        return our;
    }

    private void updateCaseState(JiraEndpoint endpoint, Issue issue, CaseObject caseObj) {
        logger.debug("update case state, issue={}, jira-status = {}, current case state = {}", issue.getKey(), issue.getStatus().getName(), caseObj.getState());

        En_CaseState state = jiraStatusMapEntryDAO.getByJiraStatus(endpoint.getStatusMapId(), issue.getStatus().getName());
        if (state == null)
            throw new RuntimeException("unable to map jira-status " + issue.getStatus().getName() + " to portal case-state");

        logger.debug("issue {}, case-state old={}, new={}", issue.getKey(), caseObj.getState(), state);
        caseObj.setState(state);
    }

    private void updatePriorityAndInfo(JiraEndpoint endpoint, Issue issue, CaseObject caseObj) {
        logger.debug("update case name, issue={}, case={}", issue.getKey(), caseObj.getCaseNumber());
        IssueField issueCLM = issue.getFieldByName(CustomJiraIssueParser.CUSTOM_FIELD_CLM);
        caseObj.setName((issueCLM == null ? "" : issueCLM.getValue() + " | ") + issue.getSummary());

        // update severity
        String severityName = CommonUtils.getIssueSeverity(issue);

        logger.debug("update case priority, issue={}, jira-level={}, current case level={}", issue.getKey(), severityName, caseObj.importanceLevel());

        JiraPriorityMapEntry jiraPriorityEntry = severityName != null ? jiraPriorityMapEntryDAO.getByJiraPriorityName(endpoint.getPriorityMapId(), severityName) : null;

        if (jiraPriorityEntry == null) {
            logger.warn("unable to map jira-priority level : {}, set as basic", issue.getPriority().getName());
            caseObj.setImpLevel(En_ImportanceLevel.BASIC.getId());
        }
        else {
            logger.debug("issue {}, case-priority old={}, new={}", issue.getKey(), caseObj.importanceLevel(), jiraPriorityEntry.importanceLevel());
            caseObj.setImpLevel(jiraPriorityEntry.getLocalPriorityId());
        }

        // update info (description)

        caseObj.setInfo(issue.getDescription());
    }

    private JiraExtAppData addIssueTypeAndSeverity(JiraExtAppData jiraExtAppData, Issue issue) {
        String issueType = issue.getIssueType().getName();
        jiraExtAppData.setIssueType(issueType);
        boolean isSeverityShouldBeSaved = En_JiraSLAIssueType.byJira().contains(En_JiraSLAIssueType.forIssueType(issueType));
        if (isSeverityShouldBeSaved) {
            String severity = CommonUtils.getIssueSeverity(issue);
            jiraExtAppData.setSlaSeverity(severity);
        }
        return jiraExtAppData;
    }

    private AssembledCaseEvent generateUpdateEvent(CaseObject oldCase, CaseObject newCase, Person person) {

        CaseObjectMeta oldCaseMeta = new CaseObjectMeta(oldCase);
        CaseObjectMeta newCaseMeta = new CaseObjectMeta(newCase);

        CaseNameAndDescriptionEvent caseNameAndDescriptionEvent = new CaseNameAndDescriptionEvent(
                this,
                newCase.getId(),
                new DiffResult<>(oldCase.getName(), newCase.getName()),
                new DiffResult<>(oldCase.getInfo(), newCase.getInfo()),
                person.getId(),
                ServiceModule.JIRA,
                En_ExtAppType.JIRA
        );

        CaseObjectMetaEvent caseObjectMetaEvent = new CaseObjectMetaEvent(
                this,
                ServiceModule.JIRA,
                person.getId(),
                En_ExtAppType.JIRA,
                oldCaseMeta,
                newCaseMeta
        );

        AssembledCaseEvent caseEvent = new AssembledCaseEvent(caseNameAndDescriptionEvent);
        caseEvent.attachCaseNameAndDescriptionEvent(caseNameAndDescriptionEvent);
        caseEvent.attachCaseObjectMetaEvent(caseObjectMetaEvent);
        caseEvent.setLastCaseObject(newCase);

        return caseEvent;
    }
}