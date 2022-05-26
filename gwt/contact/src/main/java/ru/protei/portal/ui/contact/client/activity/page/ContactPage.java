package ru.protei.portal.ui.contact.client.activity.page;

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
            fireEvent( new MenuEvents.Add( ТAB, UiConstants.TabIcons.CONTACT, ТAB,
                                           CrmConstants.PAGE_LINK.CONTACT,
                                           DebugIds.SIDEBAR_MENU.CONTACT ) );
            fireEvent( new AppEvents.InitPage( new ContactEvents.Show( false ) ) );
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
        fireEvent( new ContactEvents.Show( false ) );
    }

    private void fireSelectTab() {
        fireEvent( new ActionBarEvents.Clear() );
        if (policyService.hasPrivilegeFor(En_Privilege.CONTACT_VIEW)) {
            fireEvent( new MenuEvents.Select( ТAB ) );
        }
    }

    @Inject
    PolicyService policyService;

    @Inject
    Lang lang;

    private String ТAB;
}

