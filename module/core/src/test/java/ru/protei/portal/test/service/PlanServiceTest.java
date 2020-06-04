package ru.protei.portal.test.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.protei.portal.config.IntegrationTestsConfiguration;
import ru.protei.portal.core.model.dao.PersonDAO;
import ru.protei.portal.core.model.dict.En_CompanyCategory;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.query.PlanQuery;
import ru.protei.portal.core.model.view.CaseShortView;
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
public class PlanServiceTest extends BaseServiceTest{

    @Autowired
    PlanService planService;

    @Autowired
    PersonDAO personDAO;

    private AuthServiceMock authService;

    @Autowired
    private void authService( AuthService authService ) {
        this.authService = (AuthServiceMock) authService;
    }

    @PostConstruct
    public void postConstruct(  ) {
        Company company = makeCompany(En_CompanyCategory.HOME);
        Person person = makePerson(company);
        UserLogin userLogin = makeUserLogin(createUserLogin(person));
        authService.makeThreadAuthToken(userLogin);
    }

    @Test
    public void testCreateAndRemovePlan() {
        Plan plan = createPlan();

        Long planId = planService.createPlan(getAuthToken(), plan).getData();

        Assert.assertNotNull("Plan not created, planId is null", planId);

        PlanQuery query = new PlanQuery();
        query.setName(plan.getName());

        List<Plan> list = planService.listPlans(getAuthToken(), query).getData();

        Assert.assertTrue("Plan absent in listPlans by name", list != null && !list.isEmpty() && list.get(0).getName().equals(plan.getName()));
        Assert.assertTrue(planService.removePlan(getAuthToken(), planId).isOk());
    }

    @Test
    public void testCreatePlanWithIssues(){

    }

    @Test
    public void tempTest(){

        Plan plan = createPlan();

        AuthToken token = getAuthToken();

        Long planId = planService.createPlan(token, plan).getData();

        plan.setId(planId);

        planService.addIssueToPlan(getAuthToken(), planId, 162972L);
        planService.addIssueToPlan(getAuthToken(), planId, 162971L);
        planService.addIssueToPlan(getAuthToken(), planId, 162970L);
        planService.addIssueToPlan(getAuthToken(), planId, 162969L);
        planService.addIssueToPlan(getAuthToken(), planId, 162968L);


        PlanQuery query = new PlanQuery();

        List<Plan> data = planService.listPlans(getAuthToken(), query).getData();

        query.setName(plan.getName());

        data = planService.listPlans(getAuthToken(), query).getData();

        query.setName(null);
        query.setIssueId(162972L);

        data = planService.listPlans(getAuthToken(), query).getData();

        query.setIssueId(null);
        query.setIssueNumber(1021966L);

        data = planService.listPlans(getAuthToken(), query).getData();

        query.setIssueNumber(null);

        data = planService.listPlansWithIssues(getAuthToken(), query).getData();

        query.setName(plan.getName());

        data = planService.listPlansWithIssues(getAuthToken(), query).getData();

        query.setName(null);
        query.setIssueId(162972L);

        data = planService.listPlansWithIssues(getAuthToken(), query).getData();

        query.setIssueId(null);
        query.setIssueNumber(1021966L);

        data = planService.listPlansWithIssues(getAuthToken(), query).getData();

        plan = planService.getPlanWithIssues(getAuthToken(), planId).getData();


        plan.setName("123123123" + new Date().getTime());
        plan.setDateFrom(new Date());
        plan.setDateTo(new Date());

        planService.editPlanParams(getAuthToken(), plan);

        CaseShortView caseShortView = plan.getIssueList().get(0);
        plan.getIssueList().remove(0);
        plan.getIssueList().add(caseShortView);

        planService.changeIssueOrder(getAuthToken(), plan);

       /* List<PlanToCaseObject> issueOrderList = data1.getIssueOrderList();

        List<PlanToCaseObject> newlist = new ArrayList<>();

        PlanToCaseObject first = issueOrderList.get(0);
        first.setOrderNumber(1000);

        newlist.add(first);

        planService.changeIssueOrder(getAuthToken(), newlist);*/

        planService.removeIssueFromPlan(getAuthToken(), planId, 162972L);

      //  planService.moveIssueToOtherPlan(getAuthToken(), planId, 162970L, 4L);

        planService.removePlan(token, planId);



    }

    private Plan createPlan () {
        Plan plan = new Plan();
        plan.setName("test" + new Date().getTime());
        plan.setDateFrom(new Date());
        plan.setDateTo(new Date());
        return plan;
    }

    private PlanToCaseObject createPlanToCaseObject (Long planId) {
        PlanToCaseObject planToCaseObject = new PlanToCaseObject();


        return planToCaseObject;
    }
}
