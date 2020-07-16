package ru.protei.portal.ui.common.client.widget.selector.productdirection;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dto.ProductDirectionInfo;
import ru.protei.portal.ui.common.client.widget.form.FormSelector;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;

import java.util.List;

public class ProductDirectionFormSelector extends FormSelector<ProductDirectionInfo>
        implements SelectorWithModel<ProductDirectionInfo>
{

    @Inject
    public void init( ProductDirectionModel productDirectionModel ) {
        productDirectionModel.subscribe( this );
        setSelectorModel( productDirectionModel );

        setSearchAutoFocus( true );

        setDisplayOptionCreator( value -> new DisplayOption( value == null ? defaultValue : value.name ) );
    }

    @Override
    public void fillOptions( List< ProductDirectionInfo > products) {
        clearOptions();

        if (defaultValue != null) {
            addOption( null );
        }

        products.forEach(this::addOption);
    }

    public void setDefaultValue( String value ) {
        this.defaultValue = value;
    }

    private String defaultValue = null;
}