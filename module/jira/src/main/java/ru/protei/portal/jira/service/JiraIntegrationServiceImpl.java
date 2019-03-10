package ru.protei.portal.jira.service;

import com.atlassian.jira.rest.client.api.domain.Comment;
import com.atlassian.jira.rest.client.api.domain.Issue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.DateUtils;
import ru.protei.portal.core.service.CaseService;
import ru.protei.portal.jira.utils.CommonUtils;
import ru.protei.portal.jira.utils.JiraHookEventData;

import java.util.*;

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


    @Override
    public CaseObject create(JiraHookEventData event) {
        final Issue issue = event.getIssue();
        final JiraEndpoint endpoint = jiraEndpointDAO.getByProjectId(issue.getProject().getId());
        if (endpoint == null) {
//            throw new RuntimeException("unable to get endpoint record")
            logger.warn("unable to find end-point record for jira-issue project {}", issue.getProject());
            return null;
        }
        return createCaseObject(issue, endpoint, new CachedPersonMapper(personDAO, endpoint));
    }


    private CaseObject createCaseObject(Issue issue, JiraEndpoint endpoint, PersonMapper personMapper) {

        final CaseObject caseObj = new CaseObject();
        caseObj.setCaseType(En_CaseType.CRM_SUPPORT);
        caseObj.setCreated(issue.getCreationDate().toDate());
        caseObj.setModified(issue.getUpdateDate().toDate());
        caseObj.setInitiator(personMapper.toProteiPerson(issue.getReporter()));
        caseObj.setExtAppType("jira_nexign");

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
            }
            else {
                logger.debug("add new comment, id = {}", comment.getId());
                state.appendComment(comment.getId());
                logger.debug("convert jira-comment {} with text {}", comment.getId(), comment.getBody());
                ourCaseComments.add(convertComment(caseObj, personMapper, comment));
            }
        });

        if (!ourCaseComments.isEmpty()) {
            logger.debug("store case comments, size = {}", ourCaseComments.size());
            ourCaseComments.get(0).setCaseImpLevel(caseObj.getImpLevel());
            ourCaseComments.get(0).setCaseStateId(caseObj.getStateId());

            logger.debug("before invoke persists batch");
            commentDAO.persistBatch(ourCaseComments);
            logger.debug("after invoke persists batch");
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


    @Override
    public CaseObject updateOrCreate(JiraHookEventData event) {
        final Issue issue = event.getIssue();
        final JiraEndpoint endpoint = jiraEndpointDAO.getByProjectId(issue.getProject().getId());
        final PersonMapper personMapper = new CachedPersonMapper(personDAO, endpoint);
//        final Person person = personDAO.get(endpoint.getPersonId());

        CaseObject caseObj = caseObjectDAO.getByExternalAppCaseId(CommonUtils.makeExternalIssueID(endpoint, issue));
        if (caseObj != null) {
            ExternalCaseAppData appData = externalCaseAppDAO.get(caseObj.getId());
            logger.debug("get case external data, ext-id = {}, case-id = {}, sync-state = {}", appData.getExtAppCaseId(), appData.getId(), appData.getExtAppData());

            IssueMergeState mergeState = IssueMergeState.fromJSON(appData.getExtAppData());

            caseObj.setModified(DateUtils.max(issue.getUpdateDate().toDate(), caseObj.getModified()));
            caseObj.setExtAppType("jira_nexign");

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

//            if (event.getComment() != null) {
//                commentDAO.persist (convertComment(caseObj, personMapper, event.getComment()));
//            }
        }
        else {
            caseObj = createCaseObject(issue, endpoint, personMapper);
        }
        return caseObj;
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
        logger.debug("update case priority, issue={}, jira-level={}, current case level={}", issue.getKey(), issue.getPriority().getName(), caseObj.importanceLevel());
        JiraPriorityMapEntry jiraPriorityEntry = jiraPriorityMapEntryDAO.getByJiraPriorityId(issue.getPriority().getName());
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
