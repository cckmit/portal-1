package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.BootstrapApp;
import ru.protei.winter.jdbc.JdbcDAO;

public interface BootstrapAppDAO extends JdbcDAO<Long, BootstrapApp> {

    boolean isKeyExists( String key );

    Long createKey( String key );
}