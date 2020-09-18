package ru.protei.portal.ui.common.client.activity.page.archive;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.winter.web.common.client.events.MenuEvents;

import static ru.protei.portal.ui.common.client.activity.page.util.AccessUtil.canUseExternalLink;

public abstract class ArchivePage implements Activity {

    @PostConstruct
    public void onInit() {
        CATEGORY = lang.archive();
    }

    @Event
    public void onAuthSuccess(AuthEvents.Success event) {
        if (canUseExternalLink(event.profile)) {
            fireEvent(new MenuEvents.Add(CATEGORY, UiConstants.TabIcons.ARCHIVE, CATEGORY, DebugIds.SIDEBAR_MENU.ARCHIVE));
        }
    }

    @Inject
    Lang lang;

    private String CATEGORY;
}
