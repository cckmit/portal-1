package ru.protei.portal.ui.issue.client.activity.page;

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
import ru.protei.portal.ui.common.client.events.IssueEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.winter.web.common.client.events.MenuEvents;
import ru.protei.winter.web.common.client.events.SectionEvents;

/**
 * Активность по работе с вкладкой "Обращения"
 */
public abstract class IssuePage
        implements Activity {

    @PostConstruct
    public void onInit() {
        ТAB = lang.issues();
    }

    @Event
    public void onAuthSuccess( AuthEvents.Success event ) {
        if ( event.profile.hasPrivilegeFor( En_Privilege.ISSUE_VIEW ) ) {
            fireEvent( new MenuEvents.Add( ТAB, UiConstants.TabIcons.ISSUE, ТAB,
                                           CrmConstants.PAGE_LINK.ISSUE,
                                           DebugIds.SIDEBAR_MENU.ISSUE ) );
            fireEvent( new AppEvents.InitPage( new IssueEvents.Show(false) ) );
        }
    }

    @Event
    public void onShowTable( IssueEvents.Show event ) {
        fireSelectTab();
    }

    @Event
    public void onEdit( IssueEvents.Edit event ) {
        fireSelectTab();
    }

    @Event
    public void onShowPreview(IssueEvents.ShowFullScreen event) {
        fireSelectTab();
    }

    @Event
    public void onClickSection( SectionEvents.Clicked event ) {
        if ( !ТAB.equals( event.identity ) ) {
            return;
        }

        fireSelectTab();
        fireEvent( new IssueEvents.Show(false) );
    }

    private void fireSelectTab() {
        fireEvent( new ActionBarEvents.Clear() );
        if (policyService.hasPrivilegeFor(En_Privilege.ISSUE_VIEW)) {
            fireEvent( new MenuEvents.Select( ТAB ) );
        }
    }

    @Inject
    Lang lang;

    @Inject
    PolicyService policyService;

    private String ТAB;
}

