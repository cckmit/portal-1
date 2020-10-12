package ru.protei.portal.ui.common.client.widget.mentioningtextarea;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import org.jetbrains.annotations.NotNull;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.ui.common.client.widget.selector.login.UserLoginSelector;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static ru.protei.portal.core.model.helper.CollectionUtils.isEmpty;

class PopupWithTextAreaKeyDownEventHandler {
    PopupWithTextAreaKeyDownEventHandler(TextArea textArea, HTMLPanel childContainer) {
        this.popupIterator = new PopupIterator(childContainer::getElement);
        childContainer.addDomHandler(event -> focusTextArea(textArea), MouseOverEvent.getType());
    }

    KeyDownHandler getTextAreaKeyDownHandler(final Supplier<Boolean> isPopupVisible,
                                             final Runnable onChange) {

        return event -> {
            if (Boolean.FALSE.equals(isPopupVisible.get())) {
                onChange.run();
                return;
            }

            if (event.getNativeKeyCode() != KeyCodes.KEY_DOWN) {
                onChange.run();
                return;
            }

            if (popupIterator.getChildCount() == 0) {
                return;
            }

            event.preventDefault();

            popupIterator.reset();
            focusElement(popupIterator.getNext());
        };
    }

    KeyDownHandler getPopupKeyDownHandler(final TextArea textArea, final Runnable onChange) {
        return event -> {
            if (KeyCodes.KEY_ESCAPE == event.getNativeKeyCode()) {
                event.preventDefault();
                focusTextArea(textArea);
                return;
            }

            if (KeyCodes.KEY_UP == event.getNativeKeyCode()) {
                onKeyUpClicked(event, popupIterator, textArea);
                return;
            }

            if (KeyCodes.KEY_DOWN == event.getNativeKeyCode()) {
                onKeyDownClicked(event, popupIterator);
                return;
            }

            if (KeyCodes.KEY_ENTER != event.getNativeKeyCode()) {
                onChange.run();
                focusTextArea(textArea);
            }
        };
    }

    private void onKeyUpClicked(KeyDownEvent event, PopupIterator popupIterator, TextArea textArea) {
        event.preventDefault();
        Element previousUserLoginItem = popupIterator.getPrevious();

        if (previousUserLoginItem == null) {
            focusTextArea(textArea);
            return;
        }

        focusElement(previousUserLoginItem);
    }

    private void onKeyDownClicked(KeyDownEvent event, PopupIterator popupIterator) {
        event.preventDefault();
        Element nextElement = popupIterator.getNext();

        if (nextElement == null) {
            return;
        }

        focusElement(nextElement);
    }

    private void focusTextArea(TextArea textArea) {
        textArea.getElement().focus();
    }

    private void focusElement(Element element) {
        if (element == null) {
            return;
        }

        element.getFirstChildElement().focus();
    }

    private static final class PopupIterator {
        private final Supplier<Element> elementContainerSupplier;
        private int index = -1;

        PopupIterator(Supplier<Element> elementContainerSupplier) {
            this.elementContainerSupplier = elementContainerSupplier;
        }

        Element getNext() {
            Element container = elementContainerSupplier.get();

            if (index + 1 >= container.getChildCount()) {
                return null;
            }

            Node child = container.getChild(++index);

            return (Element) child;
        }

        Element getPrevious() {
            Element container = elementContainerSupplier.get();

            if (index - 1 < 0) {
                return null;
            }

            Node child = container.getChild(--index);

            return (Element) child;
        }

        int getChildCount() {
            return elementContainerSupplier.get().getChildCount();
        }

        public void reset() {
            index = -1;
        }
    }

    private PopupIterator popupIterator;
}
