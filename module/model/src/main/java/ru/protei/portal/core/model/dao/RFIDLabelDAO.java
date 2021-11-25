package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.ent.RFIDLabel;
import ru.protei.portal.core.model.query.RFIDLabelQuery;
import ru.protei.portal.core.model.query.SqlCondition;

public interface RFIDLabelDAO extends PortalBaseDAO<RFIDLabel> {
    @SqlConditionBuilder
    SqlCondition createSqlCondition(RFIDLabelQuery query);
    
    RFIDLabel getByEPC(String epc);
}