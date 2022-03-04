package ru.protei.portal.ui.web.client.page;

import com.google.gwt.core.client.GWT;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.ActionBarEvents;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.Test1Events;
import ru.protei.winter.web.common.client.events.MenuEvents;
import ru.protei.winter.web.common.client.events.SectionEvents;

public abstract class Test1Page implements Activity {

    @PostConstruct
    public void onInit() {
        TAB = "Test 1";
    }

    @Event
    public void onAuthSuccess(AuthEvents.Success event) {
        fireEvent(new MenuEvents.Add(TAB, UiConstants.TabIcons.TEST_ZONE, TAB));
        fireEvent(new AppEvents.InitPage(show));
    }

    @Event
    public void onShow(Test1Events.Show event) {
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

    private String TAB;
    private Test1Events.Show show = new Test1Events.Show();
}
