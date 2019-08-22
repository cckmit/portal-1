package ru.protei.portal.ui.common.client.widget.selector.product.product;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_DevUnitType;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;
import ru.protei.portal.ui.common.client.widget.selector.product.ProductModel;

import java.util.List;

public class ProductButtonSelector extends ButtonSelector<ProductShortView> implements SelectorWithModel<ProductShortView> {
    @Inject
    public void init(ProductModel model) {
        model.subscribe(this, null, En_DevUnitType.PRODUCT);
        setSelectorModel(model);
        setHasNullValue(true);
        setSearchAutoFocus(true);
        setDisplayOptionCreator(value -> new DisplayOption(value == null ? defaultValue : value.getName()));
    }

    @Override
    public void fillOptions(List<ProductShortView> options) {
        clearOptions();
        if (defaultValue != null) {
            addOption(null);
            setValue(null);
        }
        options.forEach(this::addOption);
    }

    public void setDefaultValue(String value) {
        this.defaultValue = value;
    }

    private String defaultValue = null;
}
