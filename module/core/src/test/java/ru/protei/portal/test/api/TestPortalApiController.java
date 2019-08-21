package ru.protei.portal.test.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.util.DigestUtils;
import org.springframework.web.context.WebApplicationContext;
import ru.protei.portal.config.DatabaseConfiguration;
import ru.protei.portal.config.MainTestsConfiguration;
import ru.protei.portal.core.controller.api.PortalApiController;
import ru.protei.portal.core.model.dao.PersonDAO;
import ru.protei.portal.core.model.dao.UserLoginDAO;
import ru.protei.portal.core.model.dao.UserRoleDAO;
import ru.protei.portal.core.model.dao.impl.UserLoginDAO_Impl;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.query.CaseApiQuery;
import ru.protei.portal.core.service.CaseService;
import ru.protei.portal.core.service.user.AuthService;
import ru.protei.portal.test.service.BaseServiceTest;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {CoreConfigurationContext.class, JdbcConfigurationContext.class, DatabaseConfiguration.class, MainTestsConfiguration.class, PortalApiController.class})
@WebAppConfiguration
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestPortalApiController extends BaseServiceTest {
    @Autowired
    WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;
    private static ObjectMapper objectMapper;
    private static PersonDAO personDAO;
    private static UserLoginDAO userLoginDAO;
    private static CaseService caseService;
    private static AuthService authService;
    private static UserRoleDAO userRoleDAO;
    private static Person person;
    private static final Logger log = LoggerFactory.getLogger(TestPortalApiController.class);
    private static final int COUNT_OF_ISSUES_WITH_MANAGER = new Random().nextInt(10);
    private static final int COUNT_OF_ISSUES_WITHOUT_MANAGER = new Random().nextInt(10);
    private static final int COUNT_OF_PRIVATE_ISSUES = new Random().nextInt(10);
    private static final int COUNT_OF_ISSUES = COUNT_OF_PRIVATE_ISSUES + COUNT_OF_ISSUES_WITH_MANAGER + COUNT_OF_ISSUES_WITHOUT_MANAGER;
    private static UserRole mainRole;

    @BeforeClass
    public static void initClass() throws Exception {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(
                CoreConfigurationContext.class,
                JdbcConfigurationContext.class,
                DatabaseConfiguration.class,
                MainTestsConfiguration.class
        );

        objectMapper = applicationContext.getBean(ObjectMapper.class);
        personDAO = applicationContext.getBean(PersonDAO.class);
        caseService = applicationContext.getBean(CaseService.class);
        authService = applicationContext.getBean(AuthService.class);
        userLoginDAO = applicationContext.getBean(UserLoginDAO.class);
        userRoleDAO = applicationContext.getBean(UserRoleDAO.class);

        createAndPersistPerson();
        createAndPersistUserRoles();
        createAndPersistUserLogin();
        createAndPersistSomeIssues(COUNT_OF_ISSUES_WITHOUT_MANAGER);
        createAndPersistSomeIssuesWithManager(COUNT_OF_ISSUES_WITH_MANAGER, person);
        createAndPersistSomePrivateIssues(COUNT_OF_PRIVATE_ISSUES);

        log.debug("issues={} | issues_with_manager={} | issues_without_manager={} | private_issues={}",
                COUNT_OF_ISSUES,
                COUNT_OF_ISSUES_WITH_MANAGER,
                COUNT_OF_ISSUES_WITHOUT_MANAGER,
                COUNT_OF_PRIVATE_ISSUES
        );
    }

    @Before
    public void initMockMvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void _1_testGetCaseList_all() throws Exception {
        ResultActions accept = createPostResultAction("/api/cases", new CaseApiQuery());

        accept
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(En_ResultStatus.OK.toString())))
                .andExpect(jsonPath("$.data", hasSize(COUNT_OF_ISSUES)));
    }

    @Test
    @Ignore
    public void _1_testGetCaseList_withManager() throws Exception {
        CaseApiQuery caseApiQuery = new CaseApiQuery();
        caseApiQuery.setManagerIds(Collections.singletonList(person.getId()));

        ResultActions accept = createPostResultAction("/api/cases", caseApiQuery);

        accept
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(En_ResultStatus.OK.toString())))
                .andExpect(jsonPath("$.data", hasSize(COUNT_OF_ISSUES_WITH_MANAGER)));
    }

    @Test
    @Ignore
    public void _1_testGetCaseList_publicIssues() throws Exception {
        CaseApiQuery caseApiQuery = new CaseApiQuery();
        caseApiQuery.setAllowViewPrivate(false);

        ResultActions accept = createPostResultAction("/api/cases", caseApiQuery);

        accept
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(En_ResultStatus.OK.toString())))
                .andExpect(jsonPath("$.data", hasSize(COUNT_OF_ISSUES - COUNT_OF_PRIVATE_ISSUES)));
    }

    @Test
    @Ignore
    public void _2_testCreateIssue() throws Exception {
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
    @Ignore
    public void _2_testUpdateIssue() throws Exception {
        CaseObject startCaseObject = caseObjectDAO.getAll().stream().findAny().orElse(null);
        Assert.assertNotNull("Expected at least 1 case object in db before update", startCaseObject);

        String startCaseObjectName = startCaseObject.getName();

        startCaseObject.setName(ISSUES_PREFIX + "new");

        ResultActions resultActions = createPostResultAction("/api/cases/update", startCaseObject);
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(En_ResultStatus.OK.toString())));

        List<CaseObject> caseObjects = caseObjectDAO.getAll();
        CaseObject endCaseObject = caseObjects.stream().filter(currCaseObj -> currCaseObj.getId().equals(startCaseObject.getId())).findAny().orElse(null);

        Assert.assertNotNull("Expected at least 1 case object in db after update", endCaseObject);
        Assert.assertNotEquals("Expected the names of the case object are different before and after case object update", startCaseObjectName, endCaseObject.getName());
        Assert.assertEquals("Expected the name of the case object = " + ISSUES_PREFIX + "new after case object update", ISSUES_PREFIX + "new", endCaseObject.getName());
    }

    private static void createAndPersistPerson() {
        Person p = new Person();
        String personFirstName = "Test" + new Date().getTime();

        p.setCompany(new Company(1L));
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

    private static void createAndPersistUserRoles() {
        UserRole role = new UserRole();
        role.setCode(PORTAL_API_TEST_ROLE_CODE);
        role.setInfo(PORTAL_API_TEST_ROLE_CODE);
        role.setPrivileges(new HashSet<>(Arrays.asList(PRIVILEGES)));
        role.setScope(En_Scope.SYSTEM);

        userRoleDAO.persist(role);

        mainRole = userRoleDAO.getByCondition("role_code like ?", "%" + PORTAL_API_TEST_ROLE_CODE + "%");
    }

    private static void createAndPersistUserLogin() throws Exception {
        UserLogin userLogin = userLoginDAO.createNewUserLogin(person);
        userLogin.setUlogin(person.getFirstName());
        userLogin.setUpass(DigestUtils.md5DigestAsHex(QWERTY_PASSWORD.getBytes()));
        userLogin.setPersonId(person.getId());
        userLogin.setAuthTypeId(1);
        userLogin.setAdminStateId(2);
        userLogin.setRoles(Collections.singleton(mainRole));

        userLoginDAO.persist(userLogin);
    }

    private static void createAndPersistSomeIssues(int count) {
        for (int i = 0; i < count; i++) {
            CaseObject caseObject = createNewCaseObject(person);
            caseObject.setName(ISSUES_PREFIX + i);
            caseObject.setStateId(1);
            caseObject.setImpLevel(3);
            caseObject.setInitiator(person);
            caseService.saveCaseObject(authService.findSession(null).makeAuthToken(), caseObject, person);
        }
    }

    private static void createAndPersistSomeIssuesWithManager(int count, Person manager) {
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

    private static void createAndPersistSomePrivateIssues(int count) {
        for (int i = 0; i < count; i++) {
            CaseObject caseObject = createNewCaseObject(person);
            caseObject.setName(ISSUES_PREFIX + i);
            caseObject.setStateId(1);
            caseObject.setImpLevel(3);
            caseObject.setInitiator(person);
            caseObject.setPrivateCase(true);
            caseService.saveCaseObject(authService.findSession(null).makeAuthToken(), caseObject, person);
        }
    }

    private <T> ResultActions createPostResultAction(String url, T obj) throws Exception {
        return mockMvc.perform(
                post(url)
                        .header("Accept", "application/json")
                        .header("authorization", "Basic " + Base64.getEncoder().encodeToString((person.getFirstName() + ":" + QWERTY_PASSWORD).getBytes()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(obj))
        );
    }

    private static final En_Privilege[] PRIVILEGES = new En_Privilege[] {
            En_Privilege.ISSUE_VIEW, En_Privilege.ISSUE_EDIT, En_Privilege.ISSUE_CREATE,
    };

    private static final String QWERTY_PASSWORD = "qwerty_test_API" + new Date().getTime();

    private static final String ISSUES_PREFIX = "Portal_API_issue_test_";

    private static final String PORTAL_API_TEST_ROLE_CODE = "portal_api_test_role";
}
