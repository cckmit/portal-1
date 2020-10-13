package ru.protei.portal.ui.common.client.widget.mentioningtextarea;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

import static java.util.Optional.of;

public class PopupWithTextAreaKeyDownEventHandler {
    public PopupWithTextAreaKeyDownEventHandler(TextArea textArea, ComplexPanel childContainer) {
        childContainer.addDomHandler(event -> focusTextArea(textArea), MouseOverEvent.getType());
        textArea.addKeyDownHandler(getTextAreaKeyDownHandler(childContainer));
        childContainer.addDomHandler(getPopupKeyDownHandler(childContainer, textArea), KeyDownEvent.getType());
    }

    public void setDefaultKeyDownHandler(KeyDownHandler defaultKeyDownHandler) {
        this.defaultKeyDownHandler = defaultKeyDownHandler;
    }

    private KeyDownHandler getTextAreaKeyDownHandler(ComplexPanel childContainer) {
        return event -> {
            if (!childContainer.isVisible()) {
                initDefaultKeyDownHandler(defaultKeyDownHandler, event);
                return;
            }

            if (event.getNativeKeyCode() != KeyCodes.KEY_DOWN) {
                initDefaultKeyDownHandler(defaultKeyDownHandler, event);
                return;
            }

            if (childContainer.getWidgetCount() == 0) {
                initDefaultKeyDownHandler(defaultKeyDownHandler, event);
                return;
            }

            event.preventDefault();

            focusedWidget = null;

            focusWidget(getNext(childContainer, null));
        };
    }

    private KeyDownHandler getPopupKeyDownHandler(ComplexPanel childContainer, TextArea textArea) {
        return event -> {
            if (KeyCodes.KEY_ESCAPE == event.getNativeKeyCode()) {
                event.preventDefault();
                focusTextArea(textArea);
                return;
            }

            if (KeyCodes.KEY_UP == event.getNativeKeyCode()) {
                event.preventDefault();
                focusedWidget = getPrevious(childContainer, focusedWidget);

                if (focusedWidget == null) {
                    focusTextArea(textArea);
                    return;
                }

                focusWidget(focusedWidget);
                return;
            }

            if (KeyCodes.KEY_DOWN == event.getNativeKeyCode()) {
                event.preventDefault();
                focusedWidget = getNext(childContainer, focusedWidget);
                focusWidget(focusedWidget);
                return;
            }

            if (KeyCodes.KEY_ENTER != event.getNativeKeyCode()) {
                focusTextArea(textArea);
                initDefaultKeyDownHandler(defaultKeyDownHandler, event);
            }
        };
    }

    private Widget getNext(ComplexPanel childContainer, Widget focusedWidget) {
        if (focusedWidget == null) {
            return childContainer.getWidget(0);
        }

        return of(focusedWidget)
                .map(widget -> childContainer.getWidgetIndex(focusedWidget) + 1)
                .filter(index -> index < childContainer.getWidgetCount())
                .map(childContainer::getWidget)
                .orElse(null);
    }

    private Widget getPrevious(ComplexPanel childContainer, Widget focusedWidget) {
        if (focusedWidget == null) {
            return childContainer.getWidget(0);
        }

        return of(focusedWidget)
                .map(widget -> childContainer.getWidgetIndex(focusedWidget) - 1)
                .filter(index -> index >= 0)
                .map(childContainer::getWidget)
                .orElse(null);
    }

    private void focusTextArea(TextArea textArea) {
        focusedWidget = null;
        textArea.getElement().focus();
    }

    private void focusWidget(Widget item) {
        if (item == null) {
            return;
        }

        item.getElement().getFirstChildElement().focus();
        focusedWidget = item;
    }

    private void initDefaultKeyDownHandler(KeyDownHandler defaultKeyDownHandler, KeyDownEvent event) {
        if (defaultKeyDownHandler == null) {
            return;
        }

        defaultKeyDownHandler.onKeyDown(event);
    }

    private Widget focusedWidget;

    private KeyDownHandler defaultKeyDownHandler;
}
