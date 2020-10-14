package ru.protei.portal.ui.common.client.selector.popup.arrowselectable;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Widget;
import ru.protei.portal.ui.common.client.selector.popup.SelectorPopupWithSearch;

import static java.util.Optional.of;

public class ArrowSelectableSelectorPopup extends SelectorPopupWithSearch {
    public ArrowSelectableSelectorPopup(TextAreaHandler textAreaHandler) {
        childContainer.addStyleName("arrow-selectable");
        childContainer.addDomHandler(event -> textAreaHandler.focusTextArea(), MouseOverEvent.getType());
        childContainer.addDomHandler(createPopupKeyDownHandler(childContainer, textAreaHandler), KeyDownEvent.getType());
    }

    public void focus() {
        focusWidget(getNext(childContainer, null));
    }

    private KeyDownHandler createPopupKeyDownHandler(final ComplexPanel childContainer, TextAreaHandler textAreaHandler) {
        return event -> {
            if (KeyCodes.KEY_ESCAPE == event.getNativeKeyCode()) {
                event.preventDefault();
                textAreaHandler.focusTextArea();
                return;
            }

            if (KeyCodes.KEY_UP == event.getNativeKeyCode()) {
                event.preventDefault();

                if (focusWidget(getPrevious(childContainer, currentWidget)) == null) {
                    textAreaHandler.focusTextArea();
                }

                return;
            }

            if (KeyCodes.KEY_DOWN == event.getNativeKeyCode()) {
                event.preventDefault();
                focusWidget(getNext(childContainer, currentWidget));
                return;
            }

//            Если нажали не на Enter, а продолжили ввод, то переводим фокус обратно
            if (KeyCodes.KEY_ENTER != event.getNativeKeyCode()) {
                textAreaHandler.focusTextArea();
                textAreaHandler.onValueChanged();
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

    private Widget focusWidget(Widget widget) {
        currentWidget = widget;

        if (currentWidget == null) {
            return null;
        }

        currentWidget.getElement().getFirstChildElement().focus();

        return currentWidget;
    }

    private Widget currentWidget;
}
