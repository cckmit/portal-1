package ru.protei.portal.ui.common.client.activity.issuefilter;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_CaseFilterType;
import ru.protei.portal.core.model.ent.CaseFilter;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.view.CaseFilterShortView;
import ru.protei.portal.core.service.IssueFilterService;
import ru.protei.portal.ui.common.client.events.ConfirmDialogEvents;
import ru.protei.portal.ui.common.client.events.IssueEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.IssueFilterControllerAsync;
import ru.protei.portal.ui.common.client.util.IssueFilterUtils;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

public abstract class IssueFilterParamActivity implements AbstractIssueFilterParamActivity, Activity {

    @Event
    public void onConfirmRemove( ConfirmDialogEvents.Confirm event ) {

        if (!event.identity.equals(getClass().getName()) || filterIdToRemove == null) {
            return;
        }

        filterService.removeIssueFilter(filterIdToRemove, new RequestCallback<Boolean>() {
            @Override
            public void onError(Throwable throwable) {
                filterIdToRemove = null;
                fireEvent(new NotifyEvents.Show(lang.errNotRemoved(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(Boolean aBoolean) {
                filterIdToRemove = null;
                fireEvent(new NotifyEvents.Show(lang.issueFilterRemoveSuccessed(), NotifyEvents.NotifyType.SUCCESS));
                fireEvent(new IssueEvents.ChangeUserFilterModel());
                issueFilterWidgetView.resetFilter();
                //loadTable();
            }
        });
    }

    @Event
    public void onCancelRemove( ConfirmDialogEvents.Cancel event ) {

        if (!event.identity.equals(getClass().getName())) {
            return;
        }

        filterIdToRemove = null;
    }

    @Override
    public void onSaveFilterClicked() {
        isCreateFilterAction = false;
        showUserFilterName();
    }

    @Override
    public void onCreateFilterClicked() {
        isCreateFilterAction = true;
        showUserFilterName();
    }

    @Override
    public void onUserFilterChanged() {
        CaseFilterShortView filter = issueFilterWidgetView.userFilter().getValue();
        if (filter == null){
            issueFilterWidgetView.resetFilter();
            showUserFilterControls();

            onFilterChanged();
            return;
        }

        filterService.getIssueFilter( filter.getId(), new RequestCallback< CaseFilter >() {
            @Override
            public void onError( Throwable throwable ) {
                fireEvent( new NotifyEvents.Show( lang.errNotFound(), NotifyEvents.NotifyType.ERROR ) );
            }

            @Override
            public void onSuccess( CaseFilter filter ) {
                fillFilterFields( filter );
                onFilterChanged();
            }
        } );
    }

    @Override
    public void onOkSavingFilterClicked() {
        if (issueFilterWidgetView.filterName().getValue().isEmpty()){
            issueFilterWidgetView.setFilterNameContainerErrorStyle( true );
            fireEvent( new NotifyEvents.Show( lang.errFilterNameRequired(), NotifyEvents.NotifyType.ERROR ) );
            return;
        }

        CaseFilter userFilter = fillUserFilter();
        if ( !isCreateFilterAction ){
            userFilter.setId( issueFilterWidgetView.userFilter().getValue().getId() );
        }

        filterService.saveIssueFilter( userFilter, new RequestCallback< CaseFilter >() {
            @Override
            public void onError( Throwable throwable ) {
                fireEvent( new NotifyEvents.Show( lang.errSaveIssueFilter(), NotifyEvents.NotifyType.ERROR ) );
            }

            @Override
            public void onSuccess( CaseFilter filter ) {
                fireEvent(new NotifyEvents.Show(lang.msgObjectSaved(), NotifyEvents.NotifyType.SUCCESS));

                issueFilterWidgetView.editBtnVisibility().setVisible(true);
                issueFilterWidgetView.removeFilterBtnVisibility().setVisible(true);

                CaseFilterShortView filterShortView = filter.toShortView();
                if ( isCreateFilterAction ){
                    issueFilterWidgetView.userFilter().setValue( filterShortView );
                    issueFilterWidgetView.addUserFilterDisplayOption( filterShortView );
                } else {
                    issueFilterWidgetView.changeUserFilterValueName( filterShortView );
                }

                showUserFilterControls();
            }
        } );
    }

    @Override
    public void onCancelSavingFilterClicked() {
        showUserFilterControls();
    }

    @Override
    public void onFilterChanged() {
/*
        if ( !validateMultiSelectorsTotalCount() ){
            return;
        }

        loadTable();
*/
        issueFilterWidgetView.toggleMsgSearchThreshold();
    }

    @Override
    public void onFilterRemoveClicked( Long id ) {
        filterIdToRemove = id;
        fireEvent(new ConfirmDialogEvents.Show(getClass().getName(), lang.issueFilterRemoveConfirmMessage()));
    }

    @Override
    public void onCompaniesFilterChanged() {
        onFilterChanged();
        updateInitiatorSelector();
    }

    private void showUserFilterName(){
        issueFilterWidgetView.setUserFilterControlsVisibility(false);
        issueFilterWidgetView.setUserFilterNameVisibility(true);
    }

    private void fillFilterFields( CaseFilter filter ) {
        issueFilterWidgetView.removeFilterBtnVisibility().setVisible( true );
        issueFilterWidgetView.editBtnVisibility().setVisible( true );
        issueFilterWidgetView.filterName().setValue( filter.getName() );
        issueFilterWidgetView.fillFilterFields(filter.getParams());
    }

    private CaseFilter fillUserFilter() {
        CaseFilter filter = new CaseFilter();
        filter.setName(issueFilterWidgetView.filterName().getValue());
        filter.setType( En_CaseFilterType.CASE_OBJECTS);
        CaseQuery query = IssueFilterUtils.makeCaseQuery(issueFilterWidgetView, false);
        filter.setParams(query);
        query.setSearchString(issueFilterWidgetView.searchPattern().getValue());
        return filter;
    }

    private void showUserFilterControls() {
        issueFilterWidgetView.setUserFilterControlsVisibility(true);
        issueFilterWidgetView.setUserFilterNameVisibility(false);
    }

    private void updateInitiatorSelector() {
        issueFilterWidgetView.updateInitiators();
    }

    @Inject
    Lang lang;

    @Inject
    AbstractIssueFilterWidgetView issueFilterWidgetView;

    @Inject
    IssueFilterControllerAsync filterService;

    private boolean isCreateFilterAction = true;
    private Long filterIdToRemove;
}
