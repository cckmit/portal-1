package ru.protei.portal.ui.employeeregistration.client.activity.table;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.activity.client.enums.Type;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.ent.EmployeeRegistration;
import ru.protei.portal.core.model.query.EmployeeRegistrationQuery;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.ActionBarEvents;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.EmployeeRegistrationEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.EmployeeRegistrationControllerAsync;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.employeeregistration.client.activity.filter.AbstractEmployeeRegistrationFilterActivity;
import ru.protei.portal.ui.employeeregistration.client.activity.filter.AbstractEmployeeRegistrationFilterView;

import java.util.List;

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

    @Event(Type.FILL_CONTENT)
    public void onShow(EmployeeRegistrationEvents.Show event) {
        init.parent.clear();
        init.parent.add(view.asWidget());

        fireEvent(policyService.hasPrivilegeFor(En_Privilege.EMPLOYEE_REGISTRATION_CREATE) ?
                new ActionBarEvents.Add(lang.buttonCreate(), null, UiConstants.ActionBarIdentity.EMPLOYEE_REGISTRATION) :
                new ActionBarEvents.Clear()
        );

        filterView.resetFilter();
        requestEmployeeRegistrationsCount(makeQuery());
    }

    @Event
    public void onCreateClicked(ActionBarEvents.Clicked event) {
        if (!UiConstants.ActionBarIdentity.EMPLOYEE_REGISTRATION.equals(event.identity)) {
            return;
        }
        fireEvent(new EmployeeRegistrationEvents.Create());
    }

    @Override
    public void onItemClicked(EmployeeRegistration value) {
        showPreview(value);
    }

    @Override
    public void onFilterChanged() {
        requestEmployeeRegistrationsCount(makeQuery());
    }

    @Override
    public void loadData(int offset, int limit, AsyncCallback<List<EmployeeRegistration>> asyncCallback) {
        EmployeeRegistrationQuery query = makeQuery();
        query.setOffset(offset);
        query.setLimit(limit);

        employeeRegistrationService.getEmployeeRegistrations(query, new RequestCallback<List<EmployeeRegistration>>() {
            @Override
            public void onError(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(List<EmployeeRegistration> employeeRegistrations) {
                asyncCallback.onSuccess(employeeRegistrations);
            }
        });

    }

    private EmployeeRegistrationQuery makeQuery() {
        EmployeeRegistrationQuery query = new EmployeeRegistrationQuery();
        query.searchString = filterView.searchString().getValue();
        query.setCreatedFrom(filterView.dateRange().getValue().from);
        query.setCreatedTo(filterView.dateRange().getValue().to);
        query.setStates(filterView.states().getValue());
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

    private void requestEmployeeRegistrationsCount(EmployeeRegistrationQuery query) {
        view.clearRecords();
        animation.closeDetails();

        employeeRegistrationService.getEmployeeRegistrationCount(query, new RequestCallback<Integer>() {
            @Override
            public void onError(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(Integer count) {
                view.setRecordCount(count);
            }
        });
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

    private AppEvents.InitDetails init;
}
