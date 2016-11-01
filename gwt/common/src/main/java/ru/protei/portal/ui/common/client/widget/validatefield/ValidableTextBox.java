package ru.protei.portal.ui.common.client.widget.validatefield;

/**
 * TextBox c возможностью установления состояния ошибки
 */
public class ValidableTextBox extends com.google.gwt.user.client.ui.TextBox implements HasValidable{
    @Override
    public void makeAsCorrect() {
        removeStyleName("error");
    }

    @Override
    public void makeAsIncorrect() {
        addStyleName("error");
    }
}