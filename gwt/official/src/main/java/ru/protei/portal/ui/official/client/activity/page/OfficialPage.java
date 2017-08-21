package ru.protei.portal.ui.official.client.activity.page;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.OfficialEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.winter.web.common.client.events.MenuEvents;

/**
 * Активность по работе с вкладкой "Матрица принятия решений"
 */
public abstract class OfficialPage
        implements Activity {

    @PostConstruct
    public void onInit() {
        ТAB = lang.matrixSolutions();
    }

    @Event
    public void onAuthSuccess( AuthEvents.Success event ) {
        if ( event.profile.hasPrivilegeFor( En_Privilege.OFFICIAL_VIEW ) ) {
            fireEvent( new MenuEvents.Add( ТAB, UiConstants.TabIcons.OFFICIAL ) );
            fireEvent( new AppEvents.InitPage( show ) );
        }
    }


    @Inject
    Lang lang;

    private String ТAB;
    private OfficialEvents.Show show = new OfficialEvents.Show();

}
