package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.UserLoginDAO;
import ru.protei.portal.core.model.ent.UserLogin;

/**
 * Created by michael on 16.06.16.
 */
public class UserLoginDAO_Impl extends PortalBaseJdbcDAO<UserLogin> implements UserLoginDAO {

    @Override
    public UserLogin findByLogin(String login) {
        return getByCondition("ulogin=?", login);
    }

    @Override
    public UserLogin findByPersonId(Long id) {
        return getByCondition("personId=?", id);
    }

    @Override
    public UserLogin checkExistsByLogin(String login) {
        return getByCondition("ulogin=?", login );
    }
}
