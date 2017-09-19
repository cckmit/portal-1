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
        implements HasValue<List<DecimalNumber>>
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
            return;
        } else {
            values.forEach( this :: createBoxAndFillValue );
        }

        if ( fireEvents ) {
            ValueChangeEvent.fire( this, value );
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler( ValueChangeHandler< List< DecimalNumber > > handler ) {
        return addHandler( handler, ValueChangeEvent.getType() );
    }

    @UiHandler( "addPamr" )
    public void onAddPamrClicked( ClickEvent event )  {
        createEmptyBox(En_OrganizationCode.PAMR);

    }

    @UiHandler( "addPdra" )
    public void onAddPdraClicked( ClickEvent event )  {
        createEmptyBox(En_OrganizationCode.PDRA);

    }

    private void clearBoxes() {
        pdraList.clear();
        pamrList.clear();
        numberBoxes.clear();
    }

    private void createBoxAndFillValue( DecimalNumber number ) {
        DecimalNumberBox box = boxProvider.get();
        box.setValue( number );
        numberBoxes.add(box);
        addToSublist(number, box);
        box.setFocusToNextButton(true);
        addNextItemHandler(box);
        addRemoveHandler( box, number );
    }

    private void addNextItemHandler(DecimalNumberBox box) {
        box.addAddHandler(event -> {
            DecimalNumber oldNumber = box.getValue();
            DecimalNumber newNumber = new DecimalNumber();
            newNumber.setOrganizationCode(oldNumber.getOrganizationCode());
            newNumber.setClassifierCode(oldNumber.getClassifierCode());
            newNumber.setRegisterNumber(oldNumber.getRegisterNumber());
            newNumber.setModification(oldNumber.getModification() + 1);

            if (!numberExists(newNumber)) {
                values.add(newNumber);
                createBoxAndFillValue(newNumber);
            }
        });
    }

    private void addRemoveHandler( DecimalNumberBox box, DecimalNumber number ) {
        box.addRemoveHandler( event -> {
            values.remove( number );
            box.removeFromParent();
            numberBoxes.remove( box );
        } );
    }

    private void createEmptyBox(En_OrganizationCode orgCode) {
        DecimalNumberBox box = boxProvider.get();
        DecimalNumber emptyNumber = new DecimalNumber();

        emptyNumber.setOrganizationCode(orgCode);
        box.setValue( emptyNumber );

        values.add( emptyNumber );
        numberBoxes.add( box );
        addToSublist(emptyNumber, box);
        addNextItemHandler(box);
        addRemoveHandler( box, emptyNumber );
    }

    private boolean numberExists(DecimalNumber number) {
        for (DecimalNumber value: values) {
            if (value.getOrganizationCode() == number.getOrganizationCode()
                && value.getModification() == number.getModification()
                    && value.getClassifierCode() == number.getClassifierCode())
            {
                return true;
            }
        }
        return false;
    }

    private void addToSublist(DecimalNumber number, DecimalNumberBox box) {
        if (number.getOrganizationCode().equals(En_OrganizationCode.PAMR)) {
            pamrList.add(box.asWidget());
        } else {
            pdraList.add(box.asWidget());
        }
    }

    @UiField
    HTMLPanel pamrList;
    @UiField
    HTMLPanel pdraList;
    @UiField
    Button addPamr;
    @UiField
    Button addPdra;

    @Inject
    Provider<DecimalNumberBox> boxProvider;
    private List<DecimalNumber> values = new ArrayList<>();

    private List<DecimalNumberBox> numberBoxes = new ArrayList<>();

    interface DecimalNumberListUiBinder extends UiBinder< HTMLPanel, DecimalNumberList > {}
    private static DecimalNumberListUiBinder ourUiBinder = GWT.create( DecimalNumberListUiBinder.class );

}