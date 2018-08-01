package ru.protei.portal.ui.sitefolder.client.activity.page;

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
            fireEvent(new MenuEvents.Add(TAB, UiConstants.TabIcons.SITE_FOLDER, DebugIds.SIDEBAR_MENU.SITE_FOLDER));
            fireEvent(new MenuEvents.Add(SUB_TAB_PLATFORMS, null, DebugIds.SIDEBAR_MENU.SITE_FOLDER_PLATFORMS).withParent(TAB));
            fireEvent(new MenuEvents.Add(SUB_TAB_SERVERS, null, DebugIds.SIDEBAR_MENU.SITE_FOLDER_SERVERS).withParent(TAB));
            fireEvent(new MenuEvents.Add(SUB_TAB_APPS, null, DebugIds.SIDEBAR_MENU.SITE_FOLDER_APPS).withParent(TAB));
            fireEvent(new AppEvents.InitPage(new SiteFolderPlatformEvents.Show()));
        }
    }

    @Event
    public void onShowTable(SiteFolderPlatformEvents.Show event) {
        fireSelectTab(SUB_TAB_PLATFORMS);
    }

    @Event
    public void onShowTable(SiteFolderServerEvents.Show event) {
        fireSelectTab(SUB_TAB_SERVERS);
    }

    @Event
    public void onShowTable(SiteFolderAppEvents.Show event) {
        fireSelectTab(SUB_TAB_APPS);
    }

    @Event
    public void onClickSection(SectionEvents.Clicked event) {
        if (TAB.equals(event.identity)) {
            fireSelectTab(null);
        } else if (SUB_TAB_PLATFORMS.equals(event.identity)) {
            fireEvent(new SiteFolderPlatformEvents.Show());
        } else if (SUB_TAB_SERVERS.equals(event.identity)) {
            fireEvent(new SiteFolderServerEvents.Show());
        } else if (SUB_TAB_APPS.equals(event.identity)) {
            fireEvent(new SiteFolderAppEvents.Show());
        }
    }

    private void fireSelectTab(String sub) {
        fireEvent(new ActionBarEvents.Clear());
        fireEvent(sub == null ? new MenuEvents.Select(TAB) : new MenuEvents.Select(sub, TAB));
    }

    @Inject
    Lang lang;

    private String TAB;
    private String SUB_TAB_PLATFORMS;
    private String SUB_TAB_SERVERS;
    private String SUB_TAB_APPS;
}
