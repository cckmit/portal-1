package ru.protei.portal.ui.common.client.widget.selector.product.common;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_DevUnitState;
import ru.protei.portal.core.model.dict.En_DevUnitType;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;
import ru.protei.portal.ui.common.client.widget.selector.input.MultipleInputSelector;
import ru.protei.portal.ui.common.client.widget.selector.product.ProductModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CommonProductMultiSelector extends MultipleInputSelector<ProductShortView> implements SelectorWithModel<ProductShortView> {
    @Inject
    public void init(ProductModel model, Lang lang) {
        this.model = model;
        model.subscribe(this, En_DevUnitState.ACTIVE, null);
        setSelectorModel(model);
        setAddName(lang.buttonAdd());
        setClearName(lang.buttonClear());
    }

    @Override
    public void fillOptions(List<ProductShortView> o) {
        options.clear();
        options.addAll(o);
        fillOptions();
    }

    public void exclude(ProductShortView exclude) {
        this.exclude = exclude;
        fillOptions();
    }

    public void setTypes(En_DevUnitType... types) {
        model.subscribeAndRequest(this, null, types);
    }

    private void fillOptions() {
        clearOptions();
        options.stream()
                .filter(option -> !Objects.equals(option, exclude))
                .forEach(option -> addOption(option.getName(), option));
    }


    private List<ProductShortView> options = new ArrayList<>();
    private ProductShortView exclude = null;
    private ProductModel model;
}