package ru.protei.portal.test.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.config.IntegrationTestsConfiguration;
import ru.protei.portal.core.model.dao.CaseStateDAO;
import ru.protei.portal.core.model.dao.CompanyDAO;
import ru.protei.portal.core.model.dict.En_CaseStateUsageInCompanies;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.service.CaseStateService;
import ru.protei.portal.embeddeddb.DatabaseConfiguration;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.jdbc.JdbcConfigurationContext;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {CoreConfigurationContext.class, JdbcConfigurationContext.class,
        DatabaseConfiguration.class, IntegrationTestsConfiguration.class
})
public class CaseStateServiceTest {

    public static final AuthToken TEST_AUTH_TOKEN = null;

    @Inject
    private CaseStateDAO caseStateDAO;

    @Inject
    private CompanyDAO companyDAO;

    @Inject
    private CaseStateService caseStateService;

    @Inject
    private JdbcManyRelationsHelper jdbcManyRelationsHelper;

    @Test
    public void getCaseStateDAOTest() throws Exception {
        assertNotNull(caseStateDAO);

        List<CaseState> all = caseStateDAO.getAll();

        for (CaseState caseState: all) {
            jdbcManyRelationsHelper.fill(caseState, "companies");
        }

        assertNotNull("Expected not nul CaseStates", all);
    }

    @Test
    public void getCaseStateForCrmDAOTest() throws Exception {
        assertNotNull(caseStateDAO);

        List<CaseState> all = caseStateDAO.getAllByCaseType(En_CaseType.CRM_SUPPORT);

        for (CaseState caseState: all) {
            jdbcManyRelationsHelper.fill(caseState, "companies");
        }

        assertNotNull("Expected not nul CaseStates", all);
    }

    @Test
    public void checkCaseSercviceExistsTest() throws Exception {
        assertNotNull(caseStateService);
    }

    @Test
    public void getCaseStateFromServiceTest() throws Exception {
        Result<List<CaseState>> response = caseStateService.getCaseStates(TEST_AUTH_TOKEN, En_CaseType.CRM_SUPPORT);

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
    public void changeCaseStateCompaniesTest() throws Exception {
        Company company1 = new Company();
        company1.setCname("company1");
        Company company2 = new Company();
        company2.setCname("company2");

        company1.setId(companyDAO.persist(company1));
        company2.setId(companyDAO.persist(company2));


        CaseState state = checkResultAndGetData( caseStateService.getCaseState(TEST_AUTH_TOKEN, CrmConstants.State.CREATED));
        state.setUsageInCompanies(En_CaseStateUsageInCompanies.SELECTED);
        state.setCompanies(Arrays.asList(company1, company2));
        checkResult( caseStateService.updateCaseState(TEST_AUTH_TOKEN, state));

        state = checkResultAndGetData( caseStateService.getCaseState(TEST_AUTH_TOKEN, CrmConstants.State.CREATED));
        assertNotNull("Expected not nul companies", state.companies);
    }

    @Test
    public void getCaseStatesByCompanyTest() throws Exception {
        Company company1 = new Company();
        company1.setCname("company3");
        List<CaseState> expectedStates = Arrays.asList(makeCaseState(CrmConstants.State.OPENED), makeCaseState(CrmConstants.State.DONE));
        company1.setCaseStates(expectedStates);

        company1.setId(companyDAO.persist(company1));
        jdbcManyRelationsHelper.persist(company1, "caseStates");

        List<CaseState> caseStates = checkResultAndGetData(caseStateService.getCaseStatesForCompanyOmitPrivileges(company1.getId()));
        assertNotNull("Expected not nul states", caseStates);
        assertTrue("Unexpected state received",expectedStates.containsAll(caseStates));
        assertTrue("Not all states",caseStates.containsAll(expectedStates));
    }

    private CaseState makeCaseState(Long state) {
        return new CaseState(state);
    }


    public static void checkResult( Result result) {
        assertNotNull("Expected result", result);
        assertTrue("Expected ok result", result.isOk());
    }

    public static <T> T checkResultAndGetData( Result<T> result)  {
        checkResult(result);
        return result.getData();
    }
}
