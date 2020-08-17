package ru.protei.portal.ui.employee.client.activity.page;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.winter.web.common.client.events.MenuEvents;

/**
 * Активность по работе с вкладкой "Сотрудники"
 */
public abstract class EmployeePage implements Activity {

    @PostConstruct
    public void onInit() {
        CATEGORY = lang.employees();
    }

    @Event
    public void onAuthSuccess( AuthEvents.Success event ) {
        if ( event.profile.hasPrivilegeFor( En_Privilege.EMPLOYEE_VIEW ) ) {
            fireEvent( new MenuEvents.Add( CATEGORY, UiConstants.TabIcons.EMPLOYEE, CATEGORY, DebugIds.SIDEBAR_MENU.EMPLOYEE ) );
        }
    }

    @Inject
    Lang lang;

    private String CATEGORY;
}