package ru.protei.portal.ui.contract.client.widget.contraget.box;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.Lang;

import static ru.protei.portal.test.client.DebugIds.DEBUG_ID_ATTRIBUTE;

public class ContragentBox extends Composite implements HasValue<String> {

    public ContragentBox() {
        initWidget(ourUiBinder.createAndBindUi(this));
        ensureDebugIds();
    }

    @Override
    public String getValue() {
        return name.getValue();
    }

    @Override
    public void setValue(String value) {
        name.setValue(value, false);
    }

    @Override
    public void setValue(String value, boolean fireEvents) {
        name.setValue(value, fireEvents);
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    public void setPlaceHolder(String value) {
       name.getElement().setAttribute("placeholder", value);
    }

    private void ensureDebugIds() {
        name.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.CONTRACT.CONTRAGENT.NAME);
        button.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.CONTRACT.CONTRAGENT.SEARCH_BUTTON);

    }

    @UiField
    TextBox name;

    @UiField
    Button button;

    @UiField
    Lang lang;

    interface ContragentBoxViewUiBinder extends UiBinder<HTMLPanel, ContragentBox> {}
    private static ContragentBoxViewUiBinder ourUiBinder = GWT.create( ContragentBoxViewUiBinder.class );
}
