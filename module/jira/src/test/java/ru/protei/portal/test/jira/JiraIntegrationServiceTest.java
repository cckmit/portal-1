package ru.protei.portal.test.jira;

import com.atlassian.jira.rest.client.api.domain.*;
import org.codehaus.jettison.json.JSONException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.protei.portal.core.event.AssembledCaseEvent;
import ru.protei.portal.core.model.dao.CompanyDAO;
import ru.protei.portal.core.model.dao.JiraEndpointDAO;
import ru.protei.portal.core.model.dao.JiraStatusMapEntryDAO;
import ru.protei.portal.core.model.dao.PersonDAO;
import ru.protei.portal.core.model.dict.En_CompanyCategory;
import ru.protei.portal.core.model.dict.En_Gender;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.CompanyCategory;
import ru.protei.portal.core.model.ent.JiraEndpoint;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.jira.service.JiraIntegrationService;
import ru.protei.portal.jira.utils.JiraHookEventData;
import ru.protei.portal.jira.utils.JiraHookEventType;
import ru.protei.portal.test.jira.config.DatabaseConfiguration;
import ru.protei.portal.test.jira.config.JiraTestConfiguration;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;

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

    private final String FILE_PATH = "issue.json";

    private static final Logger log = LoggerFactory.getLogger(JiraIntegrationServiceTest.class);

    @Test
    public void createIssue() {

        try {

            Issue issue = makeIssue();

            boolean isValid = validateIssue(issue);
            Assert.assertEquals(true, isValid);

            Company company = makeCompany();
            Person person = makePerson(company);

            JiraEndpoint endpoint = jiraEndpointDAO.getByProjectId(company.getId(), issue.getProject().getId());
            endpoint.setPersonId(person.getId());

            AssembledCaseEvent caseEvent = jiraIntegrationService.create(endpoint, new JiraHookEventData(JiraHookEventType.ISSUE_CREATED, issue));
            Assert.assertNotNull(caseEvent.getCaseObject().getId());

        } catch (Throwable e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void updateIssue() {
    }

    public String readFile() throws IOException
    {
        byte[] encoded = Files.readAllBytes(Paths.get(getClass().getClassLoader().getResource(FILE_PATH).getFile()));
        return new String(encoded, StandardCharsets.UTF_8);
    }

    private Issue makeIssue() throws IOException, JSONException {
        String json = readFile();
        JiraHookEventData data = JiraHookEventData.parse(json);
        return data.getIssue();
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
