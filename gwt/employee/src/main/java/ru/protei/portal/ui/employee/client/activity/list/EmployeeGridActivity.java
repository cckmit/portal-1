package ru.protei.portal.ui.employee.client.activity.list;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.query.EmployeeQuery;
import ru.protei.portal.ui.common.client.util.TopBrassPersonUtils;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.LocalStorageService;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.viewtype.ViewType;
import ru.protei.portal.ui.employee.client.activity.filter.AbstractEmployeeFilterActivity;
import ru.protei.portal.ui.employee.client.activity.filter.AbstractEmployeeFilterView;

public abstract class EmployeeGridActivity implements AbstractEmployeeGridActivity, AbstractEmployeeFilterActivity, Activity {
    @PostConstruct
    public void onInit() {
        filterView.setActivity(this);
        query = makeQuery();
        currentViewType = ViewType.valueOf(localStorageService.getOrDefault(EMPLOYEE_CURRENT_VIEW_TYPE, ViewType.LIST.toString()));
    }

    @Event
    public void onAuthSuccess(AuthEvents.Success event) {
        filterView.resetFilter();
    }

    @Event
    public void onShow(EmployeeEvents.Show event) {
        if (!policyService.hasPrivilegeFor(En_Privilege.EMPLOYEE_VIEW)) {
            fireEvent(new ForbiddenEvents.Show());
            return;
        }

        fireEvent(new ActionBarEvents.Clear());

        fireEvent(new ActionBarEvents.Add(lang.employeeTopBrassBtn(), "", UiConstants.ActionBarIdentity.TOP_BRASS));

        boolean isListCurrent = currentViewType == ViewType.LIST;
        fireEvent(new ActionBarEvents.Add(
                isListCurrent ? lang.table() : lang.list(),
                isListCurrent ? UiConstants.ActionBarIcons.TABLE : UiConstants.ActionBarIcons.LIST,
                UiConstants.ActionBarIdentity.EMPLOYEE_TYPE_VIEW
        ));

        if (policyService.hasPrivilegeFor(En_Privilege.EMPLOYEE_CREATE)) {
            fireEvent(new ActionBarEvents.Add(lang.buttonCreate(), "", UiConstants.ActionBarIdentity.EMPLOYEE_CREATE));
        }

        fireEvent(new EmployeeEvents.ShowDefinite(currentViewType, filterView.asWidget(), query));
    }

    @Event
    public void onChangeViewClicked(ActionBarEvents.Clicked event) {
        if (!(UiConstants.ActionBarIdentity.EMPLOYEE_TYPE_VIEW.equals(event.identity))) {
            return;
        }

        currentViewType = currentViewType == ViewType.TABLE ? ViewType.LIST : ViewType.TABLE;
        onShow(new EmployeeEvents.Show());

        localStorageService.set(EMPLOYEE_CURRENT_VIEW_TYPE, currentViewType.toString());
    }

    @Event
    public void onTopBrassClicked(ActionBarEvents.Clicked event) {
        if (!(UiConstants.ActionBarIdentity.TOP_BRASS.equals(event.identity))) {
            return;
        }

        if (!policyService.hasPrivilegeFor(En_Privilege.EMPLOYEE_VIEW)) {
            fireEvent(new ForbiddenEvents.Show());
            return;
        }

        fireEvent(new ActionBarEvents.Clear());
        fireEvent(new EmployeeEvents.ShowTopBrass());
    }

    @Event
    public void onEmployeeCreateClicked(ActionBarEvents.Clicked event) {
        if (!(UiConstants.ActionBarIdentity.EMPLOYEE_CREATE.equals(event.identity))) {
            return;
        }

        if (!policyService.hasPrivilegeFor(En_Privilege.EMPLOYEE_VIEW)) {
            fireEvent(new ForbiddenEvents.Show());
            return;
        }

        fireEvent(new ActionBarEvents.Clear());
        fireEvent(new EmployeeEvents.Edit());
    }

    @Override
    public void onFilterChanged() {
        query = makeQuery();
        fireEvent(new EmployeeEvents.UpdateData(currentViewType, query));
    }

    private EmployeeQuery makeQuery() {
        return new EmployeeQuery(filterView.showFired().getValue() ? null : false, false, true,
                filterView.organizations().getValue(),
                filterView.searchPattern().getValue(),
                filterView.workPhone().getValue(),
                filterView.mobilePhone().getValue(),
                filterView.ipAddress().getValue(),
                filterView.email().getValue(),
                filterView.departmentParent().getValue(),
                filterView.sortField().getValue(),
                filterView.sortDir().getValue() ? En_SortDir.ASC : En_SortDir.DESC,
                filterView.showTopBrass().getValue() ? TopBrassPersonUtils.getPersonIds() : null);
    }


    @Inject
    AbstractEmployeeFilterView filterView;
    @Inject
    Lang lang;
    @Inject
    LocalStorageService localStorageService;
    @Inject
    PolicyService policyService;

    private ViewType currentViewType;
    private boolean topBrassPage;
    private EmployeeQuery query;
    private static final String EMPLOYEE_CURRENT_VIEW_TYPE = "employeeCurrentViewType";
}
