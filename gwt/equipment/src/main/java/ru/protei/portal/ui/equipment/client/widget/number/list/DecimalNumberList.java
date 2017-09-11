package ru.protei.portal.ui.equipment.client.widget.number.list;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.protei.portal.core.model.dict.En_OrganizationCode;
import ru.protei.portal.core.model.ent.DecimalNumber;
import ru.protei.portal.ui.equipment.client.widget.number.item.DecimalNumberBox;

import java.util.*;

/**
 * Список децимальных номеров
 */
public class DecimalNumberList
        extends Composite
        implements HasValue<List<DecimalNumber>>, AbstractDecimalNumberItemHandler
{
    @Inject
    public void onInit() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
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
        this.values = value;
        if ( values == null ) {
            values = new ArrayList<>();
        }

        clearBoxes();
        if ( values == null || values.isEmpty() ) {
            createEmptyBox();
        } else {
            values.forEach( this :: createBoxAndFillValue );
        }

        checkAddButtonState();

        if ( fireEvents ) {
            ValueChangeEvent.fire( this, value );
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler( ValueChangeHandler< List< DecimalNumber > > handler ) {
        return addHandler( handler, ValueChangeEvent.getType() );
    }

    @UiHandler( "add" )
    public void onAddClicked( ClickEvent event )  {
        boolean isFull = values.size() == En_OrganizationCode.values().length;
        if ( !isFull ) {
            numberBoxes.forEach( ( s) -> s.enabledOrganizationCode().setEnabled( false )  );
            createEmptyBox();
        }

        checkAddButtonState();
    }

    private void clearBoxes() {
        list.clear();
        numberBoxes.clear();
    }

    private void createBoxAndFillValue( DecimalNumber number ) {
        DecimalNumberBox box = boxProvider.get();
        box.setValue( number );
        box.setItemHandler(this);

        numberBoxes.add( box );
        list.add( box.asWidget() );
        addRemoveHandler( box, number );
    }

    private void addRemoveHandler( DecimalNumberBox box, DecimalNumber number ) {
        box.addRemoveHandler( event -> {
            values.remove( number );
            list.remove( box );
            numberBoxes.remove( box );

            checkAddButtonState();
        } );
    }

    private void createEmptyBox() {
        DecimalNumberBox box = boxProvider.get();
        box.setItemHandler(this);
        DecimalNumber emptyNumber = new DecimalNumber();

        Set<En_OrganizationCode> availableValues = new HashSet<>( Arrays.asList( En_OrganizationCode.values() ) );
        for ( DecimalNumberBox numberBox : numberBoxes ) {
            availableValues.remove( numberBox.getValue().getOrganizationCode() );
        }
        box.fillOrganizationCodesOption( availableValues );

        box.setValue( emptyNumber );
        box.enabledOrganizationCode().setEnabled( true );

        values.add( emptyNumber );
        numberBoxes.add( box );
        list.add( box.asWidget() );
        addRemoveHandler( box, emptyNumber );
    }

    @Override
    public void onEditComplete(DecimalNumberBox box) {
        DecimalNumber number = box.getValue();
        DecimalNumber newNumber = new DecimalNumber();
        newNumber.setOrganizationCode(number.getOrganizationCode());
        newNumber.setClassifierCode(number.getClassifierCode());
        newNumber.setRegisterNumber(number.getRegisterNumber());
        newNumber.setModification(number.getModification() + 1);
        values.add( newNumber );

        createBoxAndFillValue(newNumber);
    }

    private void checkAddButtonState() {
        add.setVisible( En_OrganizationCode.values().length - values.size() >= 1 );
    }

    @UiField
    HTMLPanel list;

    @UiField
    Button add;

    @Inject
    Provider<DecimalNumberBox> boxProvider;
    private List<DecimalNumber> values = new ArrayList<>();

    private List<DecimalNumberBox> numberBoxes = new ArrayList<>();

    interface DecimalNumberListUiBinder extends UiBinder< HTMLPanel, DecimalNumberList > {}
    private static DecimalNumberListUiBinder ourUiBinder = GWT.create( DecimalNumberListUiBinder.class );

}