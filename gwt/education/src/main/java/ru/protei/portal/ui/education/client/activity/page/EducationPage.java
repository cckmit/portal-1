package ru.protei.portal.ui.education.client.activity.page;

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
import ru.protei.portal.ui.common.client.events.EducationEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.winter.web.common.client.events.MenuEvents;
import ru.protei.winter.web.common.client.events.SectionEvents;

import static ru.protei.portal.ui.education.client.util.AccessUtil.*;

public abstract class EducationPage implements Activity {

    @PostConstruct
    public void onInit() {
        TAB = lang.education();
    }

    @Event
    public void onAuthSuccess(AuthEvents.Success event) {
        if (hasAccess(policyService)) {
            fireEvent(new MenuEvents.Add(TAB, UiConstants.TabIcons.EDUCATION, TAB,
                                         CrmConstants.PAGE_LINK.EDUCATION,
                                         DebugIds.SIDEBAR_MENU.EDUCATION));
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
    public void onShow(EducationEvents.Show event) {
        fireSelectTab();
    }

    private void fireSelectTab() {
        fireEvent(new ActionBarEvents.Clear());
        if (hasAccess(policyService)) {
            fireEvent(new MenuEvents.Select(TAB));
        }
    }

    @Inject
    Lang lang;
    @Inject
    PolicyService policyService;

    private String TAB;
    private EducationEvents.Show show = new EducationEvents.Show();
}
