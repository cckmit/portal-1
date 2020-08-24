package ru.protei.portal.ui.employee.client.activity.list;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.activity.client.enums.Type;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.query.EmployeeQuery;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.LocalStorageService;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.util.TopBrassPersonUtils;
import ru.protei.portal.ui.common.client.widget.viewtype.ViewType;
import ru.protei.portal.ui.employee.client.activity.filter.AbstractEmployeeFilterActivity;
import ru.protei.portal.ui.employee.client.activity.filter.AbstractEmployeeFilterView;

import static ru.protei.portal.core.model.helper.PhoneUtils.normalizePhoneNumber;

public abstract class EmployeeGridActivity implements AbstractEmployeeGridActivity, AbstractEmployeeFilterActivity, Activity {

    @PostConstruct
    public void onInit() {
        filterView.setActivity(this);
        filterView.resetFilter();
        query = makeQuery();
    }

    @Event
    public void onAuthSuccess(AuthEvents.Success event) {
        filterView.resetFilter();
    }

    @Event(Type.FILL_CONTENT)
    public void onShow(EmployeeEvents.Show event) {
        if (!policyService.hasPrivilegeFor(En_Privilege.EMPLOYEE_VIEW)) {
            fireEvent(new ErrorPageEvents.ShowForbidden());
            return;
        }

        if (event.view != null) {
            currentViewType = event.view;
        }
        fireEvent(new EmployeeEvents.SelectTab(currentViewType));

        fireEvent(new ActionBarEvents.Clear());
        if (policyService.hasPrivilegeFor(En_Privilege.EMPLOYEE_CREATE)) {
            fireEvent(new ActionBarEvents.Add(lang.buttonCreate(), "", UiConstants.ActionBarIdentity.EMPLOYEE_CREATE));
        }

        fireEvent(new EmployeeEvents.ShowDefinite(currentViewType, filterView.asWidget(), query, event.preScroll));
    }

    @Event
    public void onEmployeeCreateClicked(ActionBarEvents.Clicked event) {
        if (!(UiConstants.ActionBarIdentity.EMPLOYEE_CREATE.equals(event.identity))) {
            return;
        }

        if (!policyService.hasPrivilegeFor(En_Privilege.EMPLOYEE_VIEW)) {
            fireEvent(new ErrorPageEvents.ShowForbidden());
            return;
        }

        fireEvent(new ActionBarEvents.Clear());
        fireEvent(new EmployeeEvents.Edit());
    }

    @Event
    public void onUpdate(EmployeeEvents.Update event) {
        if(event.id == null || !policyService.hasPrivilegeFor(En_Privilege.ABSENCE_VIEW))
            return;

        fireEvent(new EmployeeEvents.UpdateDefinite(currentViewType, event.id));
    }

    @Event
    public void onEdit(EmployeeEvents.Edit event) {
        fireEvent(new EmployeeEvents.SelectTab(currentViewType));
    }

    @Event
    public void onShowFullScreen(EmployeeEvents.ShowFullScreen event) {
        fireEvent(new EmployeeEvents.SelectTab(currentViewType));
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
                normalizePhoneNumber(filterView.workPhone().getValue()),
                normalizePhoneNumber(filterView.mobilePhone().getValue()),
                filterView.ipAddress().getValue(),
                filterView.email().getValue(),
                filterView.departmentParent().getValue(),
                filterView.sortField().getValue(),
                filterView.sortDir().getValue() ? En_SortDir.ASC : En_SortDir.DESC,
                filterView.showTopBrass().getValue() ? TopBrassPersonUtils.getPersonIds() : null,
                filterView.showAbsent().getValue());
    }


    @Inject
    AbstractEmployeeFilterView filterView;
    @Inject
    Lang lang;
    @Inject
    PolicyService policyService;

    private ViewType currentViewType = ViewType.LIST;
    private EmployeeQuery query;
}
