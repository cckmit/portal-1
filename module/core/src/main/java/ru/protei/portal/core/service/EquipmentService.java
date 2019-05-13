package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.annotations.Auditable;
import ru.protei.portal.core.model.annotations.Privileged;
import ru.protei.portal.core.model.dict.En_AuditType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.DecimalNumber;
import ru.protei.portal.core.model.ent.Equipment;
import ru.protei.portal.core.model.query.EquipmentQuery;
import ru.protei.portal.core.model.struct.DecimalNumberQuery;
import ru.protei.portal.core.model.view.EquipmentShortView;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.List;

/**
 * Сервис управления оборудованием
 */
public interface EquipmentService {

    @Privileged(En_Privilege.EQUIPMENT_VIEW)
    CoreResponse<SearchResult<Equipment>> getEquipments(AuthToken token, EquipmentQuery query);

    CoreResponse< List< EquipmentShortView > > shortViewList( AuthToken token, EquipmentQuery query );

    @Privileged( En_Privilege.EQUIPMENT_VIEW )
    CoreResponse< Equipment > getEquipment( AuthToken token, long id );

    @Privileged( En_Privilege.EQUIPMENT_VIEW )
    CoreResponse< List<DecimalNumber> > getDecimalNumbersOfEquipment( AuthToken token, long id );

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
}
