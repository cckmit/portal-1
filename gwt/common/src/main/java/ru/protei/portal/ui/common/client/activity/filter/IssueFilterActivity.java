package ru.protei.portal.ui.common.client.activity.filter;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_CaseFilterType;
import ru.protei.portal.core.model.ent.CaseFilter;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.view.CaseFilterShortView;
import ru.protei.portal.ui.common.client.activity.issuefilter.AbstractIssueFilterWidgetView;
import ru.protei.portal.ui.common.client.events.ConfirmDialogEvents;
import ru.protei.portal.ui.common.client.events.IssueEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.IssueFilterControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.function.Consumer;

public abstract class IssueFilterActivity implements Activity, AbstractIssueFilterActivity, AbstractIssueFilterModel {
    @PostConstruct
    public void onInit() {
        filterParamView = filterView.getIssueFilterWidget();
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

    private void showUserFilterName(){
        filterView.setUserFilterControlsVisibility(false);
        filterView.setUserFilterNameVisibility(true);
    }

    private void showUserFilterControls() {
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
//                    view.getIssueFilter().resetFilter();
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

    @Override
    public void onUserFilterChanged(Long id, Consumer<CaseFilter> consumer) {

    }

    @Override
    public void onSaveFilterClicked(CaseFilter caseFilter, Consumer<CaseFilterShortView> consumer) {

    }

    @Override
    public void onRemoveFilterClicked(Long id) {

    }

    @Inject
    IssueFilterControllerAsync filterService;

    @Inject
    AbstractIssueFilterView filterView;

    private AbstractIssueFilterWidgetView filterParamView;

    @Inject
    Lang lang;

    private boolean isCreateFilterAction = true;
}
