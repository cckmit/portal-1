package ru.protei.portal.ui.absence.client.activity.summarytable;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.ent.PersonAbsence;
import ru.protei.portal.core.model.query.AbsenceQuery;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerActivity;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerView;
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
        view.setActivity(this);
        pagerView.setActivity( this );
    }

    @Event
    public void onInit(AppEvents.InitDetails event) {
        this.initDetails = event;
    }

    @Event
    public void onShow(AbsenceEvents.ShowSummaryTable event) {
        view.clearRecords();

        initDetails.parent.clear();
        initDetails.parent.add(view.asWidget());

        view.getFilterWidget().resetFilter();
        view.getPagerContainer().add( pagerView.asWidget() );

        view.triggerTableLoad();
    }

    @Override
    public void onItemClicked(PersonAbsence value) {}

    @Override
    public void onCompleteAbsence(PersonAbsence value) {
        absenceController.completeAbsence(value, new FluentCallback<Boolean>()
                .withSuccess(result -> {
                    fireEvent(new NotifyEvents.Show(lang.absenceCompletedSuccessfully(), NotifyEvents.NotifyType.SUCCESS));
                    fireEvent(new EmployeeEvents.Update(value.getPersonId()));
                }));
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

    private Runnable removeAction(PersonAbsence value) {
        return () -> absenceController.removeAbsence(value, new FluentCallback<Boolean>()
                .withSuccess(result -> {
                    fireEvent(new NotifyEvents.Show(lang.absenceRemovedSuccessfully(), NotifyEvents.NotifyType.SUCCESS));
                    fireEvent(new EmployeeEvents.Update(value.getPersonId()));
                }));
    }

    private AbsenceQuery getQuery() {
        return view.getFilterWidget().getFilterParamView().getQuery();
    }

    private void restoreScroll() {
        Window.scrollTo(0, 0);
    }

    @Inject
    AbstractAbsenceSummaryTableView view;

    @Inject
    AbstractPagerView pagerView;

    @Inject
    AbsenceControllerAsync absenceController;

    @Inject
    Lang lang;

    private AppEvents.InitDetails initDetails;
    private AbsenceQuery query = null;
}
