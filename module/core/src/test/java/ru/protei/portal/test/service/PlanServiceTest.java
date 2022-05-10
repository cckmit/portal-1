package ru.protei.portal.test.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.config.IntegrationTestsConfiguration;
import ru.protei.portal.core.model.dao.CaseShortViewDAO;
import ru.protei.portal.core.model.dao.PersonDAO;
import ru.protei.portal.core.model.dao.PlanToCaseObjectDAO;
import ru.protei.portal.core.model.dict.En_CompanyCategory;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.query.PlanQuery;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.core.service.PlanService;
import ru.protei.portal.core.service.auth.AuthService;
import ru.protei.portal.embeddeddb.DatabaseConfiguration;
import ru.protei.portal.mock.AuthServiceMock;
import ru.protei.sn.remote_services.configuration.RemoteServiceFactory;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.http.HttpConfigurationContext;
import ru.protei.winter.http.client.factory.HttpClientFactory;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {CoreConfigurationContext.class, JdbcConfigurationContext.class,
        DatabaseConfiguration.class, IntegrationTestsConfiguration.class, RemoteServiceFactory.class,
        HttpClientFactory.class, HttpConfigurationContext.class})
public class PlanServiceTest extends BaseServiceTest{

    private AuthServiceMock authService;

    @Autowired
    PlanService planService;

    @Autowired
    PersonDAO personDAO;

    @Autowired
    CaseShortViewDAO caseShortViewDAO;

    @Autowired
    PlanToCaseObjectDAO planToCaseObjectDAO;

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
        makeCaseObject(person);
        makeCaseObject(person);
        makeCaseObject(person);
        makeCaseObject(person);
        makeCaseObject(person);
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
        Plan plan = createPlan();

        List<CaseShortView> issues = caseShortViewDAO.getAll();

        plan.setIssueList(issues);

        Long planId = planService.createPlan(getAuthToken(), plan).getData();

        Assert.assertNotNull("Plan not created, planId is null", planId);

        Plan planFromDB = planService.getPlanWithIssues(getAuthToken(), planId).getData();

        Assert.assertNotNull("Plan created without issues", planFromDB.getIssueList());
        Assert.assertEquals("Wrong issues quantity in created plan", plan.getIssueList().size(), planFromDB.getIssueList().size());
        Assert.assertTrue(planService.removePlan(getAuthToken(), planId).isOk());
    }

    @Test
    public void testGetAllListOfPlans(){
        Plan plan1 = createPlan("1");
        Plan plan2 = createPlan("2");
        Plan plan3 = createPlan("3");

        Long planId1 = planService.createPlan(getAuthToken(), plan1).getData();
        Long planId2 = planService.createPlan(getAuthToken(), plan2).getData();
        Long planId3 = planService.createPlan(getAuthToken(), plan3).getData();

        Assert.assertNotNull("Plan not created, planId1 is null", planId1);
        Assert.assertNotNull("Plan not created, planId2 is null", planId2);
        Assert.assertNotNull("Plan not created, planId3 is null", planId3);

        PlanQuery query = new PlanQuery();

        List<Plan> list = planService.listPlans(getAuthToken(), query).getData();

        Assert.assertNotNull("Error to get list of plans", list);
        Assert.assertEquals("Wrong plans quantity", 3, list.size());
        Assert.assertTrue(planService.removePlan(getAuthToken(), planId1).isOk());
        Assert.assertTrue(planService.removePlan(getAuthToken(), planId2).isOk());
        Assert.assertTrue(planService.removePlan(getAuthToken(), planId3).isOk());
    }

    @Test
    public void testGetListOfPlansByName(){
        Plan plan1 = createPlan("1");
        Plan plan2 = createPlan("2");
        Plan plan3 = createPlan("3");

        Long planId1 = planService.createPlan(getAuthToken(), plan1).getData();
        Long planId2 = planService.createPlan(getAuthToken(), plan2).getData();
        Long planId3 = planService.createPlan(getAuthToken(), plan3).getData();

        Assert.assertNotNull("Plan not created, planId1 is null", planId1);
        Assert.assertNotNull("Plan not created, planId2 is null", planId2);
        Assert.assertNotNull("Plan not created, planId3 is null", planId3);

        PlanQuery query = new PlanQuery();
        query.setName(plan1.getName());

        List<Plan> list = planService.listPlans(getAuthToken(), query).getData();

        Assert.assertNotNull("Error to get list of plans", list);
        Assert.assertEquals("Wrong plans quantity", 1, list.size());
        Assert.assertEquals("Wrong plan", planId1, list.get(0).getId());
        Assert.assertTrue(planService.removePlan(getAuthToken(), planId1).isOk());
        Assert.assertTrue(planService.removePlan(getAuthToken(), planId2).isOk());
        Assert.assertTrue(planService.removePlan(getAuthToken(), planId3).isOk());
    }

    @Test
    public void testGetListOfPlansByIssueId(){
        Plan plan1 = createPlan("1");
        Plan plan2 = createPlan("2");
        Plan plan3 = createPlan("3");

        List<CaseShortView> issues = caseShortViewDAO.getAll();

        plan1.setIssueList(issues);
        plan3.setIssueList(issues);

        Long planId1 = planService.createPlan(getAuthToken(), plan1).getData();
        Long planId2 = planService.createPlan(getAuthToken(), plan2).getData();
        Long planId3 = planService.createPlan(getAuthToken(), plan3).getData();

        Assert.assertNotNull("Plan not created, planId1 is null", planId1);
        Assert.assertNotNull("Plan not created, planId2 is null", planId2);
        Assert.assertNotNull("Plan not created, planId3 is null", planId3);

        PlanQuery query = new PlanQuery();
        query.setIssueId(issues.get(0).getId());

        List<Plan> list = planService.listPlans(getAuthToken(), query).getData();

        Assert.assertNotNull("Error to get list of plans", list);
        Assert.assertEquals("Wrong plans quantity", 2, list.size());
        Assert.assertTrue(planService.removePlan(getAuthToken(), planId1).isOk());
        Assert.assertTrue(planService.removePlan(getAuthToken(), planId2).isOk());
        Assert.assertTrue(planService.removePlan(getAuthToken(), planId3).isOk());
    }

    @Test
    public void testGetListOfPlansByIssueNumber(){
        Plan plan1 = createPlan("1");
        Plan plan2 = createPlan("2");
        Plan plan3 = createPlan("3");

        List<CaseShortView> issues = caseShortViewDAO.getAll();

        plan1.setIssueList(issues);
        plan3.setIssueList(issues);

        Long planId1 = planService.createPlan(getAuthToken(), plan1).getData();
        Long planId2 = planService.createPlan(getAuthToken(), plan2).getData();
        Long planId3 = planService.createPlan(getAuthToken(), plan3).getData();

        Assert.assertNotNull("Plan not created, planId1 is null", planId1);
        Assert.assertNotNull("Plan not created, planId2 is null", planId2);
        Assert.assertNotNull("Plan not created, planId3 is null", planId3);

        PlanQuery query = new PlanQuery();
        query.setIssueNumber(issues.get(0).getCaseNumber());

        List<Plan> list = planService.listPlans(getAuthToken(), query).getData();

        Assert.assertNotNull("Error to get list of plans", list);
        Assert.assertEquals("Wrong plans quantity", 2, list.size());
        Assert.assertTrue(planService.removePlan(getAuthToken(), planId1).isOk());
        Assert.assertTrue(planService.removePlan(getAuthToken(), planId2).isOk());
        Assert.assertTrue(planService.removePlan(getAuthToken(), planId3).isOk());
    }

    @Test
    public void testGetListOfPlansWithIssues(){
        Plan plan1 = createPlan("1");
        Plan plan2 = createPlan("2");
        Plan plan3 = createPlan("3");

        List<CaseShortView> issues = caseShortViewDAO.getAll();

        plan1.setIssueList(issues);
        plan2.setIssueList(issues);
        plan3.setIssueList(issues);

        Long planId1 = planService.createPlan(getAuthToken(), plan1).getData();
        Long planId2 = planService.createPlan(getAuthToken(), plan2).getData();
        Long planId3 = planService.createPlan(getAuthToken(), plan3).getData();

        Assert.assertNotNull("Plan not created, planId1 is null", planId1);
        Assert.assertNotNull("Plan not created, planId2 is null", planId2);
        Assert.assertNotNull("Plan not created, planId3 is null", planId3);

        PlanQuery query = new PlanQuery();

        List<Plan> list = planService.listPlansWithIssues(getAuthToken(), query).getData();

        Assert.assertNotNull("Error to get list of plans", list);
        Assert.assertEquals("Wrong plans quantity", 3, list.size());
        Assert.assertEquals("Wrong issues quantity", issues.size(), list.get(0).getIssueList().size());
        Assert.assertEquals("Wrong issues quantity", issues.size(), list.get(1).getIssueList().size());
        Assert.assertEquals("Wrong issues quantity", issues.size(), list.get(2).getIssueList().size());
        Assert.assertTrue(planService.removePlan(getAuthToken(), planId1).isOk());
        Assert.assertTrue(planService.removePlan(getAuthToken(), planId2).isOk());
        Assert.assertTrue(planService.removePlan(getAuthToken(), planId3).isOk());
    }

    @Test
    public void testEditPlanParams(){
        Plan oldPlan = createPlan("1");

        Long oldPlanId = planService.createPlan(getAuthToken(), oldPlan).getData();
        Assert.assertNotNull("Plan not created, oldPlan is null", oldPlanId);

        Plan oldPlanFromDB = planService.getPlanWithIssues(getAuthToken(), oldPlanId).getData();
        Assert.assertNotNull("Failed to get plan by id", oldPlanFromDB);

        Plan newPlan = new Plan();
        newPlan.setId(oldPlanId);
        newPlan.setName("new name");
        newPlan.setStartDate(new Date(oldPlan.getStartDate().getTime() + 10000L));
        newPlan.setFinishDate(new Date(oldPlan.getFinishDate().getTime() + 10000L));
        newPlan.setCreated(new Date(oldPlan.getCreated().getTime() + 10000L));

        Assert.assertTrue(planService.editPlanParams(getAuthToken(), newPlan).isOk());

        Plan newFromDB = planService.getPlanWithIssues(getAuthToken(), oldPlanId).getData();
        Assert.assertNotNull("Failed to get plan by id", newFromDB);

        Assert.assertNotEquals("Failed to update plan name", oldPlanFromDB.getName(), newFromDB.getName());
        Assert.assertNotEquals("Failed to update plan date from", oldPlanFromDB.getStartDate().getTime(), newFromDB.getStartDate().getTime());
        Assert.assertNotEquals("Failed to update plan date to", oldPlanFromDB.getFinishDate().getTime(), newFromDB.getFinishDate().getTime());
        Assert.assertEquals("Plan created date must not be updated", oldPlanFromDB.getCreated().getTime(), newFromDB.getCreated().getTime());

        Assert.assertTrue(planService.removePlan(getAuthToken(), oldPlanId).isOk());
    }

    @Test
    public void testAddIssueToPlan(){
        Plan plan1 = createPlan();

        List<CaseShortView> issues = caseShortViewDAO.getAll();
        CaseShortView issue1 = issues.get(0);
        CaseShortView issue2 = issues.get(1);

        Long planId1 = planService.createPlan(getAuthToken(), plan1).getData();
        Assert.assertNotNull("Plan not created, planId1 is null", planId1);

        Plan planFromDB = planService.getPlanWithIssues(getAuthToken(), planId1).getData();
        Assert.assertNotNull("Failed to get plan by id", planFromDB);
        Assert.assertTrue("Issue list is not empty", CollectionUtils.isEmpty(planFromDB.getIssueList()));

        planService.addIssueToPlan(getAuthToken(), planId1, issue1.getId());

        planFromDB = planService.getPlanWithIssues(getAuthToken(), planId1).getData();
        Assert.assertNotNull("Failed to get plan by id", planFromDB);
        Assert.assertEquals("Wrong issues quantity", 1, planFromDB.getIssueList().size());

        //Добавляем уже добавленное ранее обращение
        Result<Plan> result = planService.addIssueToPlan(getAuthToken(), planId1, issue1.getId());
        Assert.assertEquals( En_ResultStatus.ALREADY_EXIST, result.getStatus());

        planFromDB = planService.getPlanWithIssues(getAuthToken(), planId1).getData();
        Assert.assertNotNull("Failed to get plan by id", planFromDB);
        Assert.assertEquals("Wrong issues quantity", 1, planFromDB.getIssueList().size());

        planService.addIssueToPlan(getAuthToken(), planId1, issue2.getId());

        planFromDB = planService.getPlanWithIssues(getAuthToken(), planId1).getData();
        Assert.assertNotNull("Failed to get plan by id", planFromDB);
        Assert.assertEquals("Wrong issues quantity", 2, planFromDB.getIssueList().size());

        Assert.assertTrue(planService.removePlan(getAuthToken(), planId1).isOk());
    }

    @Test
    public void testRemoveIssueFromPlan(){
        Plan plan1 = createPlan("1");

        List<CaseShortView> issues = caseShortViewDAO.getAll();
        plan1.setIssueList(issues);

        Long planId1 = planService.createPlan(getAuthToken(), plan1).getData();
        Assert.assertNotNull("Plan not created, planId1 is null", planId1);

        Plan planFromDB = planService.getPlanWithIssues(getAuthToken(), planId1).getData();
        Assert.assertNotNull("Failed to get plan by id", planFromDB);
        Assert.assertEquals("Wrong issues quantity", issues.size(), planFromDB.getIssueList().size());

        planService.removeIssueFromPlan(getAuthToken(), planId1, issues.get(0).getId());

        planFromDB = planService.getPlanWithIssues(getAuthToken(), planId1).getData();
        Assert.assertNotNull("Failed to get plan by id", planFromDB);
        Assert.assertEquals("Wrong issues quantity", issues.size() - 1, planFromDB.getIssueList().size());

        //Удаляем уже удаленное ранее обращение
        Result<Long> result = planService.removeIssueFromPlan(getAuthToken(), planId1, issues.get(0).getId());
        Assert.assertEquals( En_ResultStatus.NOT_FOUND, result.getStatus());

        planFromDB = planService.getPlanWithIssues(getAuthToken(), planId1).getData();
        Assert.assertNotNull("Failed to get plan by id", planFromDB);
        Assert.assertEquals("Wrong issues quantity", issues.size() - 1, planFromDB.getIssueList().size());

        Assert.assertTrue(planService.removePlan(getAuthToken(), planId1).isOk());
    }


    @Test
    public void testChangeIssuesOrder(){
        Plan plan1 = createPlan("1");

        List<CaseShortView> issues = caseShortViewDAO.getAll();
        plan1.setIssueList(issues);

        Long planId1 = planService.createPlan(getAuthToken(), plan1).getData();
        Assert.assertNotNull("Plan not created, planId1 is null", planId1);

        Plan planFromDB = planService.getPlanWithIssues(getAuthToken(), planId1).getData();
        Assert.assertNotNull("Failed to get plan by id", planFromDB);
        Assert.assertTrue("Orders are not equals", compareOrder(planFromDB.getIssueList(), issues));

        Collections.rotate(issues, 2);
        Assert.assertFalse("There should be different orders", compareOrder(planFromDB.getIssueList(), issues));

        planFromDB.setIssueList(issues);
        planService.changeIssuesOrder(getAuthToken(), planFromDB);

        planFromDB = planService.getPlanWithIssues(getAuthToken(), planId1).getData();
        Assert.assertNotNull("Failed to get plan by id", planFromDB);
        Assert.assertTrue("Orders are not equals", compareOrder(planFromDB.getIssueList(), issues));

        Assert.assertTrue(planService.removePlan(getAuthToken(), planId1).isOk());
    }

    @Test
    public void testMoveIssueToAnotherPlan(){
        Plan plan1 = createPlan("1");
        Plan plan2 = createPlan("2");

        List<CaseShortView> issues = caseShortViewDAO.getAll();

        plan1.setIssueList(issues);

        Long planId1 = planService.createPlan(getAuthToken(), plan1).getData();
        Long planId2 = planService.createPlan(getAuthToken(), plan2).getData();

        Assert.assertNotNull("Plan not created, planId1 is null", planId1);
        Assert.assertNotNull("Plan not created, planId2 is null", planId2);

        Plan planFromDB1 = planService.getPlanWithIssues(getAuthToken(), planId1).getData();
        Assert.assertNotNull("Failed to get plan by id", planFromDB1);
        Plan planFromDB2 = planService.getPlanWithIssues(getAuthToken(), planId2).getData();
        Assert.assertNotNull("Failed to get plan by id", planFromDB2);

        Assert.assertEquals("Wrong issues quantity", issues.size(), planFromDB1.getIssueList().size());
        Assert.assertEquals("Wrong issues quantity", 0, planFromDB2.getIssueList().size());

        planService.moveIssueToAnotherPlan(getAuthToken(), planId1, issues.get(0).getId(), planId2);

        planFromDB1 = planService.getPlanWithIssues(getAuthToken(), planId1).getData();
        Assert.assertNotNull("Failed to get plan by id", planFromDB1);
        planFromDB2 = planService.getPlanWithIssues(getAuthToken(), planId2).getData();
        Assert.assertNotNull("Failed to get plan by id", planFromDB2);

        Assert.assertEquals("Wrong issues quantity", issues.size() - 1, planFromDB1.getIssueList().size());
        Assert.assertEquals("Wrong issues quantity", 1, planFromDB2.getIssueList().size());

        planService.addIssueToPlan(getAuthToken(), planId2, issues.get(1).getId());
        Result<Boolean> result = planService.moveIssueToAnotherPlan(getAuthToken(), planId1, issues.get(1).getId(), planId2);
        Assert.assertEquals( En_ResultStatus.ALREADY_EXIST, result.getStatus());

        planFromDB1 = planService.getPlanWithIssues(getAuthToken(), planId1).getData();
        Assert.assertNotNull("Failed to get plan by id", planFromDB1);
        planFromDB2 = planService.getPlanWithIssues(getAuthToken(), planId2).getData();
        Assert.assertNotNull("Failed to get plan by id", planFromDB2);

        Assert.assertEquals("Wrong issues quantity", issues.size() - 1, planFromDB1.getIssueList().size());
        Assert.assertEquals("Wrong issues quantity", 2, planFromDB2.getIssueList().size());

        Assert.assertTrue(planService.removePlan(getAuthToken(), planId1).isOk());
        Assert.assertTrue(planService.removePlan(getAuthToken(), planId2).isOk());
    }

    @Test
    public void testRemovePlan(){
        Plan plan1 = createPlan("1");

        List<CaseShortView> issues = caseShortViewDAO.getAll();

        plan1.setIssueList(issues);

        Long planId1 = planService.createPlan(getAuthToken(), plan1).getData();
        Assert.assertNotNull("Plan not created, planId1 is null", planId1);

        Plan planFromDB1 = planService.getPlanWithIssues(getAuthToken(), planId1).getData();
        Assert.assertNotNull("Failed to get plan by id", planFromDB1);
        Assert.assertEquals("Wrong issues quantity", issues.size(), planFromDB1.getIssueList().size());

        Assert.assertTrue(planService.removePlan(getAuthToken(), planId1).isOk());

        Result<Plan> result = planService.getPlanWithIssues(getAuthToken(), planId1);
        Assert.assertEquals(En_ResultStatus.NOT_FOUND, result.getStatus());

        List<PlanToCaseObject> sortedListByPlanId = planToCaseObjectDAO.getSortedListByPlanId(planId1);
        Assert.assertTrue("All planToCaseObject by plaId should be removed", CollectionUtils.isEmpty(sortedListByPlanId));
    }

    private boolean compareOrder (List<CaseShortView> list1, List<CaseShortView> list2){
        if (list1.size() != list2.size()){
            return false;
        }

        for (int i = 0; i < list1.size(); i++) {
            if (!list1.get(i).getId().equals(list2.get(i).getId())){
                return false;
            }
        }

        return true;
    }
}
