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
import ru.protei.portal.ui.equipment.client.widget.number.item.DecimalNumberBox;
import ru.protei.portal.ui.equipment.client.widget.number.list.DecimalNumberList;
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
        organizationCode.setValue( null );
        types.setValue( null );
        stages.setValue( null );
        numbers.setValue( null );
    }

    @Override
    public HasValue< String > name() {
        return name;
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
    public HasValue< List< DecimalNumber > > numbers() {
        return numbers;
    }

    @Override
    public HasValue< Set<En_OrganizationCode> > organizationCodes() {
        return organizationCode;
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
        fireChangeTimer();
    }

    @UiHandler( "numbers" )
    public void onNumbersChanged( ValueChangeEvent<List<DecimalNumber> > event ) {
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
    OrganizationBtnGroup organizationCode;
    @Inject
    @UiField(provided = true)
    EquipmentStageOptionList stages;
    @Inject
    @UiField(provided = true)
    EquipmentTypeBtnGroup types;
    @Inject
    @UiField(provided = true)
    DecimalNumberList numbers;

    @Inject
    FixedPositioner positioner;


    AbstractEquipmentFilterActivity activity;

    private static EquipmentFilterView.ContactFilterViewUiBinder ourUiBinder = GWT.create( EquipmentFilterView.ContactFilterViewUiBinder.class );
    interface ContactFilterViewUiBinder extends UiBinder<HTMLPanel, EquipmentFilterView> {}
}