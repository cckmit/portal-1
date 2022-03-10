package ru.protei.portal.ui.employeeregistration.client.activity.table;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.activity.client.enums.Type;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.ent.EmployeeRegistration;
import ru.protei.portal.core.model.query.EmployeeRegistrationQuery;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.EmployeeRegistrationControllerAsync;
import ru.protei.portal.ui.common.shared.model.DefaultErrorHandler;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.employeeregistration.client.activity.filter.AbstractEmployeeRegistrationFilterActivity;
import ru.protei.portal.ui.employeeregistration.client.activity.filter.AbstractEmployeeRegistrationFilterView;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.List;
import java.util.stream.Collectors;

public abstract class EmployeeRegistrationTableActivity implements AbstractEmployeeRegistrationTableActivity,
        AbstractEmployeeRegistrationFilterActivity, Activity {

    @PostConstruct
    public void init() {
        view.setActivity(this);
        view.setAnimation(animation);
        filterView.setActivity(this);
        view.getFilterContainer().add(filterView.asWidget());
    }

    @Event
    public void onInitDetails(AppEvents.InitDetails event) {
        this.init = event;
    }

    @Event
    public void onAuthSuccess (AuthEvents.Success event) {
        filterView.resetFilter();
    }

    @Event(Type.FILL_CONTENT)
    public void onShow(EmployeeRegistrationEvents.Show event) {
        if (!policyService.hasPrivilegeFor(En_Privilege.EMPLOYEE_REGISTRATION_VIEW)) {
            fireEvent(new ErrorPageEvents.ShowForbidden());
            return;
        }

        init.parent.clear();
        init.parent.add(view.asWidget());

        fireEvent(policyService.hasPrivilegeFor(En_Privilege.EMPLOYEE_REGISTRATION_CREATE) ?
                new ActionBarEvents.Add(lang.buttonCreate(), null, UiConstants.ActionBarIdentity.EMPLOYEE_REGISTRATION) :
                new ActionBarEvents.Clear()
        );

        this.preScroll = event.preScroll;

        loadTable();
    }

    @Event
    public void onCreateClicked(ActionBarEvents.Clicked event) {
        if (!UiConstants.ActionBarIdentity.EMPLOYEE_REGISTRATION.equals(event.identity)) {
            return;
        }

        view.clearSelection();

        fireEvent(new EmployeeRegistrationEvents.Create());
    }

    @Event
    public void onEmployeeRegistrationChanged(EmployeeRegistrationEvents.ChangeEmployeeRegistration event) {
        employeeRegistrationService.getEmployeeRegistration(event.employeeRegistrationId, new FluentCallback<EmployeeRegistration>()
                .withSuccess(view::updateRow)
        );
    }

    @Override
    public void onItemClicked(EmployeeRegistration value) {
        persistScroll();
        showPreview(value);
    }

    @Override
    public void onCompleteProbationClicked(EmployeeRegistration value) {
        employeeRegistrationService.completeProbationPeriod(value.getId(), new FluentCallback<EmployeeRegistration>()
                .withError(throwable -> {
                    defaultErrorHandler.accept(throwable);
                })
                .withSuccess(employeeRegistration -> {
                    view.updateRow(employeeRegistration);
                    fireEvent(new CommentAndHistoryEvents.Reload());
                    fireEvent(new NotifyEvents.Show(lang.probationCompletedSuccessfully(), NotifyEvents.NotifyType.SUCCESS));
                })
        );
    }

    @Override
    public void onFilterChanged() {
        loadTable();
    }

    @Override
    public void loadData(int offset, int limit, AsyncCallback<List<EmployeeRegistration>> asyncCallback) {
        boolean isFirstChunk = offset == 0;
        EmployeeRegistrationQuery query = makeQuery();
        query.setOffset(offset);
        query.setLimit(limit);
        employeeRegistrationService.getEmployeeRegistrations(query, new FluentCallback<SearchResult<EmployeeRegistration>>()
                .withError(throwable -> {
                    fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
                    asyncCallback.onFailure(throwable);
                })
                .withSuccess(sr -> {
                    if (isFirstChunk) {
                        view.setTotalRecords(sr.getTotalCount());
                        restoreScroll();
                    }

                    asyncCallback.onSuccess(sr.getResults());
                }));
    }

    @Override
    public void onEditClicked(EmployeeRegistration value) {
        fireEvent(new EmployeeRegistrationEvents.Edit(value.getId()));
    }

    private void persistScroll() {
        scrollTo = Window.getScrollTop();
    }

    private void restoreScroll() {
        if (!preScroll) {
            view.clearSelection();
            return;
        }

        Window.scrollTo(0, scrollTo);
        preScroll = false;
        scrollTo = 0;
    }

    private void loadTable() {
        animation.closeDetails();
        view.clearRecords();
        view.triggerTableLoad();
    }

    private EmployeeRegistrationQuery makeQuery() {
        EmployeeRegistrationQuery query = new EmployeeRegistrationQuery();
        query.searchString = filterView.searchString().getValue();
        query.setCreatedFrom(filterView.dateRange().getValue().from);
        query.setCreatedTo(filterView.dateRange().getValue().to);
        query.setStates(filterView.states().getValue() == null ? null : filterView.states().getValue().stream().map(CaseState::getId).collect(Collectors.toSet()));
        query.setSortDir(filterView.sortDir().getValue() ? En_SortDir.ASC : En_SortDir.DESC);
        query.setSortField(filterView.sortField().getValue());
        return query;
    }

    private void showPreview(EmployeeRegistration value) {
        if (value == null || value.getId() == null) {
            animation.closeDetails();
        } else {
            animation.showDetails();
            fireEvent(new EmployeeRegistrationEvents.ShowPreview(view.getPreviewContainer(), value.getId()));
        }
    }

    @Inject
    private Lang lang;
    @Inject
    private TableAnimation animation;
    @Inject
    private PolicyService policyService;
    @Inject
    private AbstractEmployeeRegistrationTableView view;
    @Inject
    private AbstractEmployeeRegistrationFilterView filterView;
    @Inject
    private EmployeeRegistrationControllerAsync employeeRegistrationService;
    @Inject
    private DefaultErrorHandler defaultErrorHandler;

    private AppEvents.InitDetails init;

    private Integer scrollTo = 0;
    private Boolean preScroll = false;
}
