package ru.protei.portal.tools.migrate.tools;

import org.springframework.context.support.AbstractApplicationContext;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by michael on 01.04.16.
 */
public interface MigrateAction {
    public void migrate(Connection src, AbstractApplicationContext ctx) throws SQLException;

    public int orderOfExec ();
}
