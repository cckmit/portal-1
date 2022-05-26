package ru.protei.portal.ui.plan.client.page;

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

public abstract class PlanPage implements Activity{

    @PostConstruct
    public void onInit() {
        ТAB = lang.plans();
    }

    @Event
    public void onAuthSuccess( AuthEvents.Success event ) {
        if ( event.profile.hasPrivilegeFor( En_Privilege.PLAN_VIEW ) ) {
            fireEvent( new MenuEvents.Add( ТAB, UiConstants.TabIcons.PLAN, ТAB,
                                           CrmConstants.PAGE_LINK.PLANS,
                                           DebugIds.SIDEBAR_MENU.PLAN ) );
            fireEvent( new AppEvents.InitPage(showPlans) );
        }
    }

    @Event
    public void onShowTable( PlanEvents.ShowPlans event ) {
        fireSelectTab();
    }

    @Event
    public void onShowPreviewFullScreen( PlanEvents.ShowFullScreen event ) {
        fireSelectTab();
    }

    @Event
    public void onShowDetail( PlanEvents.Edit event ) {
        fireSelectTab();
    }

    @Event
    public void onClickSection( SectionEvents.Clicked event ) {
        if ( !ТAB.equals( event.identity ) ) {
            return;
        }

        fireSelectTab();
        fireEvent(showPlans);
    }

    private void fireSelectTab() {
        fireEvent( new ActionBarEvents.Clear() );
        if ( policyService.hasPrivilegeFor( En_Privilege.PLAN_VIEW) ) {
            fireEvent(new MenuEvents.Select(ТAB));
        }
    }

    @Inject
    Lang lang;
    @Inject
    private PolicyService policyService;

    private String ТAB;
    private PlanEvents.ShowPlans showPlans = new PlanEvents.ShowPlans();
}
