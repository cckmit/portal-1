package ru.protei.portal.tools.migrate.tools;

/**
 * Created by michael on 20.05.16.
 */
public interface PostBatchProcess<T> {
    public void postBatch (T t);
}
