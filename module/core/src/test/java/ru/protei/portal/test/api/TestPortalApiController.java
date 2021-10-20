package ru.protei.portal.test.api;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.config.IntegrationTestsConfiguration;
import ru.protei.portal.core.client.youtrack.mapper.YtDtoFieldsMapper;
import ru.protei.portal.core.client.youtrack.mapper.YtDtoObjectMapperProvider;
import ru.protei.portal.core.controller.api.PortalApiController;
import ru.protei.portal.core.model.api.ApiAbsence;
import ru.protei.portal.core.model.api.ApiProject;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.api.ApiDocument;
import ru.protei.portal.core.model.dto.CaseTagInfo;
import ru.protei.portal.core.model.dto.DevUnitInfo;
import ru.protei.portal.core.model.dto.Project;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.query.*;
import ru.protei.portal.core.model.struct.ContactInfo;
import ru.protei.portal.core.model.struct.ContactItem;
import ru.protei.portal.core.model.struct.PlainContactInfoFacade;
import ru.protei.portal.core.model.view.PersonProjectMemberView;
import ru.protei.portal.core.model.youtrack.YtFieldDescriptor;
import ru.protei.portal.core.model.youtrack.dto.issue.YtIssueComment;
import ru.protei.portal.core.model.youtrack.dto.user.YtUser;
import ru.protei.portal.core.service.CaseCommentService;
import ru.protei.portal.core.service.auth.AuthService;
import ru.protei.portal.embeddeddb.DatabaseConfiguration;
import ru.protei.portal.embeddeddb.EmbeddedDB;
import ru.protei.portal.mock.AuthServiceMock;
import ru.protei.portal.test.service.BaseServiceTest;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;
import static ru.protei.portal.core.model.helper.CollectionUtils.emptyIfNull;
import static ru.protei.portal.core.model.helper.CollectionUtils.stream;
import static ru.protei.portal.core.model.util.CrmConstants.State.CREATED;
import static ru.protei.portal.core.model.util.CrmConstants.State.UNKNOWN;


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
    EmbeddedDB embeddedDB;


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
    public void postConstruct() {
        mockMvc = MockMvcBuilders.standaloneSetup(portalApiController).build();

        company = makeCustomerCompany();

//        В бд уже должен быть person с id из тестовых пропертей
        Long youtrackUserId = config.data().youtrack().getYoutrackUserId();
        person = personDAO.get(youtrackUserId);

        if (person == null) {
            person = makePerson(company, youtrackUserId);
        } else {
            company = person.getCompany();
        }

        List<UserLogin> userLogins = userLoginDAO.findByPersonId(person.getId());

        userLogin = emptyIfNull(userLogins).stream().findAny().orElse(null);

        if (userLogin == null) {
            userLogin = makeUserLogin(person);
        }
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

        CaseObject caseObject = createNewCaseObject(person);
        caseObject.setName("createIssueWithAutoOpenIssueWithCorrectProduct");
        caseObject.setInitiatorCompanyId(company.getId());
        caseObject.setType(En_CaseType.PROJECT);
        caseObject.setProductId(devUnit.getId());

        Long projectId = caseObjectDAO.persist(caseObject);

        Project project = new Project();
        project.setId(projectId);

        projectDAO.persist(project);

        Platform platform = new Platform();
        platform.setName("createIssueWithAutoOpenIssueWithCorrectProduct");
        platform.setCompanyId(company.getId());
        platform.setProjectId(projectId);

        ProjectToProduct projectToProduct = new ProjectToProduct(projectId, devUnit.getId());
        projectToProductDAO.persist(projectToProduct);

        Long platformId = platformDAO.persist(platform);

        CaseObject caseObjectFromDb = createNewCaseObject(person);
        caseObjectFromDb.setName("createIssueWithAutoOpenIssueWithoutProduct");
        caseObjectFromDb.setInitiator(person);
        caseObjectFromDb.setInitiatorCompany(company);
        caseObjectFromDb.setPlatformId(platformId);
        caseObjectFromDb.setProductId(devUnit.getId());

        authService.makeThreadAuthToken( userLogin );
        ResultActions actions = createPostResultAction("/api/cases/create", caseObjectFromDb);
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
                .andExpect( jsonPath( "$.data.internalDocLink", is( product.getInternalDocLink() ) ) )
                .andExpect( jsonPath( "$.data.externalDocLink", is( product.getExternalDocLink() ) ) )
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
                .andExpect(jsonPath("$.data.internalDocLink", is(product.getInternalDocLink())))
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
    @Transactional
    public void updateProductState() throws Exception {
        DevUnit product = createProduct("TestPortalApiController#updateProductState");
        Long productId = devUnitDAO.persist(product);

        Assert.assertEquals(updateProductState(productId, 1), 1);
        Assert.assertEquals(updateProductState(productId, 2), 2);

        updateProductWithIncorrectParam("/incorrect_id/1");
        updateProductWithIncorrectParam(productId + "/incorrect_state");
    }

    private int updateProductState(Long productId, int state) throws Exception {
        createPostResultAction("/api/products/updateState/" + productId + "/" + state, null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(En_ResultStatus.OK.toString())));

        return devUnitDAO.get(productId).getState().getId();
    }

    private void updateProductWithIncorrectParam(String params) throws Exception {
        createPostResultAction("/api/products/updateState/" + params, null)
                .andExpect(status().isBadRequest());
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
    public void getCaseListByProductId() throws Exception {
        final DevUnit devUnit1 = makeProduct();
        final DevUnit devUnit2 = makeProduct();
        final Company company = makeCompany(En_CompanyCategory.CUSTOMER);
        makeCaseObject(makePerson(company), devUnit1.getId(), new Date(), company.getId());
        makeCaseObject(makePerson(company), devUnit2.getId(), new Date(), company.getId());

        CaseApiQuery caseApiQuery = new CaseApiQuery();
        caseApiQuery.setProductIds(Collections.singleton(devUnit1.getId()));

        ResultActions accept = createPostResultAction("/api/cases", caseApiQuery);

        accept
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(En_ResultStatus.OK.toString())))
                .andExpect(jsonPath("$.data[*]", hasSize(is(1))))
                .andExpect(jsonPath("$.data[*].productId", everyItem(is(devUnit1.getId().intValue()))));
    }

    @Test
    @Transactional
    public void getCaseListByProductIdEmptyResult() throws Exception {
        final DevUnit devUnit1 = makeProduct();
        final DevUnit devUnit2 = makeProduct();
        final Company company = makeCompany(En_CompanyCategory.CUSTOMER);
        makeCaseObject(makePerson(company), devUnit1.getId(), new Date(), company.getId());
        makeCaseObject(makePerson(company), devUnit2.getId(), new Date(), company.getId());

        CaseApiQuery caseApiQuery = new CaseApiQuery();
        caseApiQuery.setProductIds(Collections.singleton(devUnitDAO.getMaxId()  + 1));

        ResultActions accept = createPostResultAction("/api/cases", caseApiQuery);

        accept
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(En_ResultStatus.OK.toString())))
                .andExpect(jsonPath("$.data", empty()));
    }

    @Test
    @Transactional
    public void getCaseListByTag() throws Exception {
        final Company company = makeCompany(En_CompanyCategory.CUSTOMER);
        final CaseObject caseObject1 = makeCaseObject(En_CaseType.CRM_SUPPORT, makePerson(company));
        final CaseObject caseObject2 = makeCaseObject(En_CaseType.CRM_SUPPORT, makePerson(company));
        final String testTagName = "test_tag";
        final CaseTag caseTag = makeCaseTag(testTagName, En_CaseType.CRM_SUPPORT, company.getId());
        caseObjectTagDAO.persist(new CaseObjectTag(caseObject1.getId(), caseTag.getId()));

        CaseApiQuery caseApiQuery = new CaseApiQuery();
        caseApiQuery.setCaseTagsNames(Collections.singletonList(testTagName));

        ResultActions accept = createPostResultAction("/api/cases", caseApiQuery);

        accept
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(En_ResultStatus.OK.toString())))
                .andExpect(jsonPath("$.data[*]", hasSize(is(1))))
                .andExpect(jsonPath("$.data[*].id", everyItem(is(caseObject1.getId().intValue()))));
    }

    @Test
    @Transactional
    public void getCaseListByTagEmptyResult() throws Exception {
        final Company company = makeCompany(En_CompanyCategory.CUSTOMER);
        final CaseObject caseObject = makeCaseObject(En_CaseType.CRM_SUPPORT, makePerson(company));
        final String testTagName = "test_tag";
        final CaseTag caseTag = makeCaseTag(testTagName, En_CaseType.CRM_SUPPORT, company.getId());
        caseObjectTagDAO.persist(new CaseObjectTag(caseObject.getId(), caseTag.getId()));

        CaseApiQuery caseApiQuery = new CaseApiQuery();
        caseApiQuery.setCaseTagsNames(Collections.singletonList("no_" + testTagName));

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
    @Transactional
    public void getCaseHistoryByCaseNumber() throws Exception {
        CaseObject caseObject = makeCaseObject( person );
        createNewStateHistory( person, caseObject.getId(), CREATED, new Date());

        HistoryQuery query = new HistoryQuery();
        query.setCaseNumber(caseObject.getCaseNumber());
        int caseHistoryByCaseNumberCount = historyDAO.listByQuery(query).size();

        ResultActions accept = createPostResultAction("/api/case/histories", query);

        accept
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(En_ResultStatus.OK.toString())))
                .andExpect(jsonPath("$.data", hasSize(caseHistoryByCaseNumberCount)));
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

        removeAllCaseObjectsAndLinksAndCommentsAndHistory();
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

        removeAllCaseObjectsAndLinksAndCommentsAndHistory();
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

        removeAllCaseObjectsAndLinksAndCommentsAndHistory();
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

        removeAllCaseObjectsAndLinksAndCommentsAndHistory();
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

        removeAllCaseObjectsAndLinksAndCommentsAndHistory();
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

        removeAllCaseObjectsAndLinksAndCommentsAndHistory();
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

        removeAllCaseObjectsAndLinksAndCommentsAndHistory();
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

        removeAllCaseObjectsAndLinksAndCommentsAndHistory();
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

        removeAllCaseObjectsAndLinksAndCommentsAndHistory();
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

        removeAllCaseObjectsAndLinksAndCommentsAndHistory();
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

        removeAllCaseObjectsAndLinksAndCommentsAndHistory();
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

        removeAllCaseObjectsAndLinksAndCommentsAndHistory();
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

        removeAllCaseObjectsAndLinksAndCommentsAndHistory();
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

        removeAllCaseObjectsAndLinksAndCommentsAndHistory();
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
        contactItemDAO.persistBatch(person.getContactItems());
        contactItemDAO.persistBatch(person2.getContactItems());
        jdbcManyRelationsHelper.persist(person, Person.Fields.CONTACT_ITEMS);
        jdbcManyRelationsHelper.persist(person2, Person.Fields.CONTACT_ITEMS);

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

        contactItemDAO.removeByKeys(stream(person.getContactItems()).map(ContactItem::id).collect(Collectors.toList()));
        contactItemDAO.removeByKeys(stream(person2.getContactItems()).map(ContactItem::id).collect(Collectors.toList()));
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

    @Test
    @Transactional
    public void createCompany() throws Exception {
        Company company = new Company();
        company.setCname("Gazprom");
        company.setCategoryId(1);

        company.setGroupId(null);

        company.setParentCompanyId(1L);
        company.setParentCompanyName("НТЦ Протей");

        List<ContactItem> contactInfo = new ArrayList<>();
        contactInfo.add(new ContactItem("data", En_ContactItemType.EMAIL));
        contactInfo.add(new ContactItem("data", En_ContactItemType.ADDRESS));
        contactInfo.add(new ContactItem("data", En_ContactItemType.ADDRESS_LEGAL));
        contactInfo.add(new ContactItem("data", En_ContactItemType.FAX));
        contactInfo.add(new ContactItem("data", En_ContactItemType.MOBILE_PHONE));
        contactInfo.add(new ContactItem("data", En_ContactItemType.GENERAL_PHONE));
        contactInfo.add(new ContactItem("data", En_ContactItemType.WEB_SITE));
        company.setContactInfo(new ContactInfo(contactInfo));

        company.setInfo("Company information");
        company.setHidden(true);

        company.setArchived(false);
        company.setAutoOpenIssue(true);

        createPostResultAction("/api/companies/create", company)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(En_ResultStatus.OK.toString())))
                .andExpect(jsonPath("$.data.cname", is(company.getCname())))
                .andExpect(jsonPath("$.data.categoryId", is(company.getCategoryId())))
                .andExpect(jsonPath("$.data.groupId", is(company.getGroupId())))

                .andExpect(jsonPath("$.data.parentCompanyId", is(company.getParentCompanyId().intValue())))
                .andExpect(jsonPath("$.data.parentCompanyName", is(company.getParentCompanyName())))

                .andExpect(jsonPath("$.data.contactItems[0].v", is(company.getContactInfo().getItems().get(0).value())))
                .andExpect(jsonPath("$.data.contactItems[1].v", is(company.getContactInfo().getItems().get(1).value())))
                .andExpect(jsonPath("$.data.contactItems[2].v", is(company.getContactInfo().getItems().get(2).value())))
                .andExpect(jsonPath("$.data.contactItems[3].v", is(company.getContactInfo().getItems().get(3).value())))
                .andExpect(jsonPath("$.data.contactItems[4].v", is(company.getContactInfo().getItems().get(4).value())))
                .andExpect(jsonPath("$.data.contactItems[5].v", is(company.getContactInfo().getItems().get(5).value())))
                .andExpect(jsonPath("$.data.contactItems[6].v", is(company.getContactInfo().getItems().get(6).value())))

                .andExpect(jsonPath("$.data.info", is(company.getInfo())))
                .andExpect(jsonPath("$.data.hidden", is(company.getHidden())))
                .andExpect(jsonPath("$.data.archived", is(company.isArchived())))
                .andExpect(jsonPath("$.data.autoOpenIssue", is(company.getAutoOpenIssue())));
    }

    @Test
    @Transactional
    public void updateCompanyState() throws Exception {
        Company company = new Company();
        Long companyId = 12345L;
        company.setCname("Protei");
        company.setId(companyId);

        createPostResultAction("/api/companies/create", company)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(En_ResultStatus.OK.toString())))
                .andExpect(jsonPath("$.data.id", is(company.getId().intValue())));

        Assert.assertTrue(updateCompanyState(companyId, true));
        Assert.assertFalse(updateCompanyState(companyId, false));

        updateCompanyWithIncorrectParam("/incorrect_id/false");
        updateCompanyWithIncorrectParam(companyId + "/incorrect_state");
    }

    private boolean updateCompanyState(Long companyId, boolean isArchived) throws Exception {
        createPostResultAction("/api/companies/updateState/" + companyId + "/" + isArchived, null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(En_ResultStatus.OK.toString())));

        return companyDAO.get(companyId).isArchived();
    }

    private void updateCompanyWithIncorrectParam(String params) throws Exception {
        createPostResultAction("/api/companies/updateState/" + params, null)
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    public void createPlatform() throws Exception {
        Platform platform = new Platform();
        platform.setName("name");
        platform.setParams("params");
        platform.setManagerId(1L);
        platform.setCompanyId(1L);
        platform.setComment("comment");

        createPostResultAction("/api/platforms/create", platform)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(En_ResultStatus.OK.toString())))
                .andExpect(jsonPath("$.data.name", is(platform.getName())))
                .andExpect(jsonPath("$.data.params", is(platform.getParams())))
                .andExpect(jsonPath("$.data.manager.id", is(platform.getManagerId().intValue())))
                .andExpect(jsonPath("$.data.companyId", is(platform.getCompanyId().intValue())))
                .andExpect(jsonPath("$.data.comment", is(platform.getComment())));
    }

    @Test
    @Transactional
    public void deletePlatform() throws Exception {
        Platform platform = new Platform();
        platform.setName("Platform to delete");

        ResultActions result = createPostResultAction("/api/platforms/create", platform)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(En_ResultStatus.OK.toString())))
                .andExpect(jsonPath("$.data.id", notNullValue()));

        String platformId = getPlatformId(result.andReturn().getResponse().getContentAsString());

        createPostResultAction("/api/platforms/delete/" + platformId, null)
                .andExpect(status().isOk());

        createPostResultAction("/api/platforms/delete/12345", null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(En_ResultStatus.NOT_FOUND.toString())));

        createPostResultAction("/api/platforms/delete/incorrect_id", null)
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    public void createAbsenceByPersonId() throws Exception {
        ApiAbsence apiAbsence = new ApiAbsence();
        apiAbsence.setPersonId(person.getId());
        apiAbsence.setReason(En_AbsenceReason.REMOTE_WORK);

        Date now = new Date();
        Date tomorrow = new Date();
        tomorrow.setDate(now.getDate() + 1);
        apiAbsence.setFromTime(now);
        apiAbsence.setTillTime(tomorrow);

        createPostResultAction("/api/absence/1c/create", apiAbsence)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(En_ResultStatus.OK.toString())));
    }

    @Test
    @Transactional
    public void createAbsenceByWorkerId() throws Exception {
        CompanyHomeGroupItem companyGroupHome = new CompanyHomeGroupItem();
        companyGroupHome.setCompanyId(company.getId());
        companyGroupHome.setExternalCode(company.getCname());
        companyGroupHomeDAO.persist(companyGroupHome);

        CompanyDepartment companyDepartment = new CompanyDepartment();
        companyDepartment.setCompanyId(company.getId());
        companyDepartment.setCreated(new Date());
        companyDepartment.setName("Dep");
        companyDepartmentDAO.persist(companyDepartment);

        WorkerPosition workerPosition = new WorkerPosition();
        workerPosition.setCompanyId(company.getId());
        workerPosition.setName("QA");
        workerPositionDAO.persist(workerPosition);

        WorkerEntry workerEntry = new WorkerEntry();
        workerEntry.setCreated(new Date());
        workerEntry.setPersonId(person.getId());
        workerEntry.setExternalId("0000000001");
        workerEntry.setCompanyId(company.getId());
        workerEntry.setDepartmentId(companyDepartment.getId());
        workerEntry.setPositionId(workerPosition.getId());
        workerEntryDAO.persist(workerEntry);

        ApiAbsence apiInfo = new ApiAbsence();
        apiInfo.setWorkerExtId("0000000001");
        apiInfo.setCompanyCode(company.getCname());
        apiInfo.setReason(En_AbsenceReason.REMOTE_WORK);

        Date now = new Date();
        Date tomorrow = new Date();
        tomorrow.setDate(now.getDate() + 1);
        apiInfo.setFromTime(now);
        apiInfo.setTillTime(tomorrow);

        createPostResultAction("/api/absence/1c/create", apiInfo)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(En_ResultStatus.OK.toString())));
    }

    @Test
    @Transactional
    public void createAbsenceByPersonIdAndPeriodIsNull() throws Exception {
        ApiAbsence apiInfo = new ApiAbsence();
        apiInfo.setPersonId(person.getId());
        apiInfo.setReason(En_AbsenceReason.REMOTE_WORK);

        createPostResultAction("/api/absence/1c/create", apiInfo)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(En_ResultStatus.INCORRECT_PARAMS.toString())));
    }

    @Test
    @Transactional
    public void createAbsenceByPersonIdAndIncorrectPeriod() throws Exception {
        ApiAbsence apiInfo = new ApiAbsence();
        apiInfo.setPersonId(person.getId());
        apiInfo.setReason(En_AbsenceReason.REMOTE_WORK);

        Date now = new Date();
        Date tomorrow = new Date();
        tomorrow.setDate(now.getDate() + 1);

        apiInfo.setFromTime(tomorrow);
        apiInfo.setTillTime(now);

        createPostResultAction("/api/absence/1c/create", apiInfo)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(En_ResultStatus.INCORRECT_PARAMS.toString())));
    }

    @Test
    @Transactional
    public void createAbsenceByPersonIdAndIntersectPeriod() throws Exception {
        ApiAbsence apiInfo = createApiAbsence();

        createPostResultAction("/api/absence/1c/create", apiInfo)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(En_ResultStatus.OK.toString())));

        createPostResultAction("/api/absence/1c/create", apiInfo)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(En_ResultStatus.ABSENCE_HAS_INTERSECTIONS.toString())));
    }

    @Test
    @Transactional
    public void createAbsenceAndCheckWhetherItCreatedFrom1C() throws Exception {
        ApiAbsence apiInfo = createApiAbsence();

        ResultActions resultActions = createPostResultAction("/api/absence/1c/create", apiInfo)
                .andExpect(status().isOk());

        Long absenceId = getData(resultActions, Long.class);
        Assert.assertTrue(absenceId != null && personAbsenceDAO.get(absenceId).isCreatedFrom1C());
    }

    @Test
    @Transactional
    public void createDoc() throws Exception {
        DocumentType documentType = new DocumentType();
        documentType.setName("documentType");
        documentType.setDocumentCategory(En_DocumentCategory.TP);
        Long documentTypeId = documentTypeDAO.persist(documentType);

        CaseObject caseObject = createNewCaseObject(person);
        caseObject.setName("createDoc");
        caseObject.setInitiatorCompanyId(company.getId());
        caseObject.setType(En_CaseType.PROJECT);
        Long projectId = caseObjectDAO.persist(caseObject);
        Project project = new Project();
        project.setId(projectId);
        projectDAO.persist(project);

        ApiDocument apiDocument = new ApiDocument();
        apiDocument.setName("createDoc() : Test Doc Name");
        apiDocument.setProjectId(projectId);
        apiDocument.setTypeId(documentTypeId);
        apiDocument.setRegistrarId(person.getId());
        apiDocument.setContractorId(person.getId());
        apiDocument.setExecutionType(En_DocumentExecutionType.ELECTRONIC);
        List<Long> members = CollectionUtils.listOf(person.getId());
        apiDocument.setMemberIds(members);

        createPostResultAction("/api/doc/create", apiDocument)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(En_ResultStatus.OK.toString())));
    }

    @Test
    @Transactional
    public void createDocNoProject() throws Exception {
        DocumentType documentType = new DocumentType();
        documentType.setName("documentType");
        documentType.setDocumentCategory(En_DocumentCategory.TP);
        Long documentTypeId = documentTypeDAO.persist(documentType);

        ApiDocument apiDocument = new ApiDocument();
        apiDocument.setName("createDoc() : Test Doc Name");

        apiDocument.setProjectId(null);

        apiDocument.setTypeId(documentTypeId);
        apiDocument.setRegistrarId(person.getId());
        apiDocument.setContractorId(person.getId());
        apiDocument.setExecutionType(En_DocumentExecutionType.ELECTRONIC);
        List<Long> members = CollectionUtils.listOf(person.getId());
        apiDocument.setMemberIds(members);

        createPostResultAction("/api/doc/create", apiDocument)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(En_ResultStatus.VALIDATION_ERROR.toString())));
    }


    @Test
    @Transactional
    public void createDocApprovedNoFile() throws Exception {
        DocumentType documentType = new DocumentType();
        documentType.setName("documentType");
        documentType.setDocumentCategory(En_DocumentCategory.TP);
        Long documentTypeId = documentTypeDAO.persist(documentType);

        CaseObject caseObject = createNewCaseObject(person);
        caseObject.setName("createDoc");
        caseObject.setInitiatorCompanyId(company.getId());
        caseObject.setType(En_CaseType.PROJECT);
        Long projectId = caseObjectDAO.persist(caseObject);
        Project project = new Project();
        project.setId(projectId);
        projectDAO.persist(project);

        ApiDocument apiDocument = new ApiDocument();
        apiDocument.setName("createDoc() : Test Doc Name");
        apiDocument.setProjectId(null);
        apiDocument.setTypeId(documentTypeId);
        apiDocument.setRegistrarId(person.getId());
        apiDocument.setContractorId(person.getId());
        apiDocument.setExecutionType(En_DocumentExecutionType.ELECTRONIC);
        List<Long> members = CollectionUtils.listOf(person.getId());
        apiDocument.setMemberIds(members);

        apiDocument.setApproved(true);
        apiDocument.setApprovedById(person.getId());
        apiDocument.setApprovalDate(new Date());
        apiDocument.setDecimalNumber("40412735.АПКБГОО.14417.18");
        apiDocument.setArchivePdfFileBase64(null);

        createPostResultAction("/api/doc/create", apiDocument)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(En_ResultStatus.VALIDATION_ERROR.toString())));
    }

    @Test
    @Transactional
    public void removeDoc() throws Exception {
        DocumentType documentType = new DocumentType();
        documentType.setName("documentType");
        documentType.setDocumentCategory(En_DocumentCategory.TP);
        documentTypeDAO.persist(documentType);

        CaseObject caseObject = createNewCaseObject(person);
        caseObject.setName("createDoc");
        caseObject.setInitiatorCompanyId(company.getId());
        caseObject.setType(En_CaseType.PROJECT);
        Long projectId = caseObjectDAO.persist(caseObject);
        Project project = new Project();
        project.setId(projectId);
        projectDAO.persist(project);

        Document document = new Document();
        document.setState(En_DocumentState.ACTIVE);
        document.setType(documentType);
        document.setProjectId(project.getId());
        documentDAO.persist(document);

        createPostResultAction("/api/doc/remove/" + document.getId(), null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(En_ResultStatus.OK.toString())));
    }

    @Test
    @Transactional
    public void removeDocInvalid() throws Exception {
        DocumentType documentType = new DocumentType();
        documentType.setName("documentType");
        documentType.setDocumentCategory(En_DocumentCategory.TP);
        documentTypeDAO.persist(documentType);

        CaseObject caseObject = createNewCaseObject(person);
        caseObject.setName("createDoc");
        caseObject.setInitiatorCompanyId(company.getId());
        caseObject.setType(En_CaseType.PROJECT);
        Long projectId = caseObjectDAO.persist(caseObject);
        Project project = new Project();
        project.setId(projectId);
        projectDAO.persist(project);

        Document document = new Document();
        document.setState(En_DocumentState.ACTIVE);
        document.setType(documentType);
        document.setProjectId(project.getId());
        documentDAO.persist(document);

        createPostResultAction("/api/doc/remove/" + document.getId() + 1, null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(En_ResultStatus.NOT_FOUND.toString())));
    }

    @Test
    @Transactional
    public void deleteProject() throws Exception {
        Project project = createNewProject();
        CaseObject caseObject = createCaseObjectFromProject(project);

        Long caseObjectId = caseObjectDAO.persist(caseObject);
        Assert.assertNotNull(caseObjectId);

        project.setId(caseObjectId);
        Long createdProjectId = projectDAO.persist(project);

        createPostResultAction("/api/projects/delete/" + createdProjectId, null)
                .andExpect(status().isOk());
    }

    @Test
    @Transactional
    public void deleteNotExistingProject() throws Exception {
        createPostResultAction("/api/projects/delete/12345", null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(En_ResultStatus.NOT_FOUND.toString())));
    }

    @Test
    @Transactional
    public void deleteProjectByIncorrectId() throws Exception {
        createPostResultAction("/api/projects/delete/incorrect_id", null)
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    public void createProject() throws Exception {
        ApiProject apiProject = createApiProject();
        createPostResultAction("/api/projects/create", apiProject)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(En_ResultStatus.OK.toString())))
                .andExpect(jsonPath("$.data.creatorId", is(apiProject.getCreatorId().intValue())))
                .andExpect(jsonPath("$.data.name", is(apiProject.getName())))
                .andExpect(jsonPath("$.data.description", is(apiProject.getDescription())))
                .andExpect(jsonPath("$.data.projectSlas[0].importanceCode", is(apiProject.getSlas().get(0).getImportanceCode())))
                .andExpect(jsonPath("$.data.projectSlas[0].reactionTime", is(apiProject.getSlas().get(0).getReactionTime().intValue())))
                .andExpect(jsonPath("$.data.projectSlas[0].temporarySolutionTime", is(apiProject.getSlas().get(0).getTemporarySolutionTime().intValue())))
                .andExpect(jsonPath("$.data.projectSlas[0].fullSolutionTime", is(apiProject.getSlas().get(0).getFullSolutionTime().intValue())))
                .andExpect(jsonPath("$.data.team[0].role", is(apiProject.getTeam().get(0).getRole().toString())))
                .andExpect(jsonPath("$.data.stateId", is(apiProject.getStateId().intValue())))
                .andExpect(jsonPath("$.data.pauseDate", is(apiProject.getPauseDate())))
                .andExpect(jsonPath("$.data.customer.id", is(apiProject.getCompanyId().intValue())))
                .andExpect(jsonPath("$.data.customerType", is(requireNonNull(En_CustomerType.find(apiProject.getCustomerTypeId())).toString())))
                .andExpect(jsonPath("$.data.technicalSupportValidity", is(apiProject.getTechnicalSupportValidity().getTime())))
                .andExpect(jsonPath("$.data.workCompletionDate", is(apiProject.getWorkCompletionDate().getTime())))
                .andExpect(jsonPath("$.data.purchaseDate", is(apiProject.getPurchaseDate().getTime())))
                .andExpect(jsonPath("$.data.productDirections[0].id", is(apiProject.getDirectionsIds().iterator().next().intValue())))
                .andExpect(jsonPath("$.data.products[0].id", is(apiProject.getProductsIds().iterator().next().intValue())))
                .andExpect(jsonPath("$.data.subcontractors[0].id", is(apiProject.getSubcontractorsIds().get(0).intValue())))
                .andExpect(jsonPath("$.data.projectPlans[0].id", is(apiProject.getPlansIds().get(0).intValue())));
    }

    @Test
    @Transactional
    public void createProjectWithoutRequiredFields() throws Exception {
        createPostResultAction("/api/projects/create", new ApiProject())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(En_ResultStatus.INCORRECT_PARAMS.toString())));
    }

    private String getPlatformId (String strResult) {
        int idIndex = strResult.indexOf("id");
        strResult = strResult.substring(idIndex);
        return strResult.substring(strResult.indexOf(":") + 1, strResult.indexOf(","));
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

    private void removeAllCaseObjectsAndLinksAndCommentsAndHistory() {
        List<Long> caseIds = caseObjectDAO.getAll().stream().map(CaseObject::getId).collect(Collectors.toList());

        caseIds.forEach(caseId -> {
            CaseLinkQuery query = new CaseLinkQuery(caseId, false);
            caseCommentDAO.getCaseComments(new CaseCommentQuery(caseId))
                    .forEach(caseComment -> caseCommentDAO.remove(caseComment));
            caseLinkDAO.getListByQuery(query)
                    .forEach(caseLink -> caseLinkDAO.remove(caseLink));
            historyDAO.removeByCaseId(caseId);
            projectToProductDAO.removeAllProductsFromProject(caseId);
            platformDAO.removeByProjectId(caseId);
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

    private ApiAbsence createApiAbsence() {
        ApiAbsence apiInfo = new ApiAbsence();
        apiInfo.setPersonId(person.getId());
        apiInfo.setReason(En_AbsenceReason.REMOTE_WORK);

        Date now = new Date();
        Date tomorrow = new Date(now.getTime() + 1000000);

        apiInfo.setFromTime(now);
        apiInfo.setTillTime(tomorrow);
        return apiInfo;
    }

    private Project createNewProject() {
        Project project = new Project();
        project.setStateId(UNKNOWN);
        project.setName("Test API project");
        return project;
    }

    private CaseObject createCaseObjectFromProject(Project project) {
        CaseObject caseObject = new CaseObject();
        caseObject.setCaseNumber(caseTypeDAO.generateNextId(En_CaseType.PROJECT));
        caseObject.setType(En_CaseType.PROJECT);
        caseObject.setCreated(project.getCreated() == null ? new Date() : project.getCreated());
        caseObject.setCreatorId(project.getCreatorId());

        caseObject.setId(project.getId());
        caseObject.setStateId(project.getStateId());

        caseObject.setName(project.getName());
        caseObject.setInfo(project.getDescription());
        caseObject.setManagerId(project.getLeader() == null ? null : project.getLeader().getId());
        caseObject.setPauseDate(project.getPauseDate());

        if (project.getCustomer() == null) {
            caseObject.setInitiatorCompany(null);
        } else {
            caseObject.setInitiatorCompanyId(project.getCustomer().getId());
        }

        return caseObject;
    }

    private ApiProject createApiProject() {
        ApiProject apiProject = new ApiProject();

        apiProject.setCreatorId(1L);
        apiProject.setName("Test API project");
        apiProject.setDescription("Test API description");

        List<ProjectSla> slas = new ArrayList<>();
        slas.add(new ProjectSla(1, 60L, 120L, 180L));
        apiProject.setSlas(slas);

        List<PersonProjectMemberView> team = new ArrayList<>();
        PersonProjectMemberView headManager = new PersonProjectMemberView();
        headManager.setRole(En_PersonRoleType.HEAD_MANAGER);
        team.add(headManager);
        apiProject.setTeam(team);

        apiProject.setStateId(4L);
        apiProject.setPauseDate(new Date().getTime());
        apiProject.setCompanyId(1L);
        apiProject.setCustomerTypeId(1);

        apiProject.setTechnicalSupportValidity(new Date());
        apiProject.setWorkCompletionDate(new Date());
        apiProject.setPurchaseDate(new Date());

        DevUnit devUnit = new DevUnit(En_DevUnitType.PRODUCT, "Test product", "Info");
        DevUnit devUnit_2 = new DevUnit(En_DevUnitType.PRODUCT, "Test product 2", "Info");
        devUnitDAO.saveOrUpdate(devUnit);
        devUnitDAO.saveOrUpdate(devUnit_2);

        Set<Long> directionIds = new HashSet<>();
        directionIds.add(devUnit.getId());
        apiProject.setDirectionsIds(directionIds);

        Set<Long> productsIds = new HashSet<>();
        productsIds.add(devUnit_2.getId());
        apiProject.setProductsIds(productsIds);

        List<Long> subContractors = new ArrayList<>();
        subContractors.add(1L);
        apiProject.setSubcontractorsIds(subContractors);

        Plan plan = createPlan();
        planDAO.saveOrUpdate(plan);

        List<Long> plansIds = new ArrayList<>();
        plansIds.add(plan.getId());
        apiProject.setPlansIds(plansIds);

        return apiProject;
    }

    public Plan createPlan() {
        Plan plan = new Plan();
        plan.setName("Test plan");
        plan.setCreated(new Date());
        plan.setCreatorId(1L);
        plan.setStartDate(new Date());
        plan.setFinishDate(new Date());
        return plan;
    }
}
