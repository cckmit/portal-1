package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.UserSession;

/**
 * Created by michael on 16.06.16.
 */
public interface UserSessionDAO extends PortalBaseDAO<UserSession>{

    public UserSession findBySID(String sid);
}
