package ru.protei.portal.ui.common.client.widget.selector.button;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasEnabled;
import ru.protei.portal.ui.common.client.widget.selector.base.Selector;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;

/**
 * Вид селектора
 */
public class ButtonSelector<T> extends Selector<T> implements HasValidable, HasEnabled{

    public ButtonSelector() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void fillSelectorView(String selectedValue) {
        text.setInnerText(selectedValue == null ? "" : selectedValue);
    }

    @Override
    public void onClick( ClickEvent event ) {
        super.onClick(event);
        if(isValidable)
            setValid( isValid() );
    }

    @Override
    public void setValue(T value) {
        super.setValue(value);
        if(isValidable)
            setValid( isValid() );
    }

    @Override
    public boolean isValid(){
        return getValue() != null;
    }

    @Override
    public void setValid(boolean isValid){
        if(isValid)
            button.removeStyleName(ERROR_STYLE_NAME);
        else
            button.addStyleName(ERROR_STYLE_NAME);
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public void setEnabled(boolean b) {
        this.isEnabled = b;

        if(isEnabled){
            button.removeStyleName(INACTIVE_STYLE_NAME);
        }else {
            button.addStyleName(INACTIVE_STYLE_NAME);
        }
        button.setEnabled(isEnabled);
    }

    @UiHandler( "button" )
    public void onBtnClick (ClickEvent event)
    {
        showPopup(button);
    }

    public void setHeader( String header ) {
        this.label.setInnerText( header );
    }

    public void setValidation(boolean isValidable){
        this.isValidable = isValidable;
    }


    @UiField
    HTMLPanel inputContainer;
    @UiField
    Button button;
    @UiField
    LabelElement label;
    @UiField
    SpanElement text;

    private boolean isValidable;
    private static final String ERROR_STYLE_NAME="error";
    private static final String INACTIVE_STYLE_NAME="inactive";
    private boolean isEnabled;

    interface InputSelectorUiBinder extends UiBinder<HTMLPanel, ButtonSelector > { }
    private static InputSelectorUiBinder ourUiBinder = GWT.create(InputSelectorUiBinder.class);

}