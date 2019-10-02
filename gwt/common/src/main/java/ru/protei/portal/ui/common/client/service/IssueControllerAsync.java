package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.CaseInfo;
import ru.protei.portal.core.model.ent.CaseLink;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.struct.CaseObjectWithCaseComment;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.List;

/**
 * Асинхронный сервис управления обращениями
 */
public interface IssueControllerAsync {

    void getIssues(CaseQuery query, AsyncCallback<SearchResult<CaseShortView>> async);

    void getIssue( long id, AsyncCallback< CaseObject > callback );

    void createIssue( CaseObject p, AsyncCallback<CaseObject> async );
    void saveIssueAndComment(CaseObject p, CaseComment c, AsyncCallback<CaseObjectWithCaseComment> callback );

    /**
     * Получение списка статусов
     * @return список статусов
     */
    void getStateList(AsyncCallback<List<En_CaseState>> async);

    void getIssueShortInfo(Long caseNumber, AsyncCallback<CaseInfo> async);

    void getCaseLinks( Long caseId, AsyncCallback<List<CaseLink>> async );
}
