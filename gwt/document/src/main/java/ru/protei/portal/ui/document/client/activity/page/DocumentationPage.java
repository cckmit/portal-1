package ru.protei.portal.ui.document.client.activity.page;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.winter.web.common.client.events.MenuEvents;

public abstract class DocumentationPage implements Activity {

    @PostConstruct
    public void onInit() {
        CATEGORY = lang.documentation();
    }

    @Event
    public void onAuthSuccess( AuthEvents.Success event ) {
        if ( policyService.hasAnyPrivilegeOf( En_Privilege.DOCUMENT_VIEW, En_Privilege.DOCUMENT_TYPE_VIEW, En_Privilege.EQUIPMENT_VIEW ) ) {
            fireEvent( new MenuEvents.Add( CATEGORY, UiConstants.TabIcons.DOCUMENTATION, CATEGORY, DebugIds.SIDEBAR_MENU.DOCUMENTATION ) );
        }
    }

    @Inject
    Lang lang;
    @Inject
    private PolicyService policyService;

    private String CATEGORY;
}
