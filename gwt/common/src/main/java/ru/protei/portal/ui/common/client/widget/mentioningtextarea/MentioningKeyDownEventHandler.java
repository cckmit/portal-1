package ru.protei.portal.ui.common.client.widget.mentioningtextarea;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.ui.common.client.widget.selector.login.UserLoginSelector;

import java.util.*;
import java.util.function.Supplier;

import static ru.protei.portal.core.model.helper.CollectionUtils.isEmpty;

class MentioningKeyDownEventHandler {
    KeyDownHandler getTextAreaKeyDownHandler(UserLoginSelector userLoginSelector, Timer changeTimer, Supplier<Iterator<Widget>> iteratorSupplier) {
        return event -> {
            if (!userLoginSelector.isPopupVisible()) {
                changeTimer.schedule(200);
                return;
            }

            if (event.getNativeKeyCode() != KeyCodes.KEY_DOWN) {
                changeTimer.schedule(200);
                return;
            }

            fillWidgets(iteratorSupplier.get(), widgets, widgetToIndex);

            if (isEmpty(widgets)) {
                return;
            }

            event.preventDefault();
            selectWidget(getNext(widgets));
        };
    }

    KeyDownHandler getPopupKeyDownHandler(TextArea textArea, Supplier<Iterator<Widget>> iteratorSupplier) {
        return event -> {
            if (KeyCodes.KEY_ESCAPE == event.getNativeKeyCode()) {
                event.preventDefault();
                focusTextArea(textArea);
                return;
            }

            if (KeyCodes.KEY_UP == event.getNativeKeyCode()) {
                onKeyUpClicked(event, widgets, textArea);
                return;
            }

            if (KeyCodes.KEY_DOWN == event.getNativeKeyCode()) {
                onKeyDownClicked(event, widgets, widgetToIndex, iteratorSupplier);
                return;
            }
        };
    }

    private void onKeyUpClicked(KeyDownEvent event, List<Widget> widgets, TextArea textArea) {
        event.preventDefault();
        Widget previous = getPrevious(widgets);

        if (previous == null) {
            focusTextArea(textArea);
            return;
        }

        selectWidget(previous);
    }

    private void onKeyDownClicked(KeyDownEvent event, List<Widget> widgets, Map<Widget, Integer> widgetToInteger, Supplier<Iterator<Widget>> iteratorSupplier) {
        event.preventDefault();
        Widget next = getNext(widgets);

        if (next == null) {
            fillWidgets(iteratorSupplier.get(), widgets, widgetToInteger);
        }

        selectWidget(getNext(widgets));
    }

    private Widget getNext(List<Widget> widgets) {
        if (isEmpty(widgets)) {
            return null;
        }

        Integer nextIndex = Optional
                .ofNullable(selectedWidget)
                .map(widgetToIndex::get)
                .map(currentIndex -> currentIndex + 1)
                .orElse(null);

        if (nextIndex == null) {
            return CollectionUtils.getFirst(widgets);
        }

        if (nextIndex >= widgets.size()) {
            return null;
        }

        return widgets.get(nextIndex);
    }

    private Widget getPrevious(List<Widget> widgets) {
        if (isEmpty(widgets)) {
            return null;
        }

        Integer previousIndex = Optional
                .ofNullable(selectedWidget)
                .map(widgetToIndex::get)
                .map(currentIndex -> currentIndex - 1)
                .orElse(null);

        if (previousIndex == null) {
            return CollectionUtils.getFirst(widgets);
        }

        if (previousIndex < 0) {
            return null;
        }

        return widgets.get(previousIndex);
    }

    private void selectWidget(Widget widget) {
        if (widget == null) {
            return;
        }

        selectedWidget = widget;
        widget.getElement().getFirstChildElement().focus();
    }

    private void fillWidgets(Iterator<Widget> widgetIterator, List<Widget> widgets, Map<Widget, Integer> widgetToInteger) {
        if (widgetIterator == null) {
            return;
        }

        widgets.clear();
        widgetToInteger.clear();

        int index = 0;

        while (widgetIterator.hasNext()) {
            Widget next = widgetIterator.next();
            widgetToInteger.put(next, index++);
            next.addDomHandler(event -> resetLastSelectedWidget(), MouseOverEvent.getType());
            widgets.add(next);
        }
    }

    private void focusTextArea(TextArea textArea) {
        selectedWidget = null;
        textArea.getElement().focus();
    }

    private void resetLastSelectedWidget() {
        if (selectedWidget == null) {
            return;
        }

        selectedWidget.getElement().getFirstChildElement().blur();
        selectedWidget = null;
    }

    private List<Widget> widgets = new ArrayList<>();
    private Map<Widget, Integer> widgetToIndex = new HashMap<>();
    private Widget selectedWidget;
}
