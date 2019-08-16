package ru.protei.portal.test.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import ru.protei.portal.core.model.dao.UserLoginDAO;
import ru.protei.portal.core.model.dict.En_ContactItemType;
import ru.protei.portal.core.model.dict.En_Gender;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.ent.UserLogin;
import ru.protei.portal.core.model.query.CaseApiQuery;
import ru.protei.portal.test.service.BaseServiceTest;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

import java.util.Base64;
import java.util.Date;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {PortalApiController.class, CoreConfigurationContext.class, JdbcConfigurationContext.class, DatabaseConfiguration.class, MainTestsConfiguration.class})
@WebAppConfiguration
public class TestPortalApiController extends BaseServiceTest {
    private static Logger logger = LoggerFactory.getLogger(TestPortalApiController.class);

    @Autowired
    WebApplicationContext webApplicationContext;

    @Autowired
    UserLoginDAO userLoginDAO;

    MockMvc mockMvc;

    private Person person;

    private UserLogin userLogin;

    @Before
    public void init() throws Exception {
        createAndPersistPerson();
        createAndPersistUserLogin();
        createAndPersistSomeIssues();
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

//    TODO
//    @Test
//    public void testGetCaseList() throws Exception {
//        CaseApiQuery caseApiQuery = new CaseApiQuery();
//        caseApiQuery.setLimit(3);
//        caseApiQuery.setOffset(5);
//
//        String json = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL).writerWithDefaultPrettyPrinter().writeValueAsString(caseApiQuery);
//
//        ResultActions accept = createPostResultAction("/api/cases", json);
//
//        accept
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(10)));
//    }

    @Test
    public void testCreateIssue() throws Exception {
        CaseObject caseObject = createNewCaseObject(person);
        String issuesName = "API_Test_Issue_from_test_create_issue";
        caseObject.setName(issuesName);

        String json = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL).writerWithDefaultPrettyPrinter().writeValueAsString(caseObject);

        ResultActions actions = createPostResultAction("/api/cases/create", json);

        actions.andExpect(status().isOk());

        long countOfIssues = caseObjectDAO
                .getAll()
                .stream()
                .filter(currCaseObj -> currCaseObj.getName().equals(issuesName))
                .count();

        Assert.assertEquals("Expected 1 new created issue", 1, countOfIssues);
    }

    private void createAndPersistPerson() {
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

        this.person = personDAO
                .getAll()
                .stream()
                .filter(currPerson -> currPerson.getFirstName().equals(personFirstName))
                .findFirst().get();
    }

    private void createAndPersistUserLogin() throws Exception {
        userLogin = userLoginDAO.createNewUserLogin(person);
        userLogin.setUlogin(person.getFirstName());
        userLogin.setUpass(DigestUtils.md5DigestAsHex(QWERTY_PASSWORD.getBytes()));
        userLogin.setAuthTypeId(1);
        userLogin.setAdminStateId(2);

        userLoginDAO.persist(userLogin);
    }

    private void createAndPersistSomeIssues() {
        for (int i = 0; i < 10; i++) {
            CaseObject caseObject = createNewCaseObject(person);
            caseObject.setName("API_Test_Issue_" + i);
            caseService.saveCaseObject(getAuthToken(), caseObject, person);
        }
    }

    private ResultActions createPostResultAction(String url, String json) throws Exception {
        return mockMvc.perform(
                post(url)
                        .header("Accept", "application/json")
                        .header("authorization", "Basic " + Base64.getEncoder().encodeToString((person.getFirstName() + ":" + QWERTY_PASSWORD).getBytes()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        );
    }

    private static final String QWERTY_PASSWORD = "qwerty";
}
