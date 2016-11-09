package ru.protei.portal.ui.issue.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.Person;

import java.util.List;

/**
 * Асинхронный сервис управления обращениями
 */
public interface IssueServiceAsync {

    /**
     * Получение списка компаний
     * @param searchPattern шаблон поиска
     * @param companyId id компании
     * @param fired признак уволенности
     * @param sortField поле для сортировки
     * @param sortDir направление сортировки
     * @return список контактов
     */
    void getIssues( String searchPattern, Long companyId, Boolean fired, En_SortField sortField, Boolean sortDir, AsyncCallback< List< CaseObject > > async );

    void getIssue( long id, AsyncCallback< CaseObject > callback );

    void saveIssue( CaseObject p, AsyncCallback< CaseObject > callback );
}
