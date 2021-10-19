package ru.protei.portal.ui.delivery.client.page;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.ActionBarEvents;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.CardBatchEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.winter.web.common.client.events.MenuEvents;
import ru.protei.winter.web.common.client.events.SectionEvents;

public abstract class CardBatchPage
        implements Activity {

    @PostConstruct
    public void onInit() {
        CATEGORY = lang.newStoreAndDelivery();
        TAB = lang.cardBatch();
    }

    @Event
    public void onAuthSuccess( AuthEvents.Success event ) {
        if ( event.profile.hasPrivilegeFor( En_Privilege.CARD_BATCH_VIEW) ) {
            fireEvent( new MenuEvents.Add( TAB, UiConstants.TabIcons.CARD_BATCH, TAB, DebugIds.SIDEBAR_MENU.CARD_BATCH).withParent(CATEGORY) );
            fireEvent( new AppEvents.InitPage( show ) );
        }
    }

    @Event
    public void onShowTable( CardBatchEvents.Show event ) {
        fireSelectTab();
    }

    @Event
    public void onShowDetail( CardBatchEvents.Create event ) {
        fireSelectTab();
    }

    @Event
    public void onClickSection( SectionEvents.Clicked event ) {
        if ( !TAB.equals( event.identity ) ) {
            return;
        }

        fireSelectTab();
        fireEvent( show );
    }

    private void fireSelectTab() {
        fireEvent( new ActionBarEvents.Clear() );
        if ( policyService.hasPrivilegeFor( En_Privilege.CARD_BATCH_VIEW) ) {
            fireEvent( new MenuEvents.Select( TAB, CATEGORY) );
        }
    }


    @Inject
    Lang lang;

    @Inject
    PolicyService policyService;

    private String CATEGORY;
    private String TAB;
    private CardBatchEvents.Show show = new CardBatchEvents.Show(false);
}

