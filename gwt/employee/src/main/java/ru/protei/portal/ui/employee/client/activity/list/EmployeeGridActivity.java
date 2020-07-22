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
        currentViewType = ViewType.valueOf(localStorageService.getOrDefault(EMPLOYEE_CURRENT_VIEW_TYPE, ViewType.LIST.toString()));
    }

    @Event
    public void onAuthSuccess(AuthEvents.Success event) {
        filterView.resetFilter();
    }

    @Event(Type.FILL_CONTENT)
    public void onShow(EmployeeEvents.Show event) {
        show();
        fireEvent(new EmployeeEvents.ShowDefinite(currentViewType, filterView.asWidget(), query, event.preScroll));
    }

    @Event(Type.FILL_CONTENT)
    public void onShow(EmployeeEvents.ShowAbsent event) {
        show();
        query.setAbsent(true);
        filterView.showAbsent().setValue(true);

        fireEvent(new EmployeeEvents.ShowDefinite(currentViewType, filterView.asWidget(), query, false));
    }

    @Event
    public void onChangeViewClicked(ActionBarEvents.Clicked event) {
        if (!(UiConstants.ActionBarIdentity.EMPLOYEE_TYPE_VIEW.equals(event.identity))) {
            return;
        }

        currentViewType = currentViewType == ViewType.TABLE ? ViewType.LIST : ViewType.TABLE;
        onShow(new EmployeeEvents.Show(false));

        localStorageService.set(EMPLOYEE_CURRENT_VIEW_TYPE, currentViewType.toString());
    }

    @Event
    public void onTopBrassClicked(ActionBarEvents.Clicked event) {
        if (!(UiConstants.ActionBarIdentity.TOP_BRASS.equals(event.identity))) {
            return;
        }

        if (!policyService.hasPrivilegeFor(En_Privilege.EMPLOYEE_VIEW)) {
            fireEvent(new ErrorPageEvents.ShowForbidden());
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
            fireEvent(new ErrorPageEvents.ShowForbidden());
            return;
        }

        fireEvent(new ActionBarEvents.Clear());
        fireEvent(new EmployeeEvents.Edit());
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
    public void onUpdate(EmployeeEvents.Update event) {
        if(event.id == null || !policyService.hasPrivilegeFor(En_Privilege.ABSENCE_VIEW))
            return;

        fireEvent(new EmployeeEvents.UpdateDefinite(currentViewType, event.id));
    }

    @Override
    public void onFilterChanged() {
        query = makeQuery();
        fireEvent(new EmployeeEvents.UpdateData(currentViewType, query));
    }

    private void show() {
        if (!policyService.hasPrivilegeFor(En_Privilege.EMPLOYEE_VIEW)) {
            fireEvent(new ErrorPageEvents.ShowForbidden());
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

        if (policyService.hasPrivilegeFor(En_Privilege.ABSENCE_CREATE)) {
            fireEvent(new ActionBarEvents.Add(lang.absenceButtonCreate(), "", UiConstants.ActionBarIdentity.ABSENCE));
        }

        if (policyService.hasPrivilegeFor(En_Privilege.ABSENCE_REPORT)) {
            fireEvent(new ActionBarEvents.Add(lang.absenceButtonReport(), "", UiConstants.ActionBarIdentity.ABSENCE_REPORT));
        }
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
    LocalStorageService localStorageService;
    @Inject
    PolicyService policyService;

    private ViewType currentViewType;
    private EmployeeQuery query;
    private static final String EMPLOYEE_CURRENT_VIEW_TYPE = "employeeCurrentViewType";
}
