package ru.protei.portal.ui.equipment.client.widget.number.list;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.protei.portal.core.model.dict.En_OrganizationCode;
import ru.protei.portal.core.model.ent.DecimalNumber;
import ru.protei.portal.ui.equipment.client.widget.number.item.DecimalNumberBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Список децимальных номеров
 */
public class DecimalNumberList
        extends Composite
        implements HasValue<List<DecimalNumber>>
{
    @Inject
    public void onInit() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        fillBoxes();
    }

    public void setValidable( boolean validable ) {
        this.validable = validable;
    }

    @Override
    public List< DecimalNumber > getValue() {
        return values;
    }

    @Override
    public void setValue( List< DecimalNumber > value ) {
        setValue( value, false );
    }

    @Override
    public void setValue( List< DecimalNumber > value, boolean fireEvents ) {
        if ( fireEvents ) {
            ValueChangeEvent.fire( this, value );
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler( ValueChangeHandler< List< DecimalNumber > > handler ) {
        return addHandler( handler, ValueChangeEvent.getType() );
    }

    private void fillBoxes() {
        for ( En_OrganizationCode code : En_OrganizationCode.values() ) {
            DecimalNumberBox decimalNumberBox = boxProvider.get();
            decimalNumberBox.setOrganizationCode( code );
            decimalNumberBox.setValidable( validable );

            typeToView.put( code, decimalNumberBox );
            root.add( decimalNumberBox.asWidget() );
        }
    }

    @UiField
    HTMLPanel root;

    @Inject
    Provider<DecimalNumberBox> boxProvider;

    private List<DecimalNumber> values = new ArrayList<>();
    private boolean validable = true;

    private Map<En_OrganizationCode, DecimalNumberBox> typeToView = new HashMap<>();

    interface DecimalNumberListUiBinder extends UiBinder< HTMLPanel, DecimalNumberList > {}
    private static DecimalNumberListUiBinder ourUiBinder = GWT.create( DecimalNumberListUiBinder.class );

}