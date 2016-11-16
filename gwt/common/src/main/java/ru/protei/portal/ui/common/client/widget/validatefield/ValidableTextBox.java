package ru.protei.portal.ui.common.client.widget.validatefield;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.InputElement;

/**
 * TextBox c возможностью валидации
 */
public class ValidableTextBox extends ValidableTextBoxBase{

    public ValidableTextBox() {
        super(Document.get().createTextInputElement());
//        setStyleName("gwt-TextBox");
    }

    public int getMaxLength() {
        return this.getInputElement().getMaxLength();
    }

    public int getVisibleLength() {
        return this.getInputElement().getSize();
    }

    public void setMaxLength(int length) {
        this.getInputElement().setMaxLength(length);
    }

    public void setVisibleLength(int length) {
        this.getInputElement().setSize(length);
    }

    private InputElement getInputElement() {
        return (InputElement)this.getElement().cast();
    }




}