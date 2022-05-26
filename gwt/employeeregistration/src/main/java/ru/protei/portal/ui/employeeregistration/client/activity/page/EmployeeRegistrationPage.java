package ru.protei.portal.ui.employeeregistration.client.activity.page;

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
import ru.protei.portal.ui.common.client.events.EmployeeRegistrationEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.winter.web.common.client.events.MenuEvents;
import ru.protei.winter.web.common.client.events.SectionEvents;

public abstract class EmployeeRegistrationPage
        implements Activity {

    @PostConstruct
    public void onInit() {
        CATEGORY = lang.employees();
        ТAB = lang.employeeRegistrations();
    }

    @Event
    public void onAuthSuccess( AuthEvents.Success event ) {
        if ( event.profile.hasPrivilegeFor( En_Privilege.EMPLOYEE_REGISTRATION_VIEW) ) {
            fireEvent( new MenuEvents.Add( ТAB, UiConstants.TabIcons.EMPLOYEE_REGISTRATION, ТAB,
                                           CrmConstants.PAGE_LINK.EMPLOYEE_REGISTRATION,
                                           DebugIds.SIDEBAR_MENU.EMPLOYEE_REGISTRATION).withParent( CATEGORY ) );
            fireEvent(new AppEvents.InitPage(show));
        }
    }

    @Event
    public void onShowTable( EmployeeRegistrationEvents.Show event ) {
        fireSelectTab();
    }

    @Event
    public void onShowDetail( EmployeeRegistrationEvents.Create event ) {
        fireSelectTab();
    }

    @Event
    public void onShowPreview(EmployeeRegistrationEvents.ShowFullScreen event) {
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
        if ( policyService.hasPrivilegeFor( En_Privilege.EMPLOYEE_REGISTRATION_VIEW) ) {
            fireEvent( new MenuEvents.Select( ТAB, CATEGORY ) );
        }
    }


    @Inject
    Lang lang;

    @Inject
    PolicyService policyService;

    private String CATEGORY;
    private String ТAB;
    private EmployeeRegistrationEvents.Show show = new EmployeeRegistrationEvents.Show(false);
}