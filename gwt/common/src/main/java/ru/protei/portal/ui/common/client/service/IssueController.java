package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.struct.CaseNameAndDescriptionChangeRequest;
import ru.protei.portal.core.model.struct.CaseObjectMetaJira;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.core.model.util.UiResult;
import ru.protei.portal.core.model.view.PlanOption;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.List;
import java.util.Set;

/**
 * Сервис управления контактами
 */
@RemoteServiceRelativePath( "springGwtServices/IssueController" )
public interface IssueController extends RemoteService {

    SearchResult<CaseShortView> getIssues(CaseQuery query) throws RequestFailedException;

    CaseObject getIssue(long id) throws RequestFailedException;

    CaseObjectMetaNotifiers getIssueMetaNotifiers(long id) throws RequestFailedException;

    void saveIssueNameAndDescription(CaseNameAndDescriptionChangeRequest changeRequest) throws RequestFailedException;

    CaseObjectMeta updateIssueMeta(CaseObjectMeta caseMeta) throws RequestFailedException;

    CaseObjectMetaNotifiers updateIssueMetaNotifiers(CaseObjectMetaNotifiers caseMetaNotifiers) throws RequestFailedException;

    CaseObjectMetaJira updateIssueMetaJira(CaseObjectMetaJira caseMetaJira) throws RequestFailedException;

    CaseInfo getIssueShortInfo(Long caseNumber) throws RequestFailedException;

    UiResult<Long> createIssue(CaseObjectCreateRequest p) throws RequestFailedException;

    void updateManagerOfIssue(long issueId, long personId) throws RequestFailedException;

    Set<PlanOption> updatePlans(Set<PlanOption> plans, Long caseId) throws RequestFailedException;

    Boolean removeFavoriteState(Long personId, Long issueId) throws RequestFailedException;

    Long addFavoriteState(Long personId, Long issueId) throws RequestFailedException;

    UiResult<Long> createSubtask(CaseObjectCreateRequest createRequest, Long parentCaseId) throws RequestFailedException;
}
