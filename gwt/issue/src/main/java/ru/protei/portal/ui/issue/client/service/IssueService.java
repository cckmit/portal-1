package ru.protei.portal.ui.issue.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.List;

/**
 * Сервис управления контактами
 */
@RemoteServiceRelativePath( "springGwtServices/IssueService" )
public interface IssueService extends RemoteService {

    /**
     * Получение списка компаний
     * @param searchPattern шаблон поиска
     * @param companyId id компании
     * @param fired признак уволенности
     * @param sortField поле для сортировки
     * @param sortDir направление сортировки
     * @return список контактов
     */
    List< CaseObject > getIssues( String searchPattern, Long companyId, Boolean fired, En_SortField sortField, Boolean sortDir );

    CaseObject getIssue( long id );

    CaseObject saveIssue( CaseObject p );
}
