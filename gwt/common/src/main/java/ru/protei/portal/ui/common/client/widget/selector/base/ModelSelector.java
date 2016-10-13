package ru.protei.portal.ui.common.client.widget.selector.base;

import java.util.List;

/**
 * Интерфейс селектора с моделью
 */
public interface ModelSelector<T> {

    void fillOptions(List<T> options);

    void refreshValue();
}
