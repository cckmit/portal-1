package ru.protei.portal.ui.common.client.widget.selector.product.component;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_DevUnitType;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.client.widget.components.client.buttonselector.ButtonPopupSingleSelector;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;
import ru.protei.portal.ui.common.client.widget.selector.product.ProductModel;

import java.util.List;

public class ComponentButtonSelector
        extends ButtonPopupSingleSelector<ProductShortView>
{

    @Inject
    public void init(ProductModel model) {
        model.setUnitTypes( En_DevUnitType.COMPONENT );
        setAsyncSelectorModel(model);
        setHasNullValue(true);
        setSearchAutoFocus(true);
        setPageSize( CrmConstants.DEFAULT_SELECTOR_PAGE_SIZE );
        setSelectorItemRenderer( value -> value == null ? defaultValue : value.getName() );
    }

    public void setDefaultValue(String value) {
        this.defaultValue = value;
    }

    private String defaultValue = null;
}
