package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.ent.DecimalNumber;
import ru.protei.portal.core.model.ent.Document;
import ru.protei.portal.core.model.ent.Equipment;
import ru.protei.portal.core.model.query.EquipmentQuery;
import ru.protei.portal.core.model.struct.DecimalNumberQuery;
import ru.protei.portal.core.model.view.EquipmentShortView;

import java.util.List;

/**
 * Асинхронный сервис управления оборудованием
 */
public interface EquipmentControllerAsync {

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

    void getDecimalNumbersOfEquipment(long equipmentId, AsyncCallback<List<DecimalNumber>> async);

    /**
     * Проверка валидности заданного децимального номера
     * @param number
     */
    void checkIfExistDecimalNumber( DecimalNumber number, AsyncCallback<Boolean> callback );

    /**
     * Поиск децимального номера
     */
     void findDecimalNumber(DecimalNumber number, AsyncCallback<DecimalNumber> callback);

    /**
     * Получение следующего доступного номера
     * @param filter
     * @param callback
     */
    void getNextAvailableRegisterNumber( DecimalNumberQuery filter, AsyncCallback<Integer> callback );

    void getNextAvailableRegisterNumberModification( DecimalNumberQuery filter, AsyncCallback<Integer> callback );

    void copyEquipment( Long equipmentId, String newName, AsyncCallback<Long> async );

    void removeEquipment( Long equipmentId, AsyncCallback<Boolean> async );

    void equipmentOptionList( EquipmentQuery query, AsyncCallback< List< EquipmentShortView > > async );


    void getDocuments(Long equipmentId, AsyncCallback<List<Document>> async);

    void getDocument(Long id, AsyncCallback<Document> async);

    void saveDocument(Document document, AsyncCallback<Document> async);
}
