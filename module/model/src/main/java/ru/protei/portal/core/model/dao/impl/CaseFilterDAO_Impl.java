package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.CaseFilterDAO;
import ru.protei.portal.core.model.dict.En_CaseFilterType;
import ru.protei.portal.core.model.ent.CaseFilter;
import ru.protei.portal.core.model.util.sqlcondition.Query;

import java.util.List;

import static ru.protei.portal.core.model.util.sqlcondition.SqlQueryBuilder.query;

public class CaseFilterDAO_Impl extends PortalBaseJdbcDAO< CaseFilter > implements CaseFilterDAO {

    @Override
    public List< CaseFilter > getListByLoginIdAndFilterType( Long loginId, En_CaseFilterType filterType ) {
        return getListByCondition( "login_id=? and type=?", loginId, filterType.name() );
    }

    @Override
    public List<CaseFilter> getByPersonId(Long personId) {
        Query query = query()
                .whereExpression("id in (select case_filter_id from person_to_case_filter where person_id = ?)").attributes(personId);

        return getListByCondition(query.buildSql(),  query.args());
    }

    @Override
    public void removeNotUniqueFilters() {
        String sql = "DELETE filter_1 FROM case_filter filter_1 " +
                "INNER JOIN case_filter filter_2 " +
                "WHERE filter_1.id < filter_2.id " +
                "AND filter_1.name = filter_2.name " +
                "AND filter_1.login_id = filter_2.login_id";

        jdbcTemplate.execute(sql);
    }
}
