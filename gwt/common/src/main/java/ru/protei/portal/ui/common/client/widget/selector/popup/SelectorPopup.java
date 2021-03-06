package ru.protei.portal.ui.common.client.widget.selector.popup;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
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
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.common.UiConstants;
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
        Scheduler.get().scheduleDeferred((Command) () -> search.setFocus(searchAutoFocus));
    }

    public void setSearchVisible( boolean searchVisible ) {
        if ( searchVisible ) {
            search.getElement().setPropertyString("placeholder", lang.search());
            search.setVisible(true);
            return;
        }

        search.setVisible(false);
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

    public void setSearchAutoFocus(boolean searchAutoFocus) {
        this.searchAutoFocus = searchAutoFocus;
    }

    private void ensureDefaultDebugIds() {
        setDebugAttributeAddEntryAction(DebugIds.SELECTOR.POPUP.ADD_NEW_ENTRY_BUTTON);
        setDebugAttributeSearch(DebugIds.SELECTOR.POPUP.SEARCH_INPUT);
        setDebugAttributeSearchAction(DebugIds.SELECTOR.POPUP.SEARCH_ACTION);
        setDebugAttributeListContainer(DebugIds.SELECTOR.POPUP.ENTRY_LIST_CONTAINER);
    }

    public void setDebugAttributeAddEntryAction(String attribute) {
        addButton.getElement().setAttribute(DebugIds.DEBUG_ID_ATTRIBUTE, attribute);
    }

    public void setDebugAttributeSearch(String attribute) {
        search.setDebugAttributeTextBox(attribute);
    }

    public void setDebugAttributeSearchAction(String attribute) {
        search.setDebugAttributeAction(attribute);
    }

    public void setDebugAttributeListContainer(String attribute) {
        childContainer.getElement().setAttribute(DebugIds.DEBUG_ID_ATTRIBUTE, attribute);
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
