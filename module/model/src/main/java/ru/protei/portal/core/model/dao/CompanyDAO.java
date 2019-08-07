package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.query.CompanyQuery;
import ru.protei.portal.core.model.query.SqlCondition;

import java.util.Map;

/**
 * Created by michael on 01.04.16.
 */
public interface CompanyDAO extends PortalBaseDAO<Company> {

    Company getCompanyByName( String name );


    @SqlConditionBuilder
    SqlCondition createSqlCondition(CompanyQuery query);

    /**
     * Возвращает соответствие между ID в старом портале и текущим ID записи в новой БД
     * В качестве ключа используется ID в старой базе
     * @return
     */
    Map<Long,Long> mapLegacyId ();

    boolean updateState(Company tempCompany);
}
