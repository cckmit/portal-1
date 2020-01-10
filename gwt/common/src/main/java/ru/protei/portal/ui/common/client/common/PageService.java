package ru.protei.portal.ui.common.client.common;

import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.ui.common.client.events.AppEvents;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс управления страницами приложения
 */
public abstract class PageService implements Activity {

    @Event
    public void onInitPage( AppEvents.InitPage event ) {
        pageEvents.add( event.event );
    }

    @Event
    public void onLogout( AppEvents.Logout event ) {
        pageEvents.clear();
    }

    public Object getFirstAvailablePageEvent() {
        return pageEvents == null || pageEvents.isEmpty() ? defaultPageEvent : pageEvents.get( 0 );
    }

    private List<Object> pageEvents = new ArrayList<>();
    private Object defaultPageEvent = new AppEvents.ShowProfile();
}
