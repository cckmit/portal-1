package ru.protei.portal.ui.issuereport.client.widget.issuefilter;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.ent.CaseFilter;
import ru.protei.portal.core.model.view.CaseFilterShortView;
import ru.protei.portal.ui.common.client.events.ConfirmDialogEvents;
import ru.protei.portal.ui.common.client.events.IssueEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.IssueFilterControllerAsync;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

public abstract class IssueFilterModel implements Activity {

    @Inject
    public void onInit(Lang lang) {
        this.lang = lang;
    }

    @Event
    public void onConfirmRemove(ConfirmDialogEvents.Confirm event) {

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
            }
        });
    }

    public void setIssueFilter(IssueFilter issueFilter) {

        CaseFilterShortView filter = issueFilter.userFilter.getValue();
        if (filter == null){
            issueFilter.resetFilter();
            //showUserFilterControls();
            return;
        }

        filterService.getIssueFilter(filter.getId(), new RequestCallback<CaseFilter>() {
            @Override
            public void onError(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(lang.errNotFound(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(CaseFilter filter) {
                issueFilter.setValue(filter.getParams());
            }
        } );
    }

    public void saveIssueFilter(CaseFilter filter, IssueFilter issueFilter) {
        filterService.saveIssueFilter(filter, new RequestCallback< CaseFilter >() {
            @Override
            public void onError( Throwable throwable ) {
                fireEvent(new NotifyEvents.Show(lang.errSaveIssueFilter(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(CaseFilter filter) {
                fireEvent(new NotifyEvents.Show(lang.msgObjectSaved(), NotifyEvents.NotifyType.SUCCESS));

                issueFilter.userFilter.updateFilterType(filter.getType());
                issueFilter.userFilter.setValue(filter.toShortView());
            }
        } );
    }

    public void removeIssueFilter(Long id) {
        filterIdToRemove = id;
        fireEvent(new ConfirmDialogEvents.Show(getClass().getName(), lang.issueFilterRemoveConfirmMessage()));
    }

    Lang lang;

    private Long filterIdToRemove;

    @Inject
    IssueFilterControllerAsync filterService;

}
