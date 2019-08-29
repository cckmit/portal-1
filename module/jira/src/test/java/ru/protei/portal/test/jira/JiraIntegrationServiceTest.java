package ru.protei.portal.test.jira;

import com.atlassian.jira.rest.client.api.domain.*;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.protei.portal.core.event.AssembledCaseEvent;
import ru.protei.portal.core.model.dao.CompanyDAO;
import ru.protei.portal.core.model.dao.JiraEndpointDAO;
import ru.protei.portal.core.model.dao.PersonDAO;
import ru.protei.portal.core.model.dict.En_CompanyCategory;
import ru.protei.portal.core.model.dict.En_Gender;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.CompanyCategory;
import ru.protei.portal.core.model.ent.JiraEndpoint;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.jira.service.JiraIntegrationService;
import ru.protei.portal.jira.utils.CustomJiraIssueParser;
import ru.protei.portal.jira.utils.JiraHookEventData;
import ru.protei.portal.jira.utils.JiraHookEventType;
import ru.protei.portal.test.jira.config.DatabaseConfiguration;
import ru.protei.portal.test.jira.config.JiraTestConfiguration;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
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

    @Test
    public void createIssue() {

        Issue issue = makeIssue();
        Company company = makeCompany();
        Person person = makePerson(company);

        JiraEndpoint endpoint = jiraEndpointDAO.getByProjectId(company.getId(), issue.getProject().getId());
        endpoint.setPersonId(person.getId());

        AssembledCaseEvent caseEvent = jiraIntegrationService.create(endpoint, new JiraHookEventData(JiraHookEventType.ISSUE_CREATED, issue));
        Assert.assertNotNull(caseEvent.getCaseObject().getId());
    }

    @Test
    public void updateIssue() {
    }

    private URI makeURI() {
        try {
            return new URI("https://jira.billing.ru/");
        } catch (URISyntaxException e) {
            return null;
        }
    }

    private Issue makeIssue() {
        URI uri = makeURI();
        BasicProject project = new BasicProject(uri, "", 1L, null);
        BasicPriority priority = new BasicPriority(uri, null, "10-Critical");
        Status status = new Status(uri, null, "Authorized", "", null);
        IssueType type = new IssueType(uri, null, "Error", false, null, null);
        Collection<IssueField> issueFields = new ArrayList<>();
        issueFields.add(new IssueField(null, CustomJiraIssueParser.SEVERITY_CODE_NAME, "", 10));

        return new Issue(null, uri, "55555", null, project, type, status, "Test jira",
                priority, null, new ArrayList<>(), null, null, DateTime.now(), DateTime.now(), null, null,
                null, null, null, issueFields, new ArrayList<>(), null, null, null,
                null, null, null, null, null, null, null );
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
