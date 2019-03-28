package ru.protei.portal.ui.common.client.widget.popup;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;

public abstract class PopupRightAligned extends PopupPanel {

    protected void afterShowed() { /* default impl */ }

    protected abstract Panel getRoot();

    protected void init() {
        init(true, true);
    }

    protected void init(boolean autoHide, boolean autoHideOnHistoryChanged) {

        setAutoHideEnabled(autoHide);
        setAutoHideOnHistoryEventsEnabled(autoHideOnHistoryChanged);

        resizeHandler = resizeEvent -> {
            if (isAttached()) {
                showNear(relative);
            }
        };

        windowScrollHandler = event -> {
            if (isAttached()) {
                showNear(relative);
            }
        };
    }

    @Override
    protected void onLoad() {
        resizeHandlerReg = Window.addResizeHandler(resizeHandler);
        scrollHandlerReg = Window.addWindowScrollHandler(windowScrollHandler);
    }

    @Override
    protected void onUnload() {
        if (resizeHandlerReg != null) {
            resizeHandlerReg.removeHandler();
        }
        if (scrollHandlerReg != null) {
            scrollHandlerReg.removeHandler();
        }
    }

    public void showNear(IsWidget nearWidget) {
        this.relative = nearWidget;

        getRoot().getElement().getStyle().setPosition(Style.Position.RELATIVE);
        getRoot().getElement().getStyle().setDisplay(Style.Display.BLOCK);
        setPopupPositionAndShow((popupWidth, popupHeight) -> {
            int relativeLeft = nearWidget.asWidget().getAbsoluteLeft();
            int widthDiff = popupWidth - nearWidget.asWidget().getOffsetWidth();
            int popupLeft = relativeLeft - widthDiff;
            int relativeTop = nearWidget.asWidget().getAbsoluteTop();
            int popupTop = relativeTop + nearWidget.asWidget().getOffsetHeight();
            setPopupPosition(popupLeft, popupTop);
        });

        afterShowed();
    }

    private IsWidget relative;
    private ResizeHandler resizeHandler;
    private Window.ScrollHandler windowScrollHandler;
    private HandlerRegistration resizeHandlerReg;
    private HandlerRegistration scrollHandlerReg;
}
