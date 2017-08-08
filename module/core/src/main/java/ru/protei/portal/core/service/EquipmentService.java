package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.annotations.Auditable;
import ru.protei.portal.core.model.annotations.Privileged;
import ru.protei.portal.core.model.annotations.Stored;
import ru.protei.portal.core.model.dict.En_AuditType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.DecimalNumber;
import ru.protei.portal.core.model.ent.Equipment;
import ru.protei.portal.core.model.query.EquipmentQuery;

import java.util.List;

/**
 * Сервис управления оборудованием
 */
public interface EquipmentService {

    @Privileged( En_Privilege.EQUIPMENT_VIEW )
    CoreResponse< Long > count(AuthToken token, EquipmentQuery query );

    @Privileged( En_Privilege.EQUIPMENT_VIEW )
    CoreResponse< List< Equipment > > equipmentList( AuthToken token, EquipmentQuery query );

    @Privileged( En_Privilege.EQUIPMENT_VIEW )
    CoreResponse< Equipment > getEquipment( AuthToken token, long id );

    @Privileged( requireAny = { En_Privilege.EQUIPMENT_CREATE, En_Privilege.EQUIPMENT_EDIT })
    @Auditable( En_AuditType.EQUIPMENT_MODIFY )
    CoreResponse< Equipment > saveEquipment( AuthToken token, @Stored Equipment equipment );

    @Privileged( requireAny = { En_Privilege.EQUIPMENT_CREATE, En_Privilege.EQUIPMENT_EDIT })
    CoreResponse< DecimalNumber > getNextAvailableDecimalNumber( AuthToken token, DecimalNumber number );

    @Privileged( requireAny = { En_Privilege.EQUIPMENT_CREATE, En_Privilege.EQUIPMENT_EDIT })
    CoreResponse< DecimalNumber > getNextAvailableDecimalNumberModification( AuthToken token, DecimalNumber number );

    CoreResponse< Boolean > checkIfExistDecimalNumber( DecimalNumber number );

    @Privileged( requireAny = { En_Privilege.EQUIPMENT_CREATE, En_Privilege.EQUIPMENT_EDIT })
    @Auditable( En_AuditType.EQUIPMENT_COPY )
    CoreResponse<Long> copyEquipment( AuthToken token, @Stored Long equipmentId, String newName, Long authorId );

    @Privileged( En_Privilege.EQUIPMENT_REMOVE )
    @Auditable( En_AuditType.EQUIPMENT_REMOVE )
    CoreResponse<Boolean> removeEquipment( AuthToken token, @Stored Long equipmentId );
}
