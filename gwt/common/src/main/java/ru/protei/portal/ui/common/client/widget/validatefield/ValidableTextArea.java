package ru.protei.portal.ui.common.client.widget.validatefield;


/**
 * Created by bondarenko on 01.11.16.
 */
public class ValidableTextArea extends com.google.gwt.user.client.ui.TextArea implements HasValidable{
    @Override
    public void makeAsCorrect() {
        removeStyleName("error");
    }

    @Override
    public void makeAsIncorrect() {
        addStyleName("error");
    }
}
