package ru.protei.portal.test.service;

import com.wix.mysql.EmbeddedMysql;
import com.wix.mysql.config.Charset;
import com.wix.mysql.config.MysqldConfig;
import com.wix.mysql.config.SchemaConfig;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.config.MainConfiguration;
import ru.protei.portal.config.MainTestsConfiguration;
import ru.protei.portal.core.model.dao.CaseStateDAO;
import ru.protei.portal.core.model.dao.CompanyDAO;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.En_CaseStateUsageInCompanies;
import ru.protei.portal.core.service.CaseStateService;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.jdbc.JdbcConfigurationContext;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import javax.inject.Inject;

import java.util.Arrays;
import java.util.List;

import static com.wix.mysql.EmbeddedMysql.anEmbeddedMysql;
import static com.wix.mysql.config.MysqldConfig.aMysqldConfig;
import static com.wix.mysql.config.SchemaConfig.aSchemaConfig;
import static com.wix.mysql.distribution.Version.v5_7_19;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {CoreConfigurationContext.class, JdbcConfigurationContext.class, MainTestsConfiguration.class})
public class CaseStateServiceTest {

    public static final AuthToken TEST_AUTH_TOKEN = null;
    static EmbeddedMysql mysqld;
    @Inject
    private CaseStateDAO caseStateDAO;

    @Inject
    private CompanyDAO companyDAO;

    @Inject
    private CaseStateService caseStateService;

    @Inject
    private JdbcManyRelationsHelper jdbcManyRelationsHelper;

    private final static MysqldConfig config = aMysqldConfig(v5_7_19)
            .withCharset(Charset.UTF8)
            .withPort(33062)
            .withUser("sa", "")
            .withServerVariable("lower_case_table_names", 1)
                
//                .withTimeZone("Europe/Vilnius")
            .build();

    private final static SchemaConfig schemaConfig = aSchemaConfig("portal_test").build();

    @BeforeClass
    public static void initTests() {
        mysqld =  anEmbeddedMysql(config).addSchema(schemaConfig).start();
    }
//
//    @Before
//    public void reloadSchema() {
//        mysqld.reloadSchema(schemaConfig);
//    }

    @Test
    public void getCaseStateDAOTest() throws Exception {
        assertNotNull(caseStateDAO);

        List<CaseState> all = caseStateDAO.getAll();

        for (CaseState caseState: all) {
            jdbcManyRelationsHelper.fill(caseState, "companies");
        }
    }

    @Test
    public void getCaseStateForCrmDAOTest() throws Exception {
        assertNotNull(caseStateDAO);

        List<CaseState> all = caseStateDAO.getAllByCaseType(En_CaseType.CRM_SUPPORT);

        for (CaseState caseState: all) {
            jdbcManyRelationsHelper.fill(caseState, "companies");
        }
    }

    @Test
    public void checkCaseSercviceExistsTest() throws Exception {
        assertNotNull(caseStateService);
    }

    @Test
    public void getCaseStateFromServiceTest() throws Exception {
        CoreResponse<List<CaseState>> response = caseStateService.caseStateList(TEST_AUTH_TOKEN);

        if (response.isError()) {
            fail("Expected list of CaseState");
        }

        List<CaseState> states = response.getData();
        assertNotNull("Expected not nul CaseStates", states);

    }

    @Test
    public void getCaseStateByIdTest() throws Exception {
        CaseState state = checkResultAndGetData( caseStateService.getCaseState(TEST_AUTH_TOKEN, 1L));
        assertNotNull("Expected not nul case state", state);
    }

    @Test
    public void changeCaseStateusageInCompaniesTest() throws Exception {
        CaseState state = checkResultAndGetData( caseStateService.getCaseState(TEST_AUTH_TOKEN, 1L));
        assertNotNull("Expected not nul usage in companies", state.getUsageInCompanies());

        state.setUsageInCompanies(En_CaseStateUsageInCompanies.NONE);
        state = checkResultAndGetData( caseStateService.updateCaseState(TEST_AUTH_TOKEN, state));
        assertEquals("Expected NONE usage in companies", En_CaseStateUsageInCompanies.NONE, state.getUsageInCompanies());

        state.setUsageInCompanies(En_CaseStateUsageInCompanies.ALL);
        state = checkResultAndGetData( caseStateService.updateCaseState(TEST_AUTH_TOKEN, state));
        assertEquals("Expected ALL usage in companies", En_CaseStateUsageInCompanies.ALL, state.getUsageInCompanies());
    }


    @Test
    public void getChangeCaseStateCompaniesTest() throws Exception {
        Company company1 = new Company(1L);
        company1.setCname("company1");
        Company company2 = new Company(3L);
        company2.setCname("company2");

        companyDAO.saveOrUpdate( company1);
        companyDAO.saveOrUpdate( company2);


        CaseState state = checkResultAndGetData( caseStateService.getCaseState(TEST_AUTH_TOKEN, 1L));
        state.setUsageInCompanies(En_CaseStateUsageInCompanies.SELECTED);
        state.setCompanies(Arrays.asList(company1, company2));
        checkResult( caseStateService.updateCaseState(TEST_AUTH_TOKEN, state));

        state = checkResultAndGetData( caseStateService.getCaseState(TEST_AUTH_TOKEN, 1L));
        assertNotNull("Expected not nul companies", state.companies);
    }


    public static void checkResult(CoreResponse result) {
        assertNotNull("Expected result", result);
        assertTrue("Expected ok result", result.isOk());
    }

    public static <T> T checkResultAndGetData(CoreResponse<T> result)  {
        checkResult(result);
        return result.getData();
    }
}
