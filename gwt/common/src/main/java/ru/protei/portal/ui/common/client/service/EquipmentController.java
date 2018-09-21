package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.ent.DecimalNumber;
import ru.protei.portal.core.model.ent.Document;
import ru.protei.portal.core.model.ent.Equipment;
import ru.protei.portal.core.model.query.EquipmentQuery;
import ru.protei.portal.core.model.struct.DecimalNumberQuery;
import ru.protei.portal.core.model.view.EquipmentShortView;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.List;

/**
 * Сервис управления оборудованием
 */
@RemoteServiceRelativePath( "springGwtServices/EquipmentController" )
public interface EquipmentController extends RemoteService {

    /**
     * Получение списка контактов компании
     * @param query запрос
     * @return список контактов
     */
    List< Equipment > getEquipments( EquipmentQuery query ) throws RequestFailedException;

    List< EquipmentShortView > equipmentOptionList( EquipmentQuery query ) throws RequestFailedException;

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
     * Поиск децимального номера
     */
    DecimalNumber findDecimalNumber(DecimalNumber number) throws RequestFailedException;

    /**
     * Получение следующего доступного номера
     *
     * @param filter
     */
    Integer getNextAvailableRegisterNumber( DecimalNumberQuery filter ) throws RequestFailedException;

    Integer getNextAvailableRegisterNumberModification( DecimalNumberQuery filter ) throws RequestFailedException;


    List<Document> getDocuments(List<String> decimalNumbers) throws RequestFailedException;

    Document getDocument(Long id) throws RequestFailedException;

    Document saveDocument(Document document) throws RequestFailedException;
}
