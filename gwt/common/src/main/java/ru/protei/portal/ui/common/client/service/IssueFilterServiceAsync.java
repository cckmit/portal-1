package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.ent.IssueFilter;
import ru.protei.portal.core.model.view.IssueFilterShortView;

import java.util.List;

public interface IssueFilterServiceAsync {
    /**
     * Получение списка сокращенного представления IssueFilter
     */
    void getIssueFilterShortViewListByCurrentUser( AsyncCallback< List< IssueFilterShortView > > async );

    void getIssueFilter( Long id, AsyncCallback< IssueFilter > async );

    void saveIssueFilter( IssueFilter filter, AsyncCallback< IssueFilter > async );

    void removeIssueFilter( Long id, AsyncCallback< Boolean > async );
}
