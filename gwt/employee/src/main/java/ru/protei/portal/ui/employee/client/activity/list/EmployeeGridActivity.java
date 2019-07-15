package ru.protei.portal.ui.employee.client.activity.list;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.query.EmployeeQuery;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.ActionBarEvents;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.EmployeeEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.viewtype.ViewType;
import ru.protei.portal.ui.employee.client.activity.filter.AbstractEmployeeFilterActivity;
import ru.protei.portal.ui.employee.client.activity.filter.AbstractEmployeeFilterView;

public abstract class EmployeeGridActivity implements AbstractEmployeeGridActivity, AbstractEmployeeFilterActivity, Activity {
    @PostConstruct
    public void onInit() {
        filterView.setActivity(this);
        query = makeQuery();
        currentViewType = ViewType.TABLE;
    }

    @Event
    public void onAuthSuccess(AuthEvents.Success event) {
        filterView.resetFilter();
    }

    @Event
    public void onShow(EmployeeEvents.Show event) {
        fireEvent(new AppEvents.InitPanelName(lang.employees()));

        fireEvent(new ActionBarEvents.Clear());
//        if (policyService.hasPrivilegeFor(En_Privilege.)) {
//            fireEvent(new ActionBarEvents.Add(lang.buttonCreate(), UiConstants.ActionBarIcons.CREATE, UiConstants.ActionBarIdentity.COMPANY));
//        }

        boolean isListCurrent = currentViewType == ViewType.LIST;
        fireEvent(new ActionBarEvents.Add(
                isListCurrent ? lang.table() : lang.list(),
                isListCurrent ? UiConstants.ActionBarIcons.TABLE : UiConstants.ActionBarIcons.LIST,
                UiConstants.ActionBarIdentity.EMPLOYEE_TYPE_VIEW
        ));

        fireEvent(new EmployeeEvents.ShowDefinite(currentViewType, filterView.asWidget(), query));
    }

    @Event
    public void onChangeViewClicked(ActionBarEvents.Clicked event) {
        if (!(UiConstants.ActionBarIdentity.EMPLOYEE_TYPE_VIEW.equals(event.identity)))
            return;

        currentViewType = currentViewType == ViewType.TABLE ? ViewType.LIST : ViewType.TABLE;
        onShow(new EmployeeEvents.Show());
    }

    @Override
    public void onFilterChanged() {
        query = makeQuery();
        fireEvent(new EmployeeEvents.UpdateData(currentViewType, query));
    }

    private EmployeeQuery makeQuery() {
        return new EmployeeQuery(false, false, true,
                null,
                filterView.searchPattern().getValue(),
                filterView.workPhone().getValue(),
                filterView.mobilePhone().getValue(),
                filterView.ipAddress().getValue(),
                filterView.email().getValue(),
                filterView.sortField().getValue(),
                filterView.sortDir().getValue() ? En_SortDir.ASC : En_SortDir.DESC);
    }

//    private EmployeeQuery makeQuery() {
//        EmployeeQuery eq = new EmployeeQuery(filterView.searchPattern().getValue(),
//                filterView.sortField().getValue(),
//                filterView.sortDir().getValue()? En_SortDir.ASC: En_SortDir.DESC);
//
//        return eq;
//    }


    @Inject
    AbstractEmployeeFilterView filterView;
    @Inject
    Lang lang;
//    @Inject
//    PolicyService policyService;

    private ViewType currentViewType;
    private EmployeeQuery query;
}
