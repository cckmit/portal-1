package ru.protei.portal.ui.issueassignment.client.activity.issueassignment;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.activity.client.enums.Type;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;

public abstract class IssueAssignmentActivity implements Activity, AbstractIssueAssignmentActivity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
    }

    @Event
    public void onInitDetails(AppEvents.InitDetails initDetails) {
        this.initDetails = initDetails;
    }

    @Event(Type.FILL_CONTENT)
    public void onShow(IssueAssignmentEvents.Show event) {
        if (!policyService.hasPrivilegeFor(En_Privilege.ISSUE_REPORT)) {
            fireEvent(new ForbiddenEvents.Show(initDetails.parent));
            return;
        }
        showActionBarActions();
        showView();
        showComponents();
    }

    @Event
    public void onIssueCreateClicked(ActionBarEvents.Clicked event) {
        if (!UiConstants.ActionBarIdentity.ISSUE_ASSIGNMENT_CREATE_ISSUE.equals(event.identity)) {
            return;
        }
        if (!policyService.hasPrivilegeFor(En_Privilege.ISSUE_CREATE)) {
            return;
        }
        fireEvent(new IssueEvents.Create());
    }

    @Override
    public void onToggleTableClicked() {
        isTableVisible = !isTableVisible;
        drawTableVisibility(isTableVisible);
    }

    @Override
    public void onReloadClicked() {
        showComponents();
    }

    private void showView() {
        initDetails.parent.clear();
        initDetails.parent.add(view.asWidget());
    }

    private void showActionBarActions() {
        fireEvent(new ActionBarEvents.Clear());
        if (policyService.hasPrivilegeFor(En_Privilege.ISSUE_CREATE)) {
            fireEvent(new ActionBarEvents.Add(lang.issueCreate(), null, UiConstants.ActionBarIdentity.ISSUE_ASSIGNMENT_CREATE_ISSUE));
        }
    }

    private void showComponents() {
        fireEvent(new IssueAssignmentEvents.ShowTable(view.tableContainer()));
        fireEvent(new IssueAssignmentEvents.ShowDesk(view.deskContainer()));
    }

    private void drawTableVisibility(boolean isVisible) {
        if (isVisible) {
            view.table().setStyleName("col-md-3");
            view.desk().setStyleName("col-md-9");
        } else {
            view.table().setStyleName("col-md-0");
            view.desk().setStyleName("col-md-12");
        }
    }

    @Inject
    Lang lang;
    @Inject
    AbstractIssueAssignmentView view;
    @Inject
    PolicyService policyService;

    private boolean isTableVisible = true;
    private AppEvents.InitDetails initDetails;
}
