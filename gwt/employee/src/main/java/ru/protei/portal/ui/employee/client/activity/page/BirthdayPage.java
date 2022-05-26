package ru.protei.portal.ui.employee.client.activity.page;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.winter.web.common.client.events.MenuEvents;
import ru.protei.winter.web.common.client.events.SectionEvents;

public abstract class BirthdayPage implements Activity {

    @PostConstruct
    public void onInit() {
        CATEGORY = lang.employees();
        TAB = lang.employeeBirthdays();
    }

    @Event
    public void onAuthSuccess( AuthEvents.Success event ) {
        if ( event.profile.hasPrivilegeFor( En_Privilege.EMPLOYEE_VIEW ) ) {
            fireEvent( new MenuEvents.Add(TAB, UiConstants.TabIcons.EMPLOYEE_BIRTHDAY, TAB,
                                          CrmConstants.PAGE_LINK.EMPLOYEE_BIRTHDAY,
                                          DebugIds.SIDEBAR_MENU.EMPLOYEE_BIRTHDAY ).withParent( CATEGORY ) );
            fireEvent( new AppEvents.InitPage( show ) );
        }
    }

    @Event
    public void onShowBirthdays( EmployeeEvents.ShowBirthdays event ) {
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
        if ( policyService.hasPrivilegeFor( En_Privilege.EMPLOYEE_VIEW ) ) {
            fireEvent( new MenuEvents.Select( TAB, CATEGORY ) );
        }
    }

    @Inject
    Lang lang;

    @Inject
    PolicyService policyService;

    private String CATEGORY;
    private String TAB;

    private EmployeeEvents.ShowBirthdays show = new EmployeeEvents.ShowBirthdays();
}
