package ru.protei.portal.ui.common.client.widget.components.client.selector.popup;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.events.HasAddHandlers;
import ru.protei.portal.ui.common.client.widget.components.client.popup.BasePopupView;


/**
 * Вид попапа
 */
public class SimpleSelectorPopup extends BasePopupView
        implements SelectorPopup {

    public SimpleSelectorPopup() {
        setWidget(ourUiBinder.createAndBindUi(this));
        setAutoHideEnabled(true);
        setAutoHideOnHistoryEventsEnabled(true);
        ensureDefaultDebugIds();
    }

    @Override
    protected void onLoad() {

        scrolForPagingHandleRegistration = root.addDomHandler( new ScrollHandler() {
            @Override
            public void onScroll(ScrollEvent scrollEvent) {
                Element e = root.getElement();
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

    @Override
    public HasWidgets getChildContainer() {
        return childContainer;
    }

    @Override
    public void showLoading(boolean isLoading) {
        loading.setVisible(isLoading);
    }

    @Override
    protected void onUnload() {
        scrolForPagingHandleRegistration.removeHandler();
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

    public void setEnsureDebugIdListContainer(String debugId) {
        childContainer.ensureDebugId(debugId);
    }

    private void ensureDefaultDebugIds() {
        setEnsureDebugIdListContainer( DebugIds.SELECTOR.POPUP.ENTRY_LIST_CONTAINER);
    }

    HandlerRegistration scrolForPagingHandleRegistration;
    @UiField
    HTMLPanel childContainer;

    @UiField
    HTMLPanel root;
    @UiField
    Label message;
    @UiField
    HTMLPanel loading;

    private PopupHandler popupHandler;
    interface SelectorPopupViewUiBinder extends UiBinder<HTMLPanel, SimpleSelectorPopup> {
    }

    private static SelectorPopupViewUiBinder ourUiBinder = GWT.create(SelectorPopupViewUiBinder.class);
}