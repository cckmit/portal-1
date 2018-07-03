package ru.protei.portal.ui.common.client.widget.searchbtn.popup;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.enterabletextbox.EnterableTextBox;

public class SearchInputPopup extends PopupPanel implements HasValue<String> {

    @Inject
    public void onInit() {
        setWidget(ourUiBinder.createAndBindUi(this));
        setAutoHideEnabled(true);
        setAutoHideOnHistoryEventsEnabled(true);

        resizeHandler = resizeEvent -> {
            if (isAttached()) {
                showNear(relative, false);
            }
        };

        windowScrollHandler = event -> {
            if (isAttached()) {
                showNear(relative, false);
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

    @Override
    public String getValue() {
        return searchInput.getValue();
    }

    @Override
    public void setValue(String value) {
        setValue(value, false);
    }

    @Override
    public void setValue(String value, boolean fireEvents) {
        searchInput.setValue(value, false);
        if (fireEvents) {
            ValueChangeEvent.fire(this, value);
        }
    }

    public void setPlaceholder(String value) {
        searchInput.setPlaceholder(value);
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    public void showNear(IsWidget nearWidget) {
        showNear(nearWidget, true);
    }

    public void showNear(IsWidget nearWidget, boolean reset) {
        this.relative = nearWidget;

        root.getElement().getStyle().setPosition(Style.Position.RELATIVE);
        root.getElement().getStyle().setDisplay(Style.Display.BLOCK);
        setPopupPositionAndShow((popupWidth, popupHeight) -> {
            int relativeLeft = nearWidget.asWidget().getAbsoluteLeft();
            int widthDiff = popupWidth - nearWidget.asWidget().getOffsetWidth();
            int popupLeft = relativeLeft - widthDiff;
            int popupTop = nearWidget.asWidget().getAbsoluteTop();
            setPopupPosition(popupLeft, popupTop);
        });

        if (reset) {
            searchInput.setValue("");
            searchInput.setFocus(true);
        }
    }

    @UiHandler("searchInput")
    public void onSearchInputChanged(ValueChangeEvent<String> event) {
        ValueChangeEvent.fire(this, event.getValue());
        hide();
    }

    @UiField
    HTMLPanel root;
    @UiField
    EnterableTextBox searchInput;
    @Inject
    @UiField
    Lang lang;

    private IsWidget relative;
    private ResizeHandler resizeHandler;
    private Window.ScrollHandler windowScrollHandler;
    private HandlerRegistration resizeHandlerReg;
    private HandlerRegistration scrollHandlerReg;

    interface SearchInputPopupViewUiBinder extends UiBinder<HTMLPanel, SearchInputPopup> {}
    private static SearchInputPopupViewUiBinder ourUiBinder = GWT.create(SearchInputPopupViewUiBinder.class);
}
