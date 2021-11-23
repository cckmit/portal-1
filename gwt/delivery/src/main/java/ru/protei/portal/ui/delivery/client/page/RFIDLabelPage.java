package ru.protei.portal.ui.delivery.client.page;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.winter.web.common.client.events.MenuEvents;
import ru.protei.winter.web.common.client.events.SectionEvents;

public abstract class RFIDLabelPage
        implements Activity {

    @PostConstruct
    public void onInit() {
        CATEGORY = lang.newStoreAndDelivery();
        TAB = lang.RFIDLabels();
    }

    @Event
    public void onAuthSuccess( AuthEvents.Success event ) {
        fireEvent( new MenuEvents.Add( TAB, UiConstants.TabIcons.RFID_LABEL, TAB, DebugIds.SIDEBAR_MENU.RFID_LABEL).withParent(CATEGORY) );
        fireEvent( new AppEvents.InitPage( show ) );
    }

    @Event
    public void onShowTable( RFIDLabelEvents.Show event ) {
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
        fireEvent( new MenuEvents.Select( TAB, CATEGORY) );
    }


    @Inject
    Lang lang;

    private String CATEGORY;
    private String TAB;
    private RFIDLabelEvents.Show show = new RFIDLabelEvents.Show();
}