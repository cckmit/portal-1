package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.query.PersonQuery;
import ru.protei.portal.core.model.view.PersonShortView;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Асинхронный сервис управления person
 */
public interface PersonControllerAsync {

    /**
     * Получение списка сокращенного представления сотрудника
     * @param query запрос
     * @param callback
     */
    void getPersonViewList( PersonQuery query, AsyncCallback< List< PersonShortView > > callback );

    void getPersonNames(Collection<Long> ids, AsyncCallback<Map<Long, String>> async);

    void getPerson(Long id, AsyncCallback<Person> async);
}
