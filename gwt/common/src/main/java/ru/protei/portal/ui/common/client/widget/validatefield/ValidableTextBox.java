package ru.protei.portal.ui.common.client.widget.validatefield;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.event.shared.HandlerRegistration;
import ru.protei.portal.ui.common.client.events.InputEvent;
import ru.protei.portal.ui.common.client.events.InputHandler;

/**
 * TextBox c возможностью валидации
 */
public class ValidableTextBox extends ValidableTextBoxBase{

    public ValidableTextBox() {
        super(Document.get().createTextInputElement());
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

    public HandlerRegistration addInputHandler (InputHandler handler) {
        return addDomHandler(handler, InputEvent.getType());
    }

    private InputElement getInputElement() {
        return (InputElement)this.getElement().cast();
    }
}