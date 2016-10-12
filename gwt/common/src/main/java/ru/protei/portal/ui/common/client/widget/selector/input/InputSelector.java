package ru.protei.portal.ui.common.client.widget.selector.input;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.ui.Button;
import ru.protei.portal.ui.common.client.widget.selector.base.Selector;

import java.awt.*;

/**
 * Created by turik on 12.10.16.
 */
public class InputSelector< T > extends Selector< T > {

    public InputSelector() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
    }

    @Override
    public void fillSelectorView( String selectedValue ) {
        input.setText( selectedValue == null ? "" : selectedValue );
    }

    @UiHandler( "button" )
    public void onBtnClick ( ClickEvent event )
    {
        showPopup( input );
    }

    @UiField
    HTMLPanel inputContainer;
    @UiField
    TextBox input;
    @UiField
    Button button;

    interface InputSelectorUiBinder extends UiBinder< HTMLPanel, InputSelector > {}
    private static InputSelectorUiBinder ourUiBinder = GWT.create( InputSelectorUiBinder.class );

}