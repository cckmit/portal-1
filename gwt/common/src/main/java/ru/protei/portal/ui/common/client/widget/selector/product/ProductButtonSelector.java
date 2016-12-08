package ru.protei.portal.ui.common.client.widget.selector.product;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_DevUnitState;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.base.ModelSelector;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;

import java.util.List;

/**
 * Button селектор с продуктами
 */
public class ProductButtonSelector extends ButtonSelector<ProductShortView> implements ModelSelector<ProductShortView> {

    @Inject
    public void init( ProductModel productModel) {
        productModel.subscribe( this );
        setSearchEnabled( true );
        setSearchAutoFocus( true );
    }

    public void fillOptions( List< ProductShortView > products) {
        clearOptions();

        if(defaultValue != null) {
            addOption( defaultValue, null );
            setValue(null);
        }

        products.forEach(product -> addOption(new DisplayOption(
                        product.getName(),
                        En_DevUnitState.DEPRECATED.getId() == product.getStateId() ? "not-active" : "" ,
                        En_DevUnitState.DEPRECATED.getId() == product.getStateId() ? "fa fa-ban ban" : ""),
                product));
     }

    public void setDefaultValue( String value ) {
        this.defaultValue = value;
    }

    @Inject
    Lang lang;

    private String defaultValue = null;

}
