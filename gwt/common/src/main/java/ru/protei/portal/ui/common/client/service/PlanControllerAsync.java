package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.ent.Plan;
import ru.protei.portal.core.model.query.PlanQuery;
import ru.protei.winter.core.utils.beans.SearchResult;

public interface PlanControllerAsync {

    void getPlanList(PlanQuery query, AsyncCallback<SearchResult<Plan>> async);
}
