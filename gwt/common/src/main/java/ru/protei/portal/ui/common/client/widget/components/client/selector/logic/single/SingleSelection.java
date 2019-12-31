package ru.protei.portal.ui.common.client.widget.components.client.selector.logic.single;

import ru.protei.portal.ui.common.client.widget.components.client.selector.logic.Selection;

public interface SingleSelection<T> extends Selection<T> {

    T get();
}
