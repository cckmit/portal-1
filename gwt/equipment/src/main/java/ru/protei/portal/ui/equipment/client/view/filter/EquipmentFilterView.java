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
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.common.FixedPositioner;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.company.CompanySelector;
import ru.protei.portal.ui.common.client.widget.selector.sortfield.ModuleType;
import ru.protei.portal.ui.common.client.widget.selector.sortfield.SortFieldSelector;
import ru.protei.portal.ui.common.shared.model.DecimalNumber;
import ru.protei.portal.ui.common.shared.model.OrganizationCode;
import ru.protei.portal.ui.equipment.client.activity.filter.AbstractEquipmentFilterActivity;
import ru.protei.portal.ui.equipment.client.activity.filter.AbstractEquipmentFilterView;
import ru.protei.portal.ui.equipment.client.widget.number.DecimalNumberBox;
import ru.protei.portal.ui.equipment.client.widget.organization.OrganizationBtnGroup;

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
        switchOffAndResetDNumbers();
    }

    @Override
    public HasValue< String > name() {
        return name;
    }

    @Override
    public String getClassifierCode() {
        DecimalNumber value = pdraNum.getValue();
        if ( value == null ) {
            return null;
        }

        return value.getClassifierCode();
    }

    @Override
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

    @Override
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

    @UiHandler( "resetBtn" )
    public void onResetClicked ( ClickEvent event ) {
        if ( activity != null ) {
            resetFilter();
            activity.onFilterChanged();
        }
    }

    @UiHandler( "name" )
    public void onKeyUpSearch( KeyUpEvent event ) {
        timer.cancel();
        timer.schedule( 300 );
    }

    @UiHandler( "organizationCode" )
    public void onSelectOrganizationCode( ValueChangeEvent<Set<OrganizationCode> > event ) {
        Set<OrganizationCode> values = event.getValue();

        if ( values == null || values.isEmpty() ) {
            switchOffAndResetDNumbers();
            return;
        }

        pamrNum.setVisible( values.contains( OrganizationCode.PAMR ) );
        pdraNum.setVisible( values.contains( OrganizationCode.PDRA ) );
    }

    @UiHandler( {"pamrNum", "pdraNum"} )
    public void onNumberChanged( ValueChangeEvent<DecimalNumber> event ) {
        timer.cancel();
        timer.schedule( 300 );
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
        pamrNum.setVisible( false );
        pdraNum.setVisible( false );

        pamrNum.setValue( null );
        pdraNum.setValue( null );
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
    FixedPositioner positioner;


    AbstractEquipmentFilterActivity activity;

    private static EquipmentFilterView.ContactFilterViewUiBinder ourUiBinder = GWT.create( EquipmentFilterView.ContactFilterViewUiBinder.class );
    interface ContactFilterViewUiBinder extends UiBinder<HTMLPanel, EquipmentFilterView> {}
}