package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.ent.UserLogin;
import ru.protei.portal.core.model.query.AccountQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.Date;
import java.util.List;

/**
 * Created by michael on 16.06.16.
 */
public interface UserLoginDAO extends PortalBaseDAO<UserLogin> {

    UserLogin findByLogin(String login);

    UserLogin findByPersonId(Long id);

    UserLogin findLDAPByPersonId(Long id);

    UserLogin checkExistsByLogin(String login);

    SearchResult<UserLogin> getSearchResult(AccountQuery query);

    boolean isUnique (String login);

    int removeByPersonId(Long id);

    @SqlConditionBuilder
    SqlCondition createSqlCondition ( AccountQuery query );

    default UserLogin createNewUserLogin(Person person) throws Exception {
        UserLogin userLogin = new UserLogin();
        userLogin.setCreated(new Date());
        userLogin.setPersonId(person.getId());
        userLogin.setInfo(person.getDisplayName());
        return userLogin;
    }

}
