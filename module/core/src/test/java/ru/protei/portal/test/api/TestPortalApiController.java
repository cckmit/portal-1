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
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.embeddeddb.DatabaseConfiguration;
import ru.protei.portal.config.IntegrationTestsConfiguration;
import ru.protei.portal.core.controller.api.PortalApiController;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.dto.DevUnitInfo;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.query.CaseApiQuery;
import ru.protei.portal.core.model.query.CaseCommentApiQuery;
import ru.protei.portal.core.model.query.CaseCommentQuery;
import ru.protei.portal.core.service.auth.AuthService;
import ru.protei.portal.mock.AuthServiceMock;
import ru.protei.portal.test.service.BaseServiceTest;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

import javax.annotation.PostConstruct;
import java.util.Base64;
import java.util.Collections;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringJUnit4ClassRunner.class)
@EnableTransactionManagement
@ContextConfiguration(classes = {CoreConfigurationContext.class, JdbcConfigurationContext.class, DatabaseConfiguration.class, IntegrationTestsConfiguration.class, PortalApiController.class})
@WebAppConfiguration
public class TestPortalApiController extends BaseServiceTest {
    @Autowired
    PortalApiController portalApiController;

    @Autowired
    ObjectMapper objectMapper;

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
    public void getCaseListByManager() throws Exception {
        makeCaseObject( person );
        makeCaseObject( person );
        makeCaseObject( person );

        CaseApiQuery caseApiQuery = new CaseApiQuery();
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
                .andExpect( jsonPath( "$.data.typeId", is( product.getTypeId() ) ) )
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
                .andExpect(jsonPath("$.data.typeId", is(product.getTypeId())));
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

    @Autowired
    private void authService(AuthService authService) {
        this.authService = (AuthServiceMock) authService;
    }
}
