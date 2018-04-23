package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.ent.CaseFilter;
import ru.protei.portal.core.model.view.CaseFilterShortView;

import java.util.List;

public interface IssueFilterServiceAsync {
    /**
     * Получение списка сокращенного представления CaseFilter
     */
    void getIssueFilterShortViewListByCurrentUser( AsyncCallback< List< CaseFilterShortView > > async );

    void getIssueFilter( Long id, AsyncCallback< CaseFilter > async );

    void saveIssueFilter( CaseFilter filter, AsyncCallback< CaseFilter > async );

    void removeIssueFilter( Long id, AsyncCallback< Boolean > async );
}
