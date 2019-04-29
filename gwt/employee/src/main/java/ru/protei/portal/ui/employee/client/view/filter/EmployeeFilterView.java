package ru.protei.portal.ui.employee.client.view.filter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.common.FixedPositioner;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.cleanablesearchbox.CleanableSearchBox;
import ru.protei.portal.ui.common.client.widget.homecompany.HomeCompanyBtnGroupMulti;
import ru.protei.portal.ui.common.client.widget.selector.sortfield.SortFieldSelector;
import ru.protei.portal.ui.employee.client.activity.filter.AbstractEmployeeFilterActivity;
import ru.protei.portal.ui.employee.client.activity.filter.AbstractEmployeeFilterView;

import java.util.Set;

/**
 * Представление фильтра сотрудников
 */
public class EmployeeFilterView extends Composite implements AbstractEmployeeFilterView {

    @Inject
    public void onInit() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
    }


    @Override
    protected void onAttach() {
        super.onAttach();
        positioner.watch( this, FixedPositioner.NAVBAR_TOP_OFFSET );
    }

    @Override
    protected void onDetach() {
        super.onDetach();
        positioner.ignore( this );
    }

    @Override
    public void setActivity( AbstractEmployeeFilterActivity activity ) {
        this.activity = activity;
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
    public HasValue< String > searchPattern() {
        return search;
    }

    @Override
    public HasValue< String > workPhone() {
        return workPhone;
    }

    @Override
    public HasValue< String > mobilePhone() {
        return mobilePhone;
    }

    @Override
    public HasValue< String > ipAddress() {
        return ipAddress;
    }

    @Override
    public HasValue< Set< EntityOption > > homeCompanies() {
        return homeCompanies;
    }

    @Override
    public void resetFilter() {
        sortField.setValue( En_SortField.person_full_name );
        sortDir.setValue( true );
        search.setValue( "" );
        workPhone.setValue( "" );
        mobilePhone.setValue( "" );
        ipAddress.setValue( "" );
        homeCompanies.setValue( null );
    }

    @UiHandler( "resetBtn" )
    public void onResetClicked ( ClickEvent event ) {
        if ( activity != null ) {
            resetFilter();
            activity.onFilterChanged();
        }
    }

    @UiHandler( "applyBtn" )
    public void onApplyClicked ( ClickEvent event ) {
        fireChange();
    }

    @UiHandler( "sortField" )
    public void onSortFieldSelected( ValueChangeEvent< En_SortField > event ) {
        fireChange();
    }

    @UiHandler( "sortDir" )
    public void onSortDirClicked( ClickEvent event ) {
        fireChange();
    }

    private void fireChange() {
        if ( activity != null ) {
            activity.onFilterChanged();
        }
    }

    @Inject
    @UiField( provided = true )
    SortFieldSelector sortField;

    @UiField
    ToggleButton sortDir;

    @UiField
    CleanableSearchBox search;

    @UiField
    CleanableSearchBox workPhone;

    @UiField
    CleanableSearchBox mobilePhone;

    @UiField
    CleanableSearchBox ipAddress;

    @Inject
    @UiField(provided = true)
    HomeCompanyBtnGroupMulti homeCompanies;

    @UiField
    Button resetBtn;

    @UiField
    Button applyBtn;

    @Inject
    @UiField
    Lang lang;

    @Inject
    FixedPositioner positioner;


    AbstractEmployeeFilterActivity activity;

    private static EmployeeFilterViewUiBinder ourUiBinder = GWT.create( EmployeeFilterViewUiBinder.class );
    interface EmployeeFilterViewUiBinder extends UiBinder< HTMLPanel, EmployeeFilterView > {}
}