package ru.protei.portal.ui.product.client.activity.item;

import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.ui.product.client.activity.list.AbstractProductListActivity;

public interface AbstractProductItemView extends IsWidget {

    void setActivity(AbstractProductListActivity activity);

    void setName (String name);
}
