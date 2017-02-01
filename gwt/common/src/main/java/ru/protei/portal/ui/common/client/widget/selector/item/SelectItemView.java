package ru.protei.portal.ui.common.client.widget.selector.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;

/**
 * Один элемент инпут-селектора
 */
public class SelectItemView extends Composite {
    public SelectItemView() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
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
        handler.onCloseClicked( this );
    }


    @UiField
    DivElement text;
    @UiField
    Anchor close;

    String curValue = null;
    CloseHandler handler;

    interface SelectItemViewUiBinder extends UiBinder< HTMLPanel, SelectItemView > {}
    private static SelectItemViewUiBinder ourUiBinder = GWT.create( SelectItemViewUiBinder.class );

}