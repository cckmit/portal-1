package ru.protei.portal.ui.product.client.widget.selector;

import com.google.inject.Inject;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.base.ModelSelector;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;

import java.util.List;

public class ProductButtonSelector extends ButtonSelector<ProductShortView> implements ModelSelector<ProductShortView> {

    @Inject
    public void init(ProductModel productModel) {
        productModel.subscribe(this);
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
