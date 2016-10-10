package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.core.model.view.ProductView;
import ru.protei.winter.jdbc.JdbcSort;

import java.util.List;

/**
 * Created by michael on 23.05.16.
 */
public interface DevUnitDAO extends PortalBaseDAO<DevUnit> {

    public List<ProductView> getProductsByCondition(String q, JdbcSort sort);

}
