package ru.protei.portal.ui.common.client.widget.selector.product.devunit;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_DevUnitState;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;

import java.util.List;

/**
 * Button селектор с продуктами
 */
public class DevUnitButtonSelector extends ButtonSelector<ProductShortView> implements SelectorWithModel<ProductShortView> {

    @Inject
    public void init( DevUnitModel devUnitModel) {
        setSelectorModel(devUnitModel);
        setSearchEnabled( true );
        setSearchAutoFocus( true );

        setDisplayOptionCreator( value -> {
            if ( value == null ) {
                return new DisplayOption( defaultValue );
            }

            return new DisplayOption(
                    value.getName(),
                    En_DevUnitState.DEPRECATED.getId() == value.getStateId() ? "not-active" : "" ,
                    En_DevUnitState.DEPRECATED.getId() == value.getStateId() ? "fa fa-ban ban" : "");
        } );
    }

    public void setDefaultValue( String value ) {
        this.defaultValue = value;
    }

    public void fillOptions( List< ProductShortView > products) {
        clearOptions();

        if( defaultValue != null ) {
            addOption( null );
        }
        products.forEach( this :: addOption );
     }

    private String defaultValue = null;
}
