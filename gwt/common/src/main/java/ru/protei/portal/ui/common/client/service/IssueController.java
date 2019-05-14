package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.ent.CaseInfo;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.query.CaseQuery;
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

    CaseObject saveIssue( CaseObject p ) throws RequestFailedException;

    /**
     * Получение списка статусов
     * @return список статусов
     */
    List<En_CaseState> getStateList() throws RequestFailedException;

    CaseInfo getIssueShortInfo(Long caseNumber) throws RequestFailedException;
}
