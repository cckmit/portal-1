package ru.protei.portal.ui.documentation.client.widget.select.item;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * Абстрактное представление одного элемента инпут-селектора
 */
public interface AbstractSelectItemView extends IsWidget {
    void setActivity(AbstractSelectItemActivity activity);

    void setValue(String value);

    String getValue();
}
