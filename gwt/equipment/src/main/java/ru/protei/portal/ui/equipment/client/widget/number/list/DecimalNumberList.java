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
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.equipment.client.provider.AbstractDecimalNumberDataProvider;
import ru.protei.portal.ui.equipment.client.widget.number.item.AbstractBoxHandler;
import ru.protei.portal.ui.equipment.client.widget.number.item.DecimalNumberBox;
import ru.protei.winter.web.common.client.common.DisplayStyle;

import java.util.*;

/**
 * Список децимальных номеров
 */
public class DecimalNumberList
        extends Composite
        implements HasValue<List<DecimalNumber>>, AbstractBoxHandler
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

    public boolean numberExists(DecimalNumber number) {
        for (DecimalNumber value: values) {
            if (number.equals(value)) {
                continue;
            }
            if (value.getOrganizationCode() == number.getOrganizationCode()
                && compare(value.getModification(), number.getModification())
                && compare(value.getClassifierCode(), number.getClassifierCode())
                && compare(value.getRegisterNumber(), number.getRegisterNumber()))

            {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<Integer> getRegNumbersListWithSpecificCode(Integer classifierCode) {
        List<Integer> resultList = new ArrayList<Integer>();
        for (DecimalNumber value: values) {
            if (value == null || value.getClassifierCode() == null || value.getRegisterNumber() == null) {
                continue;
            }
            if (compare(value.getClassifierCode(), classifierCode)) {
                resultList.add(value.getRegisterNumber());
            }
        }
        return resultList;
    }

    private void clearBoxes() {
        pdraList.clear();
        pamrList.clear();
        numberBoxes.clear();
    }

    private void createBoxAndFillValue( DecimalNumber number) {
        DecimalNumberBox box = boxProvider.get();
        box.setBoxHandler(this);
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
            final DecimalNumber newNumber = new DecimalNumber();
            newNumber.setOrganizationCode(oldNumber.getOrganizationCode());
            newNumber.setClassifierCode(oldNumber.getClassifierCode());
            newNumber.setRegisterNumber(oldNumber.getRegisterNumber());
            newNumber.setModification(oldNumber.getModification());
            List<Integer> mods = makeModListWithSameCodeAndRegNumber(newNumber.getClassifierCode(), newNumber.getRegisterNumber());

            dataProvider.getNextAvailableModificationNotContainsInList(mods, String.valueOf(newNumber.getClassifierCode()),
                    newNumber.getOrganizationCode().name(), String.valueOf(newNumber.getRegisterNumber()), new RequestCallback<DecimalNumber>() {
                        @Override
                        public void onError(Throwable throwable) {
                            box.setErrorMessage();
                        }

                        @Override
                        public void onSuccess(DecimalNumber result) {
                            newNumber.setModification(result.getModification());
                            values.add(newNumber);
                            createBoxAndFillValue(newNumber);
                        }
                    });
        });
    }

    private List<Integer> makeModListWithSameCodeAndRegNumber(Integer classifierCode, Integer registerNumber) {
        List<Integer> modsList = new ArrayList<Integer>();

        for (DecimalNumber value: values) {
            if (value == null || value.getClassifierCode() == null || value.getRegisterNumber() == null
                    || value.getModification() == null) {
                continue;
            }
            if (compare(value.getClassifierCode(), classifierCode) && compare(value.getRegisterNumber(), registerNumber)) {
                modsList.add(value.getModification());
            }
        }
        return modsList;


    }

    private void addRemoveHandler(final DecimalNumberBox box, final DecimalNumber number ) {
        box.addRemoveHandler( event -> {
            values.remove( number );
            box.removeFromParent();
            numberBoxes.remove( box );
        } );
    }

    private void createEmptyBox(En_OrganizationCode orgCode) {
        DecimalNumberBox box = boxProvider.get();
        box.setBoxHandler(this);
        DecimalNumber emptyNumber = new DecimalNumber();

        emptyNumber.setOrganizationCode(orgCode);
        box.setValue( emptyNumber );

        values.add( emptyNumber );
        numberBoxes.add( box );
        addToSublist(emptyNumber, box);
        addNextItemHandler(box);
        addRemoveHandler( box, emptyNumber );
    }

    private boolean compare(Integer number, Integer value) {
        return (number == null  ? value == null  : number.equals(value));
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
    AbstractDecimalNumberDataProvider dataProvider;
    @Inject
    Provider<DecimalNumberBox> boxProvider;

    private List<DecimalNumber> values = new ArrayList<>();

    private List<DecimalNumberBox> numberBoxes = new ArrayList<>();

    interface DecimalNumberListUiBinder extends UiBinder< HTMLPanel, DecimalNumberList > {}
    private static DecimalNumberListUiBinder ourUiBinder = GWT.create( DecimalNumberListUiBinder.class );

}