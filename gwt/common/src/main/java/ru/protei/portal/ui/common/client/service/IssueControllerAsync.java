package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.CaseInfo;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.view.CaseShortView;

import java.util.List;

/**
 * Асинхронный сервис управления обращениями
 */
public interface IssueControllerAsync {

    void getIssues( CaseQuery query, AsyncCallback< List<CaseShortView> > async );

    void getIssue( long id, AsyncCallback< CaseObject > callback );

    void saveIssue( CaseObject p, AsyncCallback< CaseObject > callback );

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
     */
    void removeIssueComment(CaseComment value, AsyncCallback<Void> async);

    /**
     * Редактирование комментария обращения
     */
    void editIssueComment(CaseComment comment, AsyncCallback<CaseComment> async);

    void getIssueShortInfo(Long caseNumber, AsyncCallback<CaseInfo> async);
}
