package ru.protei.portal.ui.documentation.client.activity.page;

import com.google.gwt.core.client.GWT;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.ActionBarEvents;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.DocumentationEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.winter.web.common.client.events.MenuEvents;
import ru.protei.winter.web.common.client.events.SectionEvents;


public abstract class DocumentationPage implements Activity {

    @PostConstruct
    public void onInit() {
        TAB = lang.documentation();
    }

    @Event
    public void onAuthSuccess(AuthEvents.Success event) {
        fireEvent(new MenuEvents.Add(TAB, UiConstants.TabIcons.DOCUMENTATION));
        fireEvent(new AppEvents.InitPage(show));
    }

    @Event
    public void onShowTable(DocumentationEvents.Show event) {
        fireSelectTab();
    }

    @Event
    public void onEdit(DocumentationEvents.Edit event) {
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
    private DocumentationEvents.Show show = new DocumentationEvents.Show();
}
