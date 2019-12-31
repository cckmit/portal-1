package ru.protei.portal.ui.common.client.widget.selector.product.component;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_DevUnitType;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.components.client.input.InputPopupMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;
import ru.protei.portal.ui.common.client.widget.selector.input.MultipleInputSelector;
import ru.protei.portal.ui.common.client.widget.selector.product.ProductModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ComponentMultiSelector extends InputPopupMultiSelector<ProductShortView>
{

    @Inject
    public void init(ProductModel model, Lang lang) {
        model.setUnitTypes( En_DevUnitType.COMPONENT );
        setAsyncSelectorModel( model );
        setAddName(lang.buttonAdd());
        setClearName(lang.buttonClear());
        setSelectorItemRenderer( option -> option.getName() );
    }

}
