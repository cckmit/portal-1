package ru.protei.portal.ui.product.client.activity.item;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_DevUnitType;

/**
 * Абстракция вида карточки продукта
 */
public interface AbstractProductItemView extends IsWidget {

    void setActivity(AbstractProductItemActivity activity);

    void setName (String name);

    void setType (En_DevUnitType type);

    void setArchived(boolean value);

    HasWidgets getPreviewContainer();

    void setEditEnabled( boolean value );
}
