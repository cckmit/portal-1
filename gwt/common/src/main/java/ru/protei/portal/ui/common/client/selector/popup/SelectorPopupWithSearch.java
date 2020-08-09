package ru.protei.portal.ui.common.client.selector.popup;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.events.AddEvent;
import ru.protei.portal.ui.common.client.events.AddHandler;
import ru.protei.portal.ui.common.client.events.HasAddHandlers;
import ru.protei.portal.ui.common.client.events.InputEvent;
import ru.protei.portal.ui.common.client.popup.BasePopupView;
import ru.protei.portal.ui.common.client.selector.SearchHandler;
import ru.protei.portal.ui.common.client.selector.SelectorPopup;
import ru.protei.portal.ui.common.client.widget.cleanablesearchbox.CleanableSearchBox;

import java.util.logging.Logger;

import static ru.protei.portal.ui.common.client.common.UiConstants.Styles.HIDE;

import static ru.protei.portal.ui.common.client.common.UiConstants.Styles.HIDE;

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
    public void setSearchHandler( SearchHandler searchHandler ) {
        if (searchHandler != null) {
            this.searchHandler = searchHandler;
            search.setVisible( true );
        } else {
            this.searchHandler = ignoreSearch;
            search.setVisible( false );
        }
    }

    @Override
    public void showNear( UIObject view ) {
        super.showNear( view );
        search.setFocus( isSearchAutoFocus );
    }

    @Override
    public void showNear(UIObject showNear, Position position, Integer width) {
        if (position != null) super.setPosition(position);
        super.show(showNear, width);
        search.setFocus(isSearchAutoFocus);
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
            addContainer.removeClassName(HIDE);
        } else {
            addContainer.addClassName(HIDE);
        }
    }

    @Override
    public void setAddButton(boolean addVisible, String text) {
        addButton.setText(text);
        setAddButton(addVisible);
    }

    @UiHandler( "search" )
    public void onSearchInputChanged( InputEvent event ) {
        changeSearchTimer.schedule(200);
    }

    @UiHandler("addButton")
    public void onAddButtonClick( ClickEvent event) {
        AddEvent.fire(this);
    }

    @Override
    public void setNoElements(boolean isSearchResultEmpty, String noElementsMessage) {
        message.setVisible(isSearchResultEmpty && noElementsMessage != null);
        message.setText( noElementsMessage == null ? "" : noElementsMessage );
    }

    public void clearSearchField() {
            search.setValue( "" );
    }

    @Override
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

    private static final SearchHandler ignoreSearch = searchString -> { /*ignore search*/ };
    private PopupHandler popupHandler;
    private SearchHandler searchHandler = ignoreSearch;
    private boolean isSearchAutoFocus = true;
    private HandlerRegistration scrolForPagingHandleRegistration;
    private Timer changeSearchTimer = new Timer() {
        @Override
        public void run() {
            searchHandler.onSearch( search.getValue() );
        }
    };

    interface SelectorPopupViewUiBinder extends UiBinder<HTMLPanel, SelectorPopupWithSearch> {
    }

    private static SelectorPopupViewUiBinder ourUiBinder = GWT.create(SelectorPopupViewUiBinder.class);
    private static final Logger log = Logger.getLogger( SelectorPopupWithSearch.class.getName() );
}
