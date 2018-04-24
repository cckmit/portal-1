package ru.protei.portal.ui.documenttype.client.activity.page;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.ActionBarEvents;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.DocumentTypeEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.winter.web.common.client.events.MenuEvents;
import ru.protei.winter.web.common.client.events.SectionEvents;

public abstract class DocumentTypePage implements Activity {

    @PostConstruct
    public void onInit() {
        TAB = lang.documentType();
    }

    @Event
    public void onAuthSuccess(AuthEvents.Success event) {
        if (event.profile.hasPrivilegeFor(En_Privilege.DOCUMENT_TYPE_VIEW)) {
            fireEvent(new MenuEvents.Add(TAB, UiConstants.TabIcons.DOCUMENT_TYPE));
            fireEvent(new AppEvents.InitPage(show));
        }
    }

    @Event
    public void onShowTable(DocumentTypeEvents.Show event) {
        fireSelectTab();
    }

    @Event
    public void onEdit(DocumentTypeEvents.Edit event) {
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
    private DocumentTypeEvents.Show show = new DocumentTypeEvents.Show();
}
