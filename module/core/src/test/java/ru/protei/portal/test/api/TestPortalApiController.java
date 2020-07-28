package ru.protei.portal.test.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.config.IntegrationTestsConfiguration;
import ru.protei.portal.core.client.youtrack.mapper.YtDtoFieldsMapper;
import ru.protei.portal.core.client.youtrack.mapper.YtDtoObjectMapperProvider;
import ru.protei.portal.core.controller.api.PortalApiController;
import ru.protei.portal.core.model.dict.En_CaseLink;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.dto.CaseTagInfo;
import ru.protei.portal.core.model.dto.DevUnitInfo;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.query.*;
import ru.protei.portal.core.model.struct.PlainContactInfoFacade;
import ru.protei.portal.core.model.youtrack.YtFieldDescriptor;
import ru.protei.portal.core.model.youtrack.dto.issue.YtIssueComment;
import ru.protei.portal.core.model.youtrack.dto.user.YtUser;
import ru.protei.portal.core.service.CaseCommentService;
import ru.protei.portal.core.service.auth.AuthService;
import ru.protei.portal.embeddeddb.DatabaseConfiguration;
import ru.protei.portal.mock.AuthServiceMock;
import ru.protei.portal.test.service.BaseServiceTest;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;


@RunWith(SpringJUnit4ClassRunner.class)
@EnableTransactionManagement
@ContextConfiguration(classes = {CoreConfigurationContext.class, JdbcConfigurationContext.class, DatabaseConfiguration.class, IntegrationTestsConfiguration.class, PortalApiController.class})
@WebAppConfiguration
public class TestPortalApiController extends BaseServiceTest {
    @Autowired
    PortalApiController portalApiController;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private YtDtoFieldsMapper fieldsMapper;
    @Autowired
    private CaseCommentService caseCommentService;

    @Autowired
    private void authService(AuthService authService) {
        this.authService = (AuthServiceMock) authService;
    }

    private static final long FAKE_ID = 10000L;
    private Person person;
    private UserLogin userLogin;
    private Company company;
    private MockMvc mockMvc;
    private AuthServiceMock authService;

    @PostConstruct
    public void postConstruct(  ) {
        mockMvc = MockMvcBuilders.standaloneSetup(portalApiController).build();

        company = makeCustomerCompany();
        person = makePerson(company);
        userLogin = makeUserLogin(createUserLogin(person));
    }

    @Before
    public void beforeEach() throws Exception {
        setThreadUserLogin(userLogin);
    }

    @After
    public void afterEach() {
        authService.resetThreadAuthToken();
    }

    @Test
    @Transactional
    public void createIssue() throws Exception {
        CaseObject caseObject = createNewCaseObject(person);
        String issueName = "Portal_API_issue_test_test_create";
        caseObject.setName(issueName);
        caseObject.setInitiator(person);
        caseObject.setInitiatorCompany( company );

        authService.makeThreadAuthToken( userLogin );
        ResultActions actions = createPostResultAction("/api/cases/create", caseObject);
        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(En_ResultStatus.OK.toString())))
                .andExpect(jsonPath("$.data.id", notNullValue()))
                .andExpect(jsonPath("$.data.initiatorId", is(person.getId().intValue())))
                .andExpect(jsonPath("$.data.initiatorCompanyId", is(company.getId().intValue())));

        CaseObject caseObjectFromDb = caseObjectDAO.getByCaseNameLike(issueName);

        Assert.assertNotNull("Expected 1 new created issue", caseObjectFromDb);
    }

    @Test
    @Transactional
    public void createIssueWithAutoOpenIssueWithoutProduct() throws Exception {
        company.setAutoOpenIssue(true);
        companyDAO.saveOrUpdate(company);

        CaseObject caseObject = createNewCaseObject(person);
        String issueName = "createIssueWithAutoOpenIssueWithoutProduct";
        caseObject.setName(issueName);
        caseObject.setInitiator(person);
        caseObject.setInitiatorCompany( company );

        authService.makeThreadAuthToken( userLogin );
        ResultActions actions = createPostResultAction("/api/cases/create", caseObject);
        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(En_ResultStatus.INCORRECT_PARAMS.toString())));

        company.setAutoOpenIssue(false);
        companyDAO.saveOrUpdate(company);
    }

    @Test
    @Transactional
    public void createIssueWithAutoOpenIssueWithRandomProduct() throws Exception {
        company.setAutoOpenIssue(true);
        companyDAO.saveOrUpdate(company);

        DevUnit devUnit = makeProduct("createIssueWithAutoOpenIssueWithRandomProduct");

        CaseObject caseObject = createNewCaseObject(person);
        String issueName = "createIssueWithAutoOpenIssueWithRandomProduct";
        caseObject.setName(issueName);
        caseObject.setInitiator(person);
        caseObject.setInitiatorCompany(company);
        caseObject.setProductId(devUnit.getId());

        authService.makeThreadAuthToken( userLogin );
        ResultActions actions = createPostResultAction("/api/cases/create", caseObject);
        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(En_ResultStatus.INCORRECT_PARAMS.toString())));

        company.setAutoOpenIssue(false);
        companyDAO.saveOrUpdate(company);

        devUnitDAO.remove(devUnit);
    }

    @Test
    @Transactional
    public void createIssueWithAutoOpenIssueWithPlatformOwnedProduct() throws Exception {
        company.setAutoOpenIssue(true);
        companyDAO.saveOrUpdate(company);

        DevUnit devUnit = makeProduct("createIssueWithAutoOpenIssueWithCorrectProduct");

        CaseObject project = createNewCaseObject(person);
        project.setName("createIssueWithAutoOpenIssueWithCorrectProduct");
        project.setInitiatorCompanyId(company.getId());
        project.setType(En_CaseType.PROJECT);
        project.setProductId(devUnit.getId());

        Long projectId = caseObjectDAO.persist(project);

        Platform platform = new Platform();
        platform.setName("createIssueWithAutoOpenIssueWithCorrectProduct");
        platform.setCompanyId(company.getId());
        platform.setProjectId(projectId);

        ProjectToProduct projectToProduct = new ProjectToProduct(projectId, devUnit.getId());
        projectToProductDAO.persist(projectToProduct);

        Long platformId = platformDAO.persist(platform);

        CaseObject caseObject = createNewCaseObject(person);
        caseObject.setName("createIssueWithAutoOpenIssueWithoutProduct");
        caseObject.setInitiator(person);
        caseObject.setInitiatorCompany(company);
        caseObject.setPlatformId(platformId);
        caseObject.setProductId(devUnit.getId());

        authService.makeThreadAuthToken( userLogin );
        ResultActions actions = createPostResultAction("/api/cases/create", caseObject);
        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(En_ResultStatus.OK.toString())))
                .andExpect(jsonPath("$.data.id", notNullValue()))
                .andExpect(jsonPath("$.data.initiatorId", is(person.getId().intValue())))
                .andExpect(jsonPath("$.data.initiatorCompanyId", is(company.getId().intValue())))
                .andExpect(jsonPath("$.data.productId", is(devUnit.getId().intValue())));

        company.setAutoOpenIssue(false);
        companyDAO.saveOrUpdate(company);
        platformDAO.removeByKey(platformId);
        projectToProductDAO.removeAllProductsFromProject(projectId);
        caseObjectDAO.removeByKey(projectId);
    }

    @Test
    @Transactional
    public void getCaseListByManager() throws Exception {
        makeCaseObject( person );
        makeCaseObject( person );
        makeCaseObject( person );

        CaseApiQuery caseApiQuery = new CaseApiQuery();
        caseApiQuery.setManagerCompanyIds(Collections.singletonList(person.getCompanyId()));
        caseApiQuery.setManagerIds(Collections.singletonList(person.getId()));

        ResultActions accept = createPostResultAction("/api/cases", caseApiQuery);

        accept
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(En_ResultStatus.OK.toString())))
                .andExpect(jsonPath("$.data[*].managerId", everyItem(is(person.getId().intValue()))));
    }

    @Test
    @Transactional
    public void getPublicCases() throws Exception {
        CaseObject caseObject = createNewCaseObject(person);
        caseObject.setPrivateCase(true);
        makeCaseObject( caseObject );

        CaseApiQuery caseApiQuery = new CaseApiQuery();
        caseApiQuery.setAllowViewPrivate(false);

        ResultActions accept = createPostResultAction("/api/cases", caseApiQuery);

        accept
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(En_ResultStatus.OK.toString())))
                .andExpect(jsonPath("$.data[*].privateCase", everyItem(is(false))));
    }

    @Test
    @Transactional
    public void getThreeResults() throws Exception {
        makeCaseObject( person );
        makeCaseObject( person );
        makeCaseObject( person );
        makeCaseObject( person );

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
    @Transactional
    public void getProduct() throws Exception {
        DevUnit product = makeProduct( );
        createPostResultAction( "/api/products/" + product.getId(), null )
                .andExpect( status().isOk() )
                .andExpect( jsonPath( "$.status", is( En_ResultStatus.OK.toString() ) ) )
                .andExpect( jsonPath( "$.data.id").value(  product.getId().intValue() ) )
                .andExpect( jsonPath( "$.data.historyVersion", is( product.getHistoryVersion() ) ) )
                .andExpect( jsonPath( "$.data.cdrDescription", is( product.getCdrDescription() ) ) )
                .andExpect( jsonPath( "$.data.configuration", is( product.getConfiguration() ) ) )
                .andExpect( jsonPath( "$.data.description", is( product.getInfo() ) ) )
                .andExpect( jsonPath( "$.data.wikiLink", is( product.getWikiLink() ) ) )
                .andExpect( jsonPath( "$.data.type", is( product.getType().name() ) ) )
        ;
    }

    @Test
    @Transactional
    public void createProduct() throws Exception {
        DevUnit product = createProduct("TestPortalApiController#createProduct");

        createPostResultAction("/api/products/create", DevUnitInfo.toInfo(product))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(En_ResultStatus.OK.toString())))
                .andExpect(jsonPath("$.data.id", notNullValue()))
                .andExpect(jsonPath("$.data.name", is(product.getName())))
                .andExpect(jsonPath("$.data.historyVersion", is(product.getHistoryVersion())))
                .andExpect(jsonPath("$.data.cdrDescription", is(product.getCdrDescription())))
                .andExpect(jsonPath("$.data.configuration", is(product.getConfiguration())))
                .andExpect(jsonPath("$.data.description", is(product.getInfo())))
                .andExpect(jsonPath("$.data.wikiLink", is(product.getWikiLink())))
                .andExpect(jsonPath("$.data.type", is(product.getType().name())));
    }

    @Test
    @Transactional
    public void updateProduct() throws Exception {
        DevUnit devUnit = makeProduct( );

        DevUnitInfo product = DevUnitInfo.toInfo(devUnit);

        createPostResultAction( "/api/products/update", product )
                .andExpect( status().isOk() )
                .andExpect( jsonPath( "$.status", is( En_ResultStatus.OK.toString() ) ) )
                .andExpect( jsonPath( "$.data").value(  product.getId().intValue() ) )
        ;
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
    @Transactional
    public void getCaseCommentsListByCaseNumber() throws Exception {
        CaseObject caseObject = makeCaseObject( person );
        makeCaseComment( person,caseObject.getId(), "testGetCaseCommentsListByCaseId. text comment. private" );

        CaseCommentQuery query = new CaseCommentQuery();
        query.setCaseObjectIds(Collections.singletonList(caseObject.getId()));
        int caseCommentByIdCount = caseCommentDAO.getCaseCommentsCaseIds(query).size();

        CaseCommentApiQuery caseCommentApiQuery = new CaseCommentApiQuery();
        caseCommentApiQuery.setCaseNumber(caseObject.getCaseNumber());

        ResultActions accept = createPostResultAction("/api/comments", caseCommentApiQuery);

        accept
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(En_ResultStatus.OK.toString())))
                .andExpect(jsonPath("$.data", hasSize(caseCommentByIdCount)));
    }

    @Test
    public void setYoutrackIdToEmptyCrmNumber() throws Exception {
        final String YOUTRACK_ID = "TEST-1";
        String numbers = "";
        ResultActions accept = createPostResultActionWithStringBody("/api/updateYoutrackCrmNumbers/" + YOUTRACK_ID, numbers).andExpect(status().isOk());

        Assert.assertTrue("Error message must be empty", accept.andReturn().getResponse().getContentAsString().isEmpty());

        List<Long> crmNumbers = fillAndCreateCaseObjects(3);

        numbers = crmNumbers.stream()
                .map(Object::toString)
                .collect(Collectors.joining(",\n"));

        //Устанавливаем 3 корректных номера
        accept = createPostResultActionWithStringBody("/api/updateYoutrackCrmNumbers/" + YOUTRACK_ID, numbers).andExpect(status().isOk());

        Assert.assertEquals("Received error message", "", accept.andReturn().getResponse().getContentAsString());

        numbers = "";
        createPostResultActionWithStringBody("/api/updateYoutrackCrmNumbers/" + YOUTRACK_ID, numbers).andExpect(status().isOk());

        List<Long> caseNumbersFromDB = findAllCaseNumbersByYoutrackId(YOUTRACK_ID);

        Assert.assertTrue("Case numbers list must be empty", caseNumbersFromDB.isEmpty());

        removeAllCaseObjectsAndLinksAndComments();
    }

    @Test
    public void setYoutrackIdToInvalidCrmNumber() throws Exception {
        final String YOUTRACK_ID = "TEST-1";
        String numbers = "NOT_NUMBER";
        ResultActions accept = createPostResultActionWithStringBody("/api/updateYoutrackCrmNumbers/" + YOUTRACK_ID, numbers).andExpect(status().isOk());

        Assert.assertFalse("Error message must be not empty", accept.andReturn().getResponse().getContentAsString().isEmpty());
    }

    @Test
    public void setYoutrackIdToUncreatedCrmNumber() throws Exception {
        final String YOUTRACK_ID = "TEST-1";
        String numbers = "9999999" + "," + "8888888";
        ResultActions accept = createPostResultActionWithStringBody("/api/updateYoutrackCrmNumbers/" + YOUTRACK_ID, numbers).andExpect(status().isOk());

        Assert.assertTrue("Error message must contains wrong numbers", accept.andReturn().getResponse().getContentAsString().contains(numbers));
    }

    @Test
    @Transactional
    public void setYoutrackIdToCorrectCrmNumbers() throws Exception {
        final String YOUTRACK_ID = "TEST-1";

        List<Long> caseNumbersCreated = fillAndCreateCaseObjects(3);

        String numbers = caseNumbersCreated.stream()
                .map(Object::toString)
                .collect(Collectors.joining(",\n"));

        //Устанавливаем 3 корректных номера
        ResultActions accept = createPostResultActionWithStringBody("/api/updateYoutrackCrmNumbers/" + YOUTRACK_ID, numbers).andExpect(status().isOk());

        Assert.assertEquals("Received error message", "", accept.andReturn().getResponse().getContentAsString());

        List<Long> caseNumbersFromDB = findAllCaseNumbersByYoutrackId(YOUTRACK_ID);

        Assert.assertTrue("Invalid list of case numbers", compareLists(caseNumbersFromDB, caseNumbersCreated));

        numbers = caseNumbersCreated.get(1) + ",\n" + caseNumbersCreated.get(2);

        caseNumbersCreated.remove(0);

        //Устанавливаем 2 корректных номера (то есть один удалится)
        accept = createPostResultActionWithStringBody("/api/updateYoutrackCrmNumbers/" + YOUTRACK_ID, numbers).andExpect(status().isOk());

        Assert.assertEquals("Received error message", "", accept.andReturn().getResponse().getContentAsString());

        caseNumbersFromDB = findAllCaseNumbersByYoutrackId(YOUTRACK_ID);

        Assert.assertTrue("Invalid list of case numbers", compareLists(caseNumbersFromDB, caseNumbersCreated));

        removeAllCaseObjectsAndLinksAndComments();
    }

    @Test
    @Transactional
    public void removeLinkFromCrmWithYoutrackIdInLowercase() throws Exception {
        final String YOUTRACK_ID = "TEST-1";
        final String YOUTRACK_ID_LOWERCASE = YOUTRACK_ID.toLowerCase();

        List<Long> caseNumbersCreated = fillAndCreateCaseObjects(1);

        String numbers = caseNumbersCreated.stream()
                .map(Object::toString)
                .collect(Collectors.joining(",\n"));

        ResultActions accept = createPostResultActionWithStringBody("/api/updateYoutrackCrmNumbers/" + YOUTRACK_ID, numbers).andExpect(status().isOk());

        numbers = "";

        accept = createPostResultActionWithStringBody("/api/updateYoutrackCrmNumbers/" + YOUTRACK_ID_LOWERCASE, numbers).andExpect(status().isOk());

        Assert.assertEquals("Received error message", "", accept.andReturn().getResponse().getContentAsString());

        List<Long> caseNumbersFromDB = findAllCaseNumbersByYoutrackId(YOUTRACK_ID);

        Assert.assertTrue("Case link must be removed!", caseNumbersFromDB.isEmpty());

        removeAllCaseObjectsAndLinksAndComments();
    }

    @Test
    @Transactional
    public void setYoutrackIdToDuplicatedCrmNumbers() throws Exception {
        final String YOUTRACK_ID = "TEST-1";
        final int UNIQUE_NUMBERS_COUNT = 3;

        List<Long> caseNumbersCreated = fillAndCreateCaseObjects(UNIQUE_NUMBERS_COUNT);

        String numbers = caseNumbersCreated.stream()
                .map(Object::toString)
                .collect(Collectors.joining(",\n"));

        numbers += ",\n" + caseNumbersCreated.get(0);

        //Устанавливаем 4 номера (один - дубликат)
        ResultActions accept = createPostResultActionWithStringBody("/api/updateYoutrackCrmNumbers/" + YOUTRACK_ID, numbers).andExpect(status().isOk());

        Assert.assertEquals("Received error message", "", accept.andReturn().getResponse().getContentAsString());

        List<Long> caseNumbersFromDB = findAllCaseNumbersByYoutrackId(YOUTRACK_ID);

        Assert.assertTrue("List must contain only unique numbers", compareLists(caseNumbersCreated, caseNumbersFromDB));

        removeAllCaseObjectsAndLinksAndComments();
    }


    @Test
    public void setYoutrackIdToEmptyProject() throws Exception {
        final String YOUTRACK_ID = "PROJECT_TEST-1";
        String numbers = "";
        ResultActions accept = createPostResultActionWithStringBody("/api/updateYoutrackProjectNumbers/" + YOUTRACK_ID, numbers).andExpect(status().isOk());

        Assert.assertTrue("Error message must be empty", accept.andReturn().getResponse().getContentAsString().isEmpty());

        List<Long> projectNumbersCreated = fillAndCreateProjects(3);

        numbers = projectNumbersCreated.stream()
                .map(Object::toString)
                .collect(Collectors.joining(",\n"));

        //Устанавливаем 3 корректных номера
        accept = createPostResultActionWithStringBody("/api/updateYoutrackProjectNumbers/" + YOUTRACK_ID, numbers).andExpect(status().isOk());

        Assert.assertEquals("Received error message", "", accept.andReturn().getResponse().getContentAsString());

        numbers = "";
        createPostResultActionWithStringBody("/api/updateYoutrackProjectNumbers/" + YOUTRACK_ID, numbers).andExpect(status().isOk());

        List<Long> caseNumbersFromDB = findAllCaseIdsByYoutrackId(YOUTRACK_ID);

        Assert.assertTrue("Case numbers list must be empty", caseNumbersFromDB.isEmpty());

        removeAllCaseObjectsAndLinksAndComments();
    }

    @Test
    public void setYoutrackIdToInvalidProjectNumber() throws Exception {
        final String YOUTRACK_ID = "PROJECT_TEST-2";
        String numbers = "NOT_NUMBER";
        ResultActions accept = createPostResultActionWithStringBody("/api/updateYoutrackProjectNumbers/" + YOUTRACK_ID, numbers).andExpect(status().isOk());

        Assert.assertFalse("Error message must be not empty", accept.andReturn().getResponse().getContentAsString().isEmpty());
    }

    @Test
    public void setYoutrackIdToUncreatedProjectNumber() throws Exception {
        final String YOUTRACK_ID = "PROJECT_TEST-3";
        String numbers = "9999999" + "," + "8888888";
        ResultActions accept = createPostResultActionWithStringBody("/api/updateYoutrackProjectNumbers/" + YOUTRACK_ID, numbers).andExpect(status().isOk());

        Assert.assertTrue("Error message must contains wrong numbers", accept.andReturn().getResponse().getContentAsString().contains(numbers));
    }

    @Test
    @Transactional
    public void setYoutrackIdToCorrectProjectNumbers() throws Exception {
        final String YOUTRACK_ID = "PROJECT_TEST-4";

        List<Long> projectNumbersCreated = fillAndCreateProjects(3);

        String numbers = projectNumbersCreated.stream()
                .map(Object::toString)
                .collect(Collectors.joining(",\n"));

        //Устанавливаем 3 корректных номера
        ResultActions accept = createPostResultActionWithStringBody("/api/updateYoutrackProjectNumbers/" + YOUTRACK_ID, numbers).andExpect(status().isOk());

        Assert.assertEquals("Received error message", "", accept.andReturn().getResponse().getContentAsString());

        List<Long> projectNumbersFromDB = findAllCaseIdsByYoutrackId(YOUTRACK_ID);

        Assert.assertTrue("Invalid list of project numbers", compareLists(projectNumbersFromDB, projectNumbersCreated));

        numbers = projectNumbersCreated.get(1) + ",\n" + projectNumbersCreated.get(2);

        projectNumbersCreated.remove(0);

        //Устанавливаем 2 корректных номера (то есть один удалится)
        accept = createPostResultActionWithStringBody("/api/updateYoutrackProjectNumbers/" + YOUTRACK_ID, numbers).andExpect(status().isOk());

        Assert.assertEquals("Received error message", "", accept.andReturn().getResponse().getContentAsString());

        projectNumbersFromDB = findAllCaseIdsByYoutrackId(YOUTRACK_ID);

        Assert.assertTrue("Invalid list of project numbers", compareLists(projectNumbersFromDB, projectNumbersCreated));

        removeAllCaseObjectsAndLinksAndComments();
    }

    @Test
    @Transactional
    public void removeLinkFromProjectWithYoutrackIdInLowercase() throws Exception {
        final String YOUTRACK_ID = "PROJECT_TEST-5";
        final String YOUTRACK_ID_LOWERCASE = YOUTRACK_ID.toLowerCase();

        List<Long> projectNumbersCreated = fillAndCreateProjects(1);

        String numbers = projectNumbersCreated.stream()
                .map(Object::toString)
                .collect(Collectors.joining(",\n"));

        ResultActions accept = createPostResultActionWithStringBody("/api/updateYoutrackProjectNumbers/" + YOUTRACK_ID, numbers).andExpect(status().isOk());

        numbers = "";

        accept = createPostResultActionWithStringBody("/api/updateYoutrackProjectNumbers/" + YOUTRACK_ID_LOWERCASE, numbers).andExpect(status().isOk());

        Assert.assertEquals("Received error message", "", accept.andReturn().getResponse().getContentAsString());

        List<Long> projectNumbersFromDB = findAllCaseIdsByYoutrackId(YOUTRACK_ID);

        Assert.assertTrue("Case link must be removed!", projectNumbersFromDB.isEmpty());

        removeAllCaseObjectsAndLinksAndComments();
    }

    @Test
    @Transactional
    public void setYoutrackIdToDuplicatedProjectNumbers() throws Exception {
        final String YOUTRACK_ID = "PROJECT_TEST-6";

        List<Long> projectNumbersCreated = fillAndCreateProjects(3);

        String numbers = projectNumbersCreated.stream()
                .map(Object::toString)
                .collect(Collectors.joining(",\n"));

        numbers += ",\n" + projectNumbersCreated.get(0);

        //Устанавливаем 4 номера (один - дубликат)
        ResultActions accept = createPostResultActionWithStringBody("/api/updateYoutrackProjectNumbers/" + YOUTRACK_ID, numbers).andExpect(status().isOk());

        Assert.assertEquals("Received error message", "", accept.andReturn().getResponse().getContentAsString());

        List<Long> projectNumbersFromDB = findAllCaseIdsByYoutrackId(YOUTRACK_ID);

        Assert.assertTrue("List must contain only unique numbers", compareLists(projectNumbersCreated, projectNumbersFromDB));

        removeAllCaseObjectsAndLinksAndComments();
    }


    @Test
    @Transactional
    public void setYoutrackIdToProjectAndCrm() throws Exception {
        final String YOUTRACK_ID = "PROJECT_AND_CRM_TEST";

        List<Long> projectIdsCreated = fillAndCreateProjects(3);
        List<Long> crmNumbersCreated = fillAndCreateCaseObjects(3);

        String projectIds = projectIdsCreated.stream()
                .map(Object::toString)
                .collect(Collectors.joining(",\n"));

        String crmNumbers = crmNumbersCreated.stream()
                .map(Object::toString)
                .collect(Collectors.joining(",\n"));

        ResultActions accept = createPostResultActionWithStringBody("/api/updateYoutrackProjectNumbers/" + YOUTRACK_ID, projectIds).andExpect(status().isOk());
        Assert.assertEquals("Received error message", "", accept.andReturn().getResponse().getContentAsString());

        List<Long> projectNumbersFromDB = findAllCaseIdsByYoutrackId(YOUTRACK_ID);
        Assert.assertTrue("DB List must contain only project numbers. projectNumbersCreated = " + projectIdsCreated + " and projectNumbersFromDB = " + projectNumbersFromDB, compareLists(projectIdsCreated, projectNumbersFromDB));

        accept = createPostResultActionWithStringBody("/api/updateYoutrackCrmNumbers/" + YOUTRACK_ID, crmNumbers).andExpect(status().isOk());
        Assert.assertEquals("Received error message", "", accept.andReturn().getResponse().getContentAsString());

        List<Long> crmAndProjectIdsFromDB = findAllCaseIdsByYoutrackId(YOUTRACK_ID);
        List<Long> crmAndProjectNumbersFromDB = findAllCaseNumbersByYoutrackId(YOUTRACK_ID);
        Assert.assertTrue("DB List must contain project ids. projectNumbersCreated = " + projectIdsCreated + " and crmAndProjectIdsFromDB = " + crmAndProjectIdsFromDB, crmAndProjectIdsFromDB.containsAll(projectIdsCreated));
        Assert.assertTrue("DB List must contain crm numbers. crmNumbersCreated = " + crmNumbersCreated + " and crmAndProjectNumbersFromDB = " + crmAndProjectNumbersFromDB, crmAndProjectNumbersFromDB.containsAll(crmNumbersCreated));

        //удаляем один линк с проектом. Должно остаться 2 линка с проектами и 3 линка с crm
        List<Long> projectIdsCreatedAfterRemove = new ArrayList<>(projectIdsCreated);
        projectIdsCreatedAfterRemove.remove(0);
        projectIds = projectIdsCreatedAfterRemove.stream()
                .map(Object::toString)
                .collect(Collectors.joining(",\n"));

        accept = createPostResultActionWithStringBody("/api/updateYoutrackProjectNumbers/" + YOUTRACK_ID, projectIds).andExpect(status().isOk());
        Assert.assertEquals("Received error message", "", accept.andReturn().getResponse().getContentAsString());

        crmAndProjectIdsFromDB = findAllCaseIdsByYoutrackId(YOUTRACK_ID);
        crmAndProjectNumbersFromDB = findAllCaseNumbersByYoutrackId(YOUTRACK_ID);
        Assert.assertTrue("DB List must contain project ids. projectIdsCreatedAfterRemove = " + projectIdsCreatedAfterRemove + " and crmAndProjectIdsFromDB = " + crmAndProjectIdsFromDB, crmAndProjectIdsFromDB.containsAll(projectIdsCreatedAfterRemove));
        Assert.assertTrue("DB List must contain crm numbers. crmNumbersCreated = " + crmNumbersCreated + " and crmAndProjectNumbersFromDB = " + crmAndProjectNumbersFromDB, crmAndProjectNumbersFromDB.containsAll(crmNumbersCreated));
        Assert.assertEquals("DB List must contain 5 numbers. crmAndProjectNumbersFromDB = " + crmAndProjectNumbersFromDB, 5, crmAndProjectIdsFromDB.size());

        //удаляем один линк с crm. Должно остаться 2 линка с проектами и 2 линка с crm
        List<Long> crmNumbersCreatedAfterRemove = new ArrayList<>(crmNumbersCreated);
        crmNumbersCreatedAfterRemove.remove(0);
        crmNumbers = crmNumbersCreatedAfterRemove.stream()
                .map(Object::toString)
                .collect(Collectors.joining(",\n"));

        accept = createPostResultActionWithStringBody("/api/updateYoutrackCrmNumbers/" + YOUTRACK_ID, crmNumbers).andExpect(status().isOk());
        Assert.assertEquals("Received error message", "", accept.andReturn().getResponse().getContentAsString());

        crmAndProjectIdsFromDB = findAllCaseIdsByYoutrackId(YOUTRACK_ID);
        crmAndProjectNumbersFromDB = findAllCaseNumbersByYoutrackId(YOUTRACK_ID);
        Assert.assertTrue("DB List must contain project ids. projectIdsCreatedAfterRemove = " + projectIdsCreatedAfterRemove + " and crmAndProjectIdsFromDB = " + crmAndProjectIdsFromDB, crmAndProjectIdsFromDB.containsAll(projectIdsCreatedAfterRemove));
        Assert.assertTrue("DB List must contain crm numbers. crmNumbersCreatedAfterRemove = " + crmNumbersCreatedAfterRemove + " and crmAndProjectNumbersFromDB = " + crmAndProjectNumbersFromDB, crmAndProjectNumbersFromDB.containsAll(crmNumbersCreatedAfterRemove));
        Assert.assertEquals("DB List must contain 4 numbers. crmAndProjectNumbersFromDB = " + crmAndProjectNumbersFromDB, 4, crmAndProjectIdsFromDB.size());

        removeAllCaseObjectsAndLinksAndComments();
    }

    @Test
    @Transactional
    public void changeYoutrackId() throws Exception {
        final String OLD_YOUTRACK_ID = "CHANGE_TEST-1" + System.currentTimeMillis();
        final String NEW_YOUTRACK_ID = "CHANGE_TEST-2" + System.currentTimeMillis();
        final int CASE_COUNT = 3;

        List<Long> caseNumbersFromDB = findAllCaseNumbersByYoutrackId(OLD_YOUTRACK_ID);
        Assert.assertEquals("Wrong quantity of numbers with old link", 0, caseNumbersFromDB.size());

        List<Long> caseNumbersCreated = fillAndCreateCaseObjects(CASE_COUNT);

        String numbers = caseNumbersCreated.stream()
                .map(Object::toString)
                .collect(Collectors.joining(",\n"));
        createPostResultActionWithStringBody("/api/updateYoutrackCrmNumbers/" + OLD_YOUTRACK_ID, numbers).andExpect(status().isOk());

        caseNumbersFromDB = findAllCaseNumbersByYoutrackId(OLD_YOUTRACK_ID);
        Assert.assertEquals("Wrong quantity of numbers with old link", CASE_COUNT, caseNumbersFromDB.size());

        createPostResultActionWithStringBody("/api/changeyoutrackid/" + OLD_YOUTRACK_ID + "/" + NEW_YOUTRACK_ID, null)
                .andExpect(status().isOk());

        caseNumbersFromDB = findAllCaseNumbersByYoutrackId(OLD_YOUTRACK_ID);
        Assert.assertEquals("Wrong quantity of numbers with old link", 0, caseNumbersFromDB.size());

        caseNumbersFromDB = findAllCaseNumbersByYoutrackId(NEW_YOUTRACK_ID);
        Assert.assertEquals("Wrong quantity of numbers with new link", CASE_COUNT, caseNumbersFromDB.size());

        removeAllCaseObjectsAndLinksAndComments();
    }

    @Test
    @Transactional
    public void changeUnusedYoutrackId() throws Exception {
        final String OLD_YOUTRACK_ID = "UNUSED-1";
        final String NEW_YOUTRACK_ID = "UNUSED-2";
        final int CASE_COUNT = 3;

        List<Long> caseNumbersCreated = fillAndCreateCaseObjects(CASE_COUNT);

        List<Long> caseNumbersFromDB = findAllCaseNumbersByYoutrackId(OLD_YOUTRACK_ID);
        Assert.assertEquals("Wrong quantity of numbers with old link", 0, caseNumbersFromDB.size());
        caseNumbersFromDB = findAllCaseNumbersByYoutrackId(NEW_YOUTRACK_ID);
        Assert.assertEquals("Wrong quantity of numbers with old link", 0, caseNumbersFromDB.size());

        createPostResultActionWithStringBody("/api/changeyoutrackid/" + OLD_YOUTRACK_ID + "/" + NEW_YOUTRACK_ID, null)
                .andExpect(status().isOk());

        caseNumbersFromDB = findAllCaseNumbersByYoutrackId(OLD_YOUTRACK_ID);
        Assert.assertEquals("Wrong quantity of numbers with old link", 0, caseNumbersFromDB.size());
        caseNumbersFromDB = findAllCaseNumbersByYoutrackId(NEW_YOUTRACK_ID);
        Assert.assertEquals("Wrong quantity of numbers with old link", 0, caseNumbersFromDB.size());

        removeAllCaseObjectsAndLinksAndComments();
    }

    @Test
    @Transactional
    public void createProjectCommentFromYoutrack() throws Exception {
        final String YOUTRACK_ID = "COMMENT-TEST";

        List<Long> projectIdsCreated = fillAndCreateProjects(3);

        String projectIds = projectIdsCreated.stream()
                .map(Object::toString)
                .collect(Collectors.joining(",\n"));

        ResultActions accept = createPostResultActionWithStringBody("/api/updateYoutrackProjectNumbers/" + YOUTRACK_ID, projectIds).andExpect(status().isOk());
        Assert.assertEquals("Received error message", "", accept.andReturn().getResponse().getContentAsString());

        YtIssueComment ytIssueComment = createYtIssueComment();
        ytIssueComment.text = " @crm  test text";

        accept = createPostResultActionWithStringBody("/api/saveYoutrackCommentToProjects/" + YOUTRACK_ID, serializeDto(ytIssueComment).getData()).andExpect(status().isOk());
        Assert.assertEquals("Received error message", "", accept.andReturn().getResponse().getContentAsString());

        for (Long projectId : projectIdsCreated) {
            Result<List<CaseComment>> caseCommentList = caseCommentService.getCaseCommentList(getAuthToken(), En_CaseType.PROJECT, projectId);
            Assert.assertTrue("Project must contain new comment", isListContainCommentByRemoteId(caseCommentList.getData(), ytIssueComment.id));
        }

        removeAllCaseObjectsAndLinksAndComments();
    }

    @Test
    @Transactional
    public void updateProjectCommentFromYoutrack() throws Exception {
        final String YOUTRACK_ID = "COMMENT-TEST";

        List<Long> projectIdsCreated = fillAndCreateProjects(3);

        String projectIds = projectIdsCreated.stream()
                .map(Object::toString)
                .collect(Collectors.joining(",\n"));

        ResultActions accept = createPostResultActionWithStringBody("/api/updateYoutrackProjectNumbers/" + YOUTRACK_ID, projectIds).andExpect(status().isOk());
        Assert.assertEquals("Received error message", "", accept.andReturn().getResponse().getContentAsString());

        YtIssueComment ytIssueComment = createYtIssueComment();
        ytIssueComment.text = " @crm  test text";

        accept = createPostResultActionWithStringBody("/api/saveYoutrackCommentToProjects/" + YOUTRACK_ID, serializeDto(ytIssueComment).getData()).andExpect(status().isOk());
        Assert.assertEquals("Received error message", "", accept.andReturn().getResponse().getContentAsString());

        ytIssueComment.text = " @crm  test text 2";
        String updatedTextWithoutTag = "test text 2";

        accept = createPostResultActionWithStringBody("/api/saveYoutrackCommentToProjects/" + YOUTRACK_ID, serializeDto(ytIssueComment).getData()).andExpect(status().isOk());
        Assert.assertEquals("Received error message", "", accept.andReturn().getResponse().getContentAsString());

        for (Long projectId : projectIdsCreated) {
            Result<List<CaseComment>> caseCommentList = caseCommentService.getCaseCommentList(getAuthToken(), En_CaseType.PROJECT, projectId);
            Assert.assertTrue("Project must contain new comment", updatedTextWithoutTag.equals(findCaseCommentByRemoteId(caseCommentList.getData(), ytIssueComment.id).getText()));
        }

        removeAllCaseObjectsAndLinksAndComments();
    }

    @Test
    @Transactional
    public void removeProjectCommentFromYoutrack() throws Exception {
        final String YOUTRACK_ID = "COMMENT-TEST";

        List<Long> projectIdsCreated = fillAndCreateProjects(3);

        String projectIds = projectIdsCreated.stream()
                .map(Object::toString)
                .collect(Collectors.joining(",\n"));

        ResultActions accept = createPostResultActionWithStringBody("/api/updateYoutrackProjectNumbers/" + YOUTRACK_ID, projectIds).andExpect(status().isOk());
        Assert.assertEquals("Received error message", "", accept.andReturn().getResponse().getContentAsString());

        YtIssueComment ytIssueComment = createYtIssueComment();
        ytIssueComment.text = " @crm  test text";

        accept = createPostResultActionWithStringBody("/api/saveYoutrackCommentToProjects/" + YOUTRACK_ID, serializeDto(ytIssueComment).getData()).andExpect(status().isOk());
        Assert.assertEquals("Received error message", "", accept.andReturn().getResponse().getContentAsString());

        accept = createPostResultActionWithStringBody("/api/deleteYoutrackCommentFromProjects/" + YOUTRACK_ID, serializeDto(ytIssueComment).getData()).andExpect(status().isOk());
        Assert.assertEquals("Received error message", "", accept.andReturn().getResponse().getContentAsString());

        for (Long projectId : projectIdsCreated) {
            Result<List<CaseComment>> caseCommentList = caseCommentService.getCaseCommentList(getAuthToken(), En_CaseType.PROJECT, projectId);
            Assert.assertFalse("Project must not contain comment", isListContainCommentByRemoteId(caseCommentList.getData(), ytIssueComment.id));
        }

        removeAllCaseObjectsAndLinksAndComments();
    }

    @Test
    @Transactional
    public void getCaseCommentsListByCaseIdEmptyResult() throws Exception {
        CaseObject caseObject = makeCaseObject( person );
        makeCaseComment( person,caseObject.getId(), "testGetCaseCommentsListByCaseId. text comment. private" );

        CaseCommentApiQuery caseCommentApiQuery = new CaseCommentApiQuery();
        caseCommentApiQuery.setCaseNumber(caseObject.getCaseNumber() + 1);

        ResultActions accept = createPostResultAction("/api/comments", caseCommentApiQuery);

        accept
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(En_ResultStatus.OK.toString())))
                .andExpect(jsonPath("$.data", empty()));
    }

    @Test
    public void getCaseCommentsListByCaseNumberError() throws Exception {
        CaseObject caseObject = makeCaseObject( person );
        makeCaseComment( person,caseObject.getId(), "testGetCaseCommentsListByCaseId. text comment. private" );

        CaseCommentApiQuery caseCommentApiQuery = new CaseCommentApiQuery();
        caseCommentApiQuery.setCaseNumber(null);

        ResultActions accept = createPostResultAction("/api/comments", caseCommentApiQuery);

        accept
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(En_ResultStatus.INCORRECT_PARAMS.toString())));
    }

    @Test
    @Transactional
    public void getTwoEmployees() throws Exception {
        Company homeCompany = companyDAO.get(1L);

        String email = "getEmployees@portal.ru";
        String workPhone = "222";

        Person person = createNewPerson(homeCompany);
        person.setDisplayName("getEmployees1");
        person.setFirstName("getEmployees1");
        person.setIpAddress("111");

        PlainContactInfoFacade infoFacade = new PlainContactInfoFacade();
        infoFacade.setEmail(email);
        infoFacade.setWorkPhone(workPhone);
        infoFacade.setMobilePhone("333");

        person.setContactInfo(infoFacade.editInfo());

        Person person2 = createNewPerson(homeCompany);
        person2.setDisplayName("getEmployees2");
        person2.setFirstName("getEmployees2");
        person2.setIpAddress("888");

        PlainContactInfoFacade infoFacade2 = new PlainContactInfoFacade();
        infoFacade2.setEmail(email);
        infoFacade2.setWorkPhone(workPhone);
        infoFacade2.setMobilePhone("444");

        person2.setContactInfo(infoFacade2.editInfo());

        personDAO.persistBatch(Arrays.asList(person, person2));

        Assert.assertNotNull("Expected person id not null", person.getId());
        Assert.assertNotNull("Expected person2 id not null", person2.getId());

        EmployeeApiQuery employeeApiQuery = new EmployeeApiQuery();
        employeeApiQuery.setWorkPhone(infoFacade.getWorkPhone());
        employeeApiQuery.setEmail(infoFacade.getEmail());

        createPostResultAction("/api/employees", employeeApiQuery)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(En_ResultStatus.OK.toString())))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[*].id", hasItems(person.getId().intValue(), person2.getId().intValue())))
                .andExpect(jsonPath("$.data[*].displayName", hasItems(person.getDisplayName(), person2.getDisplayName())))
        ;

        personDAO.removeByKey(person.getId());
        personDAO.removeByKey(person2.getId());
    }

    @Test
    @Transactional
    public void getEmployeesEmptyResult() throws Exception {
        Company homeCompany = companyDAO.get(1L);

        String email = "getEmployees@portal.ru";
        String workPhone = "222";

        Person person = createNewPerson(homeCompany);
        person.setDisplayName("getEmployees1");
        person.setFirstName("getEmployees1");
        person.setIpAddress("111");

        PlainContactInfoFacade infoFacade = new PlainContactInfoFacade();
        infoFacade.setEmail(email);
        infoFacade.setWorkPhone(workPhone);
        infoFacade.setMobilePhone("333");

        person.setContactInfo(infoFacade.editInfo());

        personDAO.persist(person);

        Assert.assertNotNull("Expected person id not null", person.getId());

        EmployeeApiQuery employeeApiQuery = new EmployeeApiQuery();
        employeeApiQuery.setDisplayName(person.getDisplayName());
        employeeApiQuery.setWorkPhone(infoFacade.getWorkPhone() + "!");
        employeeApiQuery.setEmail(infoFacade.getEmail());

        createPostResultAction("/api/employees", employeeApiQuery)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(En_ResultStatus.OK.toString())))
                .andExpect(jsonPath("$.data", hasSize(0)));

        personDAO.removeByKey(person.getId());
    }


    @Test
    @Transactional
    public void createTag() throws Exception {
        CaseTagInfo caseTagInfo = new CaseTagInfo();

        caseTagInfo.setName("TestPortalApiController :: test tag");
        caseTagInfo.setCompanyId(company.getId());

        ResultActions resultActions = createPostResultAction("/api/tags/create", caseTagInfo)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(En_ResultStatus.OK.toString())))
                .andExpect(jsonPath("$.data", notNullValue()));

        CaseTag result = getData(resultActions, CaseTag.class);

        caseTagDAO.removeByKey(result.getId());
    }

    @Test
    @Transactional
    public void createTagWithId() throws Exception {
        CaseTagInfo caseTagInfo = new CaseTagInfo();

        caseTagInfo.setName("TestPortalApiController :: test tag");
        caseTagInfo.setCompanyId(company.getId());
        caseTagInfo.setId(FAKE_ID);

        createPostResultAction("/api/tags/create", caseTagInfo)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(En_ResultStatus.INCORRECT_PARAMS.toString())));
    }

    @Test
    @Transactional
    public void removeTag() throws Exception {
        CaseTag caseTag = createCaseTag("TestPortalApiController :: test tag", En_CaseType.CRM_SUPPORT, company.getId());
        caseTag.setPersonId(person.getId());
        caseTagDAO.persist(caseTag);

        createPostResultAction("/api/tags/remove/" + caseTag.getId(), null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(En_ResultStatus.OK.toString())))
                .andExpect(jsonPath("$.data", notNullValue()));
    }

    @Test
    @Transactional
    public void removeNotMyTag() throws Exception {
        CaseTag caseTag = createCaseTag("TestPortalApiController :: test tag", En_CaseType.CRM_SUPPORT, company.getId());

        Person person = makePerson(company);
        caseTag.setPersonId(person.getId());

        Long persistedTagId = caseTagDAO.persist(caseTag);

        createPostResultAction("/api/tags/remove/" + caseTag.getId(), null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(En_ResultStatus.PERMISSION_DENIED.toString())));

        caseTagDAO.removeByKey(persistedTagId);
        personDAO.removeByKey(person.getId());
    }

    private boolean compareLists (List<Long> list1, List<Long> list2){
        Collections.sort(list1);
        Collections.sort(list2);
        return list1.equals(list2);
    }

    private List<Long> findAllCaseNumbersByYoutrackId(String youtrackId) {
        CaseLinkQuery caseLinkQuery = new CaseLinkQuery();
        caseLinkQuery.setRemoteId( youtrackId );
        caseLinkQuery.setType( En_CaseLink.YT );
        List<CaseLink> listByQuery = caseLinkDAO.getListByQuery(caseLinkQuery);

        return listByQuery.stream()
                .map(CaseLink::getCaseId)
                .map(caseObjectDAO::getCaseNumberById)
                .collect(Collectors.toList());
    }

    private List<Long> findAllCaseIdsByYoutrackId(String youtrackId) {
        CaseLinkQuery caseLinkQuery = new CaseLinkQuery();
        caseLinkQuery.setRemoteId( youtrackId );
        caseLinkQuery.setType( En_CaseLink.YT );
        List<CaseLink> listByQuery = caseLinkDAO.getListByQuery(caseLinkQuery);

        return listByQuery.stream()
                .map(CaseLink::getCaseId)
                .collect(Collectors.toList());
    }

    private void removeAllCaseObjectsAndLinksAndComments() {
        List<Long> caseIds = caseObjectDAO.getAll().stream().map(CaseObject::getId).collect(Collectors.toList());

        caseIds.forEach(caseId -> {
            CaseLinkQuery query = new CaseLinkQuery(caseId, false);
            caseCommentDAO.getCaseComments(new CaseCommentQuery(caseId))
                    .forEach(caseComment -> caseCommentDAO.remove(caseComment));
            caseLinkDAO.getListByQuery(query)
                    .forEach(caseLink -> caseLinkDAO.remove(caseLink));
            caseObjectDAO.removeByKey(caseId);
        });
    }

    private Long getCaseNumberFromResult (ResultActions resultActions) throws UnsupportedEncodingException {
        String json = resultActions.andReturn().getResponse().getContentAsString();

        int startIndex = json.indexOf("caseNumber")+12;
        int endIndex = json.indexOf(",", startIndex);

        return Long.parseLong(json.substring(startIndex, endIndex));
    }

    private Long getIdFromResult (ResultActions resultActions) throws UnsupportedEncodingException {
        String json = resultActions.andReturn().getResponse().getContentAsString();

        int startIndex = json.indexOf("id")+4;
        int endIndex = json.indexOf(",", startIndex);

        return Long.parseLong(json.substring(startIndex, endIndex));
    }

    private void setThreadUserLogin(UserLogin userLogin) {
        authService.makeThreadAuthToken(userLogin);
    }

    private <T> ResultActions createPostResultAction(String url, T obj) throws Exception {
        MockHttpServletRequestBuilder builder = post( url )
                .header( "Accept", "application/json" )
                .header( "authorization", "Basic " + Base64.getEncoder().encodeToString( (person.getFirstName() + ":fakePassword").getBytes() ) )
                .contentType( MediaType.APPLICATION_JSON );

        if(obj!=null){
            builder.content(objectMapper.writeValueAsString(obj));
        }

        return mockMvc.perform( builder );
    }

    private <T> ResultActions createPostResultActionWithStringBody(String url, String body) throws Exception {
        MockHttpServletRequestBuilder builder = post( url )
                .header( "authorization", "Basic " + Base64.getEncoder().encodeToString( (person.getFirstName() + ":fakePassword").getBytes() ) )
                .characterEncoding("utf-8");

        if(body!=null){
            builder.content(body);
        }

        return mockMvc.perform( builder );
    }

    private <T> T getData(ResultActions resultActions, Class<T> clazz) throws IOException {
        String contentAsString = resultActions.andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(contentAsString.substring(contentAsString.indexOf("\"data\":") + "\"data\":".length(), contentAsString.lastIndexOf("}")), clazz);
    }

    private List<Long> fillAndCreateCaseObjects (int count) throws Exception {
        List<Long> crmNumberList = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            CaseObject caseObject = createNewCaseObject(person);
            caseObject.setInitiatorCompany( company );
            ResultActions accept = createPostResultAction("/api/cases/create", caseObject).andExpect(status().isOk());
            crmNumberList.add(getCaseNumberFromResult(accept));
        }

        return crmNumberList;
    }

    private List<Long> fillAndCreateProjects (int count) throws Exception {
        List<Long> projectNumberList = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            CaseObject caseObject = createNewCaseObject(En_CaseType.PROJECT, generateNextCaseNumber(En_CaseType.CRM_SUPPORT), person);
            caseObject.setInitiatorCompany( company );
            ResultActions accept = createPostResultAction("/api/cases/create", caseObject).andExpect(status().isOk());
            projectNumberList.add(getIdFromResult(accept));
        }

        return projectNumberList;
    }

    private CaseComment convertYtIssueComment(YtIssueComment issueComment) {
        CaseComment caseComment = new CaseComment();
        caseComment.setAuthorId(999L);
        caseComment.setCreated(issueComment.created == null ? null : new Date(issueComment.created));
        caseComment.setUpdated(issueComment.updated == null ? null : new Date(issueComment.updated));
        caseComment.setRemoteId(issueComment.id);
        caseComment.setOriginalAuthorName(issueComment.author != null ? issueComment.author.fullName : null);
        caseComment.setOriginalAuthorFullName(issueComment.author != null ? issueComment.author.fullName : null);
        caseComment.setText(issueComment.text);
        caseComment.setDeleted(issueComment.deleted);
        return caseComment;
    }

    private boolean isListContainCommentByRemoteId(List<CaseComment> list, String remoteId) {
        for (CaseComment caseComment : list) {
            if (Objects.equals(caseComment.getRemoteId(), remoteId)){
                return true;
            }
        }
        return false;
    }

    private CaseComment findCaseCommentByRemoteId(List<CaseComment> list, String remoteId) {
        for (CaseComment caseComment : list) {
            if (Objects.equals(caseComment.getRemoteId(), remoteId)){
                return caseComment;
            }
        }
        return null;
    }

    private <T> Result<String> serializeDto(T dto, YtFieldDescriptor...forceIncludeFields) {
        try {
            List<YtFieldDescriptor> includeFields = forceIncludeFields == null
                    ? Collections.emptyList()
                    : Arrays.asList(forceIncludeFields);
            String body = YtDtoObjectMapperProvider.getMapper(fieldsMapper)
                    .writer(YtDtoObjectMapperProvider.getFilterProvider(includeFields))
                    .writeValueAsString(dto);
            return ok(body);
        } catch (JsonProcessingException e) {
            return error(En_ResultStatus.INTERNAL_ERROR);
        }
    }

    private YtUser createYtUser(){
        YtUser ytUser = new YtUser();
        ytUser.fullName = "test fullname";
        ytUser.id = "1";
        return ytUser;
    }

    private YtIssueComment createYtIssueComment() {
        YtIssueComment ytIssueComment = new YtIssueComment();
        ytIssueComment.author = createYtUser();
        ytIssueComment.id = "2";
        ytIssueComment.deleted = false;
        return ytIssueComment;
    }
}
