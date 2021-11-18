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
import ru.protei.portal.core.model.dto.CaseFilterDto;
import ru.protei.portal.core.model.ent.CaseFilter;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.FilterShortView;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.activity.filter.AbstractIssueFilterWidgetModel;
import ru.protei.portal.ui.common.client.activity.filter.IssueFilterWidgetModel;
import ru.protei.portal.ui.common.client.activity.issuefilter.AbstractIssueFilterParamView;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.selector.AsyncSelectorModel;
import ru.protei.portal.ui.common.client.util.CaseStateUtils;
import ru.protei.portal.ui.common.client.view.filter.IssueFilterParamView;
import ru.protei.portal.ui.common.client.widget.issuefilterselector.IssueFilterSelector;
import ru.protei.portal.ui.common.client.widget.typedrangepicker.DateIntervalWithType;

import java.util.HashSet;
import java.util.List;

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
        issueFilterParamView.commentAuthorsVisibility().setVisible(false);
        issueFilterParamView.timeElapsedVisibility().setVisible(false);
    }

    public void setInitiatorCompaniesModel(AsyncSelectorModel companyModel) {
        issueFilterParamView.setInitiatorCompaniesModel(companyModel);
    }

    public void setManagerCompaniesModel(AsyncSelectorModel companyModel) {
        issueFilterParamView.setManagerCompaniesModel(companyModel);
    }

    public void resetFilter() {
        issueFilterParamView.resetFilter();
        userFilter.setValue(null);
        removeBtn.setVisible(false);
        saveBtn.setVisible(false);
        createBtn.setVisible(true);
        filterName.removeStyleName(REQUIRED);
        filterName.setValue("");

        showUserFilterControls();
        if (filterType != null && filterType.equals(En_CaseFilterType.CASE_RESOLUTION_TIME)) {
            issueFilterParamView.states().setValue(CaseStateUtils.getFilterCaseResolutionTimeActiveStates());
        }
        applyVisibility(filterType);
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

    public void clearFooterStyle() {
        footer.removeClassName("card-footer");
    }

    public HasValue<FilterShortView> userFilter() {
        return userFilter;
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

        CaseFilterDto<CaseQuery> filledUserFilter = fillUserFilter();
        if (!isCreateFilterAction) {
            filledUserFilter.getCaseFilter().setId(userFilter.getValue().getId());
        }
        filledUserFilter.getQuery().setCheckImportanceHistory( null );//don`t save CheckImportanceHistory

        model.onOkSavingFilterClicked(filterName.getValue(), filledUserFilter,
                caseFilterDto -> {
                    editBtnVisibility().setVisible(true);
                    removeFilterBtnVisibility().setVisible(true);
                    userFilter.setValue(caseFilterDto.getCaseFilter().toShortView());

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
        FilterShortView value = userFilter.getValue();
        if (value == null || value.getId() == null) {
            return;
        }
        model.onRemoveClicked(value.getId(), () -> resetFilter() );
    }

    @UiHandler( "filterName" )
    public void onFilterNameChanged( KeyUpEvent event ) {
        filterNameChangedTimer.cancel();
        filterNameChangedTimer.schedule( 300 );
    }

    @UiHandler("userFilter")
    public void onKeyUpSearch(ValueChangeEvent<FilterShortView> event) {
        onUserFilterChanged(event.getValue());
    }

    private void onUserFilterChanged(FilterShortView filter) {
        if (filter == null){
            resetFilter();
            showUserFilterControls();

            return;
        }

        model.onUserFilterChanged(filter.getId(), caseFilterDto -> {
            issueFilterParamView.fillFilterFields(caseFilterDto.getQuery(), caseFilterDto.getCaseFilter().getSelectorsParams());
            filterName.setValue( caseFilterDto.getCaseFilter().getName() );
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


    private CaseFilterDto<CaseQuery> fillUserFilter() {
        CaseFilter filter = new CaseFilter();
        filter.setName(filterName.getValue());
        filter.setType(filterType);
        CaseQuery query = issueFilterParamView.getFilterFields(filterType);
        query.setSearchString(issueFilterParamView.searchPattern().getValue());
        return new CaseFilterDto<>(filter, query);
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
        applyVisibility(filterType);
        resetFilter();
        userFilter.updateFilterType(filterType);
    }

    private void applyVisibility(En_CaseFilterType filterType) {
        issueFilterParamView.applyVisibility(filterType);
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
    @UiField
    DivElement footer;

    AbstractIssueFilterWidgetModel model;

    private boolean isCreateFilterAction = true;
    private En_CaseFilterType filterType = En_CaseFilterType.CASE_OBJECTS;
    private static IssueFilterWidget.IssueFilterViewUiBinder ourUiBinder = GWT.create( IssueFilterWidget.IssueFilterViewUiBinder.class );
    interface IssueFilterViewUiBinder extends UiBinder<HTMLPanel, IssueFilterWidget> {}
}
