package ru.protei.portal.ui.issueassignment.client.activity.page;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.winter.web.common.client.events.MenuEvents;
import ru.protei.winter.web.common.client.events.SectionEvents;

public abstract class IssueAssignmentPage implements Activity {

    @PostConstruct
    public void onInit() {
        TAB = lang.issueAssignment();
    }

    @Event
    public void onAuthSuccess(AuthEvents.Success event) {
        if (event.profile.hasPrivilegeFor(En_Privilege.ISSUE_ASSIGNMENT_VIEW)) {
            fireEvent(new MenuEvents.Add(TAB, UiConstants.TabIcons.ISSUE_ASSIGNMENT, TAB,
                                         CrmConstants.PAGE_LINK.ISSUE_ASSIGNMENT,
                                         DebugIds.SIDEBAR_MENU.ISSUE_ASSIGNMENT));
            fireEvent(new AppEvents.InitPage(show));
        }
    }

    @Event
    public void onClickSection(SectionEvents.Clicked event) {
        if (!TAB.equals(event.identity)) {
            return;
        }
        fireSelectTab();
        fireEvent(show);
    }

    @Event
    public void onShowTable(IssueAssignmentEvents.Show event) {
        fireSelectTab();
    }

    private void fireSelectTab() {
        fireEvent(new ActionBarEvents.Clear());
        if (policyService.hasPrivilegeFor(En_Privilege.ISSUE_ASSIGNMENT_VIEW)) {
            fireEvent(new MenuEvents.Select(TAB));
        }
    }

    @Inject
    Lang lang;
    @Inject
    PolicyService policyService;

    private String TAB;
    private IssueAssignmentEvents.Show show = new IssueAssignmentEvents.Show();
}
