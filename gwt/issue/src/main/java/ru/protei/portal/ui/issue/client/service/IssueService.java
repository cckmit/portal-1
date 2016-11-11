package ru.protei.portal.ui.issue.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.List;

/**
 * Сервис управления контактами
 */
@RemoteServiceRelativePath( "springGwtServices/IssueService" )
public interface IssueService extends RemoteService {

    List< CaseObject > getIssues( CaseQuery query ) throws RequestFailedException;

    CaseObject getIssue( long id );

    CaseObject saveIssue( CaseObject p );

    /**
     * Получение списка статусов
     * @return список статусов
     */
    List<En_CaseState> getStateList() throws RequestFailedException;
}
