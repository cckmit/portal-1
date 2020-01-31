package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.dict.En_CaseFilterType;
import ru.protei.portal.core.model.ent.CaseFilter;
import ru.protei.portal.core.model.ent.SelectorsParams;
import ru.protei.portal.core.model.view.CaseFilterShortView;

import java.util.List;

public interface IssueFilterControllerAsync {
    /**
     * Получение списка сокращенного представления CaseFilter
     */
    void getIssueFilterShortViewList( En_CaseFilterType filterType, AsyncCallback< List< CaseFilterShortView > > async );

    void getIssueFilter( Long id, AsyncCallback<SelectorsParams> async );

    void saveIssueFilter( CaseFilter filter, AsyncCallback< CaseFilter > async );

    void removeIssueFilter( Long id, AsyncCallback< Boolean > async );
}
