package ru.protei.portal.ui.company.client.activity.page;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.CompanyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.winter.web.common.client.events.MenuEvents;
import ru.protei.winter.web.common.client.events.SectionEvents;

/**
 * Активность по работе с вкладкой "Компании"
 */
public abstract class CompanyPage
        implements Activity {

    @PostConstruct
    public void onInit() {
        ТAB = lang.companies();
        fireEvent( new MenuEvents.Add( ТAB, UiConstants.TabIcons.COMPANY ) );
    }

    @Event
    public void onShowTable( CompanyEvents.Show event ) {
        fireSelectTab();
    }

    @Event
    public void onShowDetail( CompanyEvents.Edit event ) {
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
        fireEvent( new MenuEvents.Select( ТAB ) );
    }

    @Inject
    Lang lang;

    private String ТAB;
    private CompanyEvents.Show show = new CompanyEvents.Show();
}

