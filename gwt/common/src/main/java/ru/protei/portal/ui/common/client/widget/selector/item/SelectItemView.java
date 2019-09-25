package ru.protei.portal.ui.common.client.widget.selector.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasEnabled;
import ru.protei.portal.test.client.DebugIds;

import static ru.protei.portal.test.client.DebugIds.DEBUG_ID_ATTRIBUTE;

/**
 * Один элемент инпут-селектора
 */
public class SelectItemView extends Composite implements HasEnabled {
    public SelectItemView() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public static interface CloseHandler {
        public void onCloseClicked( SelectItemView item );
    }

    public void setActivity( CloseHandler handler ) {
        this.handler = handler;
    }

    public void setValue( String value ) {
        curValue = value;
        text.setInnerText(  value );
    }

    public String getValue() {
         return curValue;
    }

    @UiHandler( "close" )
    public void onCloseClicked( ClickEvent event ) {
        event.preventDefault();
        if (!isEnabled) {
            return;
        }
        handler.onCloseClicked( this );
    }

    private void setTestAttributes() {
        root.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.SELECTOR.SELECTED.ITEM);
        close.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.SELECTOR.SELECTED.REMOVE_BUTTON);
    }

    @UiField
    DivElement text;
    @UiField
    Anchor close;
    @UiField
    HTMLPanel root;

    String curValue = null;
    CloseHandler handler;

    private boolean isEnabled = true;

    interface SelectItemViewUiBinder extends UiBinder< HTMLPanel, SelectItemView > {}
    private static SelectItemViewUiBinder ourUiBinder = GWT.create( SelectItemViewUiBinder.class );

}