package ru.protei.portal.jira.service;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.Attachment;
import com.atlassian.jira.rest.client.api.domain.Comment;
import com.atlassian.jira.rest.client.api.domain.Issue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamSource;
import ru.protei.portal.core.controller.cloud.FileController;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.DateUtils;
import ru.protei.portal.core.service.CaseService;
import ru.protei.portal.jira.factory.JiraClientFactory;
import ru.protei.portal.jira.utils.CommonUtils;
import ru.protei.portal.jira.utils.JiraHookEventData;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class JiraIntegrationServiceImpl implements JiraIntegrationService {

    private static Logger logger = LoggerFactory.getLogger(JiraIntegrationServiceImpl.class);

    @Autowired
    CaseService caseService;

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
    FileController fileController;


    @Override
    public CaseObject create(JiraEndpoint endpoint, JiraHookEventData event) {
        return createCaseObject(event.getIssue(),
                endpoint,
                new CachedPersonMapper(personDAO, endpoint, personDAO.get(endpoint.getPersonId()))
        );
    }

    @Override
    public CaseObject updateOrCreate(JiraEndpoint endpoint, JiraHookEventData event) {
        final Issue issue = event.getIssue();
        final Person defaultPerson = personDAO.get(endpoint.getPersonId());
        final PersonMapper personMapper = new CachedPersonMapper(personDAO, endpoint, defaultPerson);

        CaseObject caseObj = caseObjectDAO.getByExternalAppCaseId(CommonUtils.makeExternalIssueID(endpoint, issue));
        if (caseObj != null) {
            if (event.getUser().getName().equals(endpoint.getServerLogin())) {
                logger.info("skip event to prevent recursion, author is tech-login");
                return null;
            }

            ExternalCaseAppData appData = externalCaseAppDAO.get(caseObj.getId());
            logger.debug("get case external data, ext-id = {}, case-id = {}, sync-state = {}", appData.getExtAppCaseId(), appData.getId(), appData.getExtAppData());

            IssueMergeState mergeState = IssueMergeState.fromJSON(appData.getExtAppData());

            caseObj.setModified(DateUtils.max(issue.getUpdateDate().toDate(), caseObj.getModified()));
            caseObj.setExtAppType("jira");

            updateCaseState(issue, caseObj);
            updateCasePriority(issue, caseObj);

            caseObj.setName(issue.getSummary());
            caseObj.setInfo(issue.getDescription());
            caseObj.setLocal(0);
            caseObj.setInitiatorCompanyId(endpoint.getCompanyId());

            caseObjectDAO.saveOrUpdate(caseObj);

            processComments(issue, caseObj, personMapper, mergeState);

            logger.debug("save case external data, ext-id = {}, case-id = {}, sync-state = {}", appData.getExtAppCaseId(), appData.getId(), appData.getExtAppData());

            externalCaseAppDAO.merge(appData);
        }
        else {
            caseObj = createCaseObject(issue, endpoint, personMapper);
        }
        return caseObj;
    }

    private CaseObject createCaseObject(Issue issue, JiraEndpoint endpoint, PersonMapper personMapper) {
        final CaseObject caseObj = new CaseObject();
        caseObj.setCaseType(En_CaseType.CRM_SUPPORT);
        caseObj.setCreated(issue.getCreationDate().toDate());
        caseObj.setModified(issue.getUpdateDate().toDate());
        caseObj.setInitiator(personMapper.toProteiPerson(issue.getReporter()));
        caseObj.setExtAppType("jira");

        updateCaseState(issue, caseObj);
        updateCasePriority(issue, caseObj);

        caseObj.setName(issue.getSummary());
        caseObj.setInfo(issue.getDescription());
        caseObj.setLocal(0);
        caseObj.setInitiatorCompanyId(endpoint.getCompanyId());
        caseObjectDAO.insertCase(caseObj);

        IssueMergeState mergeState = new IssueMergeState();
        processComments(issue, caseObj, personMapper, mergeState);

        final ExternalCaseAppData appData = new ExternalCaseAppData(caseObj);
        appData.setExtAppCaseId(CommonUtils.makeExternalIssueID(endpoint, issue));
        appData.setId(caseObj.getId());
        appData.setExtAppData(mergeState.toString());

        logger.debug("save case external data, ext-id = {}, case-id = {}, sync-state = {}", appData.getExtAppCaseId(), appData.getId(), appData.getExtAppData());

        externalCaseAppDAO.merge(appData);

        return caseObj;
    }

    private void processComments(Issue issue, CaseObject caseObj, PersonMapper personMapper, IssueMergeState state) {
        logger.debug("process comments on {}", issue.getKey());

        if (issue.getComments() == null) {
            logger.debug("no comments in issue {}", issue.getKey());
            return;
        }

        List<CaseComment> ourCaseComments = new ArrayList<>();
        issue.getComments().forEach(comment -> {
            if (state.hasComment(comment.getId())) {
                logger.debug("skip already merged comment id = {}", comment.getId());
                return;
            }

            CaseComment caseComment = convertComment(caseObj, personMapper, comment);

//            if (skipPerson != null && skipPerson.getId().equals(caseComment.getAuthorId())) {
//                logger.debug("skip comment {} to prevent recursion", comment.getId());
//                return;
//            }

            logger.debug("add new comment, id = {}", comment.getId());
            state.appendComment(comment.getId());
            logger.debug("convert jira-comment {} with text {}", comment.getId(), comment.getBody());
            ourCaseComments.add(caseComment);
        });

        if (!ourCaseComments.isEmpty()) {
            logger.debug("store case comments, size = {}", ourCaseComments.size());
//            ourCaseComments.get(0).setCaseImpLevel(caseObj.getImpLevel());
//            ourCaseComments.get(0).setCaseStateId(caseObj.getStateId());

            logger.debug("before invoke persists batch");
            commentDAO.persistBatch(ourCaseComments);
            logger.debug("after invoke persists batch");
        }
    }


//    private void processAttachments (JiraEndpoint endpoint, Issue issue, CaseObject caseObject, IssueMergeState state, PersonMapper personMapper) {
//        if (issue.getAttachments() == null) {
//            logger.debug("issue {} has no attachments, skip merging", issue.getKey());
//            return;
//        }
//
//        List<Attachment> jiraAttachments = new ArrayList<>();
//
//        issue.getAttachments().forEach(attachment -> {
//            if (state.hasAttachment(attachment.getSelf())) {
//                logger.debug("skip attachment {}", attachment.getSelf());
//            }
//            else {
//                jiraAttachments.add(attachment);
//            }
//        });
//
//        if (!jiraAttachments.isEmpty()) {
//            logger.debug("load attachments for issue {}, size={}", issue.getKey(), jiraAttachments.size());
//            clientFactory.run(endpoint, client-> {
//                jiraAttachments.forEach(jiraAttachment -> {
//                    logger.debug("load attachment issue={}, uri={}", issue.getKey(), jiraAttachment.getContentUri());
//
//                    ru.protei.portal.core.model.ent.Attachment a = convertAttachment(personMapper, jiraAttachment);
//
//                    if (fileController.saveAttachment(a, new JiraAttachmentSource(client, jiraAttachment), caseObject.getId()) != null)
//                        state.appendAttachment(jiraAttachment.getSelf());
//                });
//            });
//        }
//    }

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
//        Attachment attachment;
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
//            our.setCaseAttachments();
        our.setCreated(comment.getCreationDate().toDate());
        our.setOriginalAuthorFullName(comment.getAuthor().getDisplayName());
        our.setOriginalAuthorName(comment.getAuthor().getDisplayName());
        our.setText(comment.getBody());
        return our;
    }


    private void updateCaseState(Issue issue, CaseObject caseObj) {
        logger.debug("update case state, issue={}, jira-status = {}, current case state = {}", issue.getKey(), issue.getStatus().getName(), caseObj.getState());

        En_CaseState state = jiraStatusMapEntryDAO.getByJiraStatus(issue.getStatus().getName());
        if (state == null)
            throw new RuntimeException("unable to map jira-status " + issue.getStatus().getName() + " to portal case-state");

        logger.debug("issue {}, case-state old={}, new={}", issue.getKey(), caseObj.getState(), state);
        caseObj.setState(state);
    }

    private void updateCasePriority(Issue issue, CaseObject caseObj) {
        String severityName = CommonUtils.getIssueSeverity(issue);

        logger.debug("update case priority, issue={}, jira-level={}, current case level={}", issue.getKey(), severityName, caseObj.importanceLevel());

        JiraPriorityMapEntry jiraPriorityEntry = jiraPriorityMapEntryDAO.getByJiraPriorityId(severityName);

        if (jiraPriorityEntry == null) {
            logger.warn("unable to map jira-priority level : {}, set as basic", issue.getPriority().getName());
            caseObj.setImpLevel(En_ImportanceLevel.BASIC.getId());
        }
        else {
            logger.debug("issue {}, case-priority old={}, new={}", issue.getKey(), caseObj.importanceLevel(), jiraPriorityEntry.importanceLevel());
            caseObj.setImpLevel(jiraPriorityEntry.getLocalPriorityId());
        }
    }
}