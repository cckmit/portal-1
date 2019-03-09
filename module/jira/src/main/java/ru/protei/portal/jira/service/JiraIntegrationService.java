package ru.protei.portal.jira.service;

import com.atlassian.jira.rest.client.api.ExpandableProperty;
import com.atlassian.jira.rest.client.api.domain.BasicUser;
import com.atlassian.jira.rest.client.api.domain.Comment;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_Gender;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.DateUtils;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.struct.PlainContactInfoFacade;
import ru.protei.portal.core.service.CaseService;
import ru.protei.portal.jira.utils.CommonUtils;
import ru.protei.portal.jira.utils.JiraHookEventData;

import java.util.*;

public class JiraIntegrationService {

    private static Logger logger = LoggerFactory.getLogger(JiraIntegrationService.class);

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


    public CaseObject create (JiraHookEventData event) {
        final Issue issue = event.getIssue();
        final JiraEndpoint endpoint = jiraEndpointDAO.getByProjectId(issue.getProject().getId());
        if (endpoint == null) {
//            throw new RuntimeException("unable to get endpoint record")
            logger.warn("unable to find end-point record for jira-issue project {}", issue.getProject());
            return null;
        }
        return createCaseObject(issue, endpoint, new CachedPersonMapper(endpoint));
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

        final ExternalCaseAppData appData = new ExternalCaseAppData(caseObj);
        appData.setExtAppCaseId(CommonUtils.makeExternalIssueID(endpoint, issue));
        appData.setExtAppData(CommonUtils.makeExtAppData(issue));
        appData.setId(caseObj.getId());

        externalCaseAppDAO.merge(appData);

        processAllComments(issue, caseObj, personMapper);

        return caseObj;
    }

    private void processAllComments(Issue issue, CaseObject caseObj, PersonMapper personMapper) {
        List<CaseComment> comments = new ArrayList<>();
        issue.getComments().forEach(comment ->
            comments.add(convertComment(caseObj, personMapper, comment))
        );

        if (!comments.isEmpty()) {
            comments.get(0).setCaseImpLevel(caseObj.getImpLevel());
            comments.get(0).setCaseStateId(caseObj.getStateId());

            commentDAO.persistBatch(comments);
        }
    }

    private CaseComment convertComment(CaseObject caseObj, PersonMapper personMapper, Comment comment) {
        CaseComment our = new CaseComment();
        our.setCaseId(caseObj.getId());
        our.setAuthor(personMapper.toProteiPerson(fromBasicUserInfo(comment.getAuthor())));
//            our.setCaseAttachments();
        our.setCreated(comment.getCreationDate().toDate());
        our.setOriginalAuthorFullName(comment.getAuthor().getDisplayName());
        our.setOriginalAuthorName(comment.getAuthor().getDisplayName());
        return our;
    }

    private User fromBasicUserInfo (BasicUser basicUser) {
        return new User(basicUser.getSelf(), basicUser.getDisplayName(), basicUser.getDisplayName(), null, true,
        null, null, null);
    }

    public CaseObject updateOrCreate (JiraHookEventData event) {
        final Issue issue = event.getIssue();
        final JiraEndpoint endpoint = jiraEndpointDAO.getByProjectId(issue.getProject().getId());
        final PersonMapper personMapper = new CachedPersonMapper(endpoint);
//        final Person person = personDAO.get(endpoint.getPersonId());

        CaseObject caseObj = caseObjectDAO.getByExternalAppCaseId(CommonUtils.makeExternalIssueID(endpoint, issue));
        if (caseObj != null) {
            caseObj.setModified(DateUtils.max(issue.getUpdateDate().toDate(), caseObj.getModified()));
            caseObj.setExtAppType("jira_nexign");

            updateCaseState(issue, caseObj);
            updateCasePriority(issue, caseObj);

            caseObj.setName(issue.getSummary());
            caseObj.setInfo(issue.getDescription());
            caseObj.setLocal(0);
            caseObj.setInitiatorCompanyId(endpoint.getCompanyId());

            caseObjectDAO.saveOrUpdate(caseObj);

            if (event.getComment() != null) {
                commentDAO.persist (convertComment(caseObj, personMapper, event.getComment()));
            }
        }
        else {
            caseObj = createCaseObject(issue, endpoint, personMapper);
        }
        return caseObj;
    }

    private void updateCaseState(Issue issue, CaseObject caseObj) {
        En_CaseState state = jiraStatusMapEntryDAO.getByJiraStatus(issue.getStatus().getName());
        if (state == null)
            throw new RuntimeException("unable to map jira-status " + issue.getStatus().getName() + " to portal case-state");

        caseObj.setState(state);
    }

    private void updateCasePriority(Issue issue, CaseObject caseObj) {
        JiraPriorityMapEntry jiraPriorityEntry = jiraPriorityMapEntryDAO.getByJiraPriorityId(issue.getPriority().getName());
        if (jiraPriorityEntry == null) {
            logger.warn("unable to map jira-priority level : {}, set as basic", issue.getPriority().getName());
            caseObj.setImpLevel(En_ImportanceLevel.BASIC.getId());
        }
        else {
            caseObj.setImpLevel(jiraPriorityEntry.getLocalPriorityId());
        }
    }


    private Person mapPerson (JiraEndpoint endpoint, User jiraUser) {
        Person person = null;

        if (HelperFunc.isNotEmpty(jiraUser.getEmailAddress())) {
            person = personDAO.findContactByEmail(endpoint.getCompanyId(), jiraUser.getEmailAddress());
        }

        if (person == null) {
            person = personDAO.findContactByName(endpoint.getCompanyId(), jiraUser.getDisplayName());
        }

        if (person == null) {
            person = createPersonForJiraUser(endpoint, jiraUser);
        }

        return person;
    }

    private Person createPersonForJiraUser(JiraEndpoint endpoint, User jiraUser) {
        Person person;// create one
        person = new Person();
        person.setCompanyId(endpoint.getCompanyId());
        person.setCreated(new Date());
        person.setCreator("jira-integration-service");
        person.setDeleted(false);
        person.setGender(En_Gender.UNDEFINED);
        person.setDisplayName(jiraUser.getDisplayName());
        person.setDisplayShortName(jiraUser.getDisplayName());
        person.setLocale("ru");

        PlainContactInfoFacade contactInfoFacade = new PlainContactInfoFacade();
        contactInfoFacade.setEmail(jiraUser.getEmailAddress());
        person.setContactInfo(contactInfoFacade.editInfo());

        personDAO.saveOrUpdate(person);
        return person;
    }


    interface PersonMapper {
        Person toProteiPerson (User jiraUser);
    }

    private class CachedPersonMapper implements PersonMapper {

        private final JiraEndpoint endpoint;
        private Person defaultEntryPointUser;
        private final Map<String, Person> index;

        public CachedPersonMapper(JiraEndpoint endpoint) {
            this.endpoint = endpoint;
            this.index = new HashMap<>();
        }


        private String emailKey (User user) {
            return HelperFunc.isNotEmpty(user.getEmailAddress()) ? "email:" + user.getEmailAddress() : "N";
        }

        private String nameKey (User user) {
            return HelperFunc.isNotEmpty(user.getDisplayName()) ? "name:" + user.getDisplayName() : "N";
        }

        @Override
        public Person toProteiPerson(User jiraUser) {
            if (jiraUser == null) {
                if (defaultEntryPointUser == null) {
                    defaultEntryPointUser = personDAO.get(endpoint.getPersonId());
                }
                return defaultEntryPointUser;
            }

            Person person = index.getOrDefault(emailKey(jiraUser), index.get(nameKey(jiraUser)));

            if (person == null) {
                person = mapPerson(endpoint, jiraUser);
                if (HelperFunc.isNotEmpty(jiraUser.getEmailAddress()))
                    index.put(emailKey(jiraUser), person);

                if (HelperFunc.isNotEmpty(jiraUser.getDisplayName()))
                    index.put(nameKey(jiraUser), person);
            }

            return person;
        }
    }
}
