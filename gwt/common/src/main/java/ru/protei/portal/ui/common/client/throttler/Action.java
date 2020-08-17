package ru.protei.portal.ui.common.client.throttler;

/**
 * Выполняемое действие требующее ограничения количества вызовов
 */
public interface Action {
    void run();
}
