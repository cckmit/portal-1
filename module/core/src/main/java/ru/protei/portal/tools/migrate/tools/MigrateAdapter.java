package ru.protei.portal.tools.migrate.tools;

import java.util.Map;

/**
 * Created by michael on 20.05.16.
 */
public interface MigrateAdapter<T> {
    public T createEntity (Map<String,Object> from);
}
