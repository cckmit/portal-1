package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.dict.En_CaseFilterType;
import ru.protei.portal.core.model.dto.CaseFilterDto;
import ru.protei.portal.core.model.ent.SelectorsParams;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.view.CaseFilterShortView;

import java.util.List;

public interface IssueFilterControllerAsync {
    /**
     * Получение списка сокращенного представления CaseFilter
     */
    void getIssueFilterShortViewList( En_CaseFilterType filterType, AsyncCallback< List< CaseFilterShortView > > async );

    void getIssueFilter( Long id, AsyncCallback<CaseFilterDto<CaseQuery>> async );

    void getSelectorsParams( CaseQuery caseQuery, AsyncCallback<SelectorsParams> async );

    void saveIssueFilter( CaseFilterDto<CaseQuery> filter, AsyncCallback< CaseFilterDto<CaseQuery> > async );

    void removeIssueFilter( Long id, AsyncCallback< Long > async );
}
