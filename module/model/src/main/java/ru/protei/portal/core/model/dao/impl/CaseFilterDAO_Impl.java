package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.CaseFilterDAO;
import ru.protei.portal.core.model.dict.En_CaseFilterType;
import ru.protei.portal.core.model.ent.CaseFilter;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.util.sqlcondition.Query;

import java.util.List;

import static ru.protei.portal.core.model.util.sqlcondition.SqlQueryBuilder.query;

public class CaseFilterDAO_Impl extends PortalBaseJdbcDAO< CaseFilter > implements CaseFilterDAO {

    @Override
    public List< CaseFilter > getListByLoginIdAndFilterType( Long loginId, En_CaseFilterType filterType ) {
        return getListByCondition( "login_id=? and type=?", loginId, filterType.name() );
    }

    @Override
    public List<CaseFilter> getListByFilterTypes(List<En_CaseFilterType> filterTypes) {
        return getListByCondition("type IN " + HelperFunc.makeInArg(filterTypes, true));
    }

    @Override
    public CaseFilter checkExistsByParams( String name, Long loginId, En_CaseFilterType type ) {
        return getByCondition("name=? and login_id=? and type=?", name, loginId, type.name() );
    }

    @Override
    public List<CaseFilter> getByPersonId(Long personId) {
        Query query = query()
                .whereExpression("id in (select case_filter_id from person_to_case_filter where person_id = ?)").attributes(personId);

        return getListByCondition(query.buildSql(),  query.args());
    }

    @Override
    public List<CaseFilter> getByPersonIdAndTypes(Long personId, List<En_CaseFilterType> filterTypes) {
        Query query = query()
                .where("id")
                .in(query()
                        .select("case_filter_id")
                        .from("person_to_case_filter")
                        .where("person_id").equal(personId)
                        .asQuery()
                )
                .and("type").in(filterTypes)
                .asQuery();

        return getListByCondition(query.buildSql(), query.args());
    }
}
