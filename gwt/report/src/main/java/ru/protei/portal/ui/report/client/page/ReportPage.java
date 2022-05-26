package ru.protei.portal.ui.report.client.page;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.ActionBarEvents;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.ReportEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.winter.web.common.client.events.MenuEvents;
import ru.protei.winter.web.common.client.events.SectionEvents;

import static ru.protei.portal.ui.report.client.util.AccessUtil.canView;

public abstract class ReportPage implements Activity {

    @PostConstruct
    public void onInit() {
        ТAB = lang.issueReports();
    }

    @Event
    public void onAuthSuccess(AuthEvents.Success event) {
        if (canView(policyService)) {
            fireEvent(new MenuEvents.Add(ТAB, UiConstants.TabIcons.REPORTS, ТAB,
                                         CrmConstants.PAGE_LINK.REPORTS,
                                         DebugIds.SIDEBAR_MENU.REPORTS));
            fireEvent(new AppEvents.InitPage(show));
        }
    }

    @Event
    public void onShowTable(ReportEvents.Show event) {
        fireSelectTab();
    }

    @Event
    public void onCreate(ReportEvents.Create event) {
        fireSelectTab();
    }

    @Event
    public void onClickSection(SectionEvents.Clicked event) {
        if (!ТAB.equals(event.identity)) {
            return;
        }
        fireSelectTab();
        fireEvent(show);
    }

    private void fireSelectTab() {
        fireEvent(new ActionBarEvents.Clear());
        if (canView(policyService)) {
            fireEvent(new MenuEvents.Select(ТAB));
        }
    }

    @Inject
    Lang lang;
    @Inject
    PolicyService policyService;

    private String ТAB;
    private ReportEvents.Show show = new ReportEvents.Show();
}
