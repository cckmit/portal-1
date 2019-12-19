package ru.protei.portal.ui.common.client.widget.components.client.selector.logic.single;

import ru.protei.portal.ui.common.client.widget.components.client.selector.logic.SelectionModel;

public interface SingleSelectionModel<T> extends SelectionModel<T> {

    T get();
}
