package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.struct.CaseNameAndDescriptionChangeRequest;
import ru.protei.portal.core.model.struct.CaseObjectMetaJira;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.winter.core.utils.beans.SearchResult;

/**
 * Асинхронный сервис управления обращениями
 */
public interface IssueControllerAsync {

    void getIssues(CaseQuery query, AsyncCallback<SearchResult<CaseShortView>> async);

    void getIssue( long id, AsyncCallback< CaseObject > callback );

    void createIssue(CaseObjectCreateRequest p, AsyncCallback<Long> callback);

    void saveIssueNameAndDescription(CaseNameAndDescriptionChangeRequest changeRequest, AsyncCallback<Void> callback);

    void updateIssueMeta( CaseObjectMeta caseMeta, AsyncCallback<CaseObjectMeta> async);

    void updateIssueMetaNotifiers( CaseObjectMetaNotifiers caseMetaNotifiers, AsyncCallback<CaseObjectMetaNotifiers> async);

    void updateIssueMetaJira( CaseObjectMetaJira caseMetaJira, AsyncCallback<CaseObjectMetaJira> async);

    void getIssueShortInfo(Long caseNumber, AsyncCallback<CaseInfo> async);

}
