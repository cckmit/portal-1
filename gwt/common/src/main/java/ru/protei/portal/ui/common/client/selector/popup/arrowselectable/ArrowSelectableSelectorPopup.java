package ru.protei.portal.ui.common.client.selector.popup.arrowselectable;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Widget;
import ru.protei.portal.ui.common.client.selector.popup.SelectorPopupWithSearch;

import static java.util.Optional.of;

public class ArrowSelectableSelectorPopup extends SelectorPopupWithSearch {
    public ArrowSelectableSelectorPopup() {
        this(KeyCodes.KEY_ENTER);
    }

    public ArrowSelectableSelectorPopup(int valueChangeKeyCode) {
        initChildContainerHandlers(new DefaultArrowSelectableSelectorHandler(), valueChangeKeyCode);

        search.addDomHandler(event -> {
            if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE || event.getNativeKeyCode() == KeyCodes.KEY_UP) {
                event.preventDefault();
                hide();
                return;
            }

            if (event.getNativeKeyCode() == KeyCodes.KEY_DOWN || event.getNativeKeyCode() == KeyCodes.KEY_TAB) {
                event.preventDefault();
                focus();
                return;
            }
        }, KeyDownEvent.getType());

        addCloseHandler(event -> currentWidget = null);
    }

    public ArrowSelectableSelectorPopup(ArrowSelectableSelectorHandler arrowSelectableSelectorHandler, int valueChangeKeyCode, String styleName) {
        childContainer.addStyleName(styleName);
        initChildContainerHandlers(arrowSelectableSelectorHandler, valueChangeKeyCode);
    }

    public void focus() {
        focusWidget(getNext(childContainer, null));
    }

    @Override
    protected void setFocusOnSearchIfNeeded(boolean isSearchAutoFocus) {
        if (currentWidget != null) {
            return;
        }

        super.setFocusOnSearchIfNeeded(isSearchAutoFocus);
    }

    private void initChildContainerHandlers(ArrowSelectableSelectorHandler arrowSelectableSelectorHandler, int valueChangeKeyCode) {
        childContainer.addDomHandler(event -> arrowSelectableSelectorHandler.onBlurSelector(), MouseOverEvent.getType());
        childContainer.addDomHandler(event -> onKeyDown(event, childContainer, arrowSelectableSelectorHandler, valueChangeKeyCode), KeyDownEvent.getType());
    }

    private void onKeyDown(KeyDownEvent event, ComplexPanel childContainer, ArrowSelectableSelectorHandler arrowSelectableSelectorHandler, int valueChangeKeyCode) {
        if (KeyCodes.KEY_ESCAPE == event.getNativeKeyCode()) {
            event.preventDefault();
            arrowSelectableSelectorHandler.escapeFromSelector();
            return;
        }

        if (KeyCodes.KEY_UP == event.getNativeKeyCode()) {
            event.preventDefault();

            if (focusWidget(getPrevious(childContainer, currentWidget)) == null) {
                arrowSelectableSelectorHandler.onBlurSelector();
            }

            return;
        }

        if (KeyCodes.KEY_DOWN == event.getNativeKeyCode() ||
                event.getNativeKeyCode() == KeyCodes.KEY_TAB) {
            event.preventDefault();
            focusWidget(getNext(childContainer, currentWidget));
            return;
        }

        if (valueChangeKeyCode != event.getNativeKeyCode()) {
            blurCurrentWidget();
            arrowSelectableSelectorHandler.onBlurSelector();
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
        public void onBlurSelector() {
            if (search.isVisible()) {
                search.setFocus(true);
                currentWidget = null;
            } else {
                focusChildContainer();
            }
        }

        @Override
        public void onValueChanged() {}
    }
}
