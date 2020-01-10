package ru.protei.portal.ui.common.client.widget.selector.product.component;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_DevUnitType;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.input.InputPopupMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.product.ProductModel;

public class ComponentMultiSelector extends InputPopupMultiSelector<ProductShortView>
{

    @Inject
    public void init(ProductModel model, Lang lang) {
        model.setUnitTypes( En_DevUnitType.COMPONENT );
        setAsyncModel( model );
        setAddName(lang.buttonAdd());
        setClearName(lang.buttonClear());
        setItemRenderer( option -> option.getName() );
    }

}
