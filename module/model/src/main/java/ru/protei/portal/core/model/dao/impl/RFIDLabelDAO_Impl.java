package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.RFIDLabelDAO;
import ru.protei.portal.core.model.ent.RFIDLabel;
import ru.protei.portal.core.model.query.RFIDLabelQuery;
import ru.protei.portal.core.model.query.SqlCondition;

import static ru.protei.portal.core.model.helper.HelperFunc.makeLikeArg;
import static ru.protei.portal.core.model.helper.StringUtils.isNotBlank;

public class RFIDLabelDAO_Impl extends PortalBaseJdbcDAO<RFIDLabel> implements RFIDLabelDAO {
    @Override
    @SqlConditionBuilder
    public SqlCondition createSqlCondition(RFIDLabelQuery query) {
        return new SqlCondition().build((condition, args) -> {
            condition.append("1=1");

            if (isNotBlank(query.getEpc())) {
                condition.append(" and " + RFIDLabel.Columns.EPC + " like ?");
                args.add(makeLikeArg(query.getEpc(), true));
            }

            if (isNotBlank(query.getName())) {
                condition.append(" and rfid_label." + RFIDLabel.Columns.NAME + " like ?");
                args.add(makeLikeArg(query.getName(), true));
            }
        });
    }

    @Override
    public RFIDLabel getByEPC(String epc) {
        return getByCondition(RFIDLabel.Columns.EPC + " = ?", epc);
    }
}
