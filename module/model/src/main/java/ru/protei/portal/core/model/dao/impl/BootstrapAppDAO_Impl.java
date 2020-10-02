package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.BootstrapAppDAO;
import ru.protei.portal.core.model.ent.BootstrapAction;
import ru.protei.winter.jdbc.JdbcBaseDAO;

import static ru.protei.portal.core.model.ent.BootstrapAction.Columns.NAME;

public class BootstrapAppDAO_Impl extends JdbcBaseDAO<Long, BootstrapAction> implements BootstrapAppDAO {
    @Override
    public boolean isActionExists( String actionName ) {
        return checkExistsByCondition(  NAME + "=?", actionName );
    }

    @Override
    public Long createAction( String actionName ) {
        BootstrapAction bootstrapApp = new BootstrapAction();
        bootstrapApp.setName( actionName );
        return persist( bootstrapApp );
    }
}
