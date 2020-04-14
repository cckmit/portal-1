package ru.protei.portal.ui.common.client.widget.issuefilter;

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
import ru.protei.portal.core.model.dict.En_CaseFilterType;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.ent.CaseFilter;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.view.CaseFilterShortView;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.activity.filter.AbstractIssueFilterWidgetModel;
import ru.protei.portal.ui.common.client.activity.filter.IssueFilterWidgetModel;
import ru.protei.portal.ui.common.client.activity.issuefilter.AbstractIssueFilterParamView;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.view.filter.IssueFilterParamView;
import ru.protei.portal.ui.common.client.widget.issuefilterselector.IssueFilterSelector;
import ru.protei.portal.ui.common.client.widget.selector.person.InitiatorModel;
import ru.protei.portal.ui.common.client.widget.selector.person.PersonModel;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import static ru.protei.portal.core.model.helper.StringUtils.isEmpty;
import static ru.protei.portal.ui.common.client.common.UiConstants.Styles.HIDE;
import static ru.protei.portal.ui.common.client.common.UiConstants.Styles.REQUIRED;

/**
 * Представление фильтра обращений
 */
public class IssueFilterWidget extends Composite {

    @Inject
    public void onInit(IssueFilterWidgetModel model) {
        initWidget(ourUiBinder.createAndBindUi(this));
        this.model = model;
        ensureDebugIds();
        issueFilterParamView.setInitiatorModel(initiatorModel);
        issueFilterParamView.setCreatorModel(personModel);
        issueFilterParamView.setInitiatorCompaniesSupplier(() -> new HashSet<>( issueFilterParamView.companies().getValue()));
        issueFilterParamView.commentAuthorsVisibility().setVisible(false);
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        issueFilterParamView.watchForScrollOf(root);
        userFilter.watchForScrollOf(root);
    }

    @Override
    protected void onDetach() {
        super.onDetach();
        issueFilterParamView.stopWatchForScrollOf(root);
        userFilter.stopWatchForScrollOf(root);
    }

    public void addAdditionalFilterValidate(Function<CaseFilter, Boolean> validate) {
        model.addAdditionalFilterValidate(validate);
    }

    public void resetFilter() {
        issueFilterParamView.resetFilter();
        userFilter.setValue(null);
        removeBtn.setVisible(false);
        saveBtn.setVisible(false);
        createBtn.setVisible(true);
        filterName.removeStyleName(REQUIRED);
        filterName.setValue("");

        setUserFilterNameVisibility(false);
        if (filterType != null && filterType.equals(En_CaseFilterType.CASE_RESOLUTION_TIME)) {
            issueFilterParamView.states().setValue(new HashSet<>(activeStates));
        }
    }

    public HasEnabled createEnabled() {
        return createBtn;
    }

    public AbstractIssueFilterParamView getIssueFilterParams() {
        return issueFilterParamView;
    }

    public void presetFilterType() {
        userFilter.updateFilterType(En_CaseFilterType.CASE_OBJECTS);
    }

    public CaseQuery getFilterFieldsByFilterType() {
        return issueFilterParamView.getFilterFields(filterType);
    }

    @UiHandler( "resetBtn" )
    public void onResetClicked ( ClickEvent event ) {
        onUserFilterChanged(null);
    }

    @UiHandler( "saveBtn" )
    public void onSaveClicked ( ClickEvent event ) {
        isCreateFilterAction = false;
        showUserFilterName();
    }

    @UiHandler( "createBtn" )
    public void onCreateClicked ( ClickEvent event ) {
        isCreateFilterAction = true;
        showUserFilterName();
    }

    @UiHandler( "okBtn" )
    public void onOkBtnClicked ( ClickEvent event ) {
        event.preventDefault();
        if (isEmpty(filterName.getValue())) {
            setFilterNameContainerErrorStyle( true );
        }

        CaseFilter filledUserFilter = fillUserFilter();
        if (!isCreateFilterAction) {
            filledUserFilter.setId(userFilter.getValue().getId());
        }

        model.onOkSavingFilterClicked(filterName.getValue(), filledUserFilter,
                filter -> {
                    editBtnVisibility().setVisible(true);
                    removeFilterBtnVisibility().setVisible(true);
                    userFilter.setValue(filter.toShortView());

                    showUserFilterControls();
                });
    }

    @UiHandler( "cancelBtn" )
    public void onCancelBtnClicked ( ClickEvent event ) {
        event.preventDefault();
        showUserFilterControls();
        if (userFilter.getValue() == null) {
            removeBtn.setVisible(false);
            saveBtn.setVisible(false);
        }
    }

    @UiHandler( "removeBtn" )
    public void onRemoveClicked ( ClickEvent event ) {
        CaseFilterShortView value = userFilter.getValue();
        if (value == null || value.getId() == null) {
            return;
        }
        model.onRemoveClicked(value.getId(), () -> {
            resetFilter();
            issueFilterParamView.resetFilter();
        });
    }

    @UiHandler( "filterName" )
    public void onFilterNameChanged( KeyUpEvent event ) {
        filterNameChangedTimer.cancel();
        filterNameChangedTimer.schedule( 300 );
    }

    @UiHandler("userFilter")
    public void onKeyUpSearch(ValueChangeEvent<CaseFilterShortView> event) {
        onUserFilterChanged(event.getValue());
    }

    private void onUserFilterChanged(CaseFilterShortView filter) {
        if (filter == null){
            resetFilter();
            showUserFilterControls();

            return;
        }

        model.onUserFilterChanged(filter.getId(), caseFilter -> {
            issueFilterParamView.fillFilterFields(caseFilter.getParams(), caseFilter.getSelectorsParams());
            filterName.setValue( caseFilter.getName() );
            removeFilterBtnVisibility().setVisible( true );
            editBtnVisibility().setVisible( true );
        });
    }

    private void showUserFilterName(){
        setUserFilterControlsVisibility(false);
        setUserFilterNameVisibility(true);
    }

    public void showUserFilterControls() {
        setUserFilterControlsVisibility(true);
        setUserFilterNameVisibility(false);
    }


    private CaseFilter fillUserFilter() {
        CaseFilter filter = new CaseFilter();
        filter.setName(filterName.getValue());
        filter.setType(filterType);
        CaseQuery query = issueFilterParamView.getFilterFields(filterType);
        filter.setParams(query);
        query.setSearchString(issueFilterParamView.searchPattern().getValue());
        return filter;
    }

    private void ensureDebugIds() {
        userFilter.setEnsureDebugId(DebugIds.FILTER.USER_FILTER.FILTERS_BUTTON);
        filterName.ensureDebugId(DebugIds.FILTER.USER_FILTER.FILTER_NAME_INPUT);
        okBtn.ensureDebugId(DebugIds.FILTER.USER_FILTER.FILTER_OK_BUTTON);
        cancelBtn.ensureDebugId(DebugIds.FILTER.USER_FILTER.FILTER_CANCEL_BUTTON);
        createBtn.ensureDebugId(DebugIds.FILTER.CREATE_BUTTON);
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

    public void updateFilterType(En_CaseFilterType filterType) {
        this.filterType = filterType;
        applyVisibilityByFilterType(filterType);
        resetFilter();
        userFilter.updateFilterType(filterType);
    }

    private void applyVisibilityByFilterType(En_CaseFilterType filterType) {
        issueFilterParamView.applyVisibilityByFilterType(filterType);
    }

    private HasVisibility removeFilterBtnVisibility(){
        return removeBtn;
    }

    private void setFilterNameContainerErrorStyle( boolean hasError ) {
        if ( hasError ) {
            filterName.addStyleName(REQUIRED);
        } else {
            filterName.removeStyleName( REQUIRED );
        }
    }

    private void setUserFilterNameVisibility( boolean hasVisible ) {
        if ( hasVisible ) {
            filterNameContainer.removeClassName( HIDE );
        } else {
            filterNameContainer.addClassName( HIDE );
        }
    }

    private void setUserFilterControlsVisibility( boolean hasVisible ) {
        if ( hasVisible ) {
            createBtn.removeStyleName( HIDE );
            saveBtn.removeStyleName( HIDE );
            resetBtn.removeStyleName( HIDE );
            removeBtn.removeStyleName( HIDE );
        } else {
            saveBtn.addStyleName( HIDE );
            createBtn.addStyleName( HIDE );
            resetBtn.addStyleName( HIDE );
            removeBtn.addStyleName( HIDE );
        }
    }

    private HasVisibility editBtnVisibility() {
        return saveBtn;
    }

    @Inject
    @UiField
    Lang lang;

    @UiField
    HTMLPanel root;

    @Inject
    @UiField(provided = true)
    IssueFilterSelector userFilter;
    @Inject
    @UiField(provided = true)
    IssueFilterParamView issueFilterParamView;
    @UiField
    Button resetBtn;
    @UiField
    Button createBtn;
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

    @Inject
    PersonModel personModel;
    @Inject
    InitiatorModel initiatorModel;

    AbstractIssueFilterWidgetModel model;

    private boolean isCreateFilterAction = true;
    private En_CaseFilterType filterType = En_CaseFilterType.CASE_OBJECTS;
    private Set<En_CaseState> activeStates = new HashSet<>(Arrays.asList(En_CaseState.CREATED, En_CaseState.OPENED,
            En_CaseState.ACTIVE, En_CaseState.TEST_LOCAL, En_CaseState.WORKAROUND,
            En_CaseState.INFO_REQUEST, En_CaseState.NX_REQUEST, En_CaseState.CUST_REQUEST));

    private static IssueFilterWidget.IssueFilterViewUiBinder ourUiBinder = GWT.create( IssueFilterWidget.IssueFilterViewUiBinder.class );
    interface IssueFilterViewUiBinder extends UiBinder<HTMLPanel, IssueFilterWidget> {}
}