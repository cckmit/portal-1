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
import ru.protei.portal.core.model.dict.En_HistoryValueType;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.Plan;
import ru.protei.portal.core.model.ent.PlanToCaseObject;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.query.PlanQuery;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.winter.core.utils.beans.SearchResult;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Autowired
    HistoryService historyService;

    @Override
    public Result<SearchResult<Plan>> getPlans(AuthToken token, PlanQuery query) {
        SearchResult<Plan> sr = planDAO.getSearchResultByQuery(query);

        if ( CollectionUtils.isEmpty(sr.getResults())) {
            return ok(sr);
        }

        Map<Long, Long> map = planToCaseObjectDAO.countByPlanIds(sr.getResults().stream()
                .map(Plan::getId)
                .collect(Collectors.toList()));

        sr.getResults().forEach(plan -> {
            Long count = map.getOrDefault(plan.getId(), 0L);
            plan.setIssuesCount(count);
        });

        return ok(sr);
    }

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
                historyService.createHistory(token, issue.getId(), En_HistoryValueType.ADD_TO_PLAN, null, String.valueOf(planId));
            });
        }

        return ok(planId);
    }

    @Override
    public Result<Boolean> editPlanParams(AuthToken token, Plan plan) {
        if (plan == null || plan.getId() == null || !validatePlan(plan)){
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        if (!planDAO.partialMerge(plan, "name", "start_date", "finish_date")){
            return error(En_ResultStatus.NOT_UPDATED);
        }

        return ok();
    }

    @Override
    @Transactional
    public Result<Plan> addIssueToPlan(AuthToken token, Long planId, Long issueId) {
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

        historyService.createHistory(token, issueId, En_HistoryValueType.ADD_TO_PLAN, null, String.valueOf(planId));

        return getPlanWithIssues(token, planId);
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
            historyService.createHistory(token, issueId, En_HistoryValueType.REMOVE_FROM_PLAN, String.valueOf(planId), null);
            return ok();
        }

        if (rowCount == 0) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        throw new RollbackTransactionException("removeIssueFromPlan(): rollback transaction");
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

        historyService.createHistory(token, issueId, En_HistoryValueType.CHANGE_PLAN, String.valueOf(currentPlanId), String.valueOf(newPlanId));

        return ok();
    }

    @Override
    public Result<Boolean> removePlan(AuthToken token, Long planId) {
        if (planId == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        Plan plan = planDAO.get(planId);
        if (plan != null && plan.getIssueList() != null){
            plan.getIssueList().forEach(issue ->{
                historyService.createHistory(token, issue.getId(), En_HistoryValueType.REMOVE_FROM_PLAN, planId.toString(), null);
            });
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

        if (plan.getStartDate() == null) {
            return false;
        }

        if (plan.getFinishDate() == null) {
            return false;
        }

        if (plan.getStartDate().after(plan.getFinishDate())) {
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
        planToCaseObjectDAO.mergeBatch(sortedListByPlanId);
    }
}
