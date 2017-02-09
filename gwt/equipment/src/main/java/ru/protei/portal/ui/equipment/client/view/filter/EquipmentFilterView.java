package ru.protei.portal.ui.equipment.client.view.filter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_EquipmentStage;
import ru.protei.portal.core.model.dict.En_EquipmentType;
import ru.protei.portal.ui.common.client.common.FixedPositioner;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.core.model.ent.DecimalNumber;
import ru.protei.portal.core.model.dict.En_OrganizationCode;
import ru.protei.portal.ui.equipment.client.activity.filter.AbstractEquipmentFilterActivity;
import ru.protei.portal.ui.equipment.client.activity.filter.AbstractEquipmentFilterView;
import ru.protei.portal.ui.equipment.client.widget.number.DecimalNumberBox;
import ru.protei.portal.ui.equipment.client.widget.organization.OrganizationBtnGroup;
import ru.protei.portal.ui.equipment.client.widget.stage.EquipmentStageOptionList;
import ru.protei.portal.ui.equipment.client.widget.type.EquipmentTypeBtnGroup;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Представление фильтра оборудования
 */
public class EquipmentFilterView extends Composite implements AbstractEquipmentFilterView {

    @Inject
    public void onInit() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        name.getElement().setPropertyString( "placeholder", lang.equipmentSearchName() );
    }


    @Override
    public void setActivity( AbstractEquipmentFilterActivity activity ) {
        this.activity = activity;
    }

    @Override
    public void resetFilter() {
        name.setValue( null );
        organizationCode.setValue( new HashSet<>() );
        types.setValue( null );
        stages.setValue( null );
        switchOffAndResetDNumbers();
    }

    @Override
    public HasValue< String > name() {
        return name;
    }

    public String getClassifierCode() {
        DecimalNumber value = pdraNum.getValue();
        if ( value == null ) {
            return null;
        }

        return value.getClassifierCode();
    }

    public String getPDRA_RegisterNumber() {
        DecimalNumber value = pdraNum.getValue();
        if ( value == null ) {
            return null;
        }

        String result = value.getRegisterNumber();
        if ( value.getModification() != null && !value.getModification().isEmpty() ) {
            result += "-" + value.getModification();
        }

        return result;
    }

    public String getPAMR_RegisterNumber() {
        DecimalNumber value = pamrNum.getValue();
        if ( value == null ) {
            return null;
        }

        String result = value.getRegisterNumber();
        if ( value.getModification() != null && !value.getModification().isEmpty() ) {
            result += "-" + value.getModification();
        }

        return result;
    }

    @Override
    public HasValue<Set<En_EquipmentStage>> stages() {
        return stages;
    }

    @Override
    public HasValue<Set<En_EquipmentType>> types() {
        return types;
    }

    @Override
    public List<DecimalNumber> getNumbers() {
        return null;
    }

    @UiHandler( "resetBtn" )
    public void onResetClicked ( ClickEvent event ) {
        if ( activity != null ) {
            resetFilter();
            activity.onFilterChanged();
        }
    }

    @UiHandler( "name" )
    public void onKeyUpSearch( KeyUpEvent event ) {
        fireChangeTimer();
    }

    @UiHandler( "organizationCode" )
    public void onSelectOrganizationCode( ValueChangeEvent<Set<En_OrganizationCode > > event ) {
        Set<En_OrganizationCode > values = event.getValue();

        if ( values == null || values.isEmpty() ) {
            switchOffAndResetDNumbers();
            return;
        }

        pamrNum.setEnabled( values.contains( En_OrganizationCode.PAMR ) );
        pdraNum.setEnabled( values.contains( En_OrganizationCode.PDRA ) );
    }

    @UiHandler( "pdraNum" )
    public void onPdraNumberChanged( ValueChangeEvent<DecimalNumber> event ) {
        DecimalNumber pdra = pdraNum.getValue();
        if ( pdra != null ) {
            pamrNum.setClassifierCode( pdra.getClassifierCode() );
        }

        fireChangeTimer();
    }

    @UiHandler( "pamrNum" )
    public void onPamrNumberChanged( ValueChangeEvent<DecimalNumber> event ) {
        DecimalNumber pamr = pamrNum.getValue();
        if ( pamr != null ) {
            pdraNum.setClassifierCode( pamr.getClassifierCode() );
        }

        fireChangeTimer();
    }

    @UiHandler( "types" )
    public void onTypeSelected( ValueChangeEvent<Set<En_EquipmentType>> event ) {
        fireChangeTimer();
    }

    @UiHandler( "stages" )
    public void onStageSelected( ValueChangeEvent<Set<En_EquipmentStage>> event ) {
        fireChangeTimer();
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        positioner.watch(this, FixedPositioner.NAVBAR_TOP_OFFSET);
    }

    @Override
    protected void onDetach() {
        super.onDetach();
        positioner.ignore(this);
    }

    private void switchOffAndResetDNumbers() {
        pamrNum.setEnabled( false );
        pdraNum.setEnabled( false );

        pamrNum.setValue( null );
        pdraNum.setValue( null );
    }

    private void fireChangeTimer() {
        timer.cancel();
        timer.schedule( 300 );
    }

    Timer timer = new Timer() {
        @Override
        public void run() {
            if ( activity != null ) {
                activity.onFilterChanged();
            }
        }
    };

    @UiField
    Button resetBtn;

    @Inject
    @UiField
    Lang lang;
    @UiField
    TextBox name;
    @Inject
    @UiField(provided = true)
    DecimalNumberBox pdraNum;
    @Inject
    @UiField(provided = true)
    DecimalNumberBox pamrNum;
    @Inject
    @UiField(provided = true)
    OrganizationBtnGroup organizationCode;
    @Inject
    @UiField(provided = true)
    EquipmentStageOptionList stages;
    @Inject
    @UiField(provided = true)
    EquipmentTypeBtnGroup types;

    @Inject
    FixedPositioner positioner;


    AbstractEquipmentFilterActivity activity;

    private static EquipmentFilterView.ContactFilterViewUiBinder ourUiBinder = GWT.create( EquipmentFilterView.ContactFilterViewUiBinder.class );
    interface ContactFilterViewUiBinder extends UiBinder<HTMLPanel, EquipmentFilterView> {}
}