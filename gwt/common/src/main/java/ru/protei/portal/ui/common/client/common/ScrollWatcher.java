package ru.protei.portal.ui.common.client.common;

import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import ru.protei.portal.core.model.helper.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class ScrollWatcher {

    public ScrollWatcher(Runnable onScrollFunction) {
        this.watchForWindow = true;
        this.onScrollFunction = onScrollFunction;
        this.isWatchingForScroll = false;
    }

    public ScrollWatcher(boolean watchForWindow, Runnable onScrollFunction) {
        this.watchForWindow = watchForWindow;
        this.onScrollFunction = onScrollFunction;
        this.isWatchingForScroll = false;
    }

    public void startWatchForScroll() {
        isWatchingForScroll = true;
        startWatchForWindow();
        for (Widget widget : CollectionUtils.emptyIfNull(widgetsForScrollRegistration)) {
            startWatchForScroll(widget);
        }
    }

    public void stopWatchForScroll() {
        isWatchingForScroll = false;
        stopWatchForWindow();
        for (Widget widget : CollectionUtils.emptyIfNull(widgetsForScrollRegistration)) {
            widget.unsinkEvents(Event.ONSCROLL);
        }
    }

    public void watchForScrollOf(Widget widget) {
        if (widgetsForScrollRegistration == null) {
            widgetsForScrollRegistration = new ArrayList<>();
        }
        widgetsForScrollRegistration.add(widget);
        startWatchForScroll(widget);
    }

    public void stopWatchForScrollOf(Widget widget) {
        if (widgetsForScrollRegistration != null) {
            widgetsForScrollRegistration.remove(widget);
        }
        stopWatchForScroll(widget);
    }

    private void startWatchForWindow() {
        if (!watchForWindow) return;
        if (!isWatchingForScroll) return;
        windowScrollRegistration = Window.addWindowScrollHandler(event -> onScrollFunction.run());
    }

    private void stopWatchForWindow() {
        if (!watchForWindow) return;
        windowScrollRegistration.removeHandler();
    }

    private void startWatchForScroll(Widget widget) {
        if (!isWatchingForScroll) return;
        widget.sinkEvents(Event.ONSCROLL);
        widget.addHandler(event -> onScrollFunction.run(), ScrollEvent.getType());
    }

    private void stopWatchForScroll(Widget widget) {
        widget.unsinkEvents(Event.ONSCROLL);
    }

    private final Runnable onScrollFunction;
    private final boolean watchForWindow;
    private boolean isWatchingForScroll;
    private HandlerRegistration windowScrollRegistration;
    private List<Widget> widgetsForScrollRegistration;
}
