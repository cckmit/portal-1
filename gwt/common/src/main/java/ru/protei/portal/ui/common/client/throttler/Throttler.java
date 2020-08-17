package ru.protei.portal.ui.common.client.throttler;

/**
 * Ограничитель количества действий
 */
public interface Throttler {
    void run();

    void setAction( Action action );
}
