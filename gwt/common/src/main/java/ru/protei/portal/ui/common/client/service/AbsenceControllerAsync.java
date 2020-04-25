package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.ent.PersonAbsence;

import java.util.Date;

/**
 * Асинхронный сервис управления отсутствиями
 */
public interface AbsenceControllerAsync {

    /**
     * Создание отсутствия
     */
    void createAbsence(PersonAbsence absence, AsyncCallback<Long> callback);

    /**
     * Проверка на существование отсутствия
     */
    void isExistsAbsence(Long employeeId, Date dateFrom, Date dateTill, Long excludeId, AsyncCallback<Boolean> callback);
}
