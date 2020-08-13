package ru.protei.portal.ui.employee.client.activity.page;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.winter.web.common.client.events.MenuEvents;
import ru.protei.winter.web.common.client.events.SectionEvents;

/**
 * Активность по работе с вкладкой "Сотрудники"
 */
public abstract class EmployeePage implements Activity {

    @PostConstruct
    public void onInit() {
        CATEGORY = lang.employees();
        ТAB1 = lang.list();
        ТAB2 = lang.birthday();
    }

    @Event
    public void onAuthSuccess( AuthEvents.Success event ) {
        if ( event.profile.hasPrivilegeFor( En_Privilege.EMPLOYEE_VIEW ) ) {
            fireEvent( new MenuEvents.Add( CATEGORY, UiConstants.TabIcons.EMPLOYEE, CATEGORY, DebugIds.SIDEBAR_MENU.EMPLOYEE ) );
            fireEvent( new MenuEvents.Add(ТAB1, UiConstants.TabIcons.EMPLOYEE_LIST, ТAB1, DebugIds.SIDEBAR_MENU.EMPLOYEE_LIST ).withParent( CATEGORY ) );
            fireEvent( new MenuEvents.Add(ТAB2, UiConstants.TabIcons.EMPLOYEE_BIRTHDAY, ТAB1, DebugIds.SIDEBAR_MENU.EMPLOYEE_BIRTHDAY ).withParent( CATEGORY ) );
            fireEvent( new AppEvents.InitPage(show1) );
        }
    }

    @Event
    public void onShowTable( EmployeeEvents.Show event ) {
        fireSelectTab();
    }

    @Event
    public void onShowTopBrass( EmployeeEvents.ShowTopBrass event ) {
        fireSelectTab();
    }

    @Event
    public void onShowEdit( EmployeeEvents.Edit event ) {
        fireSelectTab();
    }

    @Event
    public void onShowPreview( EmployeeEvents.ShowFullScreen event ) {
        fireSelectTab();
    }

    @Event
    public void onClickSection( SectionEvents.Clicked event ) {
        if ( !ТAB1.equals( event.identity ) ) {
            return;
        }

        fireSelectTab();
        fireEvent(show1);
    }

    private void fireSelectTab() {
        fireEvent( new ActionBarEvents.Clear() );
        if ( policyService.hasPrivilegeFor( En_Privilege.EMPLOYEE_VIEW ) ) {
            fireEvent( new MenuEvents.Select(ТAB1, CATEGORY ) );
        }
    }

    @Inject
    Lang lang;

    @Inject
    PolicyService policyService;

    private String CATEGORY;
    private String ТAB1;
    private String ТAB2;

    private EmployeeEvents.Show show1 = new EmployeeEvents.Show(false);
}
