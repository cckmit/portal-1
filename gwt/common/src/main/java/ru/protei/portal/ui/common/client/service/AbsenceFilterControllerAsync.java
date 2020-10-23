package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.ent.AbsenceFilter;
import ru.protei.portal.core.model.view.AbsenceFilterShortView;

import java.util.List;

public interface AbsenceFilterControllerAsync {
    void getShortViewList(AsyncCallback<List<AbsenceFilterShortView>> async);

    void getFilter(Long id, AsyncCallback<AbsenceFilter> async);

    void saveFilter(AbsenceFilter filter, AsyncCallback<AbsenceFilter> async);

    void removeFilter(Long id, AsyncCallback<Long> async);
}
