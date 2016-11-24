package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.ent.CompanyGroup;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.CompanyGroupQuery;
import ru.protei.portal.core.model.query.SqlCondition;

import java.util.List;

/**
 * Created by michael on 01.04.16.
 */
public interface CompanyGroupDAO extends PortalBaseDAO<CompanyGroup> {

    CompanyGroup getGroupByName( String name );

    List<CompanyGroup> getListByQuery (CompanyGroupQuery query);


    @SqlConditionBuilder
    SqlCondition createSqlCondition(CompanyGroupQuery query);
}
