package ru.protei.portal.ui.absencereport.client.page;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.AbsenceReportEvents;
import ru.protei.portal.ui.common.client.events.ActionBarEvents;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.winter.web.common.client.events.MenuEvents;
import ru.protei.winter.web.common.client.events.SectionEvents;

public abstract class AbsenceReportPage implements Activity {

    @PostConstruct
    public void onInit() {
        ТAB = lang.absenceReports();
    }

    @Event
    public void onAuthSuccess(AuthEvents.Success event) {
        if (event.profile.hasPrivilegeFor(En_Privilege.ABSENCE_REPORT)) {
            fireEvent(new MenuEvents.Add(ТAB, UiConstants.TabIcons.ABSENCE_REPORTS, ТAB, DebugIds.SIDEBAR_MENU.ABSENCE_REPORTS));
            fireEvent(new AppEvents.InitPage(show));
        }
    }

    @Event
    public void onShow(AbsenceReportEvents.Show event) {
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
        if (policyService.hasPrivilegeFor(En_Privilege.ABSENCE_REPORT)) {
            fireEvent(new MenuEvents.Select(ТAB));
        }
    }


    @Inject
    Lang lang;

    @Inject
    PolicyService policyService;

    private String ТAB;
    private AbsenceReportEvents.Show show = new AbsenceReportEvents.Show();
}
