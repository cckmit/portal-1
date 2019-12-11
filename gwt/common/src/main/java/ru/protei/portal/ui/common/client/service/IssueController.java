package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.struct.CaseNameAndDescriptionChangeRequest;
import ru.protei.portal.core.model.struct.CaseObjectMetaJira;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.List;

/**
 * Сервис управления контактами
 */
@RemoteServiceRelativePath( "springGwtServices/IssueController" )
public interface IssueController extends RemoteService {

    SearchResult<CaseShortView> getIssues(CaseQuery query) throws RequestFailedException;

    CaseObject getIssue( long id ) throws RequestFailedException;

    void saveIssueNameAndDescription(CaseNameAndDescriptionChangeRequest changeRequest) throws RequestFailedException;

    CaseObjectMeta updateIssueMeta( CaseObjectMeta caseMeta ) throws RequestFailedException;

    CaseObjectMetaNotifiers updateIssueMetaNotifiers( CaseObjectMetaNotifiers caseMetaNotifiers ) throws RequestFailedException;

    CaseObjectMetaJira updateIssueMetaJira( CaseObjectMetaJira caseMetaJira ) throws RequestFailedException;

    CaseInfo getIssueShortInfo(Long caseNumber) throws RequestFailedException;

    Long createIssue(IssueCreateRequest p) throws RequestFailedException;
}
