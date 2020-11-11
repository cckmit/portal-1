package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.ent.DutyLogFilter;
import ru.protei.portal.core.model.ent.SelectorsParams;
import ru.protei.portal.core.model.query.DutyLogQuery;
import ru.protei.portal.core.model.view.FilterShortView;

import java.util.List;

public interface DutyLogFilterControllerAsync {
    void getShortViewList(AsyncCallback<List<FilterShortView>> async);

    void getFilter(Long id, AsyncCallback<DutyLogFilter> async);

    void getSelectorsParams(DutyLogQuery caseQuery, AsyncCallback<SelectorsParams> async);

    void saveFilter(DutyLogFilter filter, AsyncCallback<DutyLogFilter> async);

    void removeFilter(Long id, AsyncCallback<Long> async);
}
