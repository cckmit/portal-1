package ru.protei.portal.test.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
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
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.query.CaseApiQuery;
import ru.protei.portal.core.model.query.CaseCommentApiQuery;
import ru.protei.portal.core.service.auth.AuthService;
import ru.protei.portal.mock.AuthServiceMock;
import ru.protei.portal.test.service.BaseServiceTest;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {CoreConfigurationContext.class, JdbcConfigurationContext.class, DatabaseConfiguration.class, IntegrationTestsConfiguration.class, PortalApiController.class})
@WebAppConfiguration
public class TestPortalApiController extends BaseServiceTest {
    @Autowired
    PortalApiController portalApiController;

    @Autowired
    ObjectMapper objectMapper;

    private static final int COUNT_OF_ISSUES_WITH_MANAGER = 5;
    private static final int COUNT_OF_ISSUES_WITHOUT_MANAGER = 5;
    private static final int COUNT_OF_PRIVATE_ISSUES = 5;
    private final List<Long> issuesIds = new ArrayList<>();
    private final En_Privilege[] PRIVILEGES = new En_Privilege[]{En_Privilege.ISSUE_VIEW, En_Privilege.ISSUE_EDIT, En_Privilege.ISSUE_CREATE};
    private final String QWERTY_PASSWORD = "qwerty_test_API_password";
    private final String ISSUES_PREFIX = "Portal_API_issue_test_";
    private final String PORTAL_API_TEST_ROLE_CODE = "portal_api_test_role";

    private Person person;
    private UserLogin userLogin;
    private Company company;
    private UserRole mainRole;
    private MockMvc mockMvc;
    private AuthServiceMock authService;

    @Before
    public void init() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(portalApiController).build();

        company = new Company(1L);
        person = createAndPersistPerson(company);
        mainRole = createAndPersistUserRoles();
        userLogin = createAndPersistUserLogin();

        setThreadUserLogin(userLogin);

        createAndPersistSomeIssues(person, company.getId());
    }

    @Test
    public void createIssue() throws Exception {
        CaseObject caseObject = createNewCaseObject(person);
        String issueName = ISSUES_PREFIX + "test_create";
        caseObject.setName(issueName);
        caseObject.setInitiator(person);
        caseObject.setInitiatorCompany( company );

        authService.makeThreadAuthToken( userLogin );
        ResultActions actions = createPostResultAction("/api/cases/create", caseObject);
        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(En_ResultStatus.OK.toString())))
                .andExpect(jsonPath("$.data.initiatorId", is(person.getId().intValue())))
                .andExpect(jsonPath("$.data.initiatorCompanyId", is(company.getId().intValue())));

        CaseObject caseObjectFromDb = caseObjectDAO.getByCaseNameLike(ISSUES_PREFIX + "test_create");

        Assert.assertNotNull("Expected 1 new created issue", caseObjectFromDb);

        issuesIds.add(caseObjectFromDb.getId());
        authService.resetThreadAuthToken();
    }

    @Test
    public void getCaseListByManager() throws Exception {
        createAndPersistSomeIssuesWithManager(person, company.getId());

        CaseApiQuery caseApiQuery = new CaseApiQuery();
        caseApiQuery.setManagerIds(Collections.singletonList(person.getId()));

        ResultActions accept = createPostResultAction("/api/cases", caseApiQuery);

        accept
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(En_ResultStatus.OK.toString())))
                .andExpect(jsonPath("$.data[*].managerId", everyItem(is(person.getId().intValue()))));
    }

    @Test
    public void getPublicCases() throws Exception {
        createAndPersistSomePrivateIssues(person, company.getId());

        CaseApiQuery caseApiQuery = new CaseApiQuery();
        caseApiQuery.setAllowViewPrivate(false);

        ResultActions accept = createPostResultAction("/api/cases", caseApiQuery);

        accept
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(En_ResultStatus.OK.toString())))
                .andExpect(jsonPath("$.data[*].privateCase", everyItem(is(false))));
    }

    @Test
    public void getThreeResults() throws Exception {
        final int LIMIT = 3;

        CaseApiQuery caseApiQuery = new CaseApiQuery();
        caseApiQuery.setLimit(LIMIT);

        ResultActions accept = createPostResultAction("/api/cases", caseApiQuery);

        accept
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(En_ResultStatus.OK.toString())))
                .andExpect(jsonPath("$.data", hasSize(LIMIT)));
    }

    @Test
    public void getCaseListByCompanyId() throws Exception {
        CaseApiQuery caseApiQuery = new CaseApiQuery();
        caseApiQuery.setCompanyIds(Collections.singletonList(1L));

        ResultActions accept = createPostResultAction("/api/cases", caseApiQuery);

        accept
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(En_ResultStatus.OK.toString())))
                .andExpect(jsonPath("$.data[*].initiatorCompanyId", everyItem(is(1))));
    }

    @Test
    public void getCaseListByCompanyIdEmptyResult() throws Exception {
        CaseApiQuery caseApiQuery = new CaseApiQuery();
        caseApiQuery.setCompanyIds(Collections.singletonList(companyDAO.getMaxId() + 1));

        ResultActions accept = createPostResultAction("/api/cases", caseApiQuery);

        accept
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(En_ResultStatus.OK.toString())))
                .andExpect(jsonPath("$.data", empty()));
    }

    @Test
    public void getCaseCommentsListByCaseId() throws Exception {
        createAndPersistIssueForComments(person, company.getId());

        final int COMMENTS_COUNT = 3;

        CaseCommentApiQuery caseCommentApiQuery = new CaseCommentApiQuery();
        caseCommentApiQuery.setCaseId(caseObjectDAO.getByCaseNameLike(ISSUES_PREFIX + "testGetCaseCommentsListByCaseId").getId());

        ResultActions accept = createPostResultAction("/api/comments", caseCommentApiQuery);

        accept
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(En_ResultStatus.OK.toString())))
                .andExpect(jsonPath("$.data", hasSize(COMMENTS_COUNT)));
    }

    @Test
    public void getCaseCommentsListByCaseIdEmptyResult() throws Exception {
        createAndPersistIssueForComments(person, company.getId());

        CaseCommentApiQuery caseCommentApiQuery = new CaseCommentApiQuery();
        caseCommentApiQuery.setCaseId(caseObjectDAO.getMaxId() + 1);

        ResultActions accept = createPostResultAction("/api/comments", caseCommentApiQuery);

        accept
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(En_ResultStatus.OK.toString())))
                .andExpect(jsonPath("$.data", empty()));
    }

    @Test
    public void getCaseCommentsListByCaseIdError() throws Exception {
        createAndPersistIssueForComments(person, company.getId());

        CaseCommentApiQuery caseCommentApiQuery = new CaseCommentApiQuery();
        caseCommentApiQuery.setCaseId(null);

        ResultActions accept = createPostResultAction("/api/comments", caseCommentApiQuery);

        accept
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(En_ResultStatus.INCORRECT_PARAMS.toString())));
    }

    @After
    public void destroy() {
        caseCommentDAO.removeByCaseIds(issuesIds);
        caseObjectDAO.removeByNameLike(ISSUES_PREFIX);
        userLoginDAO.removeByPersonId(person.getId());
        userRoleDAO.removeByRoleCodeLike(PORTAL_API_TEST_ROLE_CODE);
        personDAO.removeByKey(person.getId());
    }

    private Person createAndPersistPerson(Company company) {
        Person p = createNewPerson( company );
        String personFirstName = "Test" + new Date().getTime();

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

       return personDAO
                .getAll()
                .stream()
                .filter(currPerson -> currPerson.getFirstName() != null && currPerson.getFirstName().equals(personFirstName))
                .findFirst().get();
    }

    private UserRole createAndPersistUserRoles() {
        UserRole role = new UserRole();
        role.setCode(PORTAL_API_TEST_ROLE_CODE);
        role.setInfo(PORTAL_API_TEST_ROLE_CODE);
        role.setPrivileges(new HashSet<>(Arrays.asList(PRIVILEGES)));
        role.setScope(En_Scope.SYSTEM);

        role.setId(  userRoleDAO.persist(role)  );

        return role;
    }

    private UserLogin createAndPersistUserLogin() throws Exception {
        UserLogin userLogin = userLoginDAO.createNewUserLogin(person);
        userLogin.setUlogin(person.getFirstName());
        userLogin.setUpass(DigestUtils.md5DigestAsHex(QWERTY_PASSWORD.getBytes()));
        userLogin.setPersonId(person.getId());
        userLogin.setAuthTypeId(En_AuthType.LOCAL.getId());
        userLogin.setAdminStateId(En_AdminState.UNLOCKED.getId());
        userLogin.setRoles(Collections.singleton(mainRole));

        userLogin.setId( userLoginDAO.persist( userLogin ) );
        return userLogin;
    }

    private void createAndPersistSomeIssues(Person person, Long companyId) {
        for (int i = 0; i < COUNT_OF_ISSUES_WITHOUT_MANAGER; i++) {
            CaseObject caseObject = createNewCaseObject(person);
            caseObject.setName(ISSUES_PREFIX + i);
            caseObject.setInitiator(person);
            caseObject.setInitiatorCompanyId(companyId);
            issuesIds.add(caseService.createCaseObject(authService.getAuthToken(), new CaseObjectCreateRequest(caseObject)).getData().getId());
        }
    }

    private void createAndPersistSomeIssuesWithManager(Person person, Long companyId) {
        for (int i = 0; i < COUNT_OF_ISSUES_WITH_MANAGER; i++) {
            CaseObject caseObject = createNewCaseObject(person);
            caseObject.setName(ISSUES_PREFIX + i);
            caseObject.setManager(person);
            caseObject.setInitiatorCompanyId(companyId);
            issuesIds.add(caseService.createCaseObject(authService.getAuthToken(), new CaseObjectCreateRequest(caseObject)).getData().getId());
        }
    }

    private void createAndPersistIssueForComments(Person manager, Long companyId ) {
        CaseObject caseObject = createNewCaseObject(manager);
        caseObject.setName(ISSUES_PREFIX + "testGetCaseCommentsListByCaseId");
        caseObject.setInitiator(manager);
        caseObject.setInitiatorCompanyId(companyId);
        issuesIds.add(caseService.createCaseObject(authService.getAuthToken(), new CaseObjectCreateRequest(caseObject)).getData().getId());

        CaseComment caseComment = new CaseComment();
        caseComment.setCaseId(caseObject.getId());
        caseComment.setCreated(new Date((new Date()).getTime() + 1000));
        caseComment.setAuthorId(manager.getId());
        caseComment.setText("testGetCaseCommentsListByCaseId. text comment. private");
        caseComment.setPrivateComment(true);
        caseCommentDAO.persist(caseComment);
    }

    private void createAndPersistSomePrivateIssues(Person person, Long companyId) {
        for (int i = 0; i < COUNT_OF_PRIVATE_ISSUES; i++) {
            CaseObject caseObject = createNewCaseObject(person);
            caseObject.setName(ISSUES_PREFIX + i);
            caseObject.setInitiator(person);
            caseObject.setPrivateCase(true);
            caseObject.setInitiatorCompanyId(companyId);
            issuesIds.add(caseService.createCaseObject(authService.getAuthToken(), new CaseObjectCreateRequest(caseObject)).getData().getId());
        }
    }

    private void setThreadUserLogin(UserLogin userLogin) {
        authService.makeThreadAuthToken(userLogin);
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

    @Autowired
    private void authService(AuthService authService) {
        this.authService = (AuthServiceMock) authService;
    }
}
