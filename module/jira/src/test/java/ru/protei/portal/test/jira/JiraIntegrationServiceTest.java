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
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.event.AssembledCaseEvent;
import ru.protei.portal.core.mail.MailSendChannel;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.En_CompanyCategory;
import ru.protei.portal.core.model.dict.En_Gender;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.service.AssemblerService;
import ru.protei.portal.jira.service.JiraIntegrationService;
import ru.protei.portal.jira.dto.JiraHookEventData;
import ru.protei.portal.jira.dict.JiraHookEventType;
import ru.protei.portal.jira.utils.JiraHookEventParser;
import ru.protei.portal.jira.utils.CustomJiraIssueParser;
import ru.protei.portal.embeddeddb.DatabaseConfiguration;
import ru.protei.portal.test.jira.config.JiraTestConfiguration;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

import static ru.protei.portal.core.model.helper.CollectionUtils.getFirst;
import static ru.protei.portal.core.model.helper.CollectionUtils.listOf;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        CoreConfigurationContext.class,
        JdbcConfigurationContext.class,
        DatabaseConfiguration.class,
        JiraTestConfiguration.class})
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

    @Autowired
    AssemblerService assemblerService;
    @Autowired
    MailSendChannel sendChannel;
    @Autowired
    PortalConfig portalConfig;

    private final String FILE_PATH_JSON = "issue.json";
    private final String FILE_PATH_UPDATED_JSON = "issue.updated.json";
    private final String FILE_PATH_EMPTY_PROJECT_JSON = "issue.empty.project.json";
    private final String FILE_PATH_EMPTY_STATUS_JSON = "issue.empty.status.json";
    private final String FILE_PATH_COMPANY_GROUP_JSON = "issue.companygroup.json";

    private String jsonString;
    private String updatedJsonString;
    private String emptyKeyJsonString;
    private String unknownStatusJsonString;
    private String companyGroupJsonString;

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
        encoded = Files.readAllBytes(Paths.get(getClass().getClassLoader().getResource(FILE_PATH_COMPANY_GROUP_JSON).getFile()));
        companyGroupJsonString = new String(encoded, StandardCharsets.UTF_8);
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
    public void createAndUpdateIssue() throws Exception{
        Issue issue = makeIssue(jsonString);
        Assert.assertNotNull("Error parsing json for create", issue);

        Company company = makeCompany();
        Person person = makePerson(company);

        JiraEndpoint endpoint = jiraEndpointDAO.getByProjectId(company.getId(), issue.getProject().getId());
        endpoint.setPersonId(person.getId());

        CompletableFuture<AssembledCaseEvent> caseEvent = jiraIntegrationService.create(endpoint, new JiraHookEventData(JiraHookEventType.ISSUE_CREATED, issue));
        Assert.assertNotNull("Issue not created", caseEvent.get().getCaseObject().getId());

        issue = makeIssue(updatedJsonString);
        Assert.assertNotNull("Error parsing json for update", issue);

        caseEvent = jiraIntegrationService.updateOrCreate(endpoint, new JiraHookEventData(JiraHookEventType.ISSUE_UPDATED, issue));
        CaseObject object = caseObjectDAO.get(caseEvent.get().getCaseObjectId());
        Assert.assertEquals("Issue not updated", object.getStateId(), CrmConstants.State.OPENED);
    }

    @Test
    public void updateIssueSimultaneously() throws Exception{
        Company company = makeCompany();
        Person person = makePerson( company );

        List<Issue> issues = listOf( makeIssue( jsonString ), makeIssue( jsonString ), makeIssue( jsonString ) );

        JiraEndpoint endpoint = jiraEndpointDAO.getByProjectId( company.getId(), getFirst( issues ).getProject().getId() );
        endpoint.setPersonId( person.getId() );

        List<CompletableFuture<AssembledCaseEvent>> caseEvents = new ArrayList<>();
        for (Issue issue : issues) {
            caseEvents.add( jiraIntegrationService.create( endpoint, new JiraHookEventData( JiraHookEventType.ISSUE_CREATED, issue ) ) );
        }

        for (CompletableFuture<AssembledCaseEvent> caseEvent : caseEvents) {
            Assert.assertNotNull( "Issue not created", caseEvent.get().getCaseObject().getId() );
        }

        issues = listOf( makeIssue( updatedJsonString ), makeIssue( updatedJsonString ), makeIssue( updatedJsonString ) );
        caseEvents.clear();
        for (Issue issue : issues) {
            caseEvents.add( jiraIntegrationService.updateOrCreate( endpoint, new JiraHookEventData( JiraHookEventType.ISSUE_UPDATED, issue ) ) );
        }

        for (CompletableFuture<AssembledCaseEvent> caseEvent : caseEvents) {
            CaseObject object = caseObjectDAO.get( caseEvent.get().getCaseObjectId() );
            Assert.assertEquals( "Issue not updated", object.getStateId(), CrmConstants.State.OPENED );
        }
    }

    @Test
    public void parseIssueWithCompanyGroup() {

        Issue issue = makeIssue(companyGroupJsonString);
        Assert.assertNotNull("Parsed json with company group", issue);
        Assert.assertEquals(issue.getFieldByName(CustomJiraIssueParser.COMPANY_GROUP_CODE_NAME).getValue(), "chinguitel_mr_Group");
    }

    private Issue makeIssue(String jsonString) {
        jsonString = jsonString.replaceAll( "PRT-82", "PRT-82" + uniquieIndex.getAndIncrement() );
        try {
            JiraHookEventData data = JiraHookEventParser.parse(jsonString);
            return data == null ? null : data.getIssue();
        } catch (JSONException e) {
            return null;
        }
    }

    private Company makeCompany() {
        Company company = new Company();
        company.setCname("Jira_test_company");
        company.setCategory(En_CompanyCategory.CUSTOMER);
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

    private static AtomicInteger uniquieIndex = new AtomicInteger( 0 );
}
