package ru.protei.portal.test.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.protei.portal.config.DatabaseConfiguration;
import ru.protei.portal.config.MainTestsConfiguration;
import ru.protei.portal.core.controller.api.PortalApiController;
import ru.protei.portal.core.model.dao.PersonDAO;
import ru.protei.portal.core.model.dict.En_ContactItemType;
import ru.protei.portal.core.model.dict.En_Gender;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.query.CaseApiQuery;
import ru.protei.portal.core.service.CaseService;
import ru.protei.portal.core.service.user.AuthService;
import ru.protei.portal.test.service.BaseServiceTest;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {CoreConfigurationContext.class, JdbcConfigurationContext.class, DatabaseConfiguration.class, MainTestsConfiguration.class, PortalApiController.class})
@WebAppConfiguration
public class TestPortalApiController extends BaseServiceTest {
    @Autowired
    WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    private static ObjectMapper objectMapper;
    private static PersonDAO personDAO;
    private static CaseService caseService;
    private static AuthService authService;
    private static Person person;
    private static final int COUNT_OF_ISSUES_WITH_MANAGER = 3;
    private static final int COUNT_OF_ISSUES_WITHOUT_MANAGER = 15;

    @BeforeClass
    public static void initClass() {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(CoreConfigurationContext.class, JdbcConfigurationContext.class, DatabaseConfiguration.class, MainTestsConfiguration.class);
        objectMapper = applicationContext.getBean(ObjectMapper.class);
        personDAO = applicationContext.getBean(PersonDAO.class);
        caseService = applicationContext.getBean(CaseService.class);
        authService = applicationContext.getBean(AuthService.class);
        createAndPersistPerson();
        createAndPersistSomeIssues(COUNT_OF_ISSUES_WITHOUT_MANAGER, null);
        createAndPersistSomeIssues(COUNT_OF_ISSUES_WITH_MANAGER, person);
    }

    @Before
    public void init() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testGetCaseList() throws Exception {
        CaseApiQuery caseApiQuery = new CaseApiQuery();
        caseApiQuery.setManagerIds(Collections.singletonList(person.getId()));

        ResultActions accept = createPostResultAction("/api/cases", caseApiQuery);

        accept
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(En_ResultStatus.OK.toString())))
                .andExpect(jsonPath("$.data", hasSize(COUNT_OF_ISSUES_WITH_MANAGER)));
    }

    @Test
    public void testCreateIssue() throws Exception {
        CaseObject caseObject = createNewCaseObject(person);
        String issueName = "API_Test_Issue_from_test_create_issue";
        caseObject.setName(issueName);
        caseObject.setImpLevel(3);
        caseObject.setInitiator(person);

        ResultActions actions = createPostResultAction("/api/cases/create", caseObject);
        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(En_ResultStatus.OK.toString())));

        long countOfIssues = caseObjectDAO
                .getAll()
                .stream()
                .filter(currCaseObj -> currCaseObj.getName().equals(issueName))
                .count();

        Assert.assertEquals("Expected 1 new created issue", 1, countOfIssues);
    }

    @Test
    public void testUpdateIssue() throws Exception {
        CaseObject startCaseObject = caseObjectDAO.getAll().stream().findAny().orElse(null);
        Assert.assertNotNull(startCaseObject);

        String startCaseObjectName = startCaseObject.getName();

        startCaseObject.setName(ISSUES_PREFIX + "new");

        ResultActions resultActions = createPostResultAction("/api/cases/update", startCaseObject);
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(En_ResultStatus.OK.toString())));

        List<CaseObject> caseObjects = caseObjectDAO.getAll();
        CaseObject endCaseObject = caseObjects.stream().filter(currCaseObj -> currCaseObj.getId().equals(startCaseObject.getId())).findAny().orElse(null);

        Assert.assertNotNull(endCaseObject);
        Assert.assertNotEquals(startCaseObjectName, endCaseObject.getName());
        Assert.assertEquals(ISSUES_PREFIX + "new", endCaseObject.getName());
    }

    private static void createAndPersistPerson() {
        Person p = new Person();
        String personFirstName = "Test" + new Date().getTime();

        p.setCompanyId(1L);
        p.setFirstName(personFirstName);
        p.setLastName("API");
        p.setDisplayName("Test API");
        p.setCreated(new Date());
        p.setCreator("");
        p.setGender(En_Gender.MALE);

        p.getContactInfo().addItem(En_ContactItemType.EMAIL).modify("junit@test.org", "test-email");
        p.getContactInfo().addItem(En_ContactItemType.FAX).modify("999-22-33-11", "work fax");
        p.getContactInfo().addItem(En_ContactItemType.MOBILE_PHONE).modify("+7-921-555-44-33", "main phone");
        p.getContactInfo().addItem(En_ContactItemType.GENERAL_PHONE).modify("8(812)-4494727", "protei");
        p.getContactInfo().addItem(En_ContactItemType.ICQ).modify("00000000001");
        p.getContactInfo().addItem(En_ContactItemType.JABBER).modify("dev@jabber.protei.ru");
        p.getContactInfo().addItem(En_ContactItemType.WEB_SITE).modify("http://www.protei.ru");

        personDAO.persist(p);

        person = personDAO
                .getAll()
                .stream()
                .filter(currPerson -> currPerson.getFirstName().equals(personFirstName))
                .findFirst().get();
    }

    private static void createAndPersistSomeIssues(int count, Person manager) {
        for (int i = 0; i < count; i++) {
            CaseObject caseObject = createNewCaseObject(person);
            caseObject.setName(ISSUES_PREFIX + i);
            caseObject.setStateId(1);
            caseObject.setImpLevel(3);
            caseObject.setInitiator(person);
            caseObject.setManager(manager);
            caseService.saveCaseObject(authService.findSession(null).makeAuthToken(), caseObject, person);
        }
    }

    private <T> ResultActions createPostResultAction(String url, T obj) throws Exception {
        String json = objectMapper.writeValueAsString(obj);

        return mockMvc.perform(
                post(url)
                        .header("Accept", "application/json")
                        .header("authorization", "Basic " + Base64.getEncoder().encodeToString((person.getFirstName() + ":" + QWERTY_PASSWORD + "1234").getBytes()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        );
    }

    private static final String QWERTY_PASSWORD = "qwerty";

    private static final String ISSUES_PREFIX = "Portal_API_issue_test_";
}
