package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.struct.CaseNameAndDescriptionChangeRequest;
import ru.protei.portal.core.model.struct.CaseObjectMetaJira;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.core.model.util.UiResult;
import ru.protei.portal.core.model.view.PlanOption;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.List;
import java.util.Set;

/**
 * Асинхронный сервис управления обращениями
 */
public interface IssueControllerAsync {

    void getIssues(CaseQuery query, AsyncCallback<SearchResult<CaseShortView>> async);

    void getIssue( long id, AsyncCallback< CaseObject > callback );

    void createIssue(CaseObjectCreateRequest p, AsyncCallback<UiResult<Long>> callback);

    void saveIssueNameAndDescription(CaseNameAndDescriptionChangeRequest changeRequest, AsyncCallback<Void> callback);

    void updateIssueMeta( CaseObjectMeta caseMeta, AsyncCallback<CaseObjectMeta> async);

    void updateIssueMetaNotifiers( CaseObjectMetaNotifiers caseMetaNotifiers, AsyncCallback<CaseObjectMetaNotifiers> async);

    void updateIssueMetaJira( CaseObjectMetaJira caseMetaJira, AsyncCallback<CaseObjectMetaJira> async);

    void getIssueShortInfo(Long caseNumber, AsyncCallback<CaseInfo> async);

    void updateManagerOfIssue(long issueId, long personId, AsyncCallback<Void> async);

    void updatePlans(Set<PlanOption> plans, Long caseId, AsyncCallback<Set<PlanOption>> async);

    void getPersonFavoritesIssueIds(Long personId, AsyncCallback<List<Long>> async);

    void removeFavoriteState(Long personId, Long issueId, AsyncCallback<Boolean> async);

    void addFavoriteState(Long personId, Long issueId, AsyncCallback<Long> async);
}
