package ru.protei.portal.core.utils;

/**
 * Created by michael on 10.10.16.
 */
public interface EntitySelector<T> {
    boolean matches (T entity);
}
