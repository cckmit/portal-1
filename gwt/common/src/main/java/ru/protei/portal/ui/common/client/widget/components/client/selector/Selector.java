package ru.protei.portal.ui.common.client.widget.components.client.selector;

import ru.protei.portal.ui.common.client.widget.components.client.selector.SelectorItemRenderer;
import ru.protei.portal.ui.common.client.widget.components.client.selector.SelectorModel;

public interface Selector<T>
{
    void setSelectorModel( SelectorModel<T> selectorModel );

    void setSelectorItemRenderer( SelectorItemRenderer<T> selectorItemRenderer );
}
