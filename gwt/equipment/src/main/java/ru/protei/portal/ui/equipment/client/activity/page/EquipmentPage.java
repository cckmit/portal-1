package ru.protei.portal.ui.equipment.client.activity.page;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.ActionBarEvents;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.EquipmentEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.shared.model.Profile;
import ru.protei.winter.web.common.client.events.MenuEvents;
import ru.protei.winter.web.common.client.events.SectionEvents;

/**
 * Активность по работе с вкладкой "Классификатор оборудования"
 */
public abstract class EquipmentPage
        implements Activity {

    @PostConstruct
    public void onInit() {
        ТAB = lang.classifier();
    }

    @Event
    public void onAuthSuccess( AuthEvents.Success event ) {
        this.profile = event.profile;

        if ( profile.hasPrivilegeFor( En_Privilege.EQUIPMENT_VIEW ) ) {
            fireEvent( new MenuEvents.Add( ТAB, UiConstants.TabIcons.EQUIPMENT, DebugIds.SIDEBAR_MENU.EQUIPMENT ) );
            fireEvent( new AppEvents.InitPage( new EquipmentEvents.Show( true ) ) );
        }
    }

    @Event
    public void onShowTable( EquipmentEvents.Show event ) {
        fireSelectTab();
    }

    @Event
    public void onShowPreviewFullScreen( EquipmentEvents.ShowFullScreen event ) {
        fireSelectTab();
    }

    @Event
    public void onShowDetail( EquipmentEvents.Edit event ) {
        fireSelectTab();
    }

    @Event
    public void onClickSection( SectionEvents.Clicked event ) {
        if ( !ТAB.equals( event.identity ) ) {
            return;
        }

        fireSelectTab();
        fireEvent( new EquipmentEvents.Show( true ) );
    }

    private void fireSelectTab() {
        fireEvent( new ActionBarEvents.Clear() );
        if ( profile.hasPrivilegeFor( En_Privilege.EQUIPMENT_VIEW ) ) {
            fireEvent( new MenuEvents.Select( ТAB ) );
        }
    }


    @Inject
    Lang lang;

    private String ТAB;
    private Profile profile;
}

