package ru.protei.portal.ui.issue.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.List;

/**
 * Асинхронный сервис управления обращениями
 */
public interface IssueServiceAsync {

    void getIssues( CaseQuery query, AsyncCallback< List< CaseObject > > async );

    void getIssue( long id, AsyncCallback< CaseObject > callback );

    void saveIssue( CaseObject p, AsyncCallback< Boolean > callback );

    /**
     * Получение списка статусов
     * @return список статусов
     */
    void getStateList(AsyncCallback<List<En_CaseState>> async);

    void getIssuesCount( CaseQuery query, AsyncCallback<Long> callback );

    /**
     * Получение списка комментариев по обращению
     * @param caseId
     * @param async
     */
    void getIssueComments( Long caseId, AsyncCallback<List<CaseComment>> async );

    /**
     * Удаление комментария обращения
     * @param value
     * @param async
     */
    void removeIssueComment( CaseComment value, AsyncCallback<Void> async );

    /**
     * Редактирование комментария обращения
     * @param value
     * @param async
     */
    void editIssueComment( CaseComment value, AsyncCallback<CaseComment> async );
}
