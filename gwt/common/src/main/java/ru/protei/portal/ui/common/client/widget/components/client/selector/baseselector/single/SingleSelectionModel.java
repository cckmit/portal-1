package ru.protei.portal.ui.common.client.widget.components.client.selector.baseselector.single;

import ru.protei.portal.ui.common.client.widget.components.client.selector.baseselector.SelectionModel;

public interface SingleSelectionModel<T> extends SelectionModel<T> {

    T get();

}
