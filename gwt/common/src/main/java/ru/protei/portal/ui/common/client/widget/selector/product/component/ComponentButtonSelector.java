package ru.protei.portal.ui.common.client.widget.selector.product.component;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_DevUnitType;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonPopupSingleSelector;
import ru.protei.portal.ui.common.client.widget.selector.product.ProductModel;

public class ComponentButtonSelector extends ButtonPopupSingleSelector<ProductShortView>
{

    @Inject
    public void init(ProductModel model) {
        model.setUnitTypes( En_DevUnitType.COMPONENT );
        setAsyncModel(model);
        setHasNullValue(true);

        setItemRenderer( value -> value == null ? defaultValue : value.getName() );
    }

}
