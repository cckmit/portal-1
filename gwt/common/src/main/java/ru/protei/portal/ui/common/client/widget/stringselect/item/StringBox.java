package ru.protei.portal.ui.common.client.widget.stringselect.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;

/**
 * Один элемент инпут-селектора
 */
public class StringBox extends Composite implements HasValue<String>, HasEnabled {

    public interface CloseHandler {
        void onClose(StringBox box);
    }

    public StringBox() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    public void setHandler(CloseHandler handler) {
        this.handler = handler;
    }

    @Override
    public void setValue(String value) {
        setValue(value, false);
    }

    @Override
    public void setValue(String value, boolean fireEvents) {
        curValue = value;
        text.setInnerText(value);
    }

    @Override
    public String getValue() {
        return curValue;
    }

    @UiHandler("close")
    public void onCloseClicked(ClickEvent event) {
        event.preventDefault();
        if (handler != null) {
            handler.onClose(this);
        }
    }

    @Override
    public boolean isEnabled() {
        return close.isEnabled();
    }

    @Override
    public void setEnabled(boolean enabled) {
        close.setEnabled(enabled);
        if (enabled) {
            close.removeStyleName("no-action");
        } else {
            close.addStyleName("no-action");
        }
    }

    @UiField
    DivElement text;

    @UiField
    Anchor close;
    String curValue = null;

     CloseHandler handler;

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {
        return null;
    }

    interface SelectItemViewUiBinder extends UiBinder<HTMLPanel, StringBox> {
    }

    private static SelectItemViewUiBinder ourUiBinder = GWT.create(SelectItemViewUiBinder.class);

}