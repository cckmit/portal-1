package ru.protei.portal.ui.common.client.activity.filter;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.protei.portal.core.model.dict.En_CaseFilterType;
import ru.protei.portal.core.model.ent.CaseFilter;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.ui.common.client.activity.issuefilter.AbstractIssueFilterWidgetView;
import ru.protei.portal.ui.common.client.events.ConfirmDialogEvents;
import ru.protei.portal.ui.common.client.events.IssueEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.IssueFilterControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

public abstract class IssueFilterActivity implements Activity, AbstractIssueFilterActivity {
    @Override
    public void setView(AbstractIssueFilterView view, AbstractIssueFilterWidgetView paramView) {
        filterView = view;
        filterParamView = paramView;
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
    public void onFilterRemoveClicked( Long id ) {
        fireEvent(new ConfirmDialogEvents.Show(lang.issueFilterRemoveConfirmMessage(), removeAction(id)));
    }

    @Override
    public void onOkSavingFilterClicked() {
        if (filterView.filterName().getValue().isEmpty()){
            filterView.setFilterNameContainerErrorStyle( true );
            fireEvent( new NotifyEvents.Show( lang.errFilterNameRequired(), NotifyEvents.NotifyType.ERROR ) );
            return;
        }

        CaseFilter userFilter = fillUserFilter();
        if ( !isCreateFilterAction ){
            userFilter.setId( filterView.userFilter().getValue().getId() );
        }

        filterService.saveIssueFilter( userFilter, new RequestCallback< CaseFilter >() {
            @Override
            public void onError( Throwable throwable ) {
                fireEvent( new NotifyEvents.Show( lang.errSaveIssueFilter(), NotifyEvents.NotifyType.ERROR ) );
            }

            @Override
            public void onSuccess( CaseFilter filter ) {
                fireEvent(new NotifyEvents.Show(lang.msgObjectSaved(), NotifyEvents.NotifyType.SUCCESS));
                fireEvent(new IssueEvents.ChangeUserFilterModel());

                filterView.editBtnVisibility().setVisible(true);
                filterView.removeFilterBtnVisibility().setVisible(true);
                filterView.userFilter().setValue(filter.toShortView());

                showUserFilterControls();
            }
        } );
    }

    @Override
    public void onCancelSavingFilterClicked() {
        showUserFilterControls();
    }

    @Override
    public void onUserFilterChanged(Long id) {
        if (id == null){
            filterView.resetFilter();
            showUserFilterControls();


            return;
        }

        filterService.getIssueFilter(id, new FluentCallback<CaseFilter>()
                .withErrorMessage(lang.errNotFound())
                .withSuccess(caseFilter -> {
                    filterView.removeFilterBtnVisibility().setVisible( true );
                    filterView.editBtnVisibility().setVisible( true );
                    filterView.filterName().setValue( caseFilter.getName() );
                    filterParamView.fillFilterFields(caseFilter.getParams(), caseFilter.getSelectorsParams());
                })
        );
    }

    public void showUserFilterName(){
        filterView.setUserFilterControlsVisibility(false);
        filterView.setUserFilterNameVisibility(true);
    }

    public void showUserFilterControls() {
        filterView.setUserFilterControlsVisibility(true);
        filterView.setUserFilterNameVisibility(false);
    }

    private Runnable removeAction(Long filterId) {
        return () -> filterService.removeIssueFilter(filterId, new FluentCallback<Boolean>()
                .withError(throwable -> {
                    fireEvent(new NotifyEvents.Show(lang.errNotRemoved(), NotifyEvents.NotifyType.ERROR));
                })
                .withSuccess(aBoolean -> {
                    fireEvent(new NotifyEvents.Show(lang.issueFilterRemoveSuccessed(), NotifyEvents.NotifyType.SUCCESS));
                    fireEvent(new IssueEvents.ChangeUserFilterModel());
                    filterView.resetFilter();
                    filterView.getIssueFilterParams().resetFilter();
                }));
    }

    private CaseFilter fillUserFilter() {
        CaseFilter filter = new CaseFilter();
        filter.setName(filterView.filterName().getValue());
        filter.setType(En_CaseFilterType.CASE_OBJECTS);
        CaseQuery query = filterParamView.getFilterFields();
        filter.setParams(query);
        query.setSearchString(filterParamView.searchPattern().getValue());
        return filter;
    }

    @Inject
    IssueFilterControllerAsync filterService;

    private AbstractIssueFilterView filterView;
    private AbstractIssueFilterWidgetView filterParamView;

    @Inject
    Lang lang;

    private boolean isCreateFilterAction = true;
}
