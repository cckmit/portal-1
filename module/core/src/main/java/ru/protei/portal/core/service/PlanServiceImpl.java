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
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.query.PlanQuery;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

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

        list.forEach(plan -> jdbcManyRelationsHelper.fillAll(plan));

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

        return ok(plan);
    }

    @Override
    @Transactional
    public Result<Long> createPlan(AuthToken token, Plan plan) {
        if (plan == null || !validatePlan(plan)){
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        plan.setCreatorId(7942L);
        plan.setCreated(new Date());

        Long planId = saveToDB(plan);

        if (planId == null){
            return error(En_ResultStatus.NOT_CREATED);
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
    public Result<PlanToCaseObject> addIssueToPlan(AuthToken token, Long planId, Long issueId) {
        if (planId == null || issueId == null){
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        PlanToCaseObject newIssueInPlan = new PlanToCaseObject(planId, issueId);

        List<PlanToCaseObject> issuesInPlan = planToCaseObjectDAO.getSortedListByPlanId(planId);

        if (issuesInPlan.contains(newIssueInPlan)){
            return error(En_ResultStatus.ALREADY_EXIST);
        }

        int maxOrderNumber = issuesInPlan.isEmpty() ? 0 : issuesInPlan.get(issuesInPlan.size()-1).getOrderNumber();

        newIssueInPlan.setOrderNumber(maxOrderNumber + 10);

        newIssueInPlan.setId(planToCaseObjectDAO.persist(newIssueInPlan));

        if (newIssueInPlan.getId() == null){
            return error(En_ResultStatus.NOT_CREATED);
        }

        //add history

        return ok(newIssueInPlan);
    }

    @Override
    @Transactional
    public Result<Boolean> removeIssueFromPlan(AuthToken token, Long planId, Long issueId) {
        if (planId == null || issueId == null){
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        int rowCount = planToCaseObjectDAO.removeByPlanIdAndIssueId(planId, issueId);

        switch (rowCount) {
            case 0 :
                return error(En_ResultStatus.NOT_FOUND);
            case 1 :
                return ok();
            default:
                throw new RollbackTransactionException("removeIssueFromPlan(): rollback transaction");
        }

        //add history
    }

    @Override
    @Transactional
    public Result<Boolean> changeIssueOrder(AuthToken token, List<PlanToCaseObject> changedList) {
        if (changedList == null || !validatePlanToCaseObjects(changedList)){
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        if (planToCaseObjectDAO.mergeBatch(changedList) != changedList.size()){
            return error(En_ResultStatus.INTERNAL_ERROR);
        }

        //add audit!

        return ok();
    }

    @Override
    @Transactional
    public Result<Boolean> moveIssueToOtherPlan(AuthToken token, Long currentPlanId, Long issueId, Long newPlanId) {
        if (currentPlanId == null || issueId == null || newPlanId == null){
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        PlanToCaseObject planToCaseObject = planToCaseObjectDAO.getByPlanIdAndIssueId(currentPlanId, issueId);
        planToCaseObject.setPlanId(newPlanId);

        List<PlanToCaseObject> issuesInPlan = planToCaseObjectDAO.getSortedListByPlanId(newPlanId);

        if (issuesInPlan.contains(planToCaseObject)){
            return error(En_ResultStatus.ALREADY_EXIST);
        }

        int maxOrderNumber = issuesInPlan.isEmpty() ? 0 : issuesInPlan.get(issuesInPlan.size()-1).getOrderNumber();

        planToCaseObject.setOrderNumber(maxOrderNumber + 10);

        planToCaseObjectDAO.merge(planToCaseObject);

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

    private boolean validatePlanToCaseObjects (List<PlanToCaseObject> list){
        return list.stream().noneMatch(planToCaseObject ->
                           planToCaseObject.getPlanId() == null
                        || planToCaseObject.getCaseObjectId() == null
                        || planToCaseObject.getId() == null
                        || planToCaseObject.getOrderNumber() == null);
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

    private Long saveToDB(Plan plan) {
        Long id;
        try {
            id = planDAO.persist(plan);
            if (id == null) {
                log.error("saveToDB(): failed to save plan to the db");
                return null;
            }
            jdbcManyRelationsHelper.persist(plan, "issueOrderList");
        } catch (Exception e) {
            log.error("saveToDB(): failed to save plan to the db", e);
            return null;
        }
        return id;
    }
}
