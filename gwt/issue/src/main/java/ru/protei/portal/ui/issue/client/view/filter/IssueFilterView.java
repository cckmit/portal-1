package ru.protei.portal.ui.issue.client.view.filter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.view.CaseFilterShortView;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.common.FixedPositioner;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.issuefilter.IssueFilter;
import ru.protei.portal.ui.common.client.widget.issuefilter.IssueFilterActivity;
import ru.protei.portal.ui.common.client.widget.selector.base.Selector;
import ru.protei.portal.ui.issue.client.activity.filter.AbstractIssueFilterActivity;
import ru.protei.portal.ui.issue.client.activity.filter.AbstractIssueFilterView;

import java.util.Set;
import java.util.function.Supplier;

import static ru.protei.portal.ui.common.client.common.UiConstants.Styles.HIDE;
import static ru.protei.portal.ui.common.client.common.UiConstants.Styles.REQUIRED;

/**
 * Представление фильтра обращений
 */
public class IssueFilterView extends Composite implements AbstractIssueFilterView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        ensureDebugIds();
        issueFilter.commentAuthorsVisibility().setVisible(false);
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
    public void setActivity(AbstractIssueFilterActivity activity, IssueFilterActivity issueFilterActivity) {
        this.activity = activity;
        this.issueFilterActivity = issueFilterActivity;
        this.issueFilter.setActivity(issueFilterActivity);
    }

    @Override
    public HasValue<Set<EntityOption>> companies() {
        return issueFilter.companies();
    }

    @Override
    public HasValue<Set<ProductShortView>> products() {
        return issueFilter.products();
    }

    @Override
    public HasValue<Set<PersonShortView>> managers() {
        return issueFilter.managers();
    }

    @Override
    public HasValue<Set<PersonShortView>> initiators() {
        return issueFilter.initiators();
    }

    @Override
    public HasValue< Set <En_CaseState > > states() {
        return issueFilter.states();
    }

    @Override
    public HasValue< Set <En_ImportanceLevel> > importances() {
        return issueFilter.importances();
    }

    @Override
    public HasValue<DateInterval> dateRange() {
        return issueFilter.dateRange();
    }

    @Override
    public HasValue<En_SortField> sortField() {
        return issueFilter.sortField();
    }

    @Override
    public HasValue<Boolean> sortDir() {
        return issueFilter.sortDir();
    }

    @Override
    public HasValue<String> searchPattern() {
        return issueFilter.searchPattern();
    }

    @Override
    public HasValue<Boolean> searchByComments() {
        return issueFilter.searchByComments();
    }

    @Override
    public HasValue<Boolean> searchPrivate() {
        return issueFilter.searchPrivate();
    }

    @Override
    public void resetFilter() {
        issueFilter.resetFilter();
        removeBtn.setVisible(false);
        filterName.removeStyleName(REQUIRED);
        filterName.setValue("");
    }

    @Override
    public void fillFilterFields(CaseQuery caseQuery) {
        issueFilter.fillFilterFields(caseQuery);
    }

    @Override
    public HasVisibility managersVisibility() {
        return issueFilter.managersVisibility();
    }

    @Override
    public HasVisibility searchPrivateVisibility() {
        return issueFilter.searchPrivateVisibility();
    }

    @Override
    public HasVisibility companiesVisibility() {
        return issueFilter.companiesVisibility();
    }

    @Override
    public HasVisibility productsVisibility() {
        return issueFilter.productsVisibility();
    }

    @Override
    public HasValue<CaseFilterShortView > userFilter() {
        return issueFilter.userFilter();
    }

    @Override
    public void changeUserFilterValueName( CaseFilterShortView value ){
        issueFilter.changeUserFilterValueName( value );
    }

    @Override
    public void addUserFilterDisplayOption( CaseFilterShortView value ){
        issueFilter.addUserFilterDisplayOption( value );
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
        issueFilter.setCompaniesErrorStyle(hasError);
    }

    @Override
    public void setProductsErrorStyle( boolean hasError ) {
        issueFilter.setProductsErrorStyle(hasError);
    }

    @Override
    public void setManagersErrorStyle( boolean hasError ) {
        issueFilter.setManagersErrorStyle(hasError);
    }

    @Override
    public void setInitiatorsErrorStyle( boolean hasError ) {
        issueFilter.setInitiatorsErrorStyle(hasError);
    }

    @Override
    public void setUserFilterControlsVisibility( boolean hasVisible ) {
        if ( hasVisible ) {
            saveBtn.removeStyleName( HIDE );
            resetBtn.removeStyleName( HIDE );
            removeBtn.removeStyleName( HIDE );
        } else {
            saveBtn.addStyleName( HIDE );
            resetBtn.addStyleName( HIDE );
            removeBtn.addStyleName( HIDE );
        }
    }

    @Override
    public void toggleMsgSearchThreshold() {
        issueFilter.toggleMsgSearchThreshold();
    }

    @Override
    public void setStateFilter(Selector.SelectorFilter<En_CaseState> caseStateFilter){
        issueFilter.setStateFilter(caseStateFilter);
    }

    @Override
    public void setInitiatorCompaniesSupplier(Supplier<Set<EntityOption>> collectionSupplier) {
        issueFilter.setInitiatorCompaniesSupplier(collectionSupplier);
    }

    @Override
    public void updateInitiators() {
        issueFilter.updateInitiators();
    }

    @UiHandler( "resetBtn" )
    public void onResetClicked ( ClickEvent event ) {
        if ( issueFilterActivity != null ) {
            resetFilter();
            issueFilterActivity.onFilterChanged();
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
        if (activity == null) {
            return;
        }
        CaseFilterShortView value = issueFilter.userFilter().getValue();
        if (value == null || value.getId() == null) {
            return;
        }
        activity.onFilterRemoveClicked(value.getId());
    }

    @UiHandler( "filterName" )
    public void onFilterNameChanged( KeyUpEvent event ) {
        filterNameChangedTimer.cancel();
        filterNameChangedTimer.schedule( 300 );
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
        filterName.ensureDebugId(DebugIds.FILTER.USER_FILTER.FILTER_NAME_INPUT);
        okBtn.ensureDebugId(DebugIds.FILTER.USER_FILTER.FILTER_OK_BUTTON);
        cancelBtn.ensureDebugId(DebugIds.FILTER.USER_FILTER.FILTER_CANCEL_BUTTON);
        saveBtn.ensureDebugId(DebugIds.FILTER.SAVE_BUTTON);
        resetBtn.ensureDebugId(DebugIds.FILTER.RESET_BUTTON);
        removeBtn.ensureDebugId(DebugIds.FILTER.REMOVE_BUTTON);
    }

    Timer filterNameChangedTimer = new Timer() {
        @Override
        public void run() {
            setFilterNameContainerErrorStyle( filterName.getValue().isEmpty() );
        }
    };

    @Inject
    @UiField
    Lang lang;

    @Inject
    @UiField(provided = true)
    IssueFilter issueFilter;

    @UiField
    Button resetBtn;

    @UiField
    Button saveBtn;

    @UiField
    Button removeBtn;

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

    private AbstractIssueFilterActivity activity;
    private IssueFilterActivity issueFilterActivity;

    private static IssueFilterView.IssueFilterViewUiBinder ourUiBinder = GWT.create( IssueFilterView.IssueFilterViewUiBinder.class );
    interface IssueFilterViewUiBinder extends UiBinder<HTMLPanel, IssueFilterView > {}
}