package ru.protei.portal.tools.migrate.sybase;

import java.sql.SQLException;

public interface LegacyDAO_Action<R> {
    R doAction(LegacyDAO_Transaction transaction) throws SQLException;
}
