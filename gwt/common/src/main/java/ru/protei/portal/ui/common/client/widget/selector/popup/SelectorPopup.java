package ru.protei.portal.ui.common.client.widget.selector.popup;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.events.AddEvent;
import ru.protei.portal.ui.common.client.events.AddHandler;
import ru.protei.portal.ui.common.client.events.HasAddHandlers;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.cleanablesearchbox.CleanableSearchBox;
import ru.protei.portal.ui.common.client.widget.composite.popper.PopperComposite;
import ru.protei.portal.ui.common.client.widget.selector.item.SelectorItem;

import static ru.protei.portal.ui.common.client.common.UiConstants.Styles.HIDE;

/**
 * Вид попапа
 */
public class SelectorPopup extends PopperComposite implements HasValueChangeHandlers<String>, HasAddHandlers {
    public SelectorPopup() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        ensureDefaultDebugIds();

        setAutoHide(true);
        setAutoResize(true);
    }

    @Override
    public HandlerRegistration addValueChangeHandler( ValueChangeHandler< String > handler ) {
        return addHandler( handler, ValueChangeEvent.getType() );
    }

    @Override
    public HandlerRegistration addAddHandler(AddHandler handler) {
        return addHandler(handler, AddEvent.getType());
    }

    public HasWidgets getChildContainer() {
        return childContainer;
    }

    public void show(IsWidget nearWidget) {
        super.show(nearWidget.asWidget().getElement());
        search.setFocus(searchAutoFocus);
    }

    public void setSearchVisible( boolean searchVisible ) {
        if ( searchVisible ) {
            search.getElement().setPropertyString("placeholder", lang.search());
            search.removeStyleName(HIDE);
            return;
        }

        search.addStyleName(HIDE);
    }

    public void setAddButton(boolean addVisible) {
        if (addVisible) {
            addContainer.removeClassName(HIDE);
        } else {
            addContainer.addClassName(HIDE);
        }
    }

    public void setAddButton(boolean addVisible, String text) {
        addButton.setText(text);
        setAddButton(addVisible);
    }

    @UiHandler( "search" )
    public void onSearchKeyUpEvent( KeyUpEvent event ) {
        if(event.getNativeKeyCode() == KeyCodes.KEY_DOWN) {
            event.preventDefault();
            if (childContainer.getWidgetCount() == 0) {
                return;
            }
            SelectorItem item = (SelectorItem) childContainer.getWidget(0);
            item.setFocus(true);
        }
    }

    @UiHandler( "search" )
    public void onSearchInputChanged( ValueChangeEvent<String> event ) {
        fireChangeValueTimer();
    }

    @UiHandler("addButton")
    public void onAddButtonClick(ClickEvent event) {
        AddEvent.fire(this);
    }

    public void clearSearchField() {
        search.setValue( "" );
    }

    private void fireChangeValueTimer() {
        searchValueChangeTimer.cancel();
        searchValueChangeTimer.schedule( 200 );
    }

    public void setSearchAutoFocus(boolean val){
        searchAutoFocus = val;
    }

    private void ensureDefaultDebugIds() {
        setEnsureDebugIdAddEntryAction(DebugIds.SELECTOR.POPUP.ADD_NEW_ENTRY_BUTTON);
        setEnsureDebugIdSearch(DebugIds.SELECTOR.POPUP.SEARCH_INPUT);
        setEnsureDebugIdSearchAction(DebugIds.SELECTOR.POPUP.SEARCH_ACTION);
        setEnsureDebugIdListContainer(DebugIds.SELECTOR.POPUP.ENTRY_LIST_CONTAINER);
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

    private Timer searchValueChangeTimer = new Timer() {
        @Override
        public void run() {
            searchValueChangeTimer.cancel();
            ValueChangeEvent.fire( SelectorPopup.this, search.getValue() );
        }
    };

    private boolean searchAutoFocus = true;

    @UiField
    public HTMLPanel childContainer;
    @UiField
    public CleanableSearchBox search;
    @UiField
    public Button addButton;
    @UiField
    DivElement addContainer;
    @UiField
    HTMLPanel root;

    @Inject
    @UiField
    Lang lang;

    interface SelectorPopupViewUiBinder extends UiBinder<HTMLPanel, SelectorPopup > {}
    private static SelectorPopupViewUiBinder ourUiBinder = GWT.create( SelectorPopupViewUiBinder.class );
}
