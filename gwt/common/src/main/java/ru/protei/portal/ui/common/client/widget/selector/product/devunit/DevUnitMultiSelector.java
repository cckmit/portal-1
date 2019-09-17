package ru.protei.portal.ui.common.client.widget.selector.product.devunit;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CompanyCategory;
import ru.protei.portal.core.model.dict.En_DevUnitState;
import ru.protei.portal.core.model.dict.En_DevUnitType;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;
import ru.protei.portal.ui.common.client.widget.selector.company.CompanyModel;
import ru.protei.portal.ui.common.client.widget.selector.input.MultipleInputSelector;
import ru.protei.portal.ui.common.client.widget.selector.product.ProductModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Мультиселектор продуктов
 */
public class DevUnitMultiSelector extends MultipleInputSelector< ProductShortView > implements SelectorWithModel< ProductShortView > {

    @Inject
    public void init(ProductModel model, Lang lang ) {
        this.model = model;
        model.subscribe(this, En_DevUnitState.ACTIVE, null);
        setSelectorModel(model);
        setAddName( lang.buttonAdd() );
        setClearName( lang.buttonClear() );
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
    public void updateQuery(En_DevUnitState enDevUnitState, En_DevUnitType enDevUnitType ) {
        if ( model != null ) {
            model.updateQuery(this, enDevUnitState, enDevUnitType);
        }
    }

    public void refreshOptions() {
        model.getOptionsFromServer(this);
    }

    public void setTypes(En_DevUnitType... enDevUnitTypes) {
        if (model != null) {
            model.updateQueryAndRequest(this, En_DevUnitState.ACTIVE, enDevUnitTypes);
        }
    }

    public void setLazy(boolean isLazy) {
        if (model != null) {
            model.setLasy(isLazy);
        }
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
    protected ProductModel model;
}
