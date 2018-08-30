package ru.protei.portal.ui.common.client.widget.decimalnumber.multiple;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.protei.portal.core.model.dict.En_OrganizationCode;
import ru.protei.portal.core.model.ent.DecimalNumber;
import ru.protei.portal.core.model.struct.DecimalNumberQuery;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.decimalnumber.box.DecimalNumberBox;
import ru.protei.portal.ui.common.client.widget.decimalnumber.box.DecimalNumberBoxHandler;
import ru.protei.portal.ui.common.client.widget.decimalnumber.provider.DecimalNumberDataProvider;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.winter.web.common.client.common.DisplayStyle;

import java.util.*;

/**
 * Список децимальных номеров
 */
public class MultipleDecimalNumberInput
        extends Composite
        implements HasValue<List<DecimalNumber>>, DecimalNumberBoxHandler {
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
        setNewValues(value, true);

        if ( fireEvents ) {
            ValueChangeEvent.fire( this, value );
        }
    }

    public void setNotEditableValue(List<DecimalNumber> decimalNumbers) {
        setNewValues(decimalNumbers, false);
    }

    @Override
    public HandlerRegistration addValueChangeHandler( ValueChangeHandler< List< DecimalNumber > > handler ) {
        return addHandler( handler, ValueChangeEvent.getType() );
    }

    @Override
    public void onGetNextNumber( DecimalNumberBox box ) {
        DecimalNumberQuery query = new DecimalNumberQuery( box.getValue(), getUsedRegNumbersByClassifier( box.getValue() ) );

        dataProvider.getNextAvailableRegisterNumber( query, new RequestCallback< Integer >() {
            @Override
            public void onError( Throwable throwable ) {
                box.showMessage( lang.equipmentErrorGetNextAvailableNumber(), DisplayStyle.DANGER );
            }

            @Override
            public void onSuccess( Integer registerNumber ) {
                DecimalNumber number = box.getValue();
                number.setRegisterNumber( registerNumber );
                number.setModification( null );

                box.setValue( number );
                box.setFocusToRegisterNumberField( true );
            }
        } );
    }

    @Override
    public void onGetNextModification( DecimalNumberBox box ) {
        getNextModification( box, false );
    }

    @UiHandler( "addPamr" )
    public void onAddPamrClicked( ClickEvent event )  {
        createEmptyBox(En_OrganizationCode.PAMR);
    }

    @UiHandler( "addPdra" )
    public void onAddPdraClicked( ClickEvent event )  {
        createEmptyBox(En_OrganizationCode.PDRA);
    }

    public boolean checkIfCorrect(){
        boolean isValid = true;
        for(DecimalNumberBox box: numberBoxes){
            DecimalNumber number = box.getValue();
            if(number.getClassifierCode() == null || number.getRegisterNumber() == null){ // number.getModification() is nullable judging by DB
                box.showMessage( lang.errFieldsRequired(), DisplayStyle.DANGER);
                isValid = false;
                continue;
            }

            if ( !validateNumber(box) ) {
                isValid = false;
            }
        }
        return isValid;
    }

    private void setNewValues(List<DecimalNumber> value, boolean isEabled) {
        this.values = value;
        if ( values == null ) {
            values = new ArrayList<>();
        }

        clearBoxes();
        if ( values == null || values.isEmpty() ) {
            createEmptyBox(En_OrganizationCode.PAMR);
        } else {
            values.forEach(number -> createBoxAndFillValue(number, isEabled) );
        }
    }

    private void clearBoxes() {
        pdraList.clear();
        pamrList.clear();
        numberBoxes.clear();
        occupiedNumbers.clear();
    }

    private void createBoxAndFillValue( DecimalNumber number) {
        createBoxAndFillValue(number, true);
    }

    private void createBoxAndFillValue( DecimalNumber number, boolean isEnabled) {
        DecimalNumberBox box = boxProvider.get();

        box.setValue( number );
        box.setHandler(this);
        box.addAddHandler( event -> getNextModification( box, true ) );
        box.addRemoveHandler( event -> {
            values.remove( number );
            box.removeFromParent();
            numberBoxes.remove( box );
            checkIfCorrect();

        } );
        box.addValueChangeHandler( event -> {
            if ( !event.getValue().isEmpty() ) {
                if(validateNumber( box )) {
                    checkExistNumber(box);
                }
            }
        } );
        box.setEnabled(isEnabled);

        numberBoxes.add(box);
        placeBox(number, box);
    }

    private void createEmptyBox(En_OrganizationCode orgCode) {
        DecimalNumber emptyNumber = new DecimalNumber();
        emptyNumber.setOrganizationCode(orgCode);
        values.add( emptyNumber );

        createBoxAndFillValue( emptyNumber );
    }

    private void placeBox( DecimalNumber number, DecimalNumberBox box ) {
        if (number.getOrganizationCode().equals(En_OrganizationCode.PAMR)) {
            pamrList.add(box.asWidget());
        } else {
            pdraList.add(box.asWidget());
        }
    }

    private void getNextModification( DecimalNumberBox box, boolean needCreareNewBox ) {
        DecimalNumber value = box.getValue();
        DecimalNumberQuery query = new DecimalNumberQuery( box.getValue(), getUsedModificationsByClassifierAndRegNumber( value ) );

        dataProvider.getNextAvailableModification( query, new RequestCallback< Integer>() {
            @Override
            public void onError( Throwable throwable ) {
                box.showMessage( lang.equipmentErrorGetNextAvailableNumber(), DisplayStyle.DANGER );
            }

            @Override
            public void onSuccess( Integer modification ) {
                if ( needCreareNewBox ) {
                    DecimalNumber newNumber = new DecimalNumber( value.getOrganizationCode(), value.getClassifierCode(),
                            value.getRegisterNumber(), modification );
                    values.add( newNumber );
                    createBoxAndFillValue( newNumber );
                } else {
                    value.setModification( modification );
                    box.setValue( value );
                    box.clearBoxState();
                }
            }
        } );
    }

    private Set<Integer> getUsedRegNumbersByClassifier( DecimalNumber number ) {
        Set<Integer> registerNumbers = new HashSet<>();
        values.forEach( value -> {
            if ( value == null || value.isEmpty() ){
                return;
            }
            if ( value.getOrganizationCode() == number.getOrganizationCode()
                    && Objects.equals( value.getClassifierCode(), number.getClassifierCode() ) ) {
                registerNumbers.add( value.getRegisterNumber() );
            }
        } );

        return registerNumbers;
    }

    private Set<Integer> getUsedModificationsByClassifierAndRegNumber( DecimalNumber number ) {
        Set<Integer> modifications = new HashSet<>();

        values.forEach( value -> {
            if ( value == null || ( value.isEmpty() || value.getModification() == null) ) {
                return;
            }
            if ( value.getOrganizationCode() == number.getOrganizationCode()
                    && Objects.equals(value.getClassifierCode(), number.getClassifierCode())
                    && Objects.equals(value.getRegisterNumber(), number.getRegisterNumber())) {
                modifications.add(value.getModification());
            }
        } );

        return modifications;
    }

    private boolean validateNumber( final DecimalNumberBox box ) {
        box.clearBoxState();
        if ( isSameNumberInList(values, box.getValue() ) ) {
            box.showMessage(lang.equipmentNumberAlreadyInList(), DisplayStyle.DANGER);
            return false;
        }
        if (occupiedNumbers.contains( box.getValue()) ){
                box.showGetNextNumberMessage();
            return false;
        }

        return true;
    }

    private void checkExistNumber(final DecimalNumberBox box) {

        dataProvider.checkIfExistDecimalNumber(box.getValue(), new RequestCallback<Boolean>() {
            @Override
            public void onError(Throwable throwable) {
                box.showMessage(lang.equipmentErrorCheckNumber(), DisplayStyle.DANGER);
            }

            @Override
            public void onSuccess(Boolean result) {
                if (result) {
                    occupiedNumbers.add(box.getValue());
                } else {
                    occupiedNumbers.remove(box.getValue());
                }
                checkIfCorrect();
            }
        });

    }

    private boolean isSameNumberInList(Collection<DecimalNumber> values, DecimalNumber number) {
        return values.stream().anyMatch( value -> !number.equals( value ) && number.isSameNumber( value ) );
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
    Lang lang;
    @Inject
    DecimalNumberDataProvider dataProvider;
    @Inject
    Provider<DecimalNumberBox> boxProvider;

    Collection<DecimalNumber> occupiedNumbers = new TreeSet<>(new Comparator<DecimalNumber>() {
        @Override
        public int compare(DecimalNumber o1, DecimalNumber o2) {
            return o1.isSameNumber(o2)?0:1;
        }
    });

    private List<DecimalNumber> values = new ArrayList<>();

    private List<DecimalNumberBox> numberBoxes = new ArrayList<>();

    interface DecimalNumberListUiBinder extends UiBinder< HTMLPanel, MultipleDecimalNumberInput> {}
    private static DecimalNumberListUiBinder ourUiBinder = GWT.create( DecimalNumberListUiBinder.class );

}