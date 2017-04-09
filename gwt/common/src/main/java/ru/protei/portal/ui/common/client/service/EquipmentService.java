package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.ent.Equipment;
import ru.protei.portal.core.model.query.EquipmentQuery;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.portal.core.model.ent.DecimalNumber;

import java.util.List;

/**
 * Сервис управления оборудованием
 */
@RemoteServiceRelativePath( "springGwtServices/EquipmentService" )
public interface EquipmentService extends RemoteService {

    /**
     * Получение списка контактов компании
     * @param query запрос
     * @return список контактов
     */
    List< Equipment > getEquipments( EquipmentQuery query ) throws RequestFailedException;

    Equipment getEquipment( long id ) throws RequestFailedException;

    Equipment saveEquipment( Equipment p ) throws RequestFailedException;

    Long copyEquipment( Long equipmentId, String newName ) throws RequestFailedException;

    boolean removeEquipment( Long equipmentId ) throws RequestFailedException;

    Long getEquipmentCount( EquipmentQuery query ) throws RequestFailedException;

    /**
     * Проверка валидности заданного децимального номера
     */
    boolean checkIfExistDecimalNumber( DecimalNumber number ) throws RequestFailedException;

    /**
     * Получение следующего доступного номера
     *
     * @param number
     */
    DecimalNumber getNextAvailableRegisterNumber( DecimalNumber number ) throws RequestFailedException;

    DecimalNumber getNextAvailableRegisterNumberModification( DecimalNumber number ) throws RequestFailedException;
}
