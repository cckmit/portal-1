package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.ent.AbsenceFilter;
import ru.protei.portal.core.model.ent.SelectorsParams;
import ru.protei.portal.core.model.query.AbsenceQuery;
import ru.protei.portal.core.model.view.FilterShortView;

import java.util.List;

public interface AbsenceFilterControllerAsync {
    void getShortViewList(AsyncCallback<List<FilterShortView>> async);

    void getFilter(Long id, AsyncCallback<AbsenceFilter> async);

    void getSelectorsParams(AbsenceQuery caseQuery, AsyncCallback<SelectorsParams> async);

    void saveFilter(AbsenceFilter filter, AsyncCallback<AbsenceFilter> async);

    void removeFilter(Long id, AsyncCallback<Long> async);
}
