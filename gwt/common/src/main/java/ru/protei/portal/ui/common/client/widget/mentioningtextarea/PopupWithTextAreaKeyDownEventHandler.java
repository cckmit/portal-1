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
    public PopupWithTextAreaKeyDownEventHandler(final TextArea textArea, ComplexPanel childContainer) {
        childContainer.addDomHandler(event -> focusTextArea(textArea), MouseOverEvent.getType());
        textArea.addKeyDownHandler(createTextAreaKeyDownHandler(childContainer));
        childContainer.addDomHandler(createPopupKeyDownHandler(childContainer, textArea), KeyDownEvent.getType());
    }

    public void setDefaultKeyDownHandler(KeyDownHandler defaultKeyDownHandler) {
        this.defaultKeyDownHandler = defaultKeyDownHandler;
    }

    private KeyDownHandler createTextAreaKeyDownHandler(final ComplexPanel childContainer) {
        return event -> {
            if (event.getNativeKeyCode() != KeyCodes.KEY_DOWN) {
                initDefaultKeyDownHandler(defaultKeyDownHandler, event);
                return;
            }

            if (childContainer.getWidgetCount() == 0) {
                initDefaultKeyDownHandler(defaultKeyDownHandler, event);
                return;
            }

            event.preventDefault();

            focusWidget(getNext(childContainer, null));
        };
    }

    private KeyDownHandler createPopupKeyDownHandler(final ComplexPanel childContainer, final TextArea textArea) {
        return event -> {
            if (KeyCodes.KEY_ESCAPE == event.getNativeKeyCode()) {
                event.preventDefault();
                focusTextArea(textArea);
                return;
            }

            if (KeyCodes.KEY_UP == event.getNativeKeyCode()) {
                event.preventDefault();

                if (focusWidget(getPrevious(childContainer, currentWidget)) == null) {
                    focusTextArea(textArea);
                }

                return;
            }

            if (KeyCodes.KEY_DOWN == event.getNativeKeyCode()) {
                event.preventDefault();
                focusWidget(getNext(childContainer, currentWidget));
                return;
            }

            if (KeyCodes.KEY_ENTER != event.getNativeKeyCode()) {
                focusTextArea(textArea);
                initDefaultKeyDownHandler(defaultKeyDownHandler, event);
            }
        };
    }

    private Widget getNext(final ComplexPanel childContainer, final Widget currentWidget) {
        if (currentWidget == null) {
            return childContainer.getWidget(0);
        }

        return of(currentWidget)
                .map(widget -> childContainer.getWidgetIndex(currentWidget) + 1)
                .filter(index -> index < childContainer.getWidgetCount())
                .map(childContainer::getWidget)
                .orElse(null);
    }

    private Widget getPrevious(final ComplexPanel childContainer, final Widget currentWidget) {
        if (currentWidget == null) {
            return childContainer.getWidget(0);
        }

        return of(currentWidget)
                .map(widget -> childContainer.getWidgetIndex(currentWidget) - 1)
                .filter(index -> index >= 0)
                .map(childContainer::getWidget)
                .orElse(null);
    }

    private void focusTextArea(TextArea textArea) {
        textArea.getElement().focus();
    }

    private Widget focusWidget(Widget widget) {
        currentWidget = widget;

        if (currentWidget == null) {
            return null;
        }

        currentWidget.getElement().getFirstChildElement().focus();

        return currentWidget;
    }

    private void initDefaultKeyDownHandler(KeyDownHandler defaultKeyDownHandler, KeyDownEvent event) {
        if (defaultKeyDownHandler == null) {
            return;
        }

        defaultKeyDownHandler.onKeyDown(event);
    }

    private Widget currentWidget;

    private KeyDownHandler defaultKeyDownHandler;
}
