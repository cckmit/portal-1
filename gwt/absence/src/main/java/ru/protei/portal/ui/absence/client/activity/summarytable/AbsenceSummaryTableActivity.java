package ru.protei.portal.ui.absence.client.activity.summarytable;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.PersonAbsence;
import ru.protei.portal.core.model.query.AbsenceQuery;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerActivity;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.AbsenceControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.List;

public abstract class AbsenceSummaryTableActivity implements AbstractAbsenceSummaryTableActivity, Activity,
        AbstractPagerActivity {

    @PostConstruct
    public void onInit() {
        CREATE_ACTION = lang.buttonCreate();
        REPORT_ACTION = lang.buttonReport();
        view.setActivity(this);
        pagerView.setActivity( this );
    }

    @Event
    public void onAuthSuccess (AuthEvents.Success event) {
        if (!policyService.hasPrivilegeFor(En_Privilege.ABSENCE_VIEW)) {
            return;
        }

        view.getFilterWidget().resetFilter();
    }

    @Event
    public void onInit(AppEvents.InitDetails event) {
        this.initDetails = event;
    }

    @Event
    public void onShow(AbsenceEvents.ShowSummaryTable event) {

        initDetails.parent.clear();
        initDetails.parent.add(view.asWidget());

        view.getPagerContainer().add( pagerView.asWidget() );

        fireEvent(new ActionBarEvents.Clear());

        if (policyService.hasPrivilegeFor(En_Privilege.ABSENCE_CREATE)) {
            fireEvent(new ActionBarEvents.Add(CREATE_ACTION, "", UiConstants.ActionBarIdentity.ABSENCE));
        }

        if (policyService.hasPrivilegeFor(En_Privilege.ABSENCE_REPORT)) {
            fireEvent(new ActionBarEvents.Add(REPORT_ACTION, "", UiConstants.ActionBarIdentity.ABSENCE_REPORT));
        }

        loadTable();
    }

    @Event
    public void onAbsenceCreateClicked(ActionBarEvents.Clicked event) {
        if (!UiConstants.ActionBarIdentity.ABSENCE.equals(event.identity)) {
            return;
        }

        if (!policyService.hasPrivilegeFor(En_Privilege.ABSENCE_CREATE)) {
            fireEvent(new ErrorPageEvents.ShowForbidden());
            return;
        }

        fireEvent(new AbsenceEvents.Edit());
    }

    @Event
    public void onAbsenceReportClicked(ActionBarEvents.Clicked event) {
        if (!UiConstants.ActionBarIdentity.ABSENCE_REPORT.equals(event.identity)) {
            return;
        }

        if (!policyService.hasPrivilegeFor(En_Privilege.ABSENCE_REPORT)) {
            fireEvent(new ErrorPageEvents.ShowForbidden());
            return;
        }

        fireEvent(new AbsenceEvents.CreateReport());
    }

    @Event
    public void onUpdate(AbsenceEvents.Update event) {
        if (view.asWidget().isAttached()) {
            loadTable();
        }
    }

    @Override
    public void onItemClicked(PersonAbsence value) {
        // do nothing
    }

    @Override
    public void onCompleteAbsence(PersonAbsence value) {
        absenceController.completeAbsence(value, new FluentCallback<Boolean>()
                .withSuccess(result -> fireSuccessNotify(lang.absenceCompletedSuccessfully(), value.getPersonId())));
    }

    @Override
    public void onEditClicked(PersonAbsence value) {
        fireEvent(new AbsenceEvents.Edit(value.getId()));
    }

    @Override
    public void onRemoveClicked(PersonAbsence value) {
        fireEvent(new ConfirmDialogEvents.Show(lang.absenceRemoveConfirmMessage(), removeAction(value)));
    }

    @Override
    public void loadData(int offset, int limit, AsyncCallback<List<PersonAbsence>> asyncCallback) {
        boolean isFirstChunk = offset == 0;
        query = getQuery();
        query.setOffset(offset);
        query.setLimit(limit);
        absenceController.getAbsences(query, new FluentCallback<SearchResult<PersonAbsence>>()
                .withError(throwable -> {
                    fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
                    asyncCallback.onFailure(throwable);
                })
                .withSuccess(sr -> {
                    if (!query.equals(getQuery())) {
                        loadData(offset, limit, asyncCallback);
                    }
                    else {
                        if (isFirstChunk) {
                            view.setTotalRecords(sr.getTotalCount());
                            pagerView.setTotalPages(view.getPageCount());
                            pagerView.setTotalCount(sr.getTotalCount());
                            restoreScroll();
                        }

                        asyncCallback.onSuccess(sr.getResults());
                    }
                }));
    }

    @Override
    public void onPageChanged(int page) {
        pagerView.setCurrentPage(page);
    }

    @Override
    public void onPageSelected(int page) {
        view.scrollTo(page);
    }

    @Override
    public void onFilterChange() {
        loadTable();
    }

    private Runnable removeAction(PersonAbsence value) {
        return () -> absenceController.removeAbsence(value, new FluentCallback<Long>()
                .withSuccess(result -> fireSuccessNotify(lang.absenceRemovedSuccessfully(), value.getPersonId())));
    }

    private AbsenceQuery getQuery() {
        return view.getFilterWidget().getFilterParamView().getQuery();
    }

    private void loadTable() {
        view.clearRecords();
        view.triggerTableLoad();
    }

    private void restoreScroll() {
        Window.scrollTo(0, 0);
    }

    private void fireSuccessNotify(String message, Long personId) {
        fireEvent(new NotifyEvents.Show(message, NotifyEvents.NotifyType.SUCCESS));
        fireEvent(new EmployeeEvents.Update(personId));
        fireEvent(new AbsenceEvents.Update());
    }

    @Inject
    AbstractAbsenceSummaryTableView view;

    @Inject
    AbstractPagerView pagerView;

    @Inject
    AbsenceControllerAsync absenceController;

    @Inject
    Lang lang;

    @Inject
    PolicyService policyService;

    private static String CREATE_ACTION;
    private static String REPORT_ACTION;
    private AppEvents.InitDetails initDetails;
    private AbsenceQuery query = null;
}
