package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.ent.Plan;
import ru.protei.portal.core.model.query.PlanQuery;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.List;

public interface PlanControllerAsync {

    void getPlanList(PlanQuery query, AsyncCallback<SearchResult<Plan>> async);

    void getPlanWithIssues(Long planId, AsyncCallback<Plan> async);

    void createPlan(Plan plan, AsyncCallback<Long> async);

    void listPlans(PlanQuery query, AsyncCallback<List<Plan>> async);

    void addIssueToPlan(Long planId, Long issueId, AsyncCallback<Plan> async);

    void moveIssueToAnotherPlan(Long currentPlanId, Long issueId, Long newPlanId, AsyncCallback<Boolean> async);

    void removeIssueFromPlan(Long planId, Long issueId, AsyncCallback<Boolean> async);

    void changeIssuesOrder(Plan plan, AsyncCallback<Boolean> async);

    void editPlanParams(Plan plan, AsyncCallback<Boolean> async);
}
