package ru.protei.portal.ui.contact.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.Person;

import java.util.List;

/**
 * Асинхронный сервис управления контактами
 */
public interface ContactServiceAsync {

    /**
     * Получение списка компаний
     * @param searchPattern шаблон поиска
     * @param companyId id компании
     * @param fired признак уволенности
     * @param sortField поле для сортировки
     * @param sortDir направление сортировки
     * @return список контактов
     */
    void getContacts( String searchPattern, Long companyId, Boolean fired, En_SortField sortField, Boolean sortDir, AsyncCallback< List< Person > > async );

    void getContact (long id, AsyncCallback<Person> callback);

    void saveContact (Person p, AsyncCallback<Person> callback);
}
