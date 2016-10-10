package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.DevUnitDAO;
import ru.protei.portal.core.model.dict.En_DevUnitType;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.core.model.view.ProductView;
import ru.protei.winter.core.utils.collections.CollectionUtils;
import ru.protei.winter.core.utils.collections.Converter;
import ru.protei.winter.jdbc.JdbcSort;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by michael on 23.05.16.
 */
public class DevUnitDAO_Impl extends PortalBaseJdbcDAO<DevUnit> implements DevUnitDAO {

    @Override
    public List<ProductView> getProductsByCondition(String searchString, JdbcSort sort) {

        List<ProductView> rez = new ArrayList<ProductView>();

        for (DevUnit u : getUnitsByCondition(En_DevUnitType.PRODUCT, searchString, sort))
            rez.add(new ProductView(u));

        return rez;
    }

    @Override
    public List<DevUnit> getComponentsByCondition(String searchString, JdbcSort sort) {
        return getUnitsByCondition(En_DevUnitType.COMPONENT, searchString, sort);
    }

    @Override
    public List<DevUnit> getUnitsByCondition(En_DevUnitType type, String searchString, JdbcSort sort) {
        return getListByCondition("UTYPE_ID=? and UNIT_NAME like ?", sort, type.getId(), searchString);
    }

}
