package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.query.PersonQuery;
import ru.protei.portal.core.model.view.PersonShortView;

import java.util.List;

/**
 * Асинхронный сервис управления person
 */
public interface PersonServiceAsync {

    /**
     * Получение списка сокращенного представления сотрудника
     * @param query запрос
     * @param callback
     */
    void getPersonViewList( PersonQuery query, AsyncCallback< List< PersonShortView > > callback );
}
