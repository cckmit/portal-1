package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.ent.Equipment;
import ru.protei.portal.core.model.query.EquipmentQuery;
import ru.protei.portal.core.model.ent.DecimalNumber;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.List;

/**
 * Асинхронный сервис управления оборудованием
 */
public interface EquipmentServiceAsync {

    /**
     * Получение списка оборудования
     */
    void getEquipments( EquipmentQuery query, AsyncCallback<List<Equipment>> async );

    /**
     * Получение данных об оборудовании
     */
    void getEquipment( long id, AsyncCallback<Equipment > callback );

    /**
     * Сохранение изменения
     */
    void saveEquipment( Equipment p, AsyncCallback<Equipment> callback );

    /**
     * Получение счетчика по оборудованию
     */
    void getEquipmentCount( EquipmentQuery query, AsyncCallback<Long> callback );

    /**
     * Проверка валидности заданного децимального номера
     * @param number
     */
    void checkIfExistDecimalNumber( DecimalNumber number, AsyncCallback<Boolean> callback );

    /**
     * Получение следующего доступного номера
     * @param number
     * @param callback
     */
    void getNextAvailableRegisterNumber( DecimalNumber number, AsyncCallback<DecimalNumber> callback );

    void getNextAvailableRegNumberNotContainsInList(List<Integer> regNumbers, String classifierCode, String orgCode, AsyncCallback<DecimalNumber> callback);

    void getNextAvailableRegisterNumberModification( DecimalNumber number, AsyncCallback<DecimalNumber> callback );

    void copyEquipment( Long equipmentId, String newName, AsyncCallback<Long> async );

    void removeEquipment( Long equipmentId, AsyncCallback<Boolean> async );

    void getNextAvailableModificationNotContainsInList(List<Integer> mods, String classifierCode, String orgCode, String regNum, AsyncCallback<DecimalNumber> callback);
}
