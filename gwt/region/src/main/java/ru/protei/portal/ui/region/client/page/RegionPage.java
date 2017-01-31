package ru.protei.portal.ui.region.client.page;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.ActionBarEvents;
import ru.protei.portal.ui.common.client.events.ProductEvents;
import ru.protei.portal.ui.common.client.events.RegionEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.winter.web.common.client.events.MenuEvents;
import ru.protei.winter.web.common.client.events.SectionEvents;

/**
 * Активность по работе с вкладкой "Регионы"
 */
public abstract class RegionPage
        implements Activity {

    @PostConstruct
    public void onInit() {
        ТAB = lang.regions();
        fireEvent( new MenuEvents.Add( ТAB, UiConstants.TabIcons.REGION ) );
    }

    @Event
    public void onShowTable( RegionEvents.Show event ) {
        fireSelectTab();
    }

    @Event
    public void onShowDetail( RegionEvents.Edit event ) {
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
    private RegionEvents.Show show = new RegionEvents.Show();
}

