package ru.protei.portal.ui.common.client.widget.selector.product;

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
public class ProductMultiSelector extends MultipleInputSelector< ProductShortView > implements ModelSelector< ProductShortView > {

    @Inject
    public void init( ProductModel model, Lang lang ) {
        model.subscribe( this );
        setAddName( lang.buttonAdd() );
        setClearName( lang.buttonClear() );
    }

    public void fillOptions( List< ProductShortView > options ) {
        clearOptions();
        addOption(lang.productWithout(), new ProductShortView(CrmConstants.Product.UNDEFINED, lang.productWithout(), 0));
        for ( ProductShortView option : options ) {
            addOption( option.getName(), option );
        }
    }

    @Override
    public void refreshValue() {

    }

    @Inject
    private Lang lang;
}
