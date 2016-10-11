package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.dict.En_DevUnitType;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.winter.jdbc.JdbcSort;

import java.util.List;

/**
 * Created by michael on 23.05.16.
 */
public interface DevUnitDAO extends PortalBaseDAO<DevUnit> {

    public List<DevUnit> getProductsByCondition(String q, JdbcSort sort);

    public List<DevUnit> getComponentsByCondition(String q, JdbcSort sort);

    public List<DevUnit> getUnitsByCondition(En_DevUnitType type, String q, JdbcSort sort);
}
