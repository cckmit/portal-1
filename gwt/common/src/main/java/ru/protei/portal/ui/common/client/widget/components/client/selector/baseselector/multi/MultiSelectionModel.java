package ru.protei.portal.ui.common.client.widget.components.client.selector.baseselector.multi;

import ru.protei.portal.ui.common.client.widget.components.client.selector.baseselector.SelectionModel;

import java.util.Set;

public interface MultiSelectionModel<T> extends SelectionModel<T> {

    Set<T> get();

    void clear();
}
