package ru.protei.portal.ui.common.client.widget.selector.productdirection;

import com.google.inject.Inject;
import ru.protei.portal.core.model.struct.ProductDirectionInfo;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.base.ModelSelector;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;

import java.util.List;

/**
 * Button селектор с продуктами
 */
public class ProductDirectionButtonSelector
        extends ButtonSelector<ProductDirectionInfo>
        implements ModelSelector<ProductDirectionInfo>
{

    @Inject
    public void init( ProductDirectionModel productDirectionModel ) {
        productDirectionModel.subscribe( this );
        setSearchEnabled( true );
        setSearchAutoFocus( true );

        setDisplayOptionCreator( value -> new DisplayOption( value == null ? defaultValue : value.name ) );
    }

    @Override
    public void fillOptions( List< ProductDirectionInfo > products) {
        clearOptions();
        if( defaultValue != null ) {
            addOption( null );
            setValue( null );
        }

        products.forEach(this::addOption);
     }

    public void setDefaultValue( String value ) {
        this.defaultValue = value;
    }

    private String defaultValue = null;

}