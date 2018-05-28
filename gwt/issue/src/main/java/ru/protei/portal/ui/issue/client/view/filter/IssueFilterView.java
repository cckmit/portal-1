package ru.protei.portal.ui.issue.client.view.filter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.brainworm.factory.core.datetimepicker.client.view.input.range.RangePicker;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.view.CaseFilterShortView;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.client.common.FixedPositioner;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.company.CompanyMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.sortfield.ModuleType;
import ru.protei.portal.ui.common.client.widget.selector.sortfield.SortFieldSelector;
import ru.protei.portal.ui.issue.client.activity.filter.AbstractIssueFilterActivity;
import ru.protei.portal.ui.issue.client.activity.filter.AbstractIssueFilterView;
import ru.protei.portal.ui.issue.client.widget.filter.IssueFilterSelector;
import ru.protei.portal.ui.issue.client.widget.importance.btngroup.ImportanceBtnGroupMulti;
import ru.protei.portal.ui.common.client.widget.selector.product.ProductMultiSelector;
import ru.protei.portal.ui.issue.client.widget.state.option.IssueStatesOptionList;

import java.util.Set;

/**
 * Представление фильтра обращений
 */
public class IssueFilterView extends Composite implements AbstractIssueFilterView {
    @Inject
    public void onInit() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        search.getElement().setPropertyString( "placeholder", lang.search() );
        sortField.setType( ModuleType.ISSUE );
        sortDir.setValue( false );
        dateRange.setPlaceholder( lang.selectDate() );
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

    @Override
    public void setActivity( AbstractIssueFilterActivity activity ) {
        this.activity = activity;
    }

    @Override
    public HasValue<Set<EntityOption>> companies() {
        return companies;
    }

    @Override
    public HasValue<Set<ProductShortView>> products() {
        return products;
    }

    @Override
    public HasValue<Set<PersonShortView>> managers() { return managers; }

    @Override
    public HasValue< Set <En_CaseState > > states() { return state; }

    @Override
    public HasValue< Set <En_ImportanceLevel> > importances() { return importance; }

    @Override
    public HasValue<DateInterval> dateRange() { return dateRange; }

    @Override
    public HasValue<En_SortField> sortField() { return sortField; }

    @Override
    public HasValue<Boolean> sortDir() {
        return sortDir;
    }

    @Override
    public HasValue<String> searchPattern() {
        return search;
    }

    @Override
    public void resetFilter() {
        companies.setValue( null );
        products.setValue( null );
        managers.setValue( null );
        importance.setValue(null);
        state.setValue( null );
        dateRange.setValue( null );
        sortField.setValue( En_SortField.creation_date );
        sortDir.setValue( false );
        search.setText( "" );
        userFilter.setValue( null );
        removeBtn.setVisible( false );
        filterName.removeStyleName( "required" );
        filterName.setValue( "" );
    }

    @Override
    public HasVisibility managersVisibility() {
        return managers;
    }

    @Override
    public HasVisibility companiesVisibility() {
        return companies;
    }

    @Override
    public HasVisibility productsVisibility() {
        return products;
    }

    @Override
    public HasValue<CaseFilterShortView > userFilter() {
        return userFilter;
    }

    @Override
    public void changeUserFilterValueName( CaseFilterShortView value ){
        userFilter.changeValueName( value );
    }

    @Override
    public void addUserFilterDisplayOption( CaseFilterShortView value ){
        userFilter.addDisplayOption( value );
    }

    @Override
    public HasVisibility removeFilterBtnVisibility(){
        return removeBtn;
    }

    @Override
    public void setSaveBtnLabel( String label){
        saveBtn.setText( label );
    }

    @Override
    public HasValue< String > filterName() {
        return filterName;
    }

    @Override
    public void setFilterNameContainerErrorStyle( boolean hasError ) {
        if ( hasError ) {
            filterName.addStyleName( "required" );
        } else {
            filterName.removeStyleName( "required" );
        }
    }

    @Override
    public void setUserFilterNameVisibility( boolean hasVisible ) {
        if ( hasVisible ) {
            filterNameContainer.removeClassName( "hide" );
        } else {
            filterNameContainer.addClassName( "hide" );
        }
    }

    @Override
    public void setCompaniesErrorStyle( boolean hasError ) {
        if (hasError){
            companies.addStyleName( "required" );
        } else {
            companies.removeStyleName( "required" );
        }
    }

    @Override
    public void setProductsErrorStyle( boolean hasError ) {
        if (hasError){
            products.addStyleName( "required" );
        } else {
            products.removeStyleName( "required" );
        }
    }

    @Override
    public void setManagersErrorStyle( boolean hasError ) {
        if (hasError){
            managers.addStyleName( "required" );
        } else {
            managers.removeStyleName( "required" );
        }
    }

    @Override
    public void setUserFilterControlsVisibility( boolean hasVisible ) {
        if ( hasVisible ) {
            reportBtn.removeStyleName( "hide" );
            saveBtn.removeStyleName( "hide" );
            resetBtn.removeStyleName( "hide" );
            removeBtn.removeStyleName( "hide" );
        } else {
            reportBtn.addStyleName( "hide" );
            saveBtn.addStyleName( "hide" );
            resetBtn.addStyleName( "hide" );
            removeBtn.addStyleName( "hide" );
        }
    }

    @UiHandler( "resetBtn" )
    public void onResetClicked ( ClickEvent event ) {
        if ( activity != null ) {
            resetFilter();
            activity.onFilterChanged();
        }
    }

    @UiHandler( "saveBtn" )
    public void onSaveClicked ( ClickEvent event ) {
        if ( activity == null ) {
            return;
        }
        activity.onSaveFilterClicked();
    }

    @UiHandler( "okBtn" )
    public void onOkBtnClicked ( ClickEvent event ) {
        event.preventDefault();
        if ( activity == null ) {
            return;
        }
        activity.onOkSavingClicked();
    }

    @UiHandler( "cancelBtn" )
    public void onCancelBtnClicked ( ClickEvent event ) {
        event.preventDefault();
        if ( activity == null ) {
            return;
        }
        activity.onCancelSavingClicked();
    }

    @UiHandler( "removeBtn" )
    public void onRemoveClicked ( ClickEvent event ) {
        if ( activity == null || userFilter.getValue() == null || userFilter.getValue().getId() == null ) {
            return;
        }
        activity.onFilterRemoveClicked( userFilter.getValue().getId() );
    }

    @UiHandler("reportBtn")
    public void onReportClicked(ClickEvent event) {
        event.preventDefault();
        if ( activity == null ) {
            return;
        }
        activity.onCreateReportClicked();
    }

    @UiHandler( "companies" )
    public void onCompaniesSelected( ValueChangeEvent<Set<EntityOption>> event ) {
        if ( activity != null ) {
            activity.onFilterChanged();
        }
    }

    @UiHandler( "products" )
    public void onProductsSelected( ValueChangeEvent<Set<ProductShortView>> event ) {
        if ( activity != null ) {
            activity.onFilterChanged();
        }
    }

    @UiHandler( "managers" )
    public void onManagersSelected( ValueChangeEvent<Set<PersonShortView>> event ) {
        if ( activity != null ) {
            activity.onFilterChanged();
        }
    }

    @UiHandler( "importance" )
    public void onImportanceSelected( ValueChangeEvent<Set<En_ImportanceLevel>> event ) {
        if ( activity != null ) {
            activity.onFilterChanged();
        }
    }

    @UiHandler( "state" )
    public void onStateSelected( ValueChangeEvent<Set<En_CaseState>> event ) {
        if ( activity != null ) {
            activity.onFilterChanged();
        }
    }

    @UiHandler( "dateRange" )
    public void onDateRangeChanged( ValueChangeEvent<DateInterval> event ) {
        if ( activity != null ) {
            activity.onFilterChanged();
        }
    }


    @UiHandler( "sortField" )
    public void onSortFieldSelected( ValueChangeEvent< En_SortField > event ) {
        if ( activity != null ) {
            activity.onFilterChanged();
        }
    }

    @UiHandler("sortDir")
    public void onSortDirClicked( ClickEvent event ) {
        if ( activity != null ) {
            activity.onFilterChanged();
        }
    }

    @UiHandler( "search" )
    public void onKeyUpSearch( KeyUpEvent event ) {
        timer.cancel();
        timer.schedule( 300 );
    }

    @UiHandler( "userFilter" )
    public void onKeyUpSearch( ValueChangeEvent< CaseFilterShortView > event ) {
        if ( activity == null ) {
            return;
        }
        activity.onUserFilterChanged();
    }

    @UiHandler( "filterName" )
    public void onFilterNameChanged( KeyUpEvent event ) {
        filterNameChangedTimer.cancel();
        filterNameChangedTimer.schedule( 300 );
    }

    Timer timer = new Timer() {
        @Override
        public void run() {
            if ( activity != null ) {
                activity.onFilterChanged();
            }
        }
    };

    Timer filterNameChangedTimer = new Timer() {
        @Override
        public void run() {
            setFilterNameContainerErrorStyle( filterName.getValue().isEmpty() );
        }
    };

    @Inject
    @UiField( provided = true )
    CompanyMultiSelector companies;

    @Inject
    @UiField ( provided = true )
    ProductMultiSelector products;

    @Inject
    @UiField ( provided = true )
    EmployeeMultiSelector managers;

    @Inject
    @UiField ( provided = true )
    IssueStatesOptionList state;

    @Inject
    @UiField( provided = true )
    ImportanceBtnGroupMulti importance;

    @Inject
    @UiField(provided = true)
    RangePicker dateRange;

    @Inject
    @UiField( provided = true )
    SortFieldSelector sortField;

    @Inject
    @UiField( provided = true )
    IssueFilterSelector userFilter;

    @UiField
    ToggleButton sortDir;

    @UiField
    TextBox search;

    @UiField
    Button resetBtn;

    @UiField
    Button saveBtn;

    @UiField
    Button removeBtn;

    @UiField
    Button reportBtn;

    @UiField
    Anchor okBtn;

    @UiField
    Anchor cancelBtn;

    @UiField
    TextBox filterName;

    @UiField
    DivElement filterNameContainer;

    @Inject
    FixedPositioner positioner;

    @Inject
    @UiField
    Lang lang;

    AbstractIssueFilterActivity activity;

    private static IssueFilterView.IssueFilterViewUiBinder ourUiBinder = GWT.create( IssueFilterView.IssueFilterViewUiBinder.class );

    interface IssueFilterViewUiBinder extends UiBinder<HTMLPanel, IssueFilterView > {}
}