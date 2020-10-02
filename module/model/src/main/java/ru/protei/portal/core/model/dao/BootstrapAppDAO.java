package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.BootstrapAction;
import ru.protei.winter.jdbc.JdbcDAO;

public interface BootstrapAppDAO extends JdbcDAO<Long, BootstrapAction> {

    boolean isActionExists( String key );

    Long createAction( String key );
}