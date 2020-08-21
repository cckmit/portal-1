package ru.protei.portal.ui.common.client.widget.composite;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RootPanel;

import java.util.ArrayList;
import java.util.List;

public class PopupLikeComposite extends Composite {
    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);

        if (visible) {
            addClickHandler();
        } else {
            removeClickHandler();
        }
    }

    protected void addClickableContainer(Element container) {
        containers.add(container);
    }

    private void addClickHandler() {
        Scheduler.get().scheduleDeferred( (Command) () -> handlerRegistration = RootPanel.get().addDomHandler(this::onElementClicked, ClickEvent.getType()));
    }

    private void removeClickHandler() {
        if (handlerRegistration != null) {
            handlerRegistration.removeHandler();
        }
    }

    private void onElementClicked(ClickEvent event) {
        String clickedElement = Element.as(event.getNativeEvent().getEventTarget()).getString();

        if (!getElement().getString().contains(clickedElement) && containers.stream().noneMatch(container -> container.getString().contains(clickedElement))) {
            setVisible(false);
        }
    }

    private HandlerRegistration handlerRegistration;
    private List<Element> containers = new ArrayList<>();
}
