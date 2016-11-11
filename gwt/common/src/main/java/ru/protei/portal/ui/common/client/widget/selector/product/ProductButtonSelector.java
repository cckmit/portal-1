package ru.protei.portal.ui.common.client.widget.selector.product;

import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.ui.common.client.widget.selector.base.ModelSelector;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;

import java.util.List;

/**
 * Button селектор с продуктами
 */
public class ProductButtonSelector extends ButtonSelector<DevUnit> implements ModelSelector<DevUnit> {

    @Inject
    public void init( ProductModel productModel) {
        productModel.subscribe(this);
    }

    public void fillOptions( List< DevUnit > products) {
        clearOptions();

        addOption( defaultValue == null? "" : defaultValue , null );
        for ( DevUnit product : products) {
            addOption( product.getName(), product);
        }
    }

    public void setDefaultValue( String value ) {
        this.defaultValue = value;
    }

    private String defaultValue = null;

}
