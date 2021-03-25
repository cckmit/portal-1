package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.dict.En_CaseFilterType;
import ru.protei.portal.core.model.dto.CaseFilterDto;
import ru.protei.portal.core.model.ent.SelectorsParams;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.view.CaseFilterShortView;
import ru.protei.portal.core.model.view.filterwidget.AbstractFilterShortView;
import ru.protei.portal.core.model.view.filterwidget.DtoFilterQuery;
import ru.protei.portal.core.model.view.filterwidget.FilterQuery;

import java.util.List;

public interface IssueFilterControllerAsync {
    /**
     * Получение списка сокращенного представления CaseFilter
     */
    void getIssueFilterShortViewList( En_CaseFilterType filterType, AsyncCallback< List<AbstractFilterShortView> > async );

    <T extends DtoFilterQuery> void getIssueFilter(Long id, AsyncCallback<CaseFilterDto<T>> async );

    void getSelectorsParams( CaseQuery caseQuery, AsyncCallback<SelectorsParams> async );

    <T extends DtoFilterQuery> void saveIssueFilter( CaseFilterDto<T> filter, AsyncCallback< CaseFilterDto<T> > async );

    void removeIssueFilter( Long id, AsyncCallback< Long > async );
}
