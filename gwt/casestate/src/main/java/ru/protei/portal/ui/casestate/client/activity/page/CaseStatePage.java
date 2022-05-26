package ru.protei.portal.ui.casestate.client.activity.page;

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
 * Активность по работе с вкладкой "Статусы обращений"
 */
public abstract class CaseStatePage
        implements Activity {

    @PostConstruct
    public void onInit() {
        ТAB = lang.caseStates();
    }

    @Event
    public void onAuthSuccess( AuthEvents.Success event ) {
        if ( event.profile.hasPrivilegeFor( En_Privilege.CASE_STATES_VIEW ) ) {
            fireEvent( new MenuEvents.Add( ТAB, UiConstants.TabIcons.CASE_STATE, ТAB,
                                           CrmConstants.PAGE_LINK.CASE_STATE,
                                           DebugIds.SIDEBAR_MENU.CASE_STATE ) );
            fireEvent( new AppEvents.InitPage( show ) );
        }
    }

    @Event
    public void onShowTable( CaseStateEvents.Show event ) {
        fireSelectTab();
    }

    @Event
    public void onClickSection( SectionEvents.Clicked event ) {
        if ( !ТAB.equals( event.identity ) ) {
            return;
        }

        fireSelectTab();
        fireEvent( show );
    }

    private void fireSelectTab() {
        fireEvent( new ActionBarEvents.Clear() );
        if ( policyService.hasPrivilegeFor( En_Privilege.CASE_STATES_VIEW ) ) {
            fireEvent( new MenuEvents.Select( ТAB ) );
        }
    }


    @Inject
    Lang lang;

    @Inject
    PolicyService policyService;

    private String ТAB;
    private CaseStateEvents.Show show = new CaseStateEvents.Show();
}

