package ru.protei.portal.ui.common.client.widget.selector.product.devunit;

import com.google.inject.Inject;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.base.ModelSelector;
import ru.protei.portal.ui.common.client.widget.selector.input.MultipleInputSelector;

import java.util.List;

/**
 * Мультиселектор продуктов
 */
public class DevUnitMultiSelector extends MultipleInputSelector< ProductShortView > implements ModelSelector< ProductShortView > {

    @Inject
    public void init(DevUnitModel model, Lang lang ) {
        model.subscribe( this );
        setAddName( lang.buttonAdd() );
        setClearName( lang.buttonClear() );
    }

    public void fillOptions( List< ProductShortView > options ) {
        clearOptions();
        if (hasNullValue) {
            addOption(lang.productWithout(), new ProductShortView(CrmConstants.Product.UNDEFINED, lang.productWithout(), 0));
        }
        for ( ProductShortView option : options ) {
            addOption( option.getName(), option );
        }
    }

    @Override
    public void refreshValue() {}

    public void setHasNullValue(boolean hasNullValue) {
        this.hasNullValue = hasNullValue;
    }

    @Inject
    private Lang lang;

    private boolean hasNullValue = true;
}
