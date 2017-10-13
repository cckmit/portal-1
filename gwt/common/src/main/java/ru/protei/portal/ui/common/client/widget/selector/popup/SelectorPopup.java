package ru.protei.portal.ui.common.client.widget.selector.popup;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.*;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.item.SelectorItem;

/**
 * Вид попапа
 */
public class SelectorPopup
        extends PopupPanel
        implements HasValueChangeHandlers<String>
{

    public SelectorPopup() {
        setWidget( ourUiBinder.createAndBindUi( this ) );
        setAutoHideEnabled( true );
        setAutoHideOnHistoryEventsEnabled( true );

        resizeHandler = new ResizeHandler() {
            @Override
            public void onResize(ResizeEvent resizeEvent) {
                if ( isAttached() ) {
                    showNear( relative );
                }
            }
        };
    }

    @Override
    public HandlerRegistration addValueChangeHandler( ValueChangeHandler< String > handler ) {
        return addHandler( handler, ValueChangeEvent.getType() );
    }

    public HasWidgets getChildContainer() {
        return childContainer;
    }

    public void showNear( IsWidget nearWidget ) {
        this.relative = nearWidget;

        showRelativeTo( nearWidget.asWidget() );
        root.getElement().getStyle().setPosition( Style.Position.RELATIVE );
        root.getElement().getStyle().setDisplay( Style.Display.BLOCK );
        root.getElement().getStyle().setWidth( nearWidget.asWidget().getOffsetWidth(), Style.Unit.PX );
        setWidth( String.valueOf( nearWidget.asWidget().getOffsetWidth() ) + "px" );

        if(searchVisible && searchAutoFocus)
            search.setFocus(true);
    }

    public void showNearRight( final IsWidget nearWidget ) {
        this.relative = nearWidget;

        root.getElement().getStyle().setPosition( Style.Position.RELATIVE );
        root.getElement().getStyle().setDisplay( Style.Display.BLOCK );

        setPopupPositionAndShow( new PositionCallback() {
            @Override
            public void setPosition( int popupWidth, int popupHeight ) {
                int relativeLeft = nearWidget.asWidget().getAbsoluteLeft();
                int widthDiff = popupWidth - nearWidget.asWidget().getOffsetWidth();
                int popupLeft = relativeLeft - widthDiff;
                int relativeTop = nearWidget.asWidget().getAbsoluteTop();
                int popupTop = relativeTop + nearWidget.asWidget().getOffsetHeight();

                setPopupPosition( popupLeft, popupTop );
            }
        } );

    }

    public void setSearchVisible( boolean searchVisible ) {
        this.searchVisible = searchVisible;
        if ( searchVisible ) {
            search.getElement().setPropertyString("placeholder", lang.search());
            searchContainer.removeClassName( "hide" );
            return;
        }

        searchContainer.addClassName( "hide" );
    }


    @UiHandler( "search" )
    public void onSearchInputChanged( KeyUpEvent event ) {
        if(event.getNativeKeyCode() == KeyCodes.KEY_DOWN) {
            event.preventDefault();
            if (childContainer.getWidgetCount() == 0) {
                return;
            }
            SelectorItem item = (SelectorItem) childContainer.getWidget(0);
            item.setFocus(true);
            return;
        }
        fireChangeValueTimer();
    }

    @Override
    protected void onLoad() {
        resizeHandlerReg = Window.addResizeHandler( resizeHandler );
    }

    @Override
    protected void onUnload() {
        if ( resizeHandlerReg != null ) {
            resizeHandlerReg.removeHandler();
        }
    }

    public void clearSearchField() {
        search.setText( "" );
        ValueChangeEvent.fire( SelectorPopup.this, search.getText() );
    }

    private void fireChangeValueTimer() {
        searchValueChangeTimer.cancel();
        searchValueChangeTimer.schedule( 200 );
    }

    public void setSearchAutoFocus(boolean val){
        searchAutoFocus = val;
    }

    Timer searchValueChangeTimer = new Timer() {
        @Override
        public void run() {
            searchValueChangeTimer.cancel();
            ValueChangeEvent.fire( SelectorPopup.this, search.getText() );
        }
    };
    IsWidget relative;
    ResizeHandler resizeHandler;
    HandlerRegistration resizeHandlerReg;
    boolean searchAutoFocus = false;

    boolean searchVisible = false;
    @UiField
    HTMLPanel childContainer;
    @UiField
    TextBox search;
    @UiField
    DivElement searchContainer;
    @UiField
    HTMLPanel root;

    @Inject
    Lang lang;

    interface SelectorPopupViewUiBinder extends UiBinder<HTMLPanel, SelectorPopup > {}
    private static SelectorPopupViewUiBinder ourUiBinder = GWT.create( SelectorPopupViewUiBinder.class );
}