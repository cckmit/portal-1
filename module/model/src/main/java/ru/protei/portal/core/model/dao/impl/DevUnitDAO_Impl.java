package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.DevUnitDAO;
import ru.protei.portal.core.model.dict.En_DevUnitState;
import ru.protei.portal.core.model.dict.En_DevUnitType;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.winter.jdbc.JdbcSort;

import java.util.List;

/**
 * Created by michael on 23.05.16.
 */
public class DevUnitDAO_Impl extends PortalBaseJdbcDAO<DevUnit> implements DevUnitDAO {

    @Override
    public List<DevUnit> getProductsByCondition(String searchString, JdbcSort sort) {
        return getUnitsByCondition(En_DevUnitType.PRODUCT, En_DevUnitState.ACTIVE, searchString, sort);
    }

    @Override
    public List<DevUnit> getComponentsByCondition(String searchString, JdbcSort sort) {
        return getUnitsByCondition(En_DevUnitType.COMPONENT, En_DevUnitState.ACTIVE, searchString, sort);
    }

    @Override
    public List<DevUnit> getUnitsByCondition(En_DevUnitType type, En_DevUnitState state, String searchString, JdbcSort sort) {
        return state == null ?
                getListByCondition("UTYPE_ID=? and UNIT_NAME like ?", sort, type.getId(), searchString)
                :
                getListByCondition("UTYPE_ID=? and UNIT_NAME like ? and UNIT_STATE=?", sort, type.getId(), searchString,state.getId());
    }

    @Override
    public boolean checkExistProductByName (String name, Long id) {

        if ( name == null || name.trim().isEmpty() )
            throw new RuntimeException( );

        return id == null ? checkExistsByCondition("UNIT_NAME like ?", name) :
                checkExistsByCondition(" id != ? and UNIT_NAME like ? ", id, name);
    }

}
