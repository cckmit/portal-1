package ru.protei.portal.ui.common.client.widget.components.client.selector.logic.multi;

import ru.protei.portal.ui.common.client.widget.components.client.selector.logic.Selection;

import java.util.Set;

public interface MultiSelection<T> extends Selection<T> {

    Set<T> get();

    void clear();
}
