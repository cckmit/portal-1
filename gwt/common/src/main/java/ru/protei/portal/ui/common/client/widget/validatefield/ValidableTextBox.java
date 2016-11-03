package ru.protei.portal.ui.common.client.widget.validatefield;

import com.google.gwt.user.client.ui.TextBox;

/**
 * TextBox c возможностью валидации
 */
public class ValidableTextBox extends TextBox implements HasValidable{

    @Override
    public void setValid(boolean isValid) {
        this.isValid = isValid;
        if(isValid)
            removeStyleName("error");
        else
            addStyleName("error");
    }

    @Override
    public boolean isValid() {
        return isValid;
    }

    private boolean isValid;
}