package ru.protei.portal.tools.sybase;

import java.sql.Connection;
import java.sql.SQLException;

public interface SybConnProvider {
    Connection getConnection () throws SQLException;
}
