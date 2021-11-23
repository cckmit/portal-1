package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.RFIDLabelDAO;
import ru.protei.portal.core.model.ent.RFIDLabel;
import ru.protei.portal.core.model.query.RFIDLabelQuery;
import ru.protei.portal.core.model.query.SqlCondition;

public class RFIDLabelDAO_Impl extends PortalBaseJdbcDAO<RFIDLabel> implements RFIDLabelDAO {
    @Override
    @SqlConditionBuilder
    public SqlCondition createSqlCondition(RFIDLabelQuery query) {
        return new SqlCondition().build((condition, args) -> {
            condition.append("1=1");
        });
    }
}
