package ru.protei.portal.ui.sitefolder.client.activity.page;

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

/**
 * Активность по работе с вкладкой "Площадки заказчиков"
 */
public abstract class SiteFolderPage implements Activity {

    @PostConstruct
    public void onInit() {
        TAB = lang.siteFolder();
        SUB_TAB_PLATFORMS = lang.siteFolderPlatforms();
        SUB_TAB_SERVERS = lang.siteFolderServers();
        SUB_TAB_APPS = lang.siteFolderApps();
    }

    @Event
    public void onAuthSuccess(AuthEvents.Success event) {
        if (event.profile.hasPrivilegeFor(En_Privilege.SITE_FOLDER_VIEW)) {
            fireEvent(new MenuEvents.Add(TAB, UiConstants.TabIcons.SITE_FOLDER, TAB,
                                         CrmConstants.PAGE_LINK.SITE_FOLDER,
                                         DebugIds.SIDEBAR_MENU.SITE_FOLDER));
            fireEvent(new AppEvents.InitPage(new SiteFolderPlatformEvents.Show(false)));
        }
    }

    @Event
    public void onShowPreview(SiteFolderPlatformEvents.ShowFullScreen event) {
        fireSelectTab();
    }

    @Event
    public void onShowTable(SiteFolderPlatformEvents.Show event) {
        fireSelectTab();
    }

    @Event
    public void onShowTable(SiteFolderServerEvents.ShowSummaryTable event) {
        fireSelectTab();
    }

    @Event
    public void onShowTable(SiteFolderAppEvents.Show event) {
        fireSelectTab();
    }

    @Event
    public void onClickSection(SectionEvents.Clicked event) {
        if (!TAB.equals(event.identity)) {
            return;
        }
        fireEvent(new SiteFolderPlatformEvents.Show(false));
    }

    private void fireSelectTab() {
        fireEvent(new ActionBarEvents.Clear());
        if (policyService.hasPrivilegeFor(En_Privilege.SITE_FOLDER_VIEW)) {
            fireEvent(new MenuEvents.Select(TAB));
        }
    }

    @Inject
    Lang lang;

    @Inject
    PolicyService policyService;

    private String TAB;
    private String SUB_TAB_PLATFORMS;
    private String SUB_TAB_SERVERS;
    private String SUB_TAB_APPS;
}
