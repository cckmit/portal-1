package ru.protei.portal.ui.common.client.widget.validatefield;


import com.google.gwt.user.client.ui.TextArea;

/**
 * TextArea c возможностью валидации
 */
public class ValidableTextArea extends TextArea implements HasValidable{

    @Override
    public void setValid(boolean isValid) {
        if(isValid)
            removeStyleName("error");
        else
            addStyleName("error");
    }

}
