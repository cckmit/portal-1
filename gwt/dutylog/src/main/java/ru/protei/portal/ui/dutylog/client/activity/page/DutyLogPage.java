package ru.protei.portal.ui.dutylog.client.activity.page;

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

/**
 * Активность по работе с вкладкой "Журнал дежурств"
 */
public abstract class DutyLogPage implements Activity {

    @PostConstruct
    public void onInit() {
        TAB = lang.dutyLog();
    }

    @Event
    public void onAuthSuccess( AuthEvents.Success event ) {
        if ( event.profile.hasPrivilegeFor( En_Privilege.DUTY_LOG_VIEW) ) {
            fireEvent( new MenuEvents.Add(TAB, UiConstants.TabIcons.DUTY_LOG, TAB,
                                          CrmConstants.PAGE_LINK.DUTY_LOG,
                                          DebugIds.SIDEBAR_MENU.DUTY_LOG ) );
            fireEvent( new AppEvents.InitPage(show) );
        }
    }

    @Event
    public void onShowTable( DutyLogEvents.Show event ) {
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
        if ( policyService.hasPrivilegeFor( En_Privilege.DUTY_LOG_VIEW ) ) {
            fireEvent( new MenuEvents.Select(TAB) );
        }
    }


    @Inject
    Lang lang;

    @Inject
    PolicyService policyService;

    private String TAB;
    private DutyLogEvents.Show show = new DutyLogEvents.Show();
}
