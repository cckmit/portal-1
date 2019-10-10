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
import ru.protei.portal.config.DatabaseConfiguration;
import ru.protei.portal.config.IntegrationTestsConfiguration;
import ru.protei.portal.core.controller.api.PortalApiController;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.query.CaseApiQuery;
import ru.protei.portal.core.service.CaseService;
import ru.protei.portal.core.service.auth.AuthService;
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
@ContextConfiguration(classes = {CoreConfigurationContext.class, JdbcConfigurationContext.class, DatabaseConfiguration.class, IntegrationTestsConfiguration.class, PortalApiController.class})
@WebAppConfiguration
public class TestPortalApiController extends BaseServiceTest {
    private static final Logger log = LoggerFactory.getLogger(TestPortalApiController.class);
    private static final int COUNT_OF_ISSUES_WITH_MANAGER = 5;
    private static final int COUNT_OF_ISSUES_WITHOUT_MANAGER = 5;
    private static final int COUNT_OF_PRIVATE_ISSUES = 5;
    private static final int COUNT_OF_ISSUES = COUNT_OF_PRIVATE_ISSUES + COUNT_OF_ISSUES_WITH_MANAGER + COUNT_OF_ISSUES_WITHOUT_MANAGER;
    private static final List<Long> issuesIds = new ArrayList<>();
    private static final En_Privilege[] PRIVILEGES = new En_Privilege[]{En_Privilege.ISSUE_VIEW, En_Privilege.ISSUE_EDIT, En_Privilege.ISSUE_CREATE};
    private static final String QWERTY_PASSWORD = "qwerty_test_API" + new Date().getTime();
    private static final String ISSUES_PREFIX = "Portal_API_issue_test_";
    private static final String PORTAL_API_TEST_ROLE_CODE = "portal_api_test_role";
    private static ObjectMapper objectMapper;
    private static PersonDAO personDAO;
    private static UserLoginDAO userLoginDAO;
    private static CaseService caseService;
    private static AuthService authService;
    private static CaseObjectDAO caseObjectDAO;
    private static UserRoleDAO userRoleDAO;
    private static CaseCommentDAO caseCommentDAO;
    private static Person person;
    private static UserRole mainRole;
    private MockMvc mockMvc;

    @Autowired
    PortalApiController portalApiController;

    @BeforeClass
    public static void initClass() throws Exception {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(
                CoreConfigurationContext.class,
                JdbcConfigurationContext.class,
                DatabaseConfiguration.class,
                IntegrationTestsConfiguration.class
        );

        objectMapper = applicationContext.getBean(ObjectMapper.class);
        personDAO = applicationContext.getBean(PersonDAO.class);
        caseService = applicationContext.getBean(CaseService.class);
        authService = applicationContext.getBean(AuthService.class);
        userLoginDAO = applicationContext.getBean(UserLoginDAO.class);
        userRoleDAO = applicationContext.getBean(UserRoleDAO.class);
        caseObjectDAO = applicationContext.getBean(CaseObjectDAO.class);
        caseCommentDAO = applicationContext.getBean(CaseCommentDAO.class);

        caseCommentDAO.removeAll();
        caseObjectDAO.removeAll();

        createAndPersistPerson();
        createAndPersistUserRoles();
        createAndPersistUserLogin();
        createAndPersistSomeIssues();
        createAndPersistSomeIssuesWithManager(person);
        createAndPersistSomePrivateIssues();

        log.debug("issues={} | issues_with_manager={} | issues_without_manager={} | private_issues={}",
                COUNT_OF_ISSUES,
                COUNT_OF_ISSUES_WITH_MANAGER,
                COUNT_OF_ISSUES_WITHOUT_MANAGER,
                COUNT_OF_PRIVATE_ISSUES
        );
    }

    @Before
    public void initMockMvc() {
        mockMvc = MockMvcBuilders.standaloneSetup(portalApiController).build();
    }

    @Test
    public void testGetCaseList_all() throws Exception {
        ResultActions accept = createPostResultAction("/api/cases", new CaseApiQuery());

        accept
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(En_ResultStatus.OK.toString())))
                .andExpect(jsonPath("$.data", hasSize(COUNT_OF_ISSUES)));
    }

    @Test
    public void testGetCaseList_withManager() throws Exception {
        CaseApiQuery caseApiQuery = new CaseApiQuery();
        caseApiQuery.setManagerIds(Collections.singletonList(person.getId()));

        ResultActions accept = createPostResultAction("/api/cases", caseApiQuery);

        accept
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(En_ResultStatus.OK.toString())))
                .andExpect(jsonPath("$.data", hasSize(COUNT_OF_ISSUES_WITH_MANAGER)));
    }

    @Test
    public void testGetCaseList_publicIssues() throws Exception {
        CaseApiQuery caseApiQuery = new CaseApiQuery();
        caseApiQuery.setAllowViewPrivate(false);

        ResultActions accept = createPostResultAction("/api/cases", caseApiQuery);

        accept
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(En_ResultStatus.OK.toString())))
                .andExpect(jsonPath("$.data", hasSize(COUNT_OF_ISSUES - COUNT_OF_PRIVATE_ISSUES)));
    }

    @Test
    public void testGetThreeResults() throws Exception {
        CaseApiQuery caseApiQuery = new CaseApiQuery();
        caseApiQuery.setLimit(3);

        ResultActions accept = createPostResultAction("/api/cases", caseApiQuery);

        accept
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(En_ResultStatus.OK.toString())))
                .andExpect(jsonPath("$.data", hasSize(3)));
    }

    @Test
    public void testCreateIssue() throws Exception {
        CaseObject caseObject = createNewCaseObject(person);
        String issueName = ISSUES_PREFIX + "test_create";
        caseObject.setName(issueName);
        caseObject.setInitiator(person);

        ResultActions actions = createPostResultAction("/api/cases/create", caseObject);
        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(En_ResultStatus.OK.toString())));

        CaseObject caseObjectFromDb = caseObjectDAO.getByCaseNameLike(ISSUES_PREFIX + "test_create");

        Assert.assertNotNull("Expected 1 new created issue", caseObjectFromDb);

        caseCommentDAO.removeByCaseIds(Collections.singletonList(caseObjectFromDb.getId()));
        caseObjectDAO.removeByKey(caseObjectFromDb.getId());
    }

    @Test
    public void testUpdateIssue() throws Exception {
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

    @AfterClass
    public static void destroy() {
        caseCommentDAO.removeByCaseIds(issuesIds);
        caseObjectDAO.removeByNameLike(ISSUES_PREFIX);
        userLoginDAO.removeByPersonId(person.getId());
        userRoleDAO.removeByRoleCodeLike(PORTAL_API_TEST_ROLE_CODE);
        personDAO.removeByKey(person.getId());
    }

    private static void createAndPersistPerson() {
        Person p = new Person(0L);
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
                .filter(currPerson -> currPerson.getFirstName() != null && currPerson.getFirstName().equals(personFirstName))
                .findFirst().get();
    }

    private static void createAndPersistUserRoles() {
        UserRole role = new UserRole();
        role.setCode(PORTAL_API_TEST_ROLE_CODE);
        role.setInfo(PORTAL_API_TEST_ROLE_CODE);
        role.setPrivileges(new HashSet<>(Arrays.asList(PRIVILEGES)));
        role.setScope(En_Scope.SYSTEM);

        userRoleDAO.persist(role);

        mainRole = userRoleDAO.getByRoleCodeLike(PORTAL_API_TEST_ROLE_CODE);
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

    private static void createAndPersistSomeIssues() {
        for (int i = 0; i < COUNT_OF_ISSUES_WITHOUT_MANAGER; i++) {
            CaseObject caseObject = createNewCaseObject(person);
            caseObject.setName(ISSUES_PREFIX + i);
            caseObject.setInitiator(person);
            issuesIds.add(caseService.createCaseObject(authService.findSession(null).makeAuthToken(), caseObject, person).getData().getId());
        }
    }

    private static void createAndPersistSomeIssuesWithManager(Person manager) {
        for (int i = 0; i < COUNT_OF_ISSUES_WITH_MANAGER; i++) {
            CaseObject caseObject = createNewCaseObject(person);
            caseObject.setName(ISSUES_PREFIX + i);
            caseObject.setManager(manager);
            issuesIds.add(caseService.createCaseObject(authService.findSession(null).makeAuthToken(), caseObject, person).getData().getId());
        }
    }

    private static void createAndPersistSomePrivateIssues() {
        for (int i = 0; i < COUNT_OF_PRIVATE_ISSUES; i++) {
            CaseObject caseObject = createNewCaseObject(person);
            caseObject.setName(ISSUES_PREFIX + i);
            caseObject.setInitiator(person);
            caseObject.setPrivateCase(true);
            issuesIds.add(caseService.createCaseObject(authService.findSession(null).makeAuthToken(), caseObject, person).getData().getId());
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
}
