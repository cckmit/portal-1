package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.DevUnitDAO;
import ru.protei.portal.core.model.dict.En_DevUnitState;
import ru.protei.portal.core.model.dict.En_DevUnitType;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.winter.jdbc.JdbcSort;

import java.util.List;

/**
 * Created by michael on 23.05.16.
 */
public class DevUnitDAO_Impl extends PortalBaseJdbcDAO<DevUnit> implements DevUnitDAO {

//    @Override
//    public List<DevUnit> getProductsByCondition(String searchString, JdbcSort sort) {
//        return getUnitsByCondition(En_DevUnitType.PRODUCT, En_DevUnitState.ACTIVE, searchString, sort);
//    }
//
//    @Override
//    public List<DevUnit> getComponentsByCondition(String searchString, JdbcSort sort) {
//        return getUnitsByCondition(En_DevUnitType.COMPONENT, En_DevUnitState.ACTIVE, searchString, sort);
//    }
//
//    @Override
//    public List<DevUnit> getUnitsByCondition(En_DevUnitType type, En_DevUnitState state, String searchString, JdbcSort sort) {
//        return state == null ?
//                getListByCondition("UTYPE_ID=? and UNIT_NAME like ?", sort, type.getId(), HelperFunc.makeLikeArg(searchString))
//                :
//                getListByCondition("UTYPE_ID=? and UNIT_NAME like ? and UNIT_STATE=?", sort, type.getId(), HelperFunc.makeLikeArg(searchString),state.getId());
//    }
//
//    @Override
//    public DevUnit checkExistsProductByName(String name) {
//
//        return getByCondition("UTYPE_ID=? and UNIT_NAME=?", En_DevUnitType.PRODUCT.getId(), name);
//    }


    @Override
    public DevUnit checkExistsByName(En_DevUnitType type, String name) {
        return getByCondition("UTYPE_ID=? and UNIT_NAME=?", type.getId(), name);
    }
}
