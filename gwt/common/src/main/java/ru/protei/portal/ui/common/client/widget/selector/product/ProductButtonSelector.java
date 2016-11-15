package ru.protei.portal.ui.common.client.widget.selector.product;

import com.google.inject.Inject;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.widget.selector.base.ModelSelector;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;

import java.util.List;

/**
 * Button селектор с продуктами
 */
public class ProductButtonSelector extends ButtonSelector<EntityOption> implements ModelSelector<EntityOption> {

    @Inject
    public void init( ProductModel productModel) {
        productModel.subscribe(this);
        setSearchEnabled( true );
        setSearchAutoFocus( true );
    }

    public void fillOptions( List< EntityOption > products) {
        clearOptions();

        if(defaultValue != null)
            addOption( defaultValue , null );

        for ( EntityOption product : products) {
            addOption( product.getDisplayText(), product);
        }
    }

    public void setDefaultValue( String value ) {
        this.defaultValue = value;
    }

    private String defaultValue = null;

}
