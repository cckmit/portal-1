package ru.protei.portal.ui.common.client.widget.components.client.selector.impl;

import ru.protei.portal.ui.common.client.widget.components.client.selector.SelectorItemRenderer;

public abstract class HtmlSelectorItemRenderer<T> implements SelectorItemRenderer<T> {
    @Override
    public String getElementName( T t ) {
        return null;
    }
}
