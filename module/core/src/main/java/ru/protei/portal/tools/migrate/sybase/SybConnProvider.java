package ru.protei.portal.tools.migrate.sybase;

import java.sql.Connection;
import java.sql.SQLException;

public interface SybConnProvider {
    Connection getConnection () throws SQLException;
}
