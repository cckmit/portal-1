package ru.protei.portal.test.jira;

import com.atlassian.jira.rest.client.api.domain.*;
import com.atlassian.jira.rest.client.api.domain.Attachment;
import org.apache.commons.collections4.IteratorUtils;
import org.codehaus.jettison.json.JSONException;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.protei.portal.core.event.AssembledCaseEvent;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.En_CompanyCategory;
import ru.protei.portal.core.model.dict.En_Gender;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.jira.service.JiraIntegrationService;
import ru.protei.portal.jira.utils.JiraHookEventData;
import ru.protei.portal.jira.utils.JiraHookEventType;
import ru.protei.portal.test.jira.config.DatabaseConfiguration;
import ru.protei.portal.test.jira.config.JiraTestConfiguration;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

import javax.annotation.Nullable;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Date;
import java.util.Set;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {CoreConfigurationContext.class, JdbcConfigurationContext.class, DatabaseConfiguration.class, JiraTestConfiguration.class})
public class JiraIntegrationServiceTest {

    @Autowired
    JiraIntegrationService jiraIntegrationService;

    @Autowired
    JiraEndpointDAO jiraEndpointDAO;

    @Autowired
    CompanyDAO companyDAO;

    @Autowired
    PersonDAO personDAO;

    @Autowired
    JiraStatusMapEntryDAO jiraStatusMapEntryDAO;

    @Autowired
    CaseObjectDAO caseObjectDAO;

    private final String FILE_PATH = "issue.json";

    private String jsonString;

    private static final Logger log = LoggerFactory.getLogger(JiraIntegrationServiceTest.class);

    @Before
    public void readFile() throws IOException
    {
        byte[] encoded = Files.readAllBytes(Paths.get(getClass().getClassLoader().getResource(FILE_PATH).getFile()));
        jsonString = new String(encoded, StandardCharsets.UTF_8);
    }

    @Test
    public void createIssue() {

        Issue issue = makeIssue();
        Assert.assertNotNull("Error parsing json", issue);

        boolean isValid = validateIssue(issue);
        Assert.assertTrue("Issue is not valid", isValid);

        Company company = makeCompany();
        Person person = makePerson(company);

        JiraEndpoint endpoint = jiraEndpointDAO.getByProjectId(company.getId(), issue.getProject().getId());
        endpoint.setPersonId(person.getId());

        AssembledCaseEvent caseEvent = jiraIntegrationService.create(endpoint, new JiraHookEventData(JiraHookEventType.ISSUE_CREATED, issue));
        Assert.assertNotNull("Issue not created", caseEvent.getCaseObject().getId());

        caseEvent = jiraIntegrationService.create(endpoint, new JiraHookEventData(JiraHookEventType.ISSUE_CREATED, issue));
        Assert.assertNull("Created a duplicate issue", caseEvent.getCaseObject().getId());
    }

    @Test
    public void updateIssue() {
        Issue issue = makeIssue();
        Assert.assertNotNull("Error parsing json", issue);

        boolean isValid = validateIssue(issue);
        Assert.assertTrue("Issue is not valid", isValid);

        Company company = makeCompany();
        Person person = makePerson(company);

        JiraEndpoint endpoint = jiraEndpointDAO.getByProjectId(company.getId(), issue.getProject().getId());
        endpoint.setPersonId(person.getId());

        AssembledCaseEvent caseEvent = jiraIntegrationService.create(endpoint, new JiraHookEventData(JiraHookEventType.ISSUE_CREATED, issue));
        Assert.assertNotNull("Issue not created", caseEvent.getCaseObject().getId());

        String newDescription = "Changed issue";
        Issue changedIssue = duplicateIssueWithNewDescription(issue, newDescription);
        caseEvent = jiraIntegrationService.updateOrCreate(endpoint, new JiraHookEventData(JiraHookEventType.ISSUE_UPDATED, changedIssue));
        CaseObject caseObject = caseObjectDAO.get(caseEvent.getCaseObject().getId());
        Assert.assertEquals("Issue not updated", caseObject.getInfo(), newDescription);
    }

    private Issue makeIssue() {
        try {
            JiraHookEventData data = JiraHookEventData.parse(jsonString);
            return data.getIssue();
        } catch (JSONException e) {
            return null;
        }
    }

    private boolean validateIssue(Issue issue) {
        return issue != null &&
                issue.getKey() != null &&
                issue.getCreationDate() != null &&
                issue.getUpdateDate() != null &&
                issue.getProject() != null && issue.getProject().getId() != null &&
                issue.getPriority() != null &&
                issue.getStatus() != null && jiraStatusMapEntryDAO.getByJiraStatus(1, issue.getStatus().getName()) != null &&
                issue.getIssueType() != null &&
                issue.getDescription() != null;
    }

    private Issue duplicateIssueWithNewDescription(Issue issue, String description) {
        return duplicateIssue(issue.getSummary(), issue.getSelf(), issue.getKey(), issue.getId(), issue.getProject(), issue.getIssueType(),
                issue.getStatus(), description, issue.getPriority(), issue.getResolution(), issue.getAttachments(), issue.getReporter(),
                issue.getAssignee(), issue.getCreationDate(), issue.getUpdateDate(), issue.getDueDate(), issue.getAffectedVersions(),
                issue.getFixVersions(), issue.getComponents(), issue.getTimeTracking(), issue.getFields(), issue.getComments(),
                issue.getTransitionsUri(), issue.getIssueLinks(), issue.getVotes(), issue.getWorklogs(), issue.getWatchers(),
                issue.getExpandos(), issue.getSubtasks(), issue.getChangelog(), issue.getOperations(), issue.getLabels());
    }

    private Issue duplicateIssue(String summary, URI self, String key, Long id, BasicProject project, IssueType issueType, Status status, String description,
                                 BasicPriority priority, Resolution resolution, Iterable<Attachment> attachments, User reporter, User assignee,
                                 DateTime creationDate, DateTime updateDate, DateTime dueDate, Iterable<Version> affectedVersions, Iterable<Version> fixVersions,
                                 Iterable<BasicComponent> components, TimeTracking timeTracking, Iterable<IssueField> issueFields, Iterable<Comment> comments,
                                 URI transitionsUri, Iterable<IssueLink> issueLinks, BasicVotes votes, Iterable<Worklog> worklogs, BasicWatchers watchers,
                                 Iterable<String> expandos, Iterable<Subtask> subtasks, Iterable<ChangelogGroup> changelog, Operations operations, Set<String> labels) {

        return new Issue( summary, self, key, id, project, issueType, status, description, priority, resolution, IteratorUtils.toList(attachments.iterator()), reporter,
                assignee, creationDate, updateDate, dueDate, IteratorUtils.toList(affectedVersions.iterator()), IteratorUtils.toList(fixVersions.iterator()),
                IteratorUtils.toList(components.iterator()), timeTracking, IteratorUtils.toList(issueFields.iterator()), IteratorUtils.toList(comments.iterator()),
                transitionsUri, IteratorUtils.toList(issueLinks.iterator()), votes, IteratorUtils.toList(worklogs.iterator()), watchers, expandos,
                IteratorUtils.toList(subtasks.iterator()), IteratorUtils.toList(changelog.iterator()), operations, labels);
    }

    private Company makeCompany() {
        Company company = new Company();
        company.setCname("Jira_test_company");
        company.setCategory(new CompanyCategory(En_CompanyCategory.CUSTOMER.getId()));
        company.setId(companyDAO.persist(company));
        return company;
    }

    private Person makePerson(Company company) {
        Person person = new Person();
        person.setCreated(new Date());
        person.setCreator("TEST");
        person.setCompanyId(company.getId());
        person.setDisplayName("Jira_test_person");
        person.setGender(En_Gender.MALE);
        person.setId(personDAO.persist(person));
        return person;
    }
}
