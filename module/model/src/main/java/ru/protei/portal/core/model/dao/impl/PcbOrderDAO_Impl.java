package ru.protei.portal.core.model.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.PcbOrderDAO;
import ru.protei.portal.core.model.ent.PcbOrder;
import ru.protei.portal.core.model.query.PcbOrderQuery;
import ru.protei.portal.core.model.query.SqlCondition;


public class PcbOrderDAO_Impl extends PortalBaseJdbcDAO<PcbOrder> implements PcbOrderDAO {

    @Autowired
    PcbOrderSqlBuilder sqlBuilder;

    @Override
    @SqlConditionBuilder
    public SqlCondition createSqlCondition(PcbOrderQuery query) {
        return sqlBuilder.createSqlCondition(query);
    }
}
