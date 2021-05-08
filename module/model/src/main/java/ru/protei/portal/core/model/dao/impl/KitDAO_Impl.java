package ru.protei.portal.core.model.dao.impl;

import org.springframework.dao.EmptyResultDataAccessException;
import ru.protei.portal.core.model.dao.KitDAO;
import ru.protei.portal.core.model.dict.En_CustomerType;
import ru.protei.portal.core.model.ent.Kit;

/**
 * DAO для местоположений проекта
 */
public class KitDAO_Impl extends PortalBaseJdbcDAO<Kit> implements KitDAO {

    @Override
    public String getLastSerialNumber(boolean isArmyProject) {
        String sql ="select max(serial_number) " +
                        "from kit " +
                        "         join delivery d on d.id = kit.delivery_id " +
                        "         join project p on p.id = d.project_id " +
                        "where p.customer_type " + (isArmyProject ? "=" : "<>") + En_CustomerType.MINISTRY_OF_DEFENCE.getId() +";";

        try {
            return jdbcTemplate.queryForObject( sql, String.class );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}
