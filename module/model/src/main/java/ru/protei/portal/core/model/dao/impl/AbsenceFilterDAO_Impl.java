package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.AbsenceFilterDAO;
import ru.protei.portal.core.model.ent.AbsenceFilter;
import ru.protei.portal.core.model.util.sqlcondition.Query;

import java.util.List;

import static ru.protei.portal.core.model.util.sqlcondition.SqlQueryBuilder.query;

public class AbsenceFilterDAO_Impl extends PortalBaseJdbcDAO<AbsenceFilter> implements AbsenceFilterDAO {
    @Override
    public List<AbsenceFilter> getListByLoginId(Long loginId) {
        return getListByCondition( "login_id=?", loginId);
    }

    @Override
    public void removeNotUniqueFilters() {
        String sql = "DELETE filter_1 FROM absence_filter filter_1 " +
                "INNER JOIN absence_filter filter_2 " +
                "WHERE filter_1.id < filter_2.id " +
                "AND filter_1.name = filter_2.name " +
                "AND filter_1.login_id = filter_2.login_id";

        jdbcTemplate.execute(sql);
    }
}
