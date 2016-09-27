package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.UserLogin;

/**
 * Created by michael on 16.06.16.
 */
public interface UserLoginDAO extends PortalBaseDAO<UserLogin> {

    public UserLogin findByLogin (String login);
}
