package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.ent.PersonAbsence;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.Date;

/**
 * Сервис управления отсутствиями
 */
@RemoteServiceRelativePath( "springGwtServices/AbsenceController" )
public interface AbsenceController extends RemoteService {
    /**
     * Создание отсутствия
     */
    Long createAbsence(PersonAbsence absence) throws RequestFailedException;

    /**
     * Проверка на существование отсутствия
     */
    Boolean isExistsAbsence(Long employeeId, Date dateFrom, Date dateTill, Long excludeId) throws RequestFailedException;

}
