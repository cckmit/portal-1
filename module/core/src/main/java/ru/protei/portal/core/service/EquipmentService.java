package ru.protei.portal.core.service;

import org.apache.commons.fileupload.FileItem;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.annotations.Auditable;
import ru.protei.portal.core.model.annotations.Privileged;
import ru.protei.portal.core.model.dict.En_AuditType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.DecimalNumber;
import ru.protei.portal.core.model.ent.Document;
import ru.protei.portal.core.model.ent.Equipment;
import ru.protei.portal.core.model.query.DocumentQuery;
import ru.protei.portal.core.model.query.EquipmentQuery;
import ru.protei.portal.core.model.struct.DecimalNumberQuery;
import ru.protei.portal.core.model.view.EquipmentShortView;

import java.util.List;

/**
 * Сервис управления оборудованием
 */
public interface EquipmentService {

    @Privileged( En_Privilege.EQUIPMENT_VIEW )
    CoreResponse< Long > count(AuthToken token, EquipmentQuery query );

    @Privileged( En_Privilege.EQUIPMENT_VIEW )
    CoreResponse< List< Equipment > > equipmentList( AuthToken token, EquipmentQuery query );

    CoreResponse< List< EquipmentShortView > > shortViewList( AuthToken token, EquipmentQuery query );

    @Privileged( En_Privilege.EQUIPMENT_VIEW )
    CoreResponse< Equipment > getEquipment( AuthToken token, long id );

    @Privileged( requireAny = { En_Privilege.EQUIPMENT_CREATE, En_Privilege.EQUIPMENT_EDIT })
    @Auditable( En_AuditType.EQUIPMENT_MODIFY )
    CoreResponse< Equipment > saveEquipment( AuthToken token, Equipment equipment );

    @Privileged( requireAny = { En_Privilege.EQUIPMENT_CREATE, En_Privilege.EQUIPMENT_EDIT })
    CoreResponse< Integer > getNextAvailableDecimalNumber( AuthToken token, DecimalNumberQuery filter );

    @Privileged( requireAny = { En_Privilege.EQUIPMENT_CREATE, En_Privilege.EQUIPMENT_EDIT })
    CoreResponse< Integer > getNextAvailableDecimalNumberModification( AuthToken token, DecimalNumberQuery filter );

    CoreResponse< Boolean > checkIfExistDecimalNumber( DecimalNumber number );

    CoreResponse< DecimalNumber > findDecimalNumber(AuthToken token, DecimalNumber number);

    @Privileged( requireAny = { En_Privilege.EQUIPMENT_CREATE, En_Privilege.EQUIPMENT_EDIT })
    @Auditable( En_AuditType.EQUIPMENT_COPY )
    CoreResponse<Long> copyEquipment( AuthToken token, Long equipmentId, String newName, Long authorId );

    @Privileged( En_Privilege.EQUIPMENT_REMOVE )
    @Auditable( En_AuditType.EQUIPMENT_REMOVE )
    CoreResponse<Boolean> removeEquipment( AuthToken token, Long equipmentId );


    @Privileged(En_Privilege.EQUIPMENT_VIEW)
    CoreResponse<List<Document>> documentList(AuthToken token, String decimalNumber);

    @Privileged(En_Privilege.EQUIPMENT_VIEW)
    CoreResponse<Document> getDocument(AuthToken token, Long id);

    @Privileged(requireAny = {En_Privilege.EQUIPMENT_CREATE, En_Privilege.EQUIPMENT_EDIT})
    CoreResponse<Document> createDocument(AuthToken token, Document document, FileItem fileItem);

    @Privileged(requireAny = {En_Privilege.EQUIPMENT_CREATE, En_Privilege.EQUIPMENT_EDIT})
    @Auditable(En_AuditType.DOCUMENT_MODIFY)
    CoreResponse<Document> updateDocument(AuthToken token, Document document);

    @Privileged(requireAny = {En_Privilege.EQUIPMENT_CREATE, En_Privilege.EQUIPMENT_EDIT})
    @Auditable(En_AuditType.DOCUMENT_MODIFY)
    CoreResponse<Document> updateDocumentAndContent(AuthToken token, Document document, FileItem fileItem);
}
