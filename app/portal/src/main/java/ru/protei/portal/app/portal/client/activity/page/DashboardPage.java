package ru.protei.portal.app.portal.client.activity.page;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.ActionBarEvents;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.DashboardEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.shared.model.Profile;
import ru.protei.winter.web.common.client.events.MenuEvents;
import ru.protei.winter.web.common.client.events.SectionEvents;

/**
 * Активность по работе с вкладкой "Dashboard"
 */
public abstract class DashboardPage implements Activity {

    @PostConstruct
    public void onInit() {
        TAB = lang.dashboard();
    }

    @Event
    public void onAuthSuccess( AuthEvents.Success event ) {
        if ( event.profile.hasPrivilegeFor( En_Privilege.DASHBOARD_VIEW) ) {
            fireEvent( new MenuEvents.Add(TAB, UiConstants.TabIcons.DASHBOARD, TAB,
                                          CrmConstants.PAGE_LINK.DASHBOARD,
                                          DebugIds.SIDEBAR_MENU.DASHBOARD ) );
            fireEvent( new AppEvents.InitPage( show ) );
        }
    }

    @Event
    public void onShowTable( DashboardEvents.Show event ) {
        fireSelectTab();
    }

    @Event
    public void onClickSection( SectionEvents.Clicked event ) {
        if ( !TAB.equals( event.identity ) ) {
            return;
        }

        fireSelectTab();
        fireEvent(show);
    }

    private void fireSelectTab() {
        fireEvent( new ActionBarEvents.Clear() );
        if ( policyService.hasPrivilegeFor( En_Privilege.DASHBOARD_VIEW) ) {
            fireEvent( new MenuEvents.Select(TAB) );
        }
    }


    @Inject
    Lang lang;

    @Inject
    PolicyService policyService;

    private String TAB;
    private DashboardEvents.Show show = new DashboardEvents.Show();
}
