package ru.protei.portal.ui.common.client.widget.components.client.selector.popup;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.*;

import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.events.AddEvent;
import ru.protei.portal.ui.common.client.events.AddHandler;
import ru.protei.portal.ui.common.client.events.HasAddHandlers;
import ru.protei.portal.ui.common.client.widget.cleanablesearchbox.CleanableSearchBox;
import ru.protei.portal.ui.common.client.widget.components.client.popup.BasePopupView;
import ru.protei.portal.ui.common.client.widget.components.client.selector.search.SearchHandler;

import java.util.logging.Logger;


/**
 * Вид попапа
 */
public class SelectorPopupWithSearch extends BasePopupView
        implements SelectorPopup, HasAddHandlers {

    public SelectorPopupWithSearch() {
        setWidget(ourUiBinder.createAndBindUi(this));
        setAutoHideEnabled(true);
        setAutoHideOnHistoryEventsEnabled(true);
        ensureDefaultDebugIds();
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
    public HandlerRegistration addAddHandler( AddHandler handler) {
        return addHandler(handler, AddEvent.getType());
    }

    @Override
    public void setSearchHandler(SearchHandler searchHandler) {
        this.searchHandler = searchHandler;
    }

    @Override
    public void showNear( UIObject view ) {
        super.showNear( view );
        search.setFocus( isSearchAutoFocus );
    }

    @Override
    protected void onLoad() {

        scrolForPagingHandleRegistration = dropdown.addDomHandler( new ScrollHandler() {
            @Override
            public void onScroll( ScrollEvent scrollEvent ) {
                Element e = dropdown.getElement();
                if (e.getScrollTop() + e.getClientHeight() >= e.getScrollHeight()) {
                    if (popupHandler != null) {
                        popupHandler.onEndOfScroll();
                    }
                }
            }
        }, ScrollEvent.getType() );
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
    public void setPopupHandler(PopupHandler popupHandler) {
        this.popupHandler = popupHandler;
    }



    public void setAddButton(boolean addVisible) {
        if (addVisible) {
            addContainer.removeClassName("hide");
        } else {
            addContainer.addClassName("hide");
        }
    }

    public void setAddButton(boolean addVisible, String text) {
        addButton.setText(text);
        setAddButton(addVisible);
    }

    @UiHandler( "search" )
    public void onSearchInputChanged( ValueChangeEvent<String> event ) {
        searchHandler.onSearch( search.getValue() );
    }

    @UiHandler("addButton")
    public void onAddButtonClick( ClickEvent event) {
        AddEvent.fire(this);
    }

    @Override
    public void setNoElements(boolean isSearchResultEmpty, String noElementsMessage) {
        message.setVisible(isSearchResultEmpty);
        message.setText(isSearchResultEmpty ? (noElementsMessage == null ? "- Список пуст -" : noElementsMessage) : "");
    }

    public void clearSearchField() {
            search.setValue( "" );
    }

    public void setAddButtonVisibility(boolean isVisible) {
        addButton.setVisible( isVisible );
    }

    public String getSearchString() {
        return search.getValue();
    }


    public void setEnsureDebugIdAddEntryAction(String debugId) {
        addButton.ensureDebugId(debugId);
    }

    public void setEnsureDebugIdSearch(String debugId) {
        search.setEnsureDebugIdTextBox(debugId);
    }

    public void setEnsureDebugIdSearchAction(String debugId) {
        search.setEnsureDebugIdAction(debugId);
    }

    public void setEnsureDebugIdListContainer(String debugId) {
        childContainer.ensureDebugId(debugId);
    }

    public void setSearchAutoFocus( boolean isSearchAutoFocus ) {
        this.isSearchAutoFocus = isSearchAutoFocus;
    }

    private void ensureDefaultDebugIds() {
        setEnsureDebugIdAddEntryAction( DebugIds.SELECTOR.POPUP.ADD_NEW_ENTRY_BUTTON);
        setEnsureDebugIdSearch(DebugIds.SELECTOR.POPUP.SEARCH_INPUT);
        setEnsureDebugIdSearchAction(DebugIds.SELECTOR.POPUP.SEARCH_ACTION);
        setEnsureDebugIdListContainer(DebugIds.SELECTOR.POPUP.ENTRY_LIST_CONTAINER);
    }


    @UiField
    HTMLPanel childContainer;

    @UiField
    HTMLPanel root;
    @UiField
    public CleanableSearchBox search;
    @UiField
    public Button addButton;
    @UiField
    DivElement addContainer;
    @UiField
    Label message;
    @UiField
    HTMLPanel loading;
    @UiField
    HTMLPanel dropdown;

    public static final String HIDE = "hide";

    private PopupHandler popupHandler;
    private SearchHandler searchHandler = searchString -> { /*ignore*/ };
    private boolean isSearchAutoFocus = true;
    private HandlerRegistration scrolForPagingHandleRegistration;

    interface SelectorPopupViewUiBinder extends UiBinder<HTMLPanel, SelectorPopupWithSearch> {
    }

    private static SelectorPopupViewUiBinder ourUiBinder = GWT.create(SelectorPopupViewUiBinder.class);
    private static final Logger log = Logger.getLogger( SelectorPopupWithSearch.class.getName() );
}