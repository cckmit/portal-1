package ru.protei.portal.ui.common.client.widget.selector.product.devunit;

import com.google.inject.Inject;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.base.ModelSelector;
import ru.protei.portal.ui.common.client.widget.selector.input.MultipleInputSelector;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Мультиселектор продуктов
 */
public class DevUnitMultiSelector extends MultipleInputSelector< ProductShortView > implements ModelSelector< ProductShortView > {

    @Inject
    public void init(DevUnitModel model, Lang lang ) {
        model.subscribe( this );
        setAddName( lang.buttonAdd() );
        setClearName( lang.buttonClear() );
        setSelectorModel(model);
    }

    public void fillOptions(List<ProductShortView> o) {
        options.clear();
        options.addAll(o);
        fillOptions();
    }

    @Override
    public void refreshValue() {}

    public void setHasNullValue(boolean hasNullValue) {
        this.hasNullValue = hasNullValue;
    }

    public void exclude(ProductShortView exclude) {
        this.exclude = exclude;
        fillOptions();
    }

    private void fillOptions() {
        clearOptions();
        if (hasNullValue) {
            addOption(lang.productWithout(), new ProductShortView(CrmConstants.Product.UNDEFINED, lang.productWithout(), 0));
        }
        options.stream()
                .filter(option -> !Objects.equals(option, exclude))
                .forEach(option -> addOption(option.getName(), option));
    }

    @Inject
    private Lang lang;

    private List<ProductShortView> options = new ArrayList<>();
    private ProductShortView exclude = null;
    private boolean hasNullValue = true;
}
