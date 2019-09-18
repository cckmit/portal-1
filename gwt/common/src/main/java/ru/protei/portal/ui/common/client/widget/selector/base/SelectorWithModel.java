package ru.protei.portal.ui.common.client.widget.selector.base;

import java.util.Collection;
import java.util.List;

/**
 * Интерфейс селектора с моделью
 */
public interface SelectorWithModel<T> {

    void fillOptions(List<T> options);

    void refreshValue();

    void clearOptions();

    Collection<T> getValues();

    void setSelectorModel( SelectorModel<T> selectorModel );

    boolean isAttached();

    default boolean isLazy() {
        return false;
    }
}
