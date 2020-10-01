package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.BootstrapAppDAO;
import ru.protei.portal.core.model.ent.BootstrapApp;
import ru.protei.winter.jdbc.JdbcBaseDAO;

import static ru.protei.portal.core.model.ent.BootstrapApp.Columns.UNIQUE_NAME;

public class BootstrapAppDAO_Impl extends JdbcBaseDAO<Long, BootstrapApp> implements BootstrapAppDAO {
    @Override
    public boolean isKeyExists( String key ) {
        return checkExistsByCondition(  UNIQUE_NAME + "=?", key );
    }

    @Override
    public Long createKey( String key ) {
        BootstrapApp bootstrapApp = new BootstrapApp();
        bootstrapApp.setKey( key );
        return persist( bootstrapApp );
    }
}
