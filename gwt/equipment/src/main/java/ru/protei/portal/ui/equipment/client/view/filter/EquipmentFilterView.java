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
import com.google.inject.Provider;
import ru.protei.portal.core.model.dict.En_EquipmentType;
import ru.protei.portal.core.model.dict.En_OrganizationCode;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.view.EquipmentShortView;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.cleanablesearchbox.CleanableSearchBox;
import ru.protei.portal.ui.common.client.widget.organization.OrganizationBtnGroupMulti;
import ru.protei.portal.ui.common.client.widget.selector.equipment.EquipmentButtonSelector;
import ru.protei.portal.ui.common.client.widget.selector.equipment.EquipmentModel;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeButtonSelector;
import ru.protei.portal.ui.common.client.widget.selector.sortfield.SortFieldSelector;
import ru.protei.portal.ui.equipment.client.activity.filter.AbstractEquipmentFilterActivity;
import ru.protei.portal.ui.equipment.client.activity.filter.AbstractEquipmentFilterView;
import ru.protei.portal.ui.equipment.client.widget.type.EquipmentTypeBtnGroupMulti;

import java.util.Set;

/**
 * Представление фильтра оборудования
 */
public class EquipmentFilterView extends Composite implements AbstractEquipmentFilterView {

    @Inject
    public void onInit() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
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
        classifierCode.setValue( null );
        regNum.setValue( null );
        manager.setValue( null );
        sortField.setValue( En_SortField.name );
        sortDir.setValue( false );
        equipment.setValue(null);
    }

    @Override
    public HasValue< String > name() {
        return name;
    }

    @Override
    public HasValue< PersonShortView > manager() {
        return manager;
    }

    @Override
    public HasValue<Set<En_EquipmentType>> types() {
        return types;
    }

    @Override
    public HasValue< Set<En_OrganizationCode> > organizationCodes() {
        return organizationCode;
    }

    @Override
    public HasValue<String> classifierCode() {
        return classifierCode;
    }

    @Override
    public HasValue<String> registerNumber() {
        return regNum;
    }

    @Override
    public HasValue< En_SortField > sortField() {
        return sortField;
    }

    @Override
    public HasValue< Boolean > sortDir() {
        return sortDir;
    }

    @Override
    public HasValue< EquipmentShortView > equipment() {
        return equipment;
    }

    @UiHandler( "resetBtn" )
    public void onResetClicked ( ClickEvent event ) {
        if ( activity != null ) {
            resetFilter();
            activity.onFilterChanged();
        }
    }

    @UiHandler( "name" )
    public void onSearchChanged( ValueChangeEvent<String> event ) {
        fireChangeTimer();
    }

    @UiHandler( {"classifierCode", "regNum"} )
    public void onKeyUpSearch( KeyUpEvent event ) {
        fireChangeTimer();
    }

    @UiHandler( "organizationCode" )
    public void onSelectOrganizationCode( ValueChangeEvent<Set<En_OrganizationCode > > event ) {
        fireChangeTimer();
    }

    @UiHandler( "types" )
    public void onTypeSelected( ValueChangeEvent<Set<En_EquipmentType>> event ) {
        fireChangeTimer();
    }

    @UiHandler( "manager" )
    public void onManagerSelected( ValueChangeEvent<PersonShortView> event ) {
        fireChangeTimer();
    }

    @UiHandler( "sortDir" )
    public void onSortDirChanged( ValueChangeEvent<Boolean> event ) {
        fireChangeTimer();
    }

    @UiHandler( "sortField" )
    public void onSordFieldChanged( ValueChangeEvent<En_SortField> event ) {
        fireChangeTimer();
    }

    @UiHandler( "equipment" )
    public void onEquipmentChanged( ValueChangeEvent<EquipmentShortView> event ) {
        fireChangeTimer();
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
    CleanableSearchBox name;
    @Inject
    @UiField(provided = true)
    OrganizationBtnGroupMulti organizationCode;
    @Inject
    @UiField(provided = true)
    EquipmentTypeBtnGroupMulti types;
    @UiField
    TextBox classifierCode;
    @UiField
    TextBox regNum;
    @Inject
    @UiField(provided = true)
    EmployeeButtonSelector manager;
    @Inject
    @UiField(provided = true)
    SortFieldSelector sortField;
    @UiField
    ToggleButton sortDir;
    @Inject
    @UiField(provided = true)
    EquipmentButtonSelector equipment;


    AbstractEquipmentFilterActivity activity;

    private static EquipmentFilterView.EquipmentFilterViewUiBinder ourUiBinder = GWT.create( EquipmentFilterView.EquipmentFilterViewUiBinder.class );
    interface EquipmentFilterViewUiBinder extends UiBinder<HTMLPanel, EquipmentFilterView> {}
}