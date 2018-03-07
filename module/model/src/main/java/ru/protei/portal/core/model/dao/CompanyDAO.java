package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.CompanyQuery;
import ru.protei.portal.core.model.query.SqlCondition;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by michael on 01.04.16.
 */
public interface CompanyDAO extends PortalBaseDAO<Company> {

    List<Company> getListByQuery (CompanyQuery query);

    Company getCompanyByName( String name );


    @SqlConditionBuilder
    SqlCondition createSqlCondition(CompanyQuery query);

    /**
     * Возвращает соответствие между ID в старом портале и текущим ID записи в новой БД
     * В качестве ключа используется ID в старой базе
     * @return
     */
    Map<Long,Long> mapLegacyId ();
}
