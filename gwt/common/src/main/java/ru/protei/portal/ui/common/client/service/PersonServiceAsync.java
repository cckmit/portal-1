package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.view.PersonShortView;

import java.util.List;

/**
 * Асинхронный сервис управления person
 */
public interface PersonServiceAsync {

    /**
     * Получение списка сокращенного представления сотрудника
     * @param callback
     */
    void getPersonViewList( AsyncCallback< List< PersonShortView > > callback );
}
