package ru.protei.portal.ui.account.client.activity.page;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.ui.common.client.common.PolicyUtils;
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
        if ( PolicyUtils.isAllowedContactTab( event.profile ) ) {
            fireEvent( new MenuEvents.Add( ТAB, UiConstants.TabIcons.ACCOUNT ) );
            fireEvent( new AppEvents.InitPage( show ) );
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
        fireEvent( show );
    }

    private void fireSelectTab() {
        fireEvent( new ActionBarEvents.Clear() );
        fireEvent( new MenuEvents.Select( ТAB ) );
    }

    @Inject
    Lang lang;

    private String ТAB;
    private AccountEvents.Show show = new AccountEvents.Show();
}
