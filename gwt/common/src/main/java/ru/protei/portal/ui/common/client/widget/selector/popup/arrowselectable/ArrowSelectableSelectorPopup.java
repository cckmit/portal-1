package ru.protei.portal.ui.common.client.widget.selector.popup.arrowselectable;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Widget;
import ru.protei.portal.ui.common.client.selector.SelectorItemChangeHandler;
import ru.protei.portal.ui.common.client.widget.selector.popup.SelectorPopupWithSearch;

import static java.util.Optional.of;

public class ArrowSelectableSelectorPopup extends SelectorPopupWithSearch {
    public ArrowSelectableSelectorPopup(int valueChangeKeyCode,
                                        boolean isAutoCloseable) {

        this(valueChangeKeyCode, isAutoCloseable, null);
    }

    public ArrowSelectableSelectorPopup(int valueChangeKeyCode,
                                        boolean isAutoCloseable,
                                        ArrowSelectableSelectorHandler arrowSelectableSelectorHandler) {

        this.arrowSelectableSelectorHandler = arrowSelectableSelectorHandler == null ?
                new DefaultArrowSelectableSelectorHandler() :
                arrowSelectableSelectorHandler;

        this.valueChangeKeyCode = valueChangeKeyCode;
        this.isAutoCloseable = isAutoCloseable;

        initArrowSelectablePopupHandlers(this.arrowSelectableSelectorHandler, valueChangeKeyCode);
    }

    @Override
    public void focusPopup() {
        Widget nextWidget = getNext(childContainer, null);

        if (nextWidget == null) {
            focusChildContainer();
        } else {
            focusWidget(nextWidget);
        }
    }

    @Override
    public void addValueChangeHandlers(SelectorItemChangeHandler selectorItem) {
        addValueChangeHandlers(selectorItem, valueChangeKeyCode, isAutoCloseable, arrowSelectableSelectorHandler);
    }

    private void initArrowSelectablePopupHandlers(ArrowSelectableSelectorHandler arrowSelectableSelectorHandler, int valueChangeKeyCode) {
        search.addDomHandler(event -> {
            if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
                event.preventDefault();
                hide();
            }

            if (event.getNativeKeyCode() == KeyCodes.KEY_DOWN || event.getNativeKeyCode() == KeyCodes.KEY_TAB) {
                event.preventDefault();
                focusPopup();
            }
        }, KeyDownEvent.getType());

        addCloseHandler(event -> currentWidget = null);

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

    private void addValueChangeHandlers(SelectorItemChangeHandler selectorItem,
                                        int valueChangeKeyCode,
                                        boolean isAutoCloseable,
                                        ArrowSelectableSelectorHandler arrowSelectableSelectorHandler) {

        selectorItem.asWidget().addDomHandler(event -> {
            if (event.getNativeKeyCode() != valueChangeKeyCode) {
                return;
            }

            event.preventDefault();

            selectorItem.onItemClicked();

            if (isAutoCloseable) {
                hide();
            } else {
                refreshPopup();
            }
        }, KeyDownEvent.getType());

        selectorItem.asWidget().addDomHandler(event -> {
            event.preventDefault();

            selectorItem.onItemClicked();

            if (isAutoCloseable) {
                hide();
            } else {
                refreshPopup();
                arrowSelectableSelectorHandler.onBlurSelector();
            }
        }, ClickEvent.getType());
    }

    private Widget currentWidget;

    private final int valueChangeKeyCode;
    private final boolean isAutoCloseable;
    private final ArrowSelectableSelectorHandler arrowSelectableSelectorHandler;

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
                blurCurrentWidget();
                search.setFocus(true);
            } else {
                focusChildContainer();
            }
        }
    }
}
