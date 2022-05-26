package ru.protei.portal.ui.account.client.activity.page;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.AccountEvents;
import ru.protei.portal.ui.common.client.events.ActionBarEvents;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.winter.web.common.client.events.MenuEvents;
import ru.protei.winter.web.common.client.events.SectionEvents;

/**
 * Активность по работе с вкладкой "Контактные лица"
 */
public abstract class AccountPage implements Activity {
    @PostConstruct
    public void onInit() {
        ТAB = lang.accounts();
    }

    @Event
    public void onAuthSuccess( AuthEvents.Success event ) {
        if ( event.profile.hasPrivilegeFor( En_Privilege.ACCOUNT_VIEW ) ) {
            fireEvent( new MenuEvents.Add( ТAB, UiConstants.TabIcons.ACCOUNT, ТAB,
                                           CrmConstants.PAGE_LINK.ACCOUNT,
                                           DebugIds.SIDEBAR_MENU.ACCOUNT ) );
            fireEvent( new AppEvents.InitPage( new AccountEvents.Show(true) ) );
        }
    }

    @Event
    public void onShowTable( AccountEvents.Show event ) {
        fireSelectTab();
    }

    @Event
    public void onShowDetail( AccountEvents.Edit event ) {
        fireSelectTab();
    }

    @Event
    public void onClickSection( SectionEvents.Clicked event ) {
        if ( !ТAB.equals( event.identity ) ) {
            return;
        }

        fireSelectTab();
        fireEvent( new AccountEvents.Show(true) );
    }

    private void fireSelectTab() {
        fireEvent( new ActionBarEvents.Clear() );
        if ( policyService.hasPrivilegeFor( En_Privilege.ACCOUNT_VIEW ) ) {
            fireEvent( new MenuEvents.Select( ТAB ) );
        }
    }


    @Inject
    Lang lang;

    @Inject
    PolicyService policyService;

    private String ТAB;
}
