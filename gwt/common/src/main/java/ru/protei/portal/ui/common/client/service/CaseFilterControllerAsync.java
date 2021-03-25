package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.dict.En_CaseFilterType;
import ru.protei.portal.core.model.dto.CaseFilterDto;
import ru.protei.portal.core.model.ent.SelectorsParams;
import ru.protei.portal.core.model.query.HasFilterQueryIds;
import ru.protei.portal.core.model.view.FilterShortView;

import java.util.List;

public interface CaseFilterControllerAsync {
    /**
     * Получение списка сокращенного представления CaseFilter
     */
    void getCaseFilterShortViewList(En_CaseFilterType filterType, AsyncCallback< List<FilterShortView> > async );

    <T extends HasFilterQueryIds> void getCaseFilter(Long id, AsyncCallback<CaseFilterDto<T>> async );

    void getSelectorsParams(HasFilterQueryIds filterEntityIds, AsyncCallback<SelectorsParams> async);

    <T extends HasFilterQueryIds> void saveCaseFilter(CaseFilterDto<T> filter, AsyncCallback< CaseFilterDto<T> > async );

    void removeCaseFilter(Long id, AsyncCallback< Long > async );
}
