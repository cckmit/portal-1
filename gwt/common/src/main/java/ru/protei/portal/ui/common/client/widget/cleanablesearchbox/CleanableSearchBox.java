package ru.protei.portal.ui.common.client.widget.cleanablesearchbox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import ru.protei.portal.core.model.helper.HelperFunc;

public class CleanableSearchBox extends Composite implements HasValue<String> {

    public CleanableSearchBox() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public String getValue() {
        return textBox.getValue();
    }

    @Override
    public void setValue(String value) {
        textBox.setValue(value);
        toggleSearchAction();
    }

    @Override
    public void setValue(String value, boolean fireEvents) {
        textBox.setValue(value, fireEvents);
        toggleSearchAction();
    }

    public void setPlaceholder(String value) {
        textBox.getElement().setAttribute("placeholder", value);
    }

    @UiHandler("textBox")
    public void onTextBoxKeyUp(KeyUpEvent event) {
        keyUpEventSent = true;
        ValueChangeEvent.fire(CleanableSearchBox.this, getValue());
        toggleSearchAction();
    }

    @UiHandler("textBox")
    public void onTextBoxChanged(ValueChangeEvent<String> event) {
        if (keyUpEventSent) {
            keyUpEventSent = false;
            return;
        }
        ValueChangeEvent.fire(CleanableSearchBox.this, getValue());
        toggleSearchAction();
    }

    @UiHandler("textBoxAction")
    public void onTextBoxActionClick(ClickEvent event) {
        if (HelperFunc.isNotEmpty(textBox.getValue())) {
            textBox.setValue("", true);
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    public HandlerRegistration addChangeHandler(ChangeHandler handler) {
        return addDomHandler(handler, ChangeEvent.getType());
    }

    private void toggleSearchAction() {
        if (HelperFunc.isNotEmpty(textBox.getValue())) {
            textBoxAction.addStyleName("clear");
        } else {
            textBoxAction.removeStyleName("clear");
        }
    }

    @UiField
    TextBox textBox;

    @UiField
    Anchor textBoxAction;

    private boolean keyUpEventSent = false;

    interface CleanableTextBoxViewUiBinder extends UiBinder<HTMLPanel, CleanableSearchBox> {}
    private static CleanableTextBoxViewUiBinder ourUiBinder = GWT.create( CleanableTextBoxViewUiBinder.class );
}
