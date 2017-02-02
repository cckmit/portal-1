package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.ent.Equipment;
import ru.protei.portal.core.model.query.EquipmentQuery;
import ru.protei.portal.ui.common.shared.model.DecimalNumber;

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
    void getEquipmentCount( EquipmentQuery query, AsyncCallback<Long> async );

    /**
     * Проверка валидности заданного децимального номера
     * @param number
     * @param requestCallback
     */
    void checkIfExistDecimalNumber( DecimalNumber number, AsyncCallback<Boolean> requestCallback );
}
