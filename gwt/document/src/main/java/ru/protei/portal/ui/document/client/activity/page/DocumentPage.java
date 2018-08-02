package ru.protei.portal.ui.document.client.activity.page;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.ActionBarEvents;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.DocumentEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.winter.web.common.client.events.MenuEvents;
import ru.protei.winter.web.common.client.events.SectionEvents;


public abstract class DocumentPage implements Activity {

    @PostConstruct
    public void onInit() {
        TAB = lang.document();
    }

    @Event
    public void onAuthSuccess(AuthEvents.Success event) {
        if (event.profile.hasPrivilegeFor(En_Privilege.DOCUMENT_VIEW)) {
            fireEvent(new MenuEvents.Add(TAB, UiConstants.TabIcons.DOCUMENT, DebugIds.SIDEBAR_MENU.DOCUMENT));
            fireEvent(new AppEvents.InitPage(show));
        }
    }

    @Event
    public void onShowTable(DocumentEvents.Show event) {
        fireSelectTab();
    }

    @Event
    public void onEdit(DocumentEvents.Edit event) {
        fireSelectTab();
    }

    @Event
    public void onClickSection(SectionEvents.Clicked event) {
        if (!TAB.equals(event.identity)) {
            return;
        }

        fireSelectTab();
        fireEvent(show);
    }

    private void fireSelectTab() {
        fireEvent(new ActionBarEvents.Clear());
        fireEvent(new MenuEvents.Select(TAB));
    }

    @Inject
    Lang lang;

    private String TAB;
    private DocumentEvents.Show show = new DocumentEvents.Show();
}
