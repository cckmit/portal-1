package ru.protei.portal.ui.common.client.selector.selection;

import java.util.Set;

/**
 * Выбранные значения
 */
public interface MultiSelection<T> extends Selection<T> {

    Set<T> get();

    void clear();
}
