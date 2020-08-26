package ru.protei.portal.ui.common.client.widget.composite;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RootPanel;

import java.util.ArrayList;
import java.util.List;

public class PopupLikeComposite extends Composite implements HasCloseHandlers {
    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);

        if (visible) {
            addClickHandler(isAutoHide);
        } else {
            removeClickHandler(isAutoHide);
            CloseEvent.fire(this, null);
        }
    }

    @Override
    public HandlerRegistration addCloseHandler(CloseHandler handler) {
        return addHandler(handler, CloseEvent.getType());
    }

    protected void addClickableContainer(Element container) {
        clickableElements.add(container);
    }

    protected void setAutoHide(boolean isAutoHide) {
        this.isAutoHide = isAutoHide;
    }

    private void addClickHandler(boolean isAutoHide) {
        if (!isAutoHide) {
            return;
        }

        Scheduler.get().scheduleDeferred((Command) () -> handlerRegistration = RootPanel.get().addDomHandler(this::onElementClicked, ClickEvent.getType()));
    }

    private void removeClickHandler(boolean isAutoHide) {
        if (!isAutoHide) {
            return;
        }

        if (handlerRegistration != null) {
            handlerRegistration.removeHandler();
        }
    }

    private void onElementClicked(ClickEvent event) {
        String clickedElementString = Element.as(event.getNativeEvent().getEventTarget()).getString();

        if (keepPopupVisible(clickedElementString, clickableElements)) {
            return;
        }

        setVisible(false);
    }

    private boolean keepPopupVisible(String clickedElementString, List<Element> clickableElements) {
        if (getElement().getString().contains(clickedElementString)) {
            return true;
        }

        if (clickableElements.stream().anyMatch(container -> container.getString().contains(clickedElementString))) {
            return true;
        }

        return false;
    }

    private boolean isAutoHide;

    private HandlerRegistration handlerRegistration;
    private List<Element> clickableElements = new ArrayList<>();
}
