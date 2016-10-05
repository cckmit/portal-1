package ru.protei.portal.ui.product.client.activity.item;

import com.google.gwt.user.client.ui.IsWidget;

public interface AbstractProductItemView extends IsWidget {

    void setActivity(AbstractProductItemActivity activity);

    void setName (String name);
}
