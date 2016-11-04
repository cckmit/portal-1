package ru.protei.portal.ui.common.client.widget.validatefield;

/**
 * TextBox c возможностью валидации
 */
public class ValidableTextBox extends com.google.gwt.user.client.ui.TextBox implements HasValidable{

    @Override
    public void setValid(boolean isValid) {
        if(isValid)
            removeStyleName("error");
        else
            addStyleName("error");
    }
}