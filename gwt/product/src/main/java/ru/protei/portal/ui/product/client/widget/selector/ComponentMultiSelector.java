package ru.protei.portal.ui.product.client.widget.selector;

import com.google.inject.Inject;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.base.ModelSelector;
import ru.protei.portal.ui.common.client.widget.selector.input.MultipleInputSelector;

import java.util.List;

public class ComponentMultiSelector extends MultipleInputSelector<ProductShortView> implements ModelSelector<ProductShortView> {

    @Inject
    public void init(ComponentModel model, Lang lang) {
        model.subscribe(this);
        setAddName(lang.buttonAdd());
        setClearName(lang.buttonClear());
    }

    @Override
    public void fillOptions(List<ProductShortView> options) {
        clearOptions();
        options.forEach(option -> addOption(option.getName(), option));
    }

    @Override
    public void refreshValue() {}
}
