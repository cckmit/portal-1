package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.ent.PersonAbsence;

import java.util.Date;

/**
 * Асинхронный сервис управления отсутствиями
 */
public interface AbsenceControllerAsync {

    /**
     * Получение записи об отсутствии
     */
    void getAbsence(Long id, AsyncCallback<PersonAbsence> callback);

    /**
     * Сохранение записи об отсутствии
     */
    void saveAbsence(PersonAbsence absence, AsyncCallback<Long> callback);
}
