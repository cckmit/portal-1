package ru.protei.portal.ui.common.client.widget.selector.product.component;

import com.google.inject.Inject;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;
import ru.protei.portal.ui.common.client.widget.selector.input.MultipleInputSelector;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ComponentMultiSelector extends MultipleInputSelector<ProductShortView> implements SelectorWithModel<ProductShortView> {

    @Inject
    public void init(ComponentModel model, Lang lang) {
        model.subscribe(this);
        setAddName(lang.buttonAdd());
        setClearName(lang.buttonClear());
        setSelectorModel(model);
    }

    @Override
    public void fillOptions(List<ProductShortView> o) {
        options.clear();
        options.addAll(o);
        fillOptions();
    }

    @Override
    public void refreshValue() {}

    public void exclude(ProductShortView exclude) {
        this.exclude = exclude;
        fillOptions();
    }

    private void fillOptions() {
        clearOptions();
        options.stream()
                .filter(option -> !Objects.equals(option, exclude))
                .forEach(option -> addOption(option.getName(), option));
    }

    private List<ProductShortView> options = new ArrayList<>();
    private ProductShortView exclude = null;
}
