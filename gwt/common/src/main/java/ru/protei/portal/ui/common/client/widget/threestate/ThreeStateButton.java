package ru.protei.portal.ui.common.client.widget.threestate;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Label;

public class ThreeStateButton extends Composite implements HasValue<Boolean> {

    public ThreeStateButton() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public Boolean getValue() {
        return value;
    }

    @Override
    public void setValue(Boolean value) {
        setValue(value, false);
    }

    @Override
    public void setValue(Boolean value, boolean fireEvents) {
        this.value = value;
        refreshBtnState();

        if (fireEvents) {
            ValueChangeEvent.fire(this, value);
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Boolean> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    @UiHandler( "no" )
    public void onNoStateSelected(ClickEvent event) {
        value = false;
        refreshBtnState();
        ValueChangeEvent.fire(this, value);
    }

    @UiHandler( "yes" )
    public void onYesStateSelected(ClickEvent event) {
        value = true;
        refreshBtnState();
        ValueChangeEvent.fire(this, value);
    }

    @UiHandler( "notDefined" )
    public void onNotDefinedStateSelected(ClickEvent event) {
        value = null;
        refreshBtnState();
        ValueChangeEvent.fire(this, value);
    }

    private void refreshBtnState() {
        yes.removeStyleName("active");
        no.removeStyleName("active");
        notDefined.removeStyleName("active");

        if (value == null) {
            notDefined.addStyleName("active");
        } else if (value) {
            yes.addStyleName("active");
        } else {
            no.addStyleName("active");
        }
    }

    public void setYesText(String text) {
        yes.setText(text);
    }

    public void setNotDefinedText(String text) {
        notDefined.setText(text);
    }

    public void setNoText(String text) {
        no.setText(text);
    }

    public void setYesEnsureDebugId(String debugId) {
        yes.ensureDebugId(debugId);
    }

    public void setNotDefinedEnsureDebugId(String debugId) {
        notDefined.ensureDebugId(debugId);
    }

    public void setNoEnsureDebugId(String debugId) {
        no.ensureDebugId(debugId);
    }

    @UiField
    Label yes;
    @UiField
    Label notDefined;
    @UiField
    Label no;

    private Boolean value;

    private static ThreeStateButtonUiBinder ourUiBinder = GWT.create(ThreeStateButtonUiBinder.class);
    interface ThreeStateButtonUiBinder extends UiBinder<HTMLPanel, ThreeStateButton> {}
}
