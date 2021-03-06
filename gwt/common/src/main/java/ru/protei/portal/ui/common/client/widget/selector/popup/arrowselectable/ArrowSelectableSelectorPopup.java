package ru.protei.portal.ui.common.client.widget.selector.popup.arrowselectable;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Widget;
import ru.protei.portal.ui.common.client.selector.popup.arrowselectable.ArrowSelectableSelectorHandler;
import ru.protei.portal.ui.common.client.widget.selector.popup.SelectorPopup;

import static java.util.Optional.of;

public class ArrowSelectableSelectorPopup extends SelectorPopup {
    public ArrowSelectableSelectorPopup() {
        childContainer.addDomHandler(event -> {
            if (search.isVisible()) {
                search.setFocus(true);
                currentWidget = null;
            } else {
                blurCurrentWidget();
                focusChildContainer();
            }
        }, MouseOverEvent.getType());

        DefaultArrowSelectableSelectorHandler defaultArrowSelectableSelectorHandler
                = new DefaultArrowSelectableSelectorHandler();

        childContainer.addDomHandler(event ->
                onKeyDown(event, childContainer, defaultArrowSelectableSelectorHandler), KeyDownEvent.getType());

        search.addDomHandler(event -> {
            if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
                event.preventDefault();
                hide();
                return;
            }

            if (event.getNativeKeyCode() == KeyCodes.KEY_TAB) {
                event.preventDefault();
                return;
            }

            if (event.getNativeKeyCode() == KeyCodes.KEY_DOWN
                    || event.getNativeKeyCode() == KeyCodes.KEY_DOWN) {
                event.preventDefault();
                focus();
            }
        }, KeyDownEvent.getType());
    }

    public void focus() {
        focusWidget(getNext(childContainer, null));
    }

    private void onKeyDown(KeyDownEvent event, ComplexPanel childContainer, ArrowSelectableSelectorHandler arrowSelectableSelectorHandler) {
        if (KeyCodes.KEY_ESCAPE == event.getNativeKeyCode()) {
            event.preventDefault();
            arrowSelectableSelectorHandler.escapeFromSelector();
            return;
        }

        childContainer.clear();

        if (KeyCodes.KEY_UP == event.getNativeKeyCode()) {
            event.preventDefault();

            if (focusWidget(getPrevious(childContainer, currentWidget)) == null) {
                arrowSelectableSelectorHandler.focusTextArea();
            }

            return;
        }

        if (KeyCodes.KEY_DOWN == event.getNativeKeyCode()) {
            event.preventDefault();
            focusWidget(getNext(childContainer, currentWidget));
            return;
        }

        if (KeyCodes.KEY_ENTER != event.getNativeKeyCode()) {
            arrowSelectableSelectorHandler.focusTextArea();
            arrowSelectableSelectorHandler.onValueChanged();
        }
    }

    private Widget getNext(final ComplexPanel childContainer, final Widget currentWidget) {
        if (currentWidget != null) {
            return of(currentWidget)
                    .map(widget -> childContainer.getWidgetIndex(currentWidget) + 1)
                    .filter(index -> index < childContainer.getWidgetCount())
                    .map(childContainer::getWidget)
                    .orElse(null);
        }

        if (childContainer.getWidgetCount() == 0) {
            return null;
        }

        return childContainer.getWidget(0);
    }

    private Widget getPrevious(final ComplexPanel childContainer, final Widget currentWidget) {
        if (currentWidget != null) {
            return of(currentWidget)
                    .map(widget -> childContainer.getWidgetIndex(currentWidget) - 1)
                    .filter(index -> index >= 0)
                    .map(childContainer::getWidget)
                    .orElse(null);
        }

        if (childContainer.getWidgetCount() == 0) {
            return null;
        }

        return childContainer.getWidget(0);
    }

    private Widget focusWidget(Widget widget) {
        currentWidget = widget;

        if (currentWidget == null) {
            return null;
        }

        currentWidget.getElement().setTabIndex(0);
        currentWidget.getElement().focus();

        return currentWidget;
    }

    private void blurCurrentWidget() {
        if (currentWidget == null) {
            return;
        }

        currentWidget.getElement().blur();
        currentWidget = null;
    }
    private void focusChildContainer() {
        childContainer.getElement().setTabIndex(0);
        childContainer.getElement().focus();
    }

    private Widget currentWidget;

    private class DefaultArrowSelectableSelectorHandler implements ArrowSelectableSelectorHandler {
        @Override
        public void escapeFromSelector() {
            if (search.isVisible()) {
                search.setFocus(true);
            } else {
                hide();
            }
        }

        @Override
        public void focusTextArea() {
            if (search.isVisible()) {
                search.setFocus(true);
            } else {
                focus();
            }
        }

        @Override
        public void onValueChanged() {}
    }
}
