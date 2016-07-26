package ru.protei.portal.tools.migrate.tools;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by michael on 01.04.16.
 */
public interface MigrateAction {
    public void migrate(Connection src) throws SQLException;

    public int orderOfExec ();
}
