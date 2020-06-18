package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.ent.PersonAbsence;
import ru.protei.portal.core.model.query.AbsenceQuery;

import java.util.List;

/**
 * Асинхронный сервис управления отсутствиями
 */
public interface AbsenceControllerAsync {

    /**
     * Получение записей об отсутствиях
     */
    void getAbsences(AbsenceQuery query, AsyncCallback<List<PersonAbsence>> async);

    /**
     * Получение записи об отсутствии
     */
    void getAbsence(Long id, AsyncCallback<PersonAbsence> callback);

    /**
     * Сохранение записи об отсутствии
     */
    void saveAbsence(PersonAbsence absence, AsyncCallback<Long> callback);

    /**
     * Удаление записи об отсутствии
     */
    void removeAbsence(Long id, AsyncCallback<Boolean> callback);

    /**
     * Завершение отсутствия
     */
    void completeAbsence(Long id, AsyncCallback<Boolean> callback);
}
