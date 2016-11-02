package ru.protei.portal.ui.product.client.activity.item;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Абстракция вида карточки продукта
 */
public interface AbstractProductItemView extends IsWidget {

    void setActivity(AbstractProductItemActivity activity);

    void setName (String name);

    void setDeprecated(boolean value);

    HasWidgets getPreviewContainer();
}
