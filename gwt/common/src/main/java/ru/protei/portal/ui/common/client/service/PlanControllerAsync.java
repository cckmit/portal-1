package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.ent.Plan;
import ru.protei.portal.core.model.query.PlanQuery;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PlanOption;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.List;

public interface PlanControllerAsync {

    void getPlanList(PlanQuery query, AsyncCallback<SearchResult<Plan>> async);

    void getPlanWithIssues(Long planId, AsyncCallback<Plan> async);

    void createPlan(Plan plan, AsyncCallback<Long> async);

    void getPlanOptionList(PlanQuery query, AsyncCallback<List<PlanOption>> async);
}
