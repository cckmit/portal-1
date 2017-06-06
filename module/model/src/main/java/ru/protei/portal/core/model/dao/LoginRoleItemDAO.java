package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.LoginRoleItem;

import java.util.List;

/**
 * DAO связки логин-роль
 */
public interface LoginRoleItemDAO extends PortalBaseDAO< LoginRoleItem > {
    List< LoginRoleItem > getLoginToRoleLinks( Long loginId, Long roleId );
}
