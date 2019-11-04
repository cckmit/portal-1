package ru.protei.portal.ui.common.client.widget.components.client.selector.popup;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import ru.protei.portal.ui.common.client.widget.components.client.popup.BasePopupView;


/**
 * Вид попапа
 */
public class SimpleSelectorPopup extends BasePopupView
        implements SelectorPopup, Window.ScrollHandler {

    private PopupHandler popupHandler;

    public SimpleSelectorPopup() {
        setWidget(ourUiBinder.createAndBindUi(this));
        setAutoHideEnabled(true);
        setAutoHideOnHistoryEventsEnabled(true);
    }

    @Override
    protected void onLoad() {

        Event.sinkEvents(childContainer.getElement(), Event.ONSCROLL);
        childContainer.addHandler(new ScrollHandler() {
            @Override
            public void onScroll(ScrollEvent scrollEvent) {
                Element e = childContainer.getElement();
                if (e.getScrollTop() + e.getClientHeight() >= e.getScrollHeight()) {
                    if (popupHandler != null) {
                        popupHandler.onEndOfScroll();
                    }
                }
            }
        }, ScrollEvent.getType());
    }

    @Override
    protected UIObject getPositionRoot() {
        return root;
    }

    public HasWidgets getChildContainer() {
        return childContainer;
    }

    @Override
    public void showLoading(boolean isLoading) {
        loading.setVisible(isLoading);
    }

    @Override
    protected void onUnload() {
        popupHandler.onPopupUnload(this);
    }

    @Override
    public void setNoElements(boolean isSearchResultEmpty, String noElementsMessage) {
        message.setVisible(isSearchResultEmpty);
        message.setText(isSearchResultEmpty ? (noElementsMessage == null ? "- Список пуст -" : noElementsMessage) : "");
    }

    @Override
    public void setPopupHandler(PopupHandler popupHandler) {
        this.popupHandler = popupHandler;
    }

    @UiField
    HTMLPanel childContainer;

    @UiField
    HTMLPanel root;
    @UiField
    Label message;
    @UiField
    HTMLPanel loading;

    interface SelectorPopupViewUiBinder extends UiBinder<HTMLPanel, SimpleSelectorPopup> {
    }

    private static SelectorPopupViewUiBinder ourUiBinder = GWT.create(SelectorPopupViewUiBinder.class);
}