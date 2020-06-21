package ru.protei.portal.test.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.protei.portal.config.IntegrationTestsConfiguration;
import ru.protei.portal.core.model.dao.CaseShortViewDAO;
import ru.protei.portal.core.model.dao.HistoryDAO;
import ru.protei.portal.core.model.dict.En_CompanyCategory;
import ru.protei.portal.core.model.dict.En_HistoryValueType;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.query.HistoryQuery;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.core.service.HistoryService;
import ru.protei.portal.core.service.PlanService;
import ru.protei.portal.core.service.auth.AuthService;
import ru.protei.portal.embeddeddb.DatabaseConfiguration;
import ru.protei.portal.mock.AuthServiceMock;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {CoreConfigurationContext.class, JdbcConfigurationContext.class, DatabaseConfiguration.class, IntegrationTestsConfiguration.class})
public class HistoryServiceTest extends BaseServiceTest {

    private AuthServiceMock authService;

    @Autowired
    PlanService planService;

    @Autowired
    CaseShortViewDAO caseShortViewDAO;

    @Autowired
    HistoryService historyService;

    @Autowired
    HistoryDAO historyDAO;

    @Autowired
    private void authService(AuthService authService) {
        this.authService = (AuthServiceMock) authService;
    }

    @PostConstruct
    public void postConstruct() {
        Company company = makeCompany(En_CompanyCategory.HOME);
        Person person = makePerson(company);
        UserLogin userLogin = makeUserLogin(createUserLogin(person));
        authService.makeThreadAuthToken(userLogin);
        makeCaseObject(person);
    }

    @Test
    public void testHistoryCreate() {
        Plan plan = createPlan();
        List<CaseShortView> issues = caseShortViewDAO.getAll();

        Long planId = planService.createPlan(getAuthToken(), plan).getData();
        Assert.assertNotNull("Plan not created, planId is null", planId);

        HistoryQuery query = new HistoryQuery();
        query.setCaseObjectId(issues.get(0).getId());

        List<History> listBeforeAdding = historyService.listHistories(getAuthToken(), query).getData();
        Assert.assertNotNull(listBeforeAdding);

        Long historyId = historyService.createHistory(getAuthToken(), issues.get(0).getId(), En_HistoryValueType.ADD_TO_PLAN, null, String.valueOf(planId)).getData();
        Assert.assertNotNull("History not created, historyId is null", historyId);

        List<History> listAfterAdding = historyService.listHistories(getAuthToken(), query).getData();
        Assert.assertNotNull(listAfterAdding);

        Assert.assertTrue("History absent in listHistories by caseObjectId", (listBeforeAdding.size() + 1) == listAfterAdding.size());
        Assert.assertTrue(planService.removePlan(getAuthToken(), planId).isOk());
        Assert.assertTrue(historyDAO.removeByKey(historyId));
    }


    private Plan createPlan() {
        return createPlan("");
    }

    private Plan createPlan(String name) {
        Plan plan = new Plan();
        plan.setName(name + "test" + new Date().getTime());
        plan.setStartDate(new Date());
        plan.setFinishDate(new Date());
        return plan;
    }
}
