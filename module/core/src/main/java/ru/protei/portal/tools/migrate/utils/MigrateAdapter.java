package ru.protei.portal.tools.migrate.utils;

import java.util.Map;

/**
 * Created by michael on 20.05.16.
 */
public interface MigrateAdapter<T> {
    T createEntity (Map<String,Object> from);
}
