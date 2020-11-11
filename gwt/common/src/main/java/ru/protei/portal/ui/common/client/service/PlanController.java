package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.ent.Plan;
import ru.protei.portal.core.model.query.PlanQuery;
import ru.protei.portal.core.model.view.PlanOption;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.List;

@RemoteServiceRelativePath( "springGwtServices/PlanController" )
public interface PlanController extends RemoteService {

    SearchResult<Plan> getPlanList(PlanQuery query) throws RequestFailedException;

    Plan getPlanWithIssues(Long planId) throws RequestFailedException;

    Long createPlan(Plan plan) throws RequestFailedException;

    List<PlanOption> getPlanOptionList(PlanQuery query) throws RequestFailedException;

    List<Plan> listPlans(PlanQuery query) throws RequestFailedException;

    Plan addIssueToPlan(Long planId, Long issueId) throws RequestFailedException;

    Boolean moveIssueToAnotherPlan(Long currentPlanId, Long issueId, Long newPlanId) throws RequestFailedException;

    Long removeIssueFromPlan(Long planId, Long issueId) throws RequestFailedException;

    Boolean changeIssuesOrder(Plan plan) throws RequestFailedException;

    Boolean editPlanParams(Plan plan) throws RequestFailedException;

    Long removePlan(Long planId) throws RequestFailedException;
}
