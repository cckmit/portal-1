package ru.protei.portal.ui.common.client.widget.cleanablesearchbox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.events.HasInputHandlers;
import ru.protei.portal.ui.common.client.events.InputEvent;
import ru.protei.portal.ui.common.client.events.InputHandler;

import static ru.protei.portal.ui.common.client.common.UiConstants.Styles.HIDE;

public class CleanableSearchBox extends Composite implements HasValue<String>, HasEnabled, HasInputHandlers {

    public CleanableSearchBox() {
        initWidget(ourUiBinder.createAndBindUi(this));
        ensureDebugIds();
        textBox.addDomHandler(event -> isFocused = false, BlurEvent.getType());
        textBox.addDomHandler(event -> isFocused = true, FocusEvent.getType());
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

    public void setStyle(String style) {
        textBox.addStyleName( style );
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        textBox.setEnabled(enabled);
    }

    @UiHandler("textBox")
    public void onTextBoxKeyUp(KeyUpEvent event) {
        if (KeyUpEvent.isArrow(event.getNativeKeyCode())) {
            return;
        }
        toggleSearchAction();
        ValueChangeEvent.fire(CleanableSearchBox.this, getValue());
    }

    @UiHandler("textBoxAction")
    public void onTextBoxActionClick(ClickEvent event) {
        if (!enabled) {
            return;
        }
        event.preventDefault();
        if (HelperFunc.isNotEmpty(getValue())) {
            setValue("");
            ValueChangeEvent.fire(CleanableSearchBox.this, getValue());
            InputEvent.fire(this);
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    @Override
    public HandlerRegistration addInputHandler(InputHandler handler) {
        return addDomHandler(handler, InputEvent.getType());
    }

    public HandlerRegistration addChangeHandler(ChangeHandler handler) {
        return addDomHandler(handler, ChangeEvent.getType());
    }

    public HandlerRegistration addKeyDownHandler(KeyDownHandler handler) {
        return textBox.addKeyDownHandler(handler);
    }

    public HandlerRegistration addKeyPressHandler(KeyPressHandler handler) {
        return textBox.addKeyPressHandler(handler);
    }

    public HandlerRegistration addKeyUpHandler(KeyUpHandler handler) {
        return textBox.addKeyUpHandler(handler);
    }

    public void setFocus(boolean focused) {
        textBox.setFocus(focused);
    }

    public boolean isFocused() {
        return isFocused;
    }

    private void toggleSearchAction() {
        if (HelperFunc.isNotEmpty(getValue())) {
            textBoxAction.addStyleName("clear");
        } else {
            textBoxAction.removeStyleName("clear");
        }
    }

    public void setDebugIdTextBox(String debugId) {
        textBox.ensureDebugId(debugId);
    }

    public void setDebugIdAction(String debugId) {
        textBoxAction.ensureDebugId(debugId);
    }

    public void setDebugAttributeTextBox(String attribute) {
        textBox.getElement().setAttribute(DebugIds.DEBUG_ID_ATTRIBUTE, attribute);
    }

    public void setDebugAttributeAction(String attribute) {
        textBoxAction.getElement().setAttribute(DebugIds.DEBUG_ID_ATTRIBUTE, attribute);
    }

    public void setAddon(String addon) {
        this.addonText.setInnerText(addon);
        this.addon.removeClassName(HIDE);
        this.textBox.removeStyleName("rounded-left-3");
    }

    public void setAddonIcon(String icon) {
        this.addonIcon.setClassName(icon);
        this.addon.removeClassName(HIDE);
        this.textBox.removeStyleName("rounded-left-3");
    }

    private void ensureDebugIds() {
        textBox.getElement().setAttribute(DebugIds.DEBUG_ID_ATTRIBUTE, DebugIds.CLEANABLE_SEARCH_BOX.SEARCH_INPUT);
    }

    @UiField
    TextBox textBox;

    @UiField
    Anchor textBoxAction;

    @UiField
    DivElement addon;
    @UiField
    Element addonIcon;

    @UiField
    SpanElement addonText;


    private boolean enabled = true;
    private boolean isFocused;

    interface CleanableTextBoxViewUiBinder extends UiBinder<HTMLPanel, CleanableSearchBox> {}
    private static CleanableTextBoxViewUiBinder ourUiBinder = GWT.create( CleanableTextBoxViewUiBinder.class );
}
