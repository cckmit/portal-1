package ru.protei.portal.ui.decision.client.activity.page;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.DecisionEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.winter.web.common.client.events.MenuEvents;

/**
 * Активность по работе с вкладкой "Матрица принятия решений"
 */
public abstract class DecisionPage
        implements Activity {

    @PostConstruct
    public void onInit() {
        ТAB = lang.matrixSolutions();
    }

    @Event
    public void onAuthSuccess( AuthEvents.Success event ) {
        if ( event.profile.hasPrivilegeFor( En_Privilege.DECISION_VIEW) ) {
            fireEvent( new MenuEvents.Add( ТAB, UiConstants.TabIcons.DECISION) );
            fireEvent( new AppEvents.InitPage( show ) );
        }
    }


    @Inject
    Lang lang;

    private String ТAB;
    private DecisionEvents.Show show = new DecisionEvents.Show();

}
