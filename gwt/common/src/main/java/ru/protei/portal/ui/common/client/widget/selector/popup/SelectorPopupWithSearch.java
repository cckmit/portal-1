package ru.protei.portal.ui.common.client.widget.selector.popup;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.events.AddEvent;
import ru.protei.portal.ui.common.client.events.AddHandler;
import ru.protei.portal.ui.common.client.events.HasAddHandlers;
import ru.protei.portal.ui.common.client.events.InputEvent;
import ru.protei.portal.ui.common.client.selector.SearchHandler;
import ru.protei.portal.ui.common.client.selector.SelectorPopup;
import ru.protei.portal.ui.common.client.widget.cleanablesearchbox.CleanableSearchBox;
import ru.protei.portal.ui.common.client.widget.composite.popper.PopperComposite;

import java.util.logging.Logger;

import static ru.protei.portal.ui.common.client.common.UiConstants.Styles.HIDE;

/**
 * Вид попапа
 */
public abstract class SelectorPopupWithSearch extends PopperComposite
        implements SelectorPopup, HasAddHandlers {

    public SelectorPopupWithSearch() {
        initWidget(ourUiBinder.createAndBindUi(this));
        setAutoHide(true);
        setAutoResize(true);
        ensureDefaultDebugIds();

        addCloseHandler(event -> {
            removePagingHandler();
            if (popupHandler != null) {
                popupHandler.onPopupHide(this);
            }
        });
    }

    @Override
    public HasWidgets getContainer() {
        return childContainer;
    }

    @Override
    public boolean isEmpty() {
        return childContainer.getWidgetCount() == 0;
    }

    @Override
    public HandlerRegistration addAddHandler(AddHandler handler) {
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
    public void showNear(Element relative) {
        addPagingHandler();
        show(relative);
        dropdown.getElement().setScrollTop(0);
        focusPopup();
    }

    @Override
    public void showNear(Element relative, PopperComposite.Placement placement) {
        addPagingHandler();
        show(relative, placement);
        dropdown.getElement().setScrollTop(0);
        focusPopup();
    }

    @Override
    public void showNear(Element relative, PopperComposite.Placement placement, int skidding, int distance) {
        addPagingHandler();
        show(relative, placement, skidding, distance);
        dropdown.getElement().setScrollTop(0);
        focusPopup();
    }

    @Override
    public void hide() {
        super.hide();

        removePagingHandler();
        if (popupHandler != null) {
            popupHandler.onPopupHide(this);
        }
    }

    @Override
    public void showLoading(boolean isLoading) {
        loading.setVisible(isLoading);
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

    @Override
    public void setNoElements(boolean isSearchResultEmpty, String noElementsMessage) {
        message.setVisible(isSearchResultEmpty && noElementsMessage != null);
        message.setText( noElementsMessage == null ? "" : noElementsMessage );
    }

    @Override
    public void setAddButtonVisibility(boolean isVisible) {
        addButton.setVisible( isVisible );
    }

    @Override
    public abstract void focusPopup();

    @UiHandler( "search" )
    public void onSearchInputChanged( InputEvent event ) {
        changeSearchTimer.schedule(200);
    }

    @UiHandler("addButton")
    public void onAddButtonClick( ClickEvent event) {
        hide();
        AddEvent.fire(this);
    }

    public void clearSearchField() {
            search.setValue( "" );
    }

    public String getSearchString() {
        return search.getValue();
    }


    public void setDebugAttributeAddEntryAction(String debugId) {
        addButton.getElement().setAttribute(DebugIds.DEBUG_ID_ATTRIBUTE, debugId);
    }

    public void setDebugAttributeSearch(String debugId) {
        search.setDebugAttributeTextBox(debugId);
    }

    public void setDebugAttributeSearchAction(String debugId) {
        search.setDebugAttributeAction(debugId);
    }

    public void setDebugAttributeListContainer(String debugId) {
        childContainer.getElement().setAttribute(DebugIds.DEBUG_ID_ATTRIBUTE, debugId);
    }

    private void addPagingHandler() {
        if (scrollForPagingHandleRegistration != null) {
            return;
        }

        scrollForPagingHandleRegistration = dropdown.addDomHandler(new ScrollHandler() {
            @Override
            public void onScroll( ScrollEvent scrollEvent ) {
                Element element = dropdown.getElement();
                if (element.getScrollTop() + element.getClientHeight() >= element.getScrollHeight()) {
                    if (popupHandler != null) {
                        popupHandler.onEndOfScroll();
                    }
                }
            }
        }, ScrollEvent.getType() );
    }

    private void removePagingHandler() {
        if (scrollForPagingHandleRegistration != null) {
            scrollForPagingHandleRegistration.removeHandler();
        }

        scrollForPagingHandleRegistration = null;
    }

    private void ensureDefaultDebugIds() {
        setDebugAttributeAddEntryAction( DebugIds.SELECTOR.POPUP.ADD_NEW_ENTRY_BUTTON);
        setDebugAttributeSearch(DebugIds.SELECTOR.POPUP.SEARCH_INPUT);
        setDebugAttributeSearchAction(DebugIds.SELECTOR.POPUP.SEARCH_ACTION);
        setDebugAttributeListContainer(DebugIds.SELECTOR.POPUP.ENTRY_LIST_CONTAINER);
    }

    @UiField
    protected HTMLPanel childContainer;

    @UiField
    HTMLPanel root;
    @UiField
    protected CleanableSearchBox search;
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

    protected Timer changeSearchTimer = new Timer() {
        @Override
        public void run() {
            searchHandler.onSearch( search.getValue() );
        }
    };
    private static final SearchHandler ignoreSearch = searchString -> { /*ignore search*/ };
    private PopupHandler popupHandler;
    private SearchHandler searchHandler = ignoreSearch;
    private HandlerRegistration scrollForPagingHandleRegistration;

    interface SelectorPopupViewUiBinder extends UiBinder<HTMLPanel, SelectorPopupWithSearch> {
    }

    private static SelectorPopupViewUiBinder ourUiBinder = GWT.create(SelectorPopupViewUiBinder.class);
    private static final Logger log = Logger.getLogger( SelectorPopupWithSearch.class.getName() );
}
