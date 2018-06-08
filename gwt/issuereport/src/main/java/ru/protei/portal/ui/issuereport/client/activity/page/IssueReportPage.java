package ru.protei.portal.ui.issuereport.client.activity.page;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.winter.web.common.client.events.MenuEvents;
import ru.protei.winter.web.common.client.events.SectionEvents;

/**
 * Активность по работе с вкладкой "Отчеты обращений"
 */
public abstract class IssueReportPage implements Activity {

    @PostConstruct
    public void onInit() {
        ТAB = lang.issueReports();
    }

    @Event
    public void onAuthSuccess(AuthEvents.Success event) {
        if (event.profile.hasPrivilegeFor(En_Privilege.ISSUE_EXPORT)) {
            fireEvent(new MenuEvents.Add(ТAB, UiConstants.TabIcons.ISSUE_REPORTS));
            fireEvent(new AppEvents.InitPage(show));
        }
    }

    @Event
    public void onShowTable(IssueReportEvents.Show event) {
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
        fireEvent(new MenuEvents.Select(ТAB));
    }

    @Inject
    Lang lang;

    private String ТAB;
    private IssueReportEvents.Show show = new IssueReportEvents.Show();
}
