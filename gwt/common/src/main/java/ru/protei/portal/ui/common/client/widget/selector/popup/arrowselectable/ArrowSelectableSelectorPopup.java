package ru.protei.portal.ui.common.client.widget.selector.popup.arrowselectable;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Widget;
import ru.protei.portal.ui.common.client.widget.selector.popup.SelectorPopupWithSearch;

import static java.util.Optional.of;
import static ru.protei.portal.ui.common.client.selector.util.SelectorItemKeyboardKey.isSelectorItemKeyboardKey;

public class ArrowSelectableSelectorPopup extends SelectorPopupWithSearch {
    public ArrowSelectableSelectorPopup() {
        this(true);
    }

    public ArrowSelectableSelectorPopup(boolean isPopupAutoFocus) {
        this(isPopupAutoFocus, null);
    }

    public ArrowSelectableSelectorPopup(boolean isPopupAutoFocus,
                                        ArrowSelectableSelectorHandler arrowSelectableSelectorHandler) {

        this.isPopupAutoFocus = isPopupAutoFocus;

        initArrowSelectablePopupHandlers(arrowSelectableSelectorHandler == null ?
                new DefaultArrowSelectableSelectorHandler() :
                arrowSelectableSelectorHandler
        );
    }

    @Override
    public void focusFirst() {
        Widget nextWidget = getNext(childContainer, null);

        if (nextWidget != null) {
            focusWidget(nextWidget);
        }
    }

    @Override
    public void focusPopup() {
        if (search.isVisible()) {
            search.setFocus(true);
            return;
        }

        if (isPopupAutoFocus) {
            focusChildContainer();
        }
    }

    private void initArrowSelectablePopupHandlers(ArrowSelectableSelectorHandler arrowSelectableSelectorHandler) {
        addCloseHandler(event -> currentWidget = null);
        addDomHandler(event -> {
            if (currentWidget == null) {
                return;
            }

            arrowSelectableSelectorHandler.onBlurSelector();
        }, MouseOverEvent.getType());
        addDomHandler(event -> onKeyDown(event, childContainer, arrowSelectableSelectorHandler), KeyDownEvent.getType());
    }

    private void onKeyDown(KeyDownEvent event, ComplexPanel childContainer,
                           ArrowSelectableSelectorHandler arrowSelectableSelectorHandler) {
        if (KeyCodes.KEY_ESCAPE == event.getNativeKeyCode()) {
            event.preventDefault();
            arrowSelectableSelectorHandler.onBlurSelector();
            return;
        }

        if (KeyCodes.KEY_UP == event.getNativeKeyCode()) {
            event.preventDefault();

            Widget previous = getPrevious(childContainer, currentWidget);

            if (previous == null) {
                arrowSelectableSelectorHandler.onBlurSelector();
                return;
            }

            focusWidget(previous);
            return;
        }

        if (KeyCodes.KEY_DOWN == event.getNativeKeyCode()) {
            event.preventDefault();
            focusWidget(getNext(childContainer, currentWidget));
            return;
        }

        if (!isSelectorItemKeyboardKey(event.getNativeKeyCode())) {
            arrowSelectableSelectorHandler.onInput();
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

        return null;
    }

    private void focusWidget(Widget widget) {
        currentWidget = widget;

        if (currentWidget == null) {
            return;
        }

        currentWidget.getElement().setTabIndex(0);
        currentWidget.getElement().focus();
    }

    private void focusChildContainer() {
        currentWidget = null;

        childContainer.getElement().setTabIndex(0);
        childContainer.getElement().focus();
    }

    private void blurCurrentWidget() {
        if (currentWidget == null) {
            return;
        }

        currentWidget.getElement().blur();
        currentWidget = null;
    }

    private Widget currentWidget;

    private final boolean isPopupAutoFocus;

    private class DefaultArrowSelectableSelectorHandler implements ArrowSelectableSelectorHandler {
        @Override
        public void onInput() {
            if (!search.isVisible()) {
                return;
            }

            if (search.isFocused()) {
                return;
            }

            onBlurSelector();
        }

        @Override
        public void onBlurSelector() {
            if (currentWidget != null) {
                blurCurrentWidget();
                focusPopup();
            } else {
                hide();
            }
        }
    }
}
