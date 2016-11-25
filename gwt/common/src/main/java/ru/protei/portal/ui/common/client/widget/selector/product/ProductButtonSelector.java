package ru.protei.portal.ui.common.client.widget.selector.product;

import com.google.inject.Inject;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.base.ModelSelector;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;

import java.util.List;

/**
 * Button селектор с продуктами
 */
public class ProductButtonSelector extends ButtonSelector<EntityOption> implements ModelSelector<EntityOption> {

    @Inject
    public void init( ProductModel productModel) {
        productModel.subscribe( this );
        setSearchEnabled( true );
        setSearchAutoFocus( true );
    }

    public void fillOptions( List< EntityOption > options) {
        clearOptions();

        if(defaultValue != null) {
            addOption( defaultValue, null );
            setValue(null);
        }

        options.forEach(option -> addOption(option.getDisplayText(),option));
     }

    public void setDefaultValue( String value ) {
        this.defaultValue = value;
    }

    @Inject
    Lang lang;

    private String defaultValue = null;

}
