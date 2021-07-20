package ru.protei.portal.core.model.dao.impl;

import org.springframework.dao.EmptyResultDataAccessException;
import ru.protei.portal.core.model.dao.KitDAO;
import ru.protei.portal.core.model.dict.En_CustomerType;
import ru.protei.portal.core.model.ent.CaseMember;
import ru.protei.portal.core.model.ent.Kit;
import ru.protei.portal.core.model.helper.HelperFunc;

import java.util.List;

/**
 * DAO для местоположений проекта
 */
public class KitDAO_Impl extends PortalBaseJdbcDAO<Kit> implements KitDAO {

    @Override
    public List<Kit> listByDeliveryId(Long deliveryId) {
        return getListByCondition("kit.delivery_id = ?", deliveryId);
    }

    @Override
    public String getLastSerialNumber(boolean isArmyProject) {
        try {
            return getMaxValue("serial_number", String.class,
                    "join delivery d on d.id = kit.delivery_id join project p on p.id = d.project_id",
                    "p.customer_type " + (isArmyProject ? "= " : "<> ") + En_CustomerType.MINISTRY_OF_DEFENCE.getId());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public String getLastSerialNumber(Long deliveryId) {
        try {
            return getMaxValue("serial_number", String.class, "delivery_id = ?", deliveryId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public boolean isExistAnySerialNumbers(List<String> serialNumbers) {
        try {
            return getObjectsCount("serial_number in " + HelperFunc.makeInArg(serialNumbers, true), null) > 0;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }
}
