package ru.protei.portal.test.jira;

import com.atlassian.jira.rest.client.api.domain.Issue;
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
import ru.protei.portal.core.model.dict.En_CaseCommentPrivacyType;
import ru.protei.portal.core.model.dict.En_CompanyCategory;
import ru.protei.portal.core.model.dict.En_Gender;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.query.CaseCommentQuery;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.JiraEndpoint;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.service.AssemblerService;
import ru.protei.portal.embeddeddb.DatabaseConfiguration;
import ru.protei.portal.jira.dict.JiraHookEventType;
import ru.protei.portal.jira.dto.JiraHookEventData;
import ru.protei.portal.jira.service.JiraIntegrationService;
import ru.protei.portal.jira.utils.CustomJiraIssueParser;
import ru.protei.portal.jira.utils.JiraHookEventParser;
import ru.protei.portal.test.jira.config.JiraTestConfiguration;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
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
    CaseCommentDAO caseCommentDAO;

    @Autowired
    AssemblerService assemblerService;
    @Autowired
    MailSendChannel sendChannel;
    @Autowired
    PortalConfig portalConfig;

    private final String FILE_PATH_JSON = "issue.json";
    private final String FILE_PATH_UPDATED_JSON = "issue.updated.json";
    private final String FILE_PATH_UPDATE_STATUS_OPENED_JSON = "issue.update.status1.json";
    private final String FILE_PATH_UPDATE_STATUS_VERIFIED_JSON = "issue.update.status2.json";
    private final String FILE_PATH_EMPTY_PROJECT_JSON = "issue.empty.project.json";
    private final String FILE_PATH_EMPTY_STATUS_JSON = "issue.empty.status.json";
    private final String FILE_PATH_COMPANY_GROUP_JSON = "issue.companygroup.json";
    private final String FILE_PATH_DUPLICATE_CLM_ID_JSON = "issue.duplicate.clmid.json";
    private final String FILE_PATH_NO_CLM_ID_JSON = "issue.no.clmid.json";
    private final String FILE_PATH_PRIVACY_TYPE_JSON = "issue.privacy.type.json";

    private String jsonString;
    private String updatedJsonString;
    private String updateStatusToOpenedJsonString;
    private String updateStatusToVerifiedJsonString;
    private String emptyKeyJsonString;
    private String unknownStatusJsonString;
    private String companyGroupJsonString;
    private String duplicateClmIdJsonString;
    private String noClmIdJsonString;

    private static AtomicInteger uniqueIndex = new AtomicInteger( 0 );
    private static final String JIRA_ID = "PRT-82";
    private static final String CLM_ID = "CLM-367029";
    private String privacyTypeJsonString;

    private static final Logger log = LoggerFactory.getLogger(JiraIntegrationServiceTest.class);

    @Before
    public void readFiles() throws IOException
    {
        byte[] encoded = Files.readAllBytes(Paths.get(getClass().getClassLoader().getResource(FILE_PATH_JSON).getFile()));
        jsonString = new String(encoded, StandardCharsets.UTF_8);
        encoded = Files.readAllBytes(Paths.get(getClass().getClassLoader().getResource(FILE_PATH_UPDATED_JSON).getFile()));
        updatedJsonString = new String(encoded, StandardCharsets.UTF_8);
        encoded = Files.readAllBytes(Paths.get(getClass().getClassLoader().getResource(FILE_PATH_UPDATE_STATUS_OPENED_JSON).getFile()));
        updateStatusToOpenedJsonString = new String(encoded, StandardCharsets.UTF_8);
        encoded = Files.readAllBytes(Paths.get(getClass().getClassLoader().getResource(FILE_PATH_UPDATE_STATUS_VERIFIED_JSON).getFile()));
        updateStatusToVerifiedJsonString = new String(encoded, StandardCharsets.UTF_8);
        encoded = Files.readAllBytes(Paths.get(getClass().getClassLoader().getResource(FILE_PATH_EMPTY_PROJECT_JSON).getFile()));
        emptyKeyJsonString = new String(encoded, StandardCharsets.UTF_8);
        encoded = Files.readAllBytes(Paths.get(getClass().getClassLoader().getResource(FILE_PATH_EMPTY_STATUS_JSON).getFile()));
        unknownStatusJsonString = new String(encoded, StandardCharsets.UTF_8);
        encoded = Files.readAllBytes(Paths.get(getClass().getClassLoader().getResource(FILE_PATH_COMPANY_GROUP_JSON).getFile()));
        companyGroupJsonString = new String(encoded, StandardCharsets.UTF_8);
        encoded = Files.readAllBytes(Paths.get(getClass().getClassLoader().getResource(FILE_PATH_DUPLICATE_CLM_ID_JSON).getFile()));
        duplicateClmIdJsonString = new String(encoded, StandardCharsets.UTF_8);
        encoded = Files.readAllBytes(Paths.get(getClass().getClassLoader().getResource(FILE_PATH_NO_CLM_ID_JSON).getFile()));
        noClmIdJsonString = new String(encoded, StandardCharsets.UTF_8);
        encoded = Files.readAllBytes(Paths.get(getClass().getClassLoader().getResource(FILE_PATH_PRIVACY_TYPE_JSON).getFile()));
        privacyTypeJsonString = new String(encoded, StandardCharsets.UTF_8);
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

    /**
     * Эмулируем некорректный порядок хуков об изменении статусов Jira
     * Проверям что статус обращения изменился в соотв-ии с датой последнего изменения на Jira
     */
    @Test
    public void updateIssueStatuses() throws Exception {

        Issue issue = makeIssue(jsonString, 1, 1);
        Assert.assertNotNull("Error parsing json for create", issue);

        Company company = makeCompany();
        Person person = makePerson(company);

        JiraEndpoint endpoint = jiraEndpointDAO.getByProjectId(company.getId(), issue.getProject().getId());
        endpoint.setPersonId(person.getId());

        CompletableFuture<AssembledCaseEvent> caseEvent = jiraIntegrationService.create(endpoint, new JiraHookEventData(JiraHookEventType.ISSUE_CREATED, issue));
        Assert.assertNotNull("Issue not created", caseEvent.get().getCaseObject().getId());

        JiraHookEventData data = parseJson(updateStatusToVerifiedJsonString, 1, 1);
        Assert.assertNotNull("Error parsing json for update status2", data);

        caseEvent = jiraIntegrationService.updateOrCreate(endpoint, data);
        CaseObject objectChangedStatus2 = caseObjectDAO.get(caseEvent.get().getCaseObjectId());
        Assert.assertEquals("Issue not updated 2", CrmConstants.State.VERIFIED, objectChangedStatus2.getStateId());

        data = parseJson(updateStatusToOpenedJsonString, 1, 1);
        Assert.assertNotNull("Error parsing json for update status1", data);

        caseEvent = jiraIntegrationService.updateOrCreate(endpoint, data);
        CaseObject objectChangedStatus1 = caseObjectDAO.get(caseEvent.get().getCaseObjectId());
        Assert.assertEquals("Issue updated with earlier status", CrmConstants.State.VERIFIED, objectChangedStatus1.getStateId() );
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
    public void createWithPrivacyType() throws Exception{
        Issue issue = makeIssue(privacyTypeJsonString);
        Assert.assertNotNull("Error parsing json for create", issue);

        Company company = makeCompany();
        Person person = makePerson(company);

        JiraEndpoint endpoint = jiraEndpointDAO.getByProjectId(company.getId(), issue.getProject().getId());
        endpoint.setPersonId(person.getId());

        CompletableFuture<AssembledCaseEvent> caseEvent = jiraIntegrationService.create(endpoint, new JiraHookEventData(JiraHookEventType.ISSUE_CREATED, issue));
        Assert.assertNotNull("Issue not created", caseEvent.get().getCaseObject().getId());

        CaseObject object = caseObjectDAO.get(caseEvent.get().getCaseObjectId());
        CaseCommentQuery query = new CaseCommentQuery();
        query.setCaseObjectIds(Collections.singletonList(object.getId()));
        List<CaseComment> comments = caseCommentDAO.getCaseComments(query);

        Assert.assertTrue("no pubic comment create", comments.stream()
                        .anyMatch(comment -> "NO ROLE".equals(comment.getText())
                                && En_CaseCommentPrivacyType.PUBLIC == comment.getPrivacyType()));
        Assert.assertTrue("no Project Customer Role comment create", comments.stream()
                .anyMatch(comment -> "Project Customer Role".equals(comment.getText())
                        && En_CaseCommentPrivacyType.PUBLIC == comment.getPrivacyType()));
        Assert.assertTrue("no Project Administrator Role comment create", comments.stream()
                .anyMatch(comment -> "Project Administrator Role".equals(comment.getText())
                        && En_CaseCommentPrivacyType.PRIVATE_CUSTOMERS == comment.getPrivacyType()));
        Assert.assertTrue("no Project Developer Role comment create", comments.stream()
                .anyMatch(comment -> "Project Developer Role".equals(comment.getText())
                        && En_CaseCommentPrivacyType.PRIVATE_CUSTOMERS == comment.getPrivacyType()));
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

    @Test
    public void duplicateIssueByClmId() throws Exception {
        int index = uniqueIndex.getAndIncrement();

        Issue originalIssue = makeIssue(duplicateClmIdJsonString, index, null);
        Assert.assertNotNull("Error parsing json for create", originalIssue);

        Company company = makeCompany();
        Person person = makePerson(company);
        JiraEndpoint endpoint = jiraEndpointDAO.getByProjectId(company.getId(), originalIssue.getProject().getId());
        endpoint.setPersonId(person.getId());

        CompletableFuture<AssembledCaseEvent> caseEventOriginalIssue = jiraIntegrationService.create(endpoint, new JiraHookEventData(JiraHookEventType.ISSUE_CREATED, originalIssue));
        AssembledCaseEvent assembledCaseEventOriginalIssue = caseEventOriginalIssue.get();
        Assert.assertNotNull("Issue not created", assembledCaseEventOriginalIssue.getCaseObject().getId());

        Issue duplicateIssue = makeIssue(replaceJiraId(duplicateClmIdJsonString, JIRA_ID + index), null, null);
        Assert.assertNotNull("Error parsing json for create", duplicateIssue);

        CompletableFuture<AssembledCaseEvent> CaseEventDuplicateIssue = jiraIntegrationService.create(endpoint, new JiraHookEventData(JiraHookEventType.ISSUE_CREATED, duplicateIssue));
        AssembledCaseEvent assembledCaseEventDublicateIssue = CaseEventDuplicateIssue.get();

        Assert.assertNull("Issue created", assembledCaseEventDublicateIssue);
    }

    @Test
    public void IssueNoClmId() throws Exception {
        Issue issue = makeIssue(noClmIdJsonString);
        Assert.assertNotNull("Error parsing json for create", issue);

        Company company = makeCompany();
        Person person = makePerson(company);

        JiraEndpoint endpoint = jiraEndpointDAO.getByProjectId(company.getId(), issue.getProject().getId());
        endpoint.setPersonId(person.getId());

        CompletableFuture<AssembledCaseEvent> caseEvent = jiraIntegrationService.create(endpoint, new JiraHookEventData(JiraHookEventType.ISSUE_CREATED, issue));
        Assert.assertNotNull("Issue not created", caseEvent.get().getCaseObject().getId());
    }

    private String replaceJiraId(String json, String jiraId) {
        return json.replaceAll(jiraId, JIRA_ID + uniqueIndex.getAndIncrement());
    }

    private Issue makeIssue(String jsonString) {
        JiraHookEventData data = parseJson(jsonString);
        return data == null ? null : data.getIssue();
    }

    private Issue makeIssue(String jsonString, Integer uniqueIndexId, Integer uniqueIndexCldId) {
        JiraHookEventData data = parseJson(jsonString, uniqueIndexId, uniqueIndexCldId);
        return data == null ? null : data.getIssue();
    }

    private JiraHookEventData parseJson(String jsonString) {
        int index = uniqueIndex.getAndIncrement();
        return parseJson(jsonString, index, index);
    }

    private JiraHookEventData parseJson(String jsonString, Integer uniqueIndexId, Integer uniqueIndexCldId) {
        if (uniqueIndexId != null) {
            jsonString = jsonString.replaceAll(JIRA_ID, JIRA_ID + uniqueIndexId);
        }
        if (uniqueIndexCldId != null) {
            jsonString = jsonString.replaceAll(CLM_ID, CLM_ID + uniqueIndexCldId);
        }
        try {
            return JiraHookEventParser.parse(jsonString);
        } catch (JSONException e) {
            return null;
        }
    }

    private Company makeCompany() {
        Company company = new Company();
        company.setCname("Jira_test_company" + new Date().getTime());
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
}
