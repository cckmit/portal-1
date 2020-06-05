package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.exception.RollbackTransactionException;
import ru.protei.portal.core.model.dao.CaseObjectDAO;
import ru.protei.portal.core.model.dao.PlanDAO;
import ru.protei.portal.core.model.dao.PlanToCaseObjectDAO;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.Plan;
import ru.protei.portal.core.model.ent.PlanToCaseObject;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.query.PlanQuery;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;

public class PlanServiceImpl implements PlanService{

    private static Logger log = LoggerFactory.getLogger( PlanServiceImpl.class );

    @Autowired
    PlanDAO planDAO;

    @Autowired
    PlanToCaseObjectDAO planToCaseObjectDAO;

    @Autowired
    CaseObjectDAO caseObjectDAO;

    @Autowired
    JdbcManyRelationsHelper jdbcManyRelationsHelper;

    @Override
    public Result<List<Plan>> listPlans(AuthToken token, PlanQuery query) {

        List<Plan> list = planDAO.listByQuery(query);
        if (list == null) {
            return error(En_ResultStatus.GET_DATA_ERROR);
        }

        return ok(list);
    }

    @Override
    public Result<List<Plan>> listPlansWithIssues(AuthToken token, PlanQuery query) {

        List<Plan> list = planDAO.listByQuery(query);
        if (list == null) {
            return error(En_ResultStatus.GET_DATA_ERROR);
        }

        list.forEach(plan -> {
            jdbcManyRelationsHelper.fillAll(plan);
            orderIssuesInPlan(plan);
        });

        return ok(list);
    }

    @Override
    public Result<Plan> getPlanWithIssues(AuthToken token, Long planId) {
        if (planId == null){
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        Plan plan = planDAO.get(planId);

        if (plan == null){
            return error(En_ResultStatus.NOT_FOUND);
        }

        jdbcManyRelationsHelper.fillAll(plan);
        orderIssuesInPlan(plan);

        return ok(plan);
    }

    @Override
    @Transactional
    public Result<Long> createPlan(AuthToken token, Plan plan) {
        if (plan == null || !validatePlan(plan)){
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        plan.setCreatorId(token.getPersonId());
        plan.setCreated(new Date());

        Long planId =  planDAO.persist(plan);

        if (planId == null){
            return error(En_ResultStatus.NOT_CREATED);
        }

        if (CollectionUtils.isNotEmpty(plan.getIssueList())){
            plan.getIssueList().forEach(issue -> {
                PlanToCaseObject planToCaseObject = new PlanToCaseObject(planId, issue.getId());
                planToCaseObject.setOrderNumber(plan.getIssueList().indexOf(issue));
                planToCaseObjectDAO.persist(planToCaseObject);
            });
        }

        return ok(planId);
    }

    @Override
    public Result<Boolean> editPlanParams(AuthToken token, Plan plan) {
        if (plan == null || plan.getId() == null || !validatePlan(plan)){
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        if (!planDAO.partialMerge(plan, "name", "date_from", "date_to")){
            return error(En_ResultStatus.NOT_UPDATED);
        }

        return ok();
    }

    @Override
    @Transactional
    public Result<Boolean> addIssueToPlan(AuthToken token, Long planId, Long issueId) {
        if (planId == null || issueId == null){
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        PlanToCaseObject newIssueInPlan = new PlanToCaseObject(planId, issueId);

        List<PlanToCaseObject> issuesInPlan = planToCaseObjectDAO.getSortedListByPlanId(planId);

        if (issuesInPlan.contains(newIssueInPlan)){
            return error(En_ResultStatus.ALREADY_EXIST);
        }

        newIssueInPlan.setOrderNumber(issuesInPlan.size());

        newIssueInPlan.setId(planToCaseObjectDAO.persist(newIssueInPlan));

        if (newIssueInPlan.getId() == null){
            return error(En_ResultStatus.NOT_CREATED);
        }

        //add history

        return ok();
    }

    @Override
    @Transactional
    public Result<Boolean> removeIssueFromPlan(AuthToken token, Long planId, Long issueId) {
        if (planId == null || issueId == null){
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        int rowCount = planToCaseObjectDAO.removeByPlanIdAndIssueId(planId, issueId);

        if (rowCount == 1) {
            updateOrderNumbers(planId);
            return ok();
        }

        if (rowCount == 0) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        throw new RollbackTransactionException("removeIssueFromPlan(): rollback transaction");

        //add history
    }



    @Override
    @Transactional
    public Result<Boolean> changeIssuesOrder(AuthToken token, Plan plan) {
        if (plan == null || CollectionUtils.isEmpty(plan.getIssueList())){
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        List<PlanToCaseObject> issueOrderList = planToCaseObjectDAO.getSortedListByPlanId(plan.getId());
        List<CaseShortView> issueList = plan.getIssueList();

        if (issueOrderList.size() != issueList.size()){
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        for (PlanToCaseObject planToCaseObject : issueOrderList) {
            for (int i = 0; i < issueList.size(); i++) {
                if (planToCaseObject.getCaseObjectId().equals(issueList.get(i).getId())){
                    planToCaseObject.setOrderNumber(i);
                    break;
                }
            }
        }

        if (planToCaseObjectDAO.mergeBatch(issueOrderList) != issueOrderList.size()){
            return error(En_ResultStatus.INTERNAL_ERROR);
        }

        //add audit!
        //add history

        return ok();
    }

    @Override
    @Transactional
    public Result<Boolean> moveIssueToAnotherPlan(AuthToken token, Long currentPlanId, Long issueId, Long newPlanId) {
        if (currentPlanId == null || issueId == null || newPlanId == null){
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        PlanToCaseObject planToCaseObject = planToCaseObjectDAO.getByPlanIdAndIssueId(currentPlanId, issueId);

        if (planToCaseObject == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        planToCaseObject.setPlanId(newPlanId);

        List<PlanToCaseObject> issuesInPlan = planToCaseObjectDAO.getSortedListByPlanId(newPlanId);

        if (issuesInPlan.contains(planToCaseObject)){
            return error(En_ResultStatus.ALREADY_EXIST);
        }

        planToCaseObject.setOrderNumber(issuesInPlan.size());

        planToCaseObjectDAO.merge(planToCaseObject);

        updateOrderNumbers(currentPlanId);

        //add history

        return error(En_ResultStatus.INTERNAL_ERROR);
    }

    @Override
    public Result<Boolean> removePlan(AuthToken token, Long planId) {
        if (planId == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        if (!planDAO.removeByKey(planId)) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        return ok();
    }

    private boolean validatePlan (Plan plan) {
        if (StringUtils.isBlank(plan.getName())) {
            return false;
        }

        if (plan.getDateFrom() == null) {
            return false;
        }

        if (plan.getDateTo() == null) {
            return false;
        }

        if (plan.getDateFrom().after(plan.getDateTo())) {
            return false;
        }

        return true;
    }

    private void orderIssuesInPlan(Plan plan) {
        List<PlanToCaseObject> sortedListByPlanId = planToCaseObjectDAO.getSortedListByPlanId(plan.getId());

        for (int i = 0; i < sortedListByPlanId.size(); i++) {
            Long caseIdCurrent = plan.getIssueList().get(i).getId();
            Long caseIdOrdered = sortedListByPlanId.get(i).getCaseObjectId();

            if (!caseIdCurrent.equals(caseIdOrdered)){

                for (int j = i; j < sortedListByPlanId.size(); j++) {
                    if (plan.getIssueList().get(j).getId().equals(caseIdOrdered)){
                        Collections.swap(plan.getIssueList(), i, j);
                    }
                }

            }
        }
    }

    private void updateOrderNumbers(Long planId) {
        List<PlanToCaseObject> sortedListByPlanId = planToCaseObjectDAO.getSortedListByPlanId(planId);

        for (int i = 0; i < sortedListByPlanId.size(); i++) {
            sortedListByPlanId.get(i).setOrderNumber(i);
        }
        planToCaseObjectDAO.persistBatch(sortedListByPlanId);
    }
}
