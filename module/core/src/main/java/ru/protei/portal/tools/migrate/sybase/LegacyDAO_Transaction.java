package ru.protei.portal.tools.migrate.sybase;

import ru.protei.portal.core.model.ent.LegacyEntity;

import java.sql.SQLException;

public interface LegacyDAO_Transaction extends AutoCloseable {
    <T extends LegacyEntity> LegacyEntityDAO<T> dao(Class<T> type) throws SQLException;
    void commit() throws SQLException;
    void close();
}
