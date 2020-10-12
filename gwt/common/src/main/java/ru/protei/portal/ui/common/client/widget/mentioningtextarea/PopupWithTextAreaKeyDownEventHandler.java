package ru.protei.portal.ui.common.client.widget.mentioningtextarea;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TextArea;

import java.util.function.Supplier;

class PopupWithTextAreaKeyDownEventHandler {
    PopupWithTextAreaKeyDownEventHandler(TextArea textArea, HTMLPanel childContainer) {
        this.popupElementsIterator = new PopupElementsIterator(childContainer::getElement);
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

            if (popupElementsIterator.isEmpty()) {
                return;
            }

            event.preventDefault();

            popupElementsIterator.reset();
            focusItem(popupElementsIterator.getNext());
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
                onKeyUpClicked(event, popupElementsIterator, textArea);
                return;
            }

            if (KeyCodes.KEY_DOWN == event.getNativeKeyCode()) {
                onKeyDownClicked(event, popupElementsIterator);
                return;
            }

            if (KeyCodes.KEY_ENTER != event.getNativeKeyCode()) {
                onChange.run();
                focusTextArea(textArea);
            }
        };
    }

    private void onKeyUpClicked(KeyDownEvent event, PopupElementsIterator popupElementsIterator, TextArea textArea) {
        event.preventDefault();
        Element previousItem = popupElementsIterator.getPrevious();

        if (previousItem == null) {
            focusTextArea(textArea);
            return;
        }

        focusItem(previousItem);
    }

    private void onKeyDownClicked(KeyDownEvent event, PopupElementsIterator popupElementsIterator) {
        event.preventDefault();
        Element nextItem = popupElementsIterator.getNext();

        if (nextItem == null) {
            return;
        }

        focusItem(nextItem);
    }

    private void focusTextArea(TextArea textArea) {
        textArea.getElement().focus();
    }

    private void focusItem(Element item) {
        if (item == null) {
            return;
        }

        item.getFirstChildElement().focus();
    }

    private static final class PopupElementsIterator {
        private final Supplier<Element> elementContainerSupplier;
        private int index = -1;

        PopupElementsIterator(Supplier<Element> elementContainerSupplier) {
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

        boolean isEmpty() {
            return elementContainerSupplier.get().getChildCount() == 0;
        }

        public void reset() {
            index = -1;
        }
    }

    private PopupElementsIterator popupElementsIterator;
}
