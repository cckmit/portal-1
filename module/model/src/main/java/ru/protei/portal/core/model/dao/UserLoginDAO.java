package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.ent.UserLogin;
import ru.protei.portal.core.model.query.AccountQuery;
import ru.protei.portal.core.model.query.SqlCondition;

import java.util.List;

/**
 * Created by michael on 16.06.16.
 */
public interface UserLoginDAO extends PortalBaseDAO<UserLogin> {

    UserLogin findByLogin(String login);

    UserLogin findByPersonId(Long id);

    UserLogin checkExistsByLogin(String login);

    List< UserLogin > getAccounts ( AccountQuery query );

    Long count( AccountQuery query );

    boolean isUnique (String login);

    @SqlConditionBuilder
    SqlCondition createSqlCondition ( AccountQuery query );
}
