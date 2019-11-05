package ru.protei.portal.ui.common.client.widget.components.client.selector.popup;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.UIObject;
import ru.protei.portal.ui.common.client.widget.components.client.popup.BasePopupView;
import ru.protei.portal.ui.common.client.widget.components.client.selector.search.SearchField;
import ru.protei.portal.ui.common.client.widget.components.client.selector.search.SearchHandler;


/**
 * Вид попапа
 */
public class SelectorPopupWithSearch extends BasePopupView
        implements SelectorPopup {

    public SelectorPopupWithSearch() {
        setWidget(ourUiBinder.createAndBindUi(this));
        setAutoHideEnabled(true);
        setAutoHideOnHistoryEventsEnabled(true);
    }

    @Override
    protected void onLoad() {

//        Event.sinkEvents(root.getElement(), Event.ONSCROLL);
        root.addDomHandler(new ScrollHandler() {
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
        popupHandler.onPopupUnload(this);
    }

    @Override
    public void setPopupHandler(PopupHandler popupHandler) {
        this.popupHandler = popupHandler;
    }

    public void setSearchHandler(SearchHandler searchHandler) {
        searchField.setSearchHandler(searchHandler);
    }

    @Override
    public void setNoElements(boolean isSearchResultEmpty, String noElementsMessage) {
//        boolean isSearchResultEmpty = loading.isVisible() || childContainer.getElement().getChildCount() < 1;
        message.setVisible(isSearchResultEmpty);
        message.setText(isSearchResultEmpty ? (noElementsMessage == null ? "- Список пуст -" : noElementsMessage) : "");
    }

    public void clearSearchField() {
        searchField.clearSearchText();
    }

    public String getSearchString() {
        return searchField.getSearchString();
    }

    @UiField
    HTMLPanel childContainer;

    @UiField
    HTMLPanel root;
    @UiField
    SearchField searchField;
    @UiField
    Label message;
    @UiField
    HTMLPanel loading;

    private PopupHandler popupHandler;

    interface SelectorPopupViewUiBinder extends UiBinder<HTMLPanel, SelectorPopupWithSearch> {
    }

    private static SelectorPopupViewUiBinder ourUiBinder = GWT.create(SelectorPopupViewUiBinder.class);
}