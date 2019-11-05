package ru.protei.portal.ui.common.client.widget.selector.product.devunit;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_DevUnitState;
import ru.protei.portal.core.model.dict.En_DevUnitType;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;
import ru.protei.portal.ui.common.client.widget.selector.input.MultipleInputSelector;
import ru.protei.portal.ui.common.client.widget.selector.product.ProductModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Мультиселектор продуктов
 */
public class DevUnitMultiSelector extends MultipleInputSelector<ProductShortView> implements SelectorWithModel<ProductShortView> {

    @Inject
    public void init(ProductModel model, Lang lang) {
        this.model = model;
        model.subscribe(this, En_DevUnitState.ACTIVE, null);
        setSelectorModel(model);
        setAddName(lang.buttonAdd());
        setClearName(lang.buttonClear());
    }

    @Override
    public boolean requestByOnLoad() {
        return requestByOnLoad;
    }

    public void fillOptions(List<ProductShortView> o) {
        options.clear();
        options.addAll(o);
        fillOptions();
    }

    public void setHasNullValue(boolean hasNullValue) {
        this.hasNullValue = hasNullValue;
    }

    public void exclude(ProductShortView exclude) {
        this.exclude = exclude;
        fillOptions();
    }

    public void refreshOptions() {
        model.getOptionsFromServer(this);
    }

    public void setTypes(En_DevUnitType... enDevUnitTypes) {
        if (model != null) {
            model.updateQueryAndRequest(this, En_DevUnitState.ACTIVE, enDevUnitTypes);
        }
    }

    public void setRequestByOnLoad(boolean requestByOnLoad) {
        this.requestByOnLoad = requestByOnLoad;
    }

    private void fillOptions() {
        clearOptions();
        if (hasNullValue) {
            addOption(lang.productWithout(), new ProductShortView(CrmConstants.Product.UNDEFINED, lang.productWithout(), 0));
        }
        options.stream()
                .filter(option -> !Objects.equals(option, exclude))
                .forEach(option -> addOption((option.getName() + (HelperFunc.isEmpty(option.getAliases()) ? "" : " (" + option.getAliases() + ")")), option));
    }

    @Inject
    private Lang lang;

    private boolean requestByOnLoad = true;
    private List<ProductShortView> options = new ArrayList<>();
    private ProductShortView exclude = null;
    private boolean hasNullValue = true;
    protected ProductModel model;
}
