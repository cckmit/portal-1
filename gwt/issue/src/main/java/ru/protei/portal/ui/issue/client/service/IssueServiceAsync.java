package ru.protei.portal.ui.issue.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.query.CaseQuery;

import java.util.List;

/**
 * Асинхронный сервис управления обращениями
 */
public interface IssueServiceAsync {

    void getIssues( CaseQuery query, AsyncCallback< List< CaseObject > > async );

    void getIssue( long id, AsyncCallback< CaseObject > callback );

    void saveIssue( CaseObject p, AsyncCallback< CaseObject > callback );
}
