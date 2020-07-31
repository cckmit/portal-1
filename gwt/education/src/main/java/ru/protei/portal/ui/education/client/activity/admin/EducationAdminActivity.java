package ru.protei.portal.ui.education.client.activity.admin;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.ent.EducationEntry;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerActivity;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.EducationEvents;
import ru.protei.portal.ui.common.client.events.ErrorPageEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.EducationControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.education.client.activity.admin.filter.AbstractEducationAdminFilterActivity;
import ru.protei.portal.ui.education.client.activity.admin.filter.AbstractEducationAdminFilterView;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.List;

import static ru.protei.portal.ui.education.client.util.AccessUtil.isAdmin;

public abstract class EducationAdminActivity implements Activity,
        AbstractEducationAdminActivity, AbstractEducationAdminFilterActivity, AbstractPagerActivity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
        pagerView.setActivity(this);
        filterView.setActivity(this);
        view.getFilterContainer().add(filterView.asWidget());
    }

    @Event
    public void onAuthSuccess(AuthEvents.Success event) {
        filterView.resetFilter();
    }

    @Event
    public void onShow(EducationEvents.ShowAdmin event) {
        if (!isAdmin(policyService)) {
            fireEvent(new ErrorPageEvents.ShowForbidden(event.parent));
            return;
        }

        event.parent.clear();
        event.parent.add(view.asWidget());
        view.getPagerContainer().add(pagerView.asWidget());

        loadTable();
    }

    @Override
    public void onEditClicked(EducationEntry entry) {
        if (entry == null) {
            return;
        }
        fireEvent(new EducationEvents.EditEducationEntry(entry));
    }

    @Override
    public void loadData(int offset, int limit, AsyncCallback<List<EducationEntry>> handler) {
        boolean isFirstChunk = offset == 0;
        boolean showOnlyNotApproved = filterView.showOnlyNotApproved().getValue() == Boolean.TRUE;
        boolean showOutdated = filterView.showOutdated().getValue() == Boolean.TRUE;
        educationController.adminGetEntries(offset, limit, showOnlyNotApproved, showOutdated, new FluentCallback<SearchResult<EducationEntry>>()
                .withError(throwable -> {
                    fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
                    handler.onFailure(throwable);
                })
                .withSuccess(sr -> {
                    if (isFirstChunk) {
                        view.setTotalRecords(sr.getTotalCount());
                        pagerView.setTotalPages(view.getPageCount());
                        pagerView.setTotalCount(sr.getTotalCount());
                    }

                    handler.onSuccess(sr.getResults());
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
    public void onFilterChanged() {
        loadTable();
    }

    private void loadTable() {
        view.clearRecords();
        view.triggerTableLoad();
    }

    @Inject
    Lang lang;
    @Inject
    AbstractEducationAdminView view;
    @Inject
    AbstractEducationAdminFilterView filterView;
    @Inject
    AbstractPagerView pagerView;
    @Inject
    EducationControllerAsync educationController;
    @Inject
    PolicyService policyService;
}
