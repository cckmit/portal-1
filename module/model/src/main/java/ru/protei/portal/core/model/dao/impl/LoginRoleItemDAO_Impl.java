package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.LoginRoleItemDAO;
import ru.protei.portal.core.model.ent.LoginRoleItem;

import java.util.ArrayList;
import java.util.List;

/**
 * DAO связки логин-роль
 */
public class LoginRoleItemDAO_Impl extends PortalBaseJdbcDAO< LoginRoleItem > implements LoginRoleItemDAO {
    @Override
    public List< LoginRoleItem > getLoginToRoleLinks( Long loginId, Long roleId ) {

        if ( loginId == null && roleId == null )
            return new ArrayList<>();

        StringBuilder condition = new StringBuilder();
        List< Object > args = new ArrayList<>();
        if ( loginId != null ) {
            condition.append( " login_id=? " );
            args.add( loginId );
        }
        if ( roleId != null ) {
            condition.append( ( loginId != null ? " and " : "" ) + " role_id=? " );
            args.add( roleId );
        }

        return getListByCondition( condition.toString(), args );
    }

}
