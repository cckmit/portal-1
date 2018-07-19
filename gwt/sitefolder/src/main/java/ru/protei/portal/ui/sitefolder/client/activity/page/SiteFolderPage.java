package ru.protei.portal.ui.sitefolder.client.activity.page;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.winter.web.common.client.events.MenuEvents;
import ru.protei.winter.web.common.client.events.SectionEvents;
import ru.protei.winter.web.common.client.struct.SubSection;

import java.util.ArrayList;
import java.util.List;

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

            List<SubSection> subSections = new ArrayList<>();
            subSections.add(new SubSection(SUB_TAB_PLATFORMS, UiConstants.TabIcons.SUB_ITEM));
            subSections.add(new SubSection(SUB_TAB_SERVERS, UiConstants.TabIcons.SUB_ITEM));
            subSections.add(new SubSection(SUB_TAB_APPS, UiConstants.TabIcons.SUB_ITEM));

            fireEvent(new MenuEvents.Add(TAB, UiConstants.TabIcons.SITE_FOLDER, subSections));
            fireEvent(new AppEvents.InitPage(new SiteFolderPlatformEvents.Show()));
        }
    }

    @Event
    public void onShowTable(SiteFolderPlatformEvents.Show event) {
        fireSelectTab();
    }

    @Event
    public void onClickSection(SectionEvents.Clicked event) {
        if (!TAB.equals(event.identity) && !SUB_TAB_PLATFORMS.equals(event.identity) &&
            !SUB_TAB_SERVERS.equals(event.identity) && !SUB_TAB_APPS.equals(event.identity)) {
            return;
        }

        fireSelectTab();

        if (TAB.equals(event.identity) || SUB_TAB_PLATFORMS.equals(event.identity)) {
            fireEvent(new SiteFolderPlatformEvents.Show());
        } else if (SUB_TAB_SERVERS.equals(event.identity)) {
            fireEvent(new SiteFolderServerEvents.Show());
        } else if (SUB_TAB_APPS.equals(event.identity)) {
            fireEvent(new SiteFolderAppEvents.Show());
        }
    }

    private void fireSelectTab() {
        fireEvent(new ActionBarEvents.Clear());
        fireEvent(new MenuEvents.Select(TAB));
    }

    @Inject
    Lang lang;

    private String TAB;
    private String SUB_TAB_PLATFORMS;
    private String SUB_TAB_SERVERS;
    private String SUB_TAB_APPS;
}
