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
     * Получение записи об отсутствии
     */
    PersonAbsence getAbsence(Long id) throws RequestFailedException;

    /**
     * Сохранение записи об отсутствии
     */
    Long saveAbsence(PersonAbsence absence) throws RequestFailedException;
}
