package ru.protei.portal.ui.common.client.widget.selector.product.component;

import com.google.inject.Inject;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.base.ModelSelector;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;

import java.util.List;

public class ComponentButtonSelector extends ButtonSelector<ProductShortView> implements ModelSelector<ProductShortView> {

    @Inject
    public void init(ComponentModel componentModel) {
        componentModel.subscribe(this);
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
