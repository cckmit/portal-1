package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.dict.En_DevUnitState;
import ru.protei.portal.core.model.dict.En_DevUnitType;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.winter.jdbc.JdbcSort;

import java.util.List;

/**
 * Created by michael on 23.05.16.
 */
public interface DevUnitDAO extends PortalBaseDAO<DevUnit> {

    public List<DevUnit> getProductsByCondition(String searchExpression, JdbcSort sort);

    public List<DevUnit> getComponentsByCondition(String searchExpression, JdbcSort sort);

    public List<DevUnit> getUnitsByCondition(En_DevUnitType type, En_DevUnitState state,  String searchExpression, JdbcSort sort);

    public boolean checkExistProductByName(String name, Long id);

}
