package ru.protei.portal.ui.contact.client.activity.page;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.ui.common.client.common.PolicyUtils;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.ActionBarEvents;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.ContactEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.winter.web.common.client.events.MenuEvents;
import ru.protei.winter.web.common.client.events.SectionEvents;

/**
 * Активность по работе с вкладкой "Контактные лица"
 */
public abstract class ContactPage
        implements Activity {

    @PostConstruct
    public void onInit() {
        ТAB = lang.contacts();
    }

    @Event
    public void onAuthSuccess( AuthEvents.Success event ) {
        if ( event.profile.hasPrivilegeFor( En_Privilege.CONTACT_VIEW ) ) {
            fireEvent( new MenuEvents.Add( ТAB, UiConstants.TabIcons.CONTACT ) );
            fireEvent( new AppEvents.InitPage( show ) );
        }
    }

    @Event
    public void onShowTable( ContactEvents.Show event ) {
        fireSelectTab();
    }

    @Event
    public void onShowDetail( ContactEvents.Edit event ) {
        fireSelectTab();
    }

    @Event
    public void onShowPreview( ContactEvents.ShowFullScreen event ) {
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
    private ContactEvents.Show show = new ContactEvents.Show();
}

