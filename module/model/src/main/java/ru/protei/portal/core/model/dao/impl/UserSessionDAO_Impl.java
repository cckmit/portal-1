package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.UserSessionDAO;
import ru.protei.portal.core.model.ent.UserSession;

/**
 * Created by michael on 16.06.16.
 */
public class UserSessionDAO_Impl extends PortalBaseJdbcDAO<UserSession> implements UserSessionDAO {

    public UserSession findBySID(String sid) {
        return getByCondition("session_id=?", sid);
    }
}
