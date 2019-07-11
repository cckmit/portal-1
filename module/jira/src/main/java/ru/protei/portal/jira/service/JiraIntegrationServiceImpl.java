package ru.protei.portal.jira.service;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.Attachment;
import com.atlassian.jira.rest.client.api.domain.Comment;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamSource;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.api.struct.FileStorage;
import ru.protei.portal.core.ServiceModule;
import ru.protei.portal.core.event.AssembledCaseEvent;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.DateUtils;
import ru.protei.portal.core.model.struct.FileStream;
import ru.protei.portal.core.service.AttachmentService;
import ru.protei.portal.core.service.CaseService;
import ru.protei.portal.jira.factory.JiraClientFactory;
import ru.protei.portal.jira.utils.CommonUtils;
import ru.protei.portal.jira.utils.JiraHookEventData;

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
        return createCaseObject(event.getUser(), event.getIssue(),
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
        if (caseObj != null) {
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

            AssembledCaseEvent caseEvent = new AssembledCaseEvent(ServiceModule.JIRA, this, caseObjectDAO.get(caseObj.getId()), caseObj, personMapper.toProteiPerson(event.getUser()));

            ExternalCaseAppData appData = externalCaseAppDAO.get(caseObj.getId());
            logger.debug("get case external data, ext-id = {}, case-id = {}, sync-state = {}", appData.getExtAppCaseId(), appData.getId(), appData.getExtAppData());

            IssueMergeState mergeState = IssueMergeState.fromJSON(appData.getExtAppData());

            caseObj.setModified(DateUtils.max(issue.getUpdateDate().toDate(), caseObj.getModified()));
            caseObj.setExtAppType("jira");
//            caseObj.setName(issue.getSummary()); -- update it with priority and info
            caseObj.setLocal(0);
            caseObj.setInitiatorCompanyId(endpoint.getCompanyId());

            updateCaseState(endpoint, issue, caseObj);
            updatePriorityAndInfo(endpoint, issue, caseObj);

            caseObjectDAO.saveOrUpdate(caseObj);

            caseEvent.includeCaseComments(processComments(endpoint, issue, caseObj, personMapper, mergeState));
            caseEvent.includeCaseAttachments(processAttachments(endpoint, issue, caseObj, mergeState, personMapper));

            appData.setExtAppData(mergeState.toString());

            logger.debug("save case external data, ext-id = {}, case-id = {}, sync-state = {}", appData.getExtAppCaseId(), appData.getId(), appData.getExtAppData());

            externalCaseAppDAO.merge(appData);

            return caseEvent;
        }
        else {
            return createCaseObject(event.getUser(), issue, endpoint, personMapper);
        }
    }

    private AssembledCaseEvent createCaseObject(User initiator, Issue issue, JiraEndpoint endpoint, PersonMapper personMapper) {
        final CaseObject caseObj = new CaseObject();
        final AssembledCaseEvent caseEvent = new AssembledCaseEvent(ServiceModule.JIRA, this, null, caseObj, personMapper.toProteiPerson(initiator));

        caseObj.setCaseType(En_CaseType.CRM_SUPPORT);
        caseObj.setCreated(issue.getCreationDate().toDate());
        caseObj.setModified(issue.getUpdateDate().toDate());

        // TODO for what? initiator is null at the moment
        caseObj.setCreatorId(caseObj.getInitiatorId());

        caseObj.setExtAppType("jira");
//        caseObj.setName(issue.getSummary()); -- update it with priority and info
        caseObj.setLocal(0);
        caseObj.setInitiator(personMapper.toProteiPerson(issue.getReporter()));
        caseObj.setInitiatorCompany(companyDAO.get(endpoint.getCompanyId()));
        caseObj.setCreator (caseObj.getInitiator());
        caseObj.setCreatorInfo("jira");

        updateCaseState(endpoint, issue, caseObj);
        updatePriorityAndInfo(endpoint, issue, caseObj);

        caseObjectDAO.insertCase(caseObj);

        IssueMergeState mergeState = new IssueMergeState();

        caseEvent.includeCaseComments(processComments(endpoint, issue, caseObj, personMapper, mergeState));
        caseEvent.includeCaseAttachments(processAttachments(endpoint, issue, caseObj, mergeState, personMapper));

        final ExternalCaseAppData appData = new ExternalCaseAppData(caseObj);
        appData.setExtAppCaseId(CommonUtils.makeExternalIssueID(endpoint, issue));
        appData.setId(caseObj.getId());
        appData.setExtAppData(mergeState.toString());

        logger.debug("save case external data, ext-id = {}, case-id = {}, sync-state = {}", appData.getExtAppCaseId(), appData.getId(), appData.getExtAppData());

        externalCaseAppDAO.merge(appData);

        return caseEvent;
    }

    private List<CaseComment> processComments(JiraEndpoint endpoint, Issue issue, CaseObject caseObj, PersonMapper personMapper, IssueMergeState state) {
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
                                                                                 IssueMergeState state,
                                                                                 PersonMapper personMapper) {
        if (issue.getAttachments() == null) {
            logger.debug("issue {} has no attachments", issue.getKey());
            return Collections.emptyList();
        }

        List<Attachment> jiraAttachments = new ArrayList<>();

        issue.getAttachments().forEach(attachment -> {
            if (state.hasAttachment(attachment.getSelf())) {
                logger.debug("skip attachment {} (exists)", attachment.getSelf());
            }
            else {
                jiraAttachments.add(attachment);
                state.appendAttachment(attachment.getSelf());
            }
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

        CoreResponse<Long> caseAttachId = caseService.attachToCaseId(attachment, caseId);
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
//        logger.debug("update case name, issue={}, case={}", issue.getKey(), caseObj.getCaseNumber());
        caseObj.setName(issue.getKey() + " | " + issue.getSummary());

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
        StringBuilder infoValue = new StringBuilder("Тип: " + issue.getIssueType().getName());

        if (jiraPriorityEntry != null) {
            infoValue.append("\r\n")
                    .append("SLA: ")
                    .append(jiraPriorityEntry.getSlaInfo());
        }

        infoValue.append("\r\n").append(issue.getDescription());

        caseObj.setInfo(infoValue.toString());
    }
}