package ru.protei.portal.tools.migrate.sybase;

import ru.protei.portal.core.model.ent.LegacyEntity;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public interface LegacyEntityDAO<T extends LegacyEntity> {
    boolean exists(Long id) throws SQLException;
    boolean exists(String cond, Object... args) throws SQLException;

    T get(Long id) throws SQLException;
    T get(String cond, Object... args) throws SQLException;

    <K> List<T> list (Collection<K> keys) throws SQLException;
    <K> List<T> list(String column, Collection<K> values) throws SQLException;
    List<T> list(String cond, Object... args) throws SQLException;
    List<T> list() throws SQLException;

    T insert(T entity) throws SQLException;
    T update(T entity) throws SQLException;

    default T store(T entity) throws SQLException {
        return (exists(entity.getId())) ? update(entity) : insert(entity);
    }

    default T saveOrUpdate (T entity) throws SQLException {
        if (entity.getId() != null && entity.getId() > 0L)
            return update(entity);
        else
            return insert(entity);
    }

    T delete(T entity) throws SQLException;
    void delete (List<T> entities) throws SQLException;
    void delete(Long id) throws SQLException;
    void delete(String condition, Object...args) throws SQLException;
}
