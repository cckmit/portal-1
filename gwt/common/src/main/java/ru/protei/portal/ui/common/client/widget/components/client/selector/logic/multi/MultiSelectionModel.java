package ru.protei.portal.ui.common.client.widget.components.client.selector.logic.multi;

import ru.protei.portal.ui.common.client.widget.components.client.selector.logic.SelectionModel;

import java.util.Set;

public interface MultiSelectionModel<T> extends SelectionModel<T> {

    Set<T> get();

    void clear();
}
