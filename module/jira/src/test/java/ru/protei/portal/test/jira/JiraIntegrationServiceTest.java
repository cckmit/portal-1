package ru.protei.portal.test.jira;

import com.atlassian.jira.rest.client.api.domain.*;
import org.codehaus.jettison.json.JSONException;
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
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_CompanyCategory;
import ru.protei.portal.core.model.dict.En_Gender;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.jira.service.JiraIntegrationService;
import ru.protei.portal.jira.utils.JiraHookEventData;
import ru.protei.portal.jira.utils.JiraHookEventType;
import ru.protei.portal.test.jira.config.DatabaseTestConfiguration;
import ru.protei.portal.test.jira.config.JiraTestConfiguration;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {CoreConfigurationContext.class, JdbcConfigurationContext.class, DatabaseTestConfiguration.class, JiraTestConfiguration.class})
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

    private final String FILE_PATH_JSON = "issue.json";
    private final String FILE_PATH_UPDATED_JSON = "issue.updated.json";
    private final String FILE_PATH_EMPTY_PROJECT_JSON = "issue.empty.project.json";
    private final String FILE_PATH_EMPTY_STATUS_JSON = "issue.empty.status.json";

    private String jsonString;
    private String updatedJsonString;
    private String emptyKeyJsonString;
    private String unknownStatusJsonString;

    private static final Logger log = LoggerFactory.getLogger(JiraIntegrationServiceTest.class);

    @Before
    public void readFiles() throws IOException
    {
        byte[] encoded = Files.readAllBytes(Paths.get(getClass().getClassLoader().getResource(FILE_PATH_JSON).getFile()));
        jsonString = new String(encoded, StandardCharsets.UTF_8);
        encoded = Files.readAllBytes(Paths.get(getClass().getClassLoader().getResource(FILE_PATH_UPDATED_JSON).getFile()));
        updatedJsonString = new String(encoded, StandardCharsets.UTF_8);
        encoded = Files.readAllBytes(Paths.get(getClass().getClassLoader().getResource(FILE_PATH_EMPTY_PROJECT_JSON).getFile()));
        emptyKeyJsonString = new String(encoded, StandardCharsets.UTF_8);
        encoded = Files.readAllBytes(Paths.get(getClass().getClassLoader().getResource(FILE_PATH_EMPTY_STATUS_JSON).getFile()));
        unknownStatusJsonString = new String(encoded, StandardCharsets.UTF_8);
    }

    @Test
    public void parseIssueWithEmptyProject() {

        Issue issue = makeIssue(emptyKeyJsonString);
        Assert.assertNull("Parsed json with empty project", issue);
    }

    @Test
    public void parseIssueWithEmptyStatus() {

        Issue issue = makeIssue(unknownStatusJsonString);
        Assert.assertNull("Parsed json with empty status", issue);
    }

    @Test
    public void createAndUpdateIssue() {
        Issue issue = makeIssue(jsonString);
        Assert.assertNotNull("Error parsing json for create", issue);

        Company company = makeCompany();
        Person person = makePerson(company);

        JiraEndpoint endpoint = jiraEndpointDAO.getByProjectId(company.getId(), issue.getProject().getId());
        endpoint.setPersonId(person.getId());

        AssembledCaseEvent caseEvent = jiraIntegrationService.create(endpoint, new JiraHookEventData(JiraHookEventType.ISSUE_CREATED, issue));
        Assert.assertNotNull("Issue not created", caseEvent.getCaseObject().getId());

        issue = makeIssue(updatedJsonString);
        Assert.assertNotNull("Error parsing json for update", issue);

        caseEvent = jiraIntegrationService.updateOrCreate(endpoint, new JiraHookEventData(JiraHookEventType.ISSUE_UPDATED, issue));
        CaseObject object = caseObjectDAO.get(caseEvent.getCaseObject().getId());
        Assert.assertEquals("Issue not updated", object.getState(), En_CaseState.OPENED);
    }

    private Issue makeIssue(String jsonString) {
        try {
            JiraHookEventData data = JiraHookEventData.parse(jsonString);
            return data.getIssue();
        } catch (JSONException e) {
            return null;
        }
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
