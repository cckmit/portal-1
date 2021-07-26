package ru.protei.portal.core.model.dao.impl;

import org.springframework.dao.EmptyResultDataAccessException;
import ru.protei.portal.core.model.dao.KitDAO;
import ru.protei.portal.core.model.dict.En_CustomerType;
import ru.protei.portal.core.model.ent.Kit;
import ru.protei.portal.core.model.helper.HelperFunc;

import java.sql.ResultSet;
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

    @Override
    public List<Kit> getModulesGroupedByKit(Long deliveryId) {
        return jdbcTemplate.query( "SELECT id AS kitId, (SELECT count(id) FROM module WHERE module.kit_id = kitId) AS modulesCount " +
                        "FROM kit " +
                        "WHERE kit.delivery_id = " + deliveryId,
                (ResultSet rs, int rowNum) -> {
                    Kit shortView = new Kit();
                    shortView.setId(rs.getLong("kitId"));
                    shortView.setModulesCount(rs.getInt("modulesCount"));
                    return shortView;
                });
    }
}
