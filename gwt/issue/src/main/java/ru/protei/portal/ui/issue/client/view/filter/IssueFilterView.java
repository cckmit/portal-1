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
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.view.CaseFilterShortView;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.common.FixedPositioner;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.cleanablesearchbox.CleanableSearchBox;
import ru.protei.portal.ui.common.client.widget.optionlist.item.OptionItem;
import ru.protei.portal.ui.common.client.widget.selector.base.Selector;
import ru.protei.portal.ui.common.client.widget.selector.company.CompanyMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.product.devunit.DevUnitMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.sortfield.ModuleType;
import ru.protei.portal.ui.common.client.widget.selector.sortfield.SortFieldSelector;
import ru.protei.portal.ui.common.client.widget.threestate.ThreeStateButton;
import ru.protei.portal.ui.issue.client.activity.filter.AbstractIssueFilterActivity;
import ru.protei.portal.ui.issue.client.activity.filter.AbstractIssueFilterView;
import ru.protei.portal.ui.issue.client.widget.filter.IssueFilterSelector;
import ru.protei.portal.ui.issue.client.widget.importance.btngroup.ImportanceBtnGroupMulti;
import ru.protei.portal.ui.issue.client.widget.state.option.IssueStatesOptionList;

import java.util.Set;

import static ru.protei.portal.ui.common.client.common.UiConstants.Styles.*;

/**
 * Представление фильтра обращений
 */
public class IssueFilterView extends Composite implements AbstractIssueFilterView {
    @Inject
    public void onInit() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        ensureDebugIds();
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
    public HasValue<Set<PersonShortView>> initiators() { return initiators; }

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
    public HasValue<Boolean> searchByComments() {
        return searchByComments;
    }

    @Override
    public HasValue<Boolean> searchPrivate() {
        return searchPrivate;
    }

    @Override
    public void resetFilter() {
        companies.setValue( null );
        products.setValue( null );
        managers.setValue( null );
        initiators.setValue( null );
        importance.setValue(null);
        state.setValue( null );
        dateRange.setValue( null );
        sortField.setValue( En_SortField.creation_date );
        sortDir.setValue( false );
        search.setValue( "" );
        userFilter.setValue( null );
        removeBtn.setVisible( false );
        filterName.removeStyleName( REQUIRED );
        filterName.setValue( "" );
        searchByComments.setValue( false );
        searchPrivate.setValue( null );
        toggleMsgSearchThreshold();
    }

    @Override
    public HasVisibility managersVisibility() {
        return managers;
    }

    @Override
    public HasVisibility searchPrivateVisibility() {
        return searchPrivateContainer;
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
            filterName.addStyleName(REQUIRED);
        } else {
            filterName.removeStyleName( REQUIRED );
        }
    }

    @Override
    public void setUserFilterNameVisibility( boolean hasVisible ) {
        if ( hasVisible ) {
            filterNameContainer.removeClassName( HIDE );
        } else {
            filterNameContainer.addClassName( HIDE );
        }
    }

    @Override
    public void setCompaniesErrorStyle( boolean hasError ) {
        if (hasError){
            companies.addStyleName( REQUIRED );
        } else {
            companies.removeStyleName( REQUIRED );
        }
    }

    @Override
    public void setProductsErrorStyle( boolean hasError ) {
        if (hasError){
            products.addStyleName( REQUIRED );
        } else {
            products.removeStyleName( REQUIRED );
        }
    }

    @Override
    public void setManagersErrorStyle( boolean hasError ) {
        if (hasError){
            managers.addStyleName( REQUIRED );
        } else {
            managers.removeStyleName( REQUIRED );
        }
    }

    @Override
    public void setInitiatorsErrorStyle( boolean hasError ) {
        if (hasError){
            initiators.addStyleName( REQUIRED );
        } else {
            initiators.removeStyleName( REQUIRED );
        }
    }

    @Override
    public void setUserFilterControlsVisibility( boolean hasVisible ) {
        if ( hasVisible ) {
            if (reportBtnVisible) {
                reportBtn.removeStyleName( HIDE );
            }
            saveBtn.removeStyleName( HIDE );
            resetBtn.removeStyleName( HIDE );
            removeBtn.removeStyleName( HIDE );
        } else {
            if (reportBtnVisible) {
                reportBtn.addStyleName( HIDE );
            }
            saveBtn.addStyleName( HIDE );
            resetBtn.addStyleName( HIDE );
            removeBtn.addStyleName( HIDE );
        }
    }

    @Override
    public void setReportButtonVisibility(boolean hasVisible) {
        reportBtnVisible = hasVisible;
        if (reportBtnVisible) {
            reportBtn.removeStyleName( HIDE );
        } else {
            reportBtn.addStyleName( HIDE );
        }
    }

    @Override
    public void toggleMsgSearchThreshold() {
        if (searchByComments.getValue()) {
            int actualLength = search.getValue().length();
            if (actualLength >= CrmConstants.Issue.MIN_LENGTH_FOR_SEARCH_BY_COMMENTS) {
                searchByCommentsWarning.setVisible(false);
            } else {
                searchByCommentsWarning.setText(lang.searchByCommentsUnavailable(CrmConstants.Issue.MIN_LENGTH_FOR_SEARCH_BY_COMMENTS));
                searchByCommentsWarning.setVisible(true);
            }
        } else if (searchByCommentsWarning.isVisible()) {
            searchByCommentsWarning.setVisible(false);
        }
    }

    @Override
    public void setStateFilter(Selector.SelectorFilter<En_CaseState> caseStateFilter){
        state.setFilter(caseStateFilter);
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

    @UiHandler( "initiators" )
    public void onInitiatorsSelected( ValueChangeEvent<Set<PersonShortView>> event ) {
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
    public void onSearchChanged( ValueChangeEvent<String> event ) {
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

    @UiHandler( "searchByComments" )
    public void onSearchByCommentsChanged( ValueChangeEvent<Boolean> event ) {
        toggleMsgSearchThreshold();
        if (activity != null) {
            activity.onFilterChanged();
        }
    }

    @UiHandler( "searchPrivate" )
    public void onSearchOnlyPrivateChanged( ValueChangeEvent<Boolean> event ) {
        if (activity != null) {
            activity.onFilterChanged();
        }
    }

    @UiHandler("filterRestoreBtn")
    public void onFilterRestoreBtnClick(ClickEvent event) {
        if (activity != null) {
            activity.onFilterRestore();
        }
    }

    @UiHandler("filterCollapseBtn")
    public void onFilterCollapseBtnClick(ClickEvent event) {
        if (activity != null) {
            activity.onFilterCollapse();
        }
    }

    private void ensureDebugIds() {
        filterCollapseBtn.ensureDebugId(DebugIds.FILTER.COLLAPSE_BUTTON);
        filterRestoreBtn.ensureDebugId(DebugIds.FILTER.RESTORE_BUTTON);
        userFilter.setEnsureDebugId(DebugIds.FILTER.USER_FILTER.FILTERS_BUTTON);
        search.setEnsureDebugIdTextBox(DebugIds.FILTER.SEARCH_INPUT);
        search.setEnsureDebugIdAction(DebugIds.FILTER.SEARCH_CLEAR_BUTTON);
        searchByComments.setEnsureDebugId(DebugIds.FILTER.SEARCH_BY_COMMENTS_TOGGLE);
        dateRange.setEnsureDebugId(DebugIds.FILTER.DATE_RANGE_SELECTOR);
        sortField.setEnsureDebugId(DebugIds.FILTER.SORT_FIELD_SELECTOR);
        sortDir.ensureDebugId(DebugIds.FILTER.SORT_DIR_BUTTON);
        companies.setAddEnsureDebugId(DebugIds.FILTER.COMPANY_SELECTOR_ADD_BUTTON);
        companies.setClearEnsureDebugId(DebugIds.FILTER.COMPANY_SELECTOR_CLEAR_BUTTON);
        products.setAddEnsureDebugId(DebugIds.FILTER.PRODUCT_SELECTOR_ADD_BUTTON);
        products.setClearEnsureDebugId(DebugIds.FILTER.PRODUCT_SELECTOR_CLEAR_BUTTON);
        managers.setAddEnsureDebugId(DebugIds.FILTER.MANAGER_SELECTOR_ADD_BUTTON);
        managers.setClearEnsureDebugId(DebugIds.FILTER.MANAGER_SELECTOR_CLEAR_BUTTON);
        initiators.setAddEnsureDebugId(DebugIds.FILTER.INITIATORS_SELECTOR_ADD_BUTTON);
        initiators.setClearEnsureDebugId(DebugIds.FILTER.INITIATORS_SELECTOR_CLEAR_BUTTON);
        searchPrivate.setYesEnsureDebugId(DebugIds.FILTER.PRIVACY_YES_BUTTON);
        searchPrivate.setNotDefinedEnsureDebugId(DebugIds.FILTER.PRIVACY_NOT_DEFINED_BUTTON);
        searchPrivate.setNoEnsureDebugId(DebugIds.FILTER.PRIVACY_NO_BUTTON);
        filterName.ensureDebugId(DebugIds.FILTER.USER_FILTER.FILTER_NAME_INPUT);
        okBtn.ensureDebugId(DebugIds.FILTER.USER_FILTER.FILTER_OK_BUTTON);
        cancelBtn.ensureDebugId(DebugIds.FILTER.USER_FILTER.FILTER_CANCEL_BUTTON);
        reportBtn.ensureDebugId(DebugIds.FILTER.REPORT_BUTTON);
        saveBtn.ensureDebugId(DebugIds.FILTER.SAVE_BUTTON);
        resetBtn.ensureDebugId(DebugIds.FILTER.RESET_BUTTON);
        removeBtn.ensureDebugId(DebugIds.FILTER.REMOVE_BUTTON);
    }

    Timer timer = new Timer() {
        @Override
        public void run() {
            toggleMsgSearchThreshold();
            if (activity != null) {
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
    DevUnitMultiSelector products;

    @Inject
    @UiField ( provided = true )
    EmployeeMultiSelector managers;

    @Inject
    @UiField ( provided = true )
    EmployeeMultiSelector initiators;

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
    CleanableSearchBox search;

    @UiField
    OptionItem searchByComments;

    @UiField
    Label searchByCommentsWarning;

    @UiField
    ThreeStateButton searchPrivate;

    @UiField
    HTMLPanel searchPrivateContainer;

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

    @UiField
    Anchor filterRestoreBtn;

    @UiField
    Anchor filterCollapseBtn;

    @Inject
    FixedPositioner positioner;

    @Inject
    @UiField
    Lang lang;


    AbstractIssueFilterActivity activity;
    private boolean reportBtnVisible = true;

    private static IssueFilterView.IssueFilterViewUiBinder ourUiBinder = GWT.create( IssueFilterView.IssueFilterViewUiBinder.class );

    interface IssueFilterViewUiBinder extends UiBinder<HTMLPanel, IssueFilterView > {}
}