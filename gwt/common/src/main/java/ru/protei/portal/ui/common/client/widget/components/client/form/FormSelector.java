package ru.protei.portal.ui.common.client.widget.components.client.form;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.inject.Inject;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.widget.components.client.buttonselector.AbstractPopupSelector;
import ru.protei.portal.ui.common.client.widget.components.client.selector.baseselector.AbstractPageableSelector;
import ru.protei.portal.ui.common.client.widget.components.client.selector.baseselector.SelectorItem;
import ru.protei.portal.ui.common.client.widget.components.client.selector.baseselector.single.SingleValueSelector;
import ru.protei.portal.ui.common.client.widget.components.client.selector.item.PopupSelectorItem;
import ru.protei.portal.ui.common.client.widget.components.client.selector.popup.SelectorPopup;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;

/**
 * Вид селектора
 */
public class FormSelector<T> extends AbstractPopupSelector<T>
        implements HasValidable, HasValue<T>, HasEnabled, HasVisibility {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        initHandler();
    }

    @Override
    protected void onSelectionChanged() {
        formContainer.removeStyleName(FOCUS_STYLENAME);
        T value = selector.getValue();
        showValue(value);

        if(isValidable)
            setValid( isValid() );

        ValueChangeEvent.fire(this, value);
    }

    @Override
    public void setValue(T value) {
        setValue(value, false);
    }

    @Override
    public void setValue(T value, boolean fireEvents) {
        selector.setValue(value);
        showValue(value);
        if (fireEvents) {
            ValueChangeEvent.fire(this, value);
        }
        if(isValidable)
            setValid( isValid() );
    }

    @Override
    public T getValue() {
        return selector.getValue();
    }

    @Override
    public HandlerRegistration addValueChangeHandler( ValueChangeHandler<T> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    @Override
    public boolean isValid(){
        return getValue() != null;
    }

    @Override
    public void setValid(boolean isValid){
        if(isValid)
            formContainer.removeStyleName(ERROR_STYLENAME);
        else
            formContainer.addStyleName(ERROR_STYLENAME);
    }

    @Override
    public boolean isEnabled() {
        return formContainer.getStyleName().contains(DISABLE_STYLENAME);
    }

    @Override
    public void setEnabled(boolean enabled) {
        if ( enabled ) {
            formContainer.removeStyleName(DISABLE_STYLENAME);
            return;
        }
        formContainer.addStyleName(DISABLE_STYLENAME);
    }

    public void setValidation(boolean isValidable){
        this.isValidable = isValidable;
    }

    public void setHeader( String header ) {
        label.removeClassName("hide");
        label.setInnerText( header );
    }

    public void setMandatory( boolean mandatory ) {
        if ( mandatory ) {
            formContainer.addStyleName(REQUIRED_STYLENAME);
            return;
        }
        formContainer.removeStyleName(REQUIRED_STYLENAME);
    }

    public void onShowPopupClicked( HTMLPanel button) {
        getPopup().getChildContainer().clear();
        selector.fillFromBegin(this);
        getPopup().showNear(button);
    }

    public void setEnsureDebugId(String debugId) {
        formContainer.ensureDebugId(debugId);
        text.setId(DebugIds.DEBUG_ID_PREFIX + debugId + "-text");
    }

    public void ensureLabelDebugId(String debugId) {
        label.setId(DebugIds.DEBUG_ID_PREFIX + debugId);
    }

    private void initHandler() {
        formContainer.sinkEvents(Event.ONCLICK);
        formContainer.addHandler(event -> {
            formContainer.addStyleName(FOCUS_STYLENAME);
            onShowPopupClicked(formContainer);
        }, ClickEvent.getType());

    }

    @Override
    public void onPopupUnload( SelectorPopup selectorPopup ) {
        super.onPopupUnload( selectorPopup );
        formContainer.removeStyleName(FOCUS_STYLENAME);
    }

    protected void showValue( T value) {
        this.text.setInnerHTML(selector.makeElementHtml(value));
    }

    protected SelectorItem makeSelectorItem( T element, String elementHtml ) {
        PopupSelectorItem item = new PopupSelectorItem();
        item.setName(elementHtml);
        return item;
    }


    @UiField
    HTMLPanel formContainer;
    @UiField
    LabelElement label;
    @UiField
    SpanElement text;

    @Override
    protected AbstractPageableSelector getSelector() {
        return selector;
    }
    private SingleValueSelector<T> selector = new SingleValueSelector<T>();
    private boolean isValidable;

    private static final String ERROR_STYLENAME ="has-error";
    private static final String REQUIRED_STYLENAME ="required";
    private static final String DISABLE_STYLENAME ="disabled";
    private static final String FOCUS_STYLENAME ="focused";

    interface InputSelectorUiBinder extends UiBinder<HTMLPanel, FormSelector> { }
    private static InputSelectorUiBinder ourUiBinder = GWT.create(InputSelectorUiBinder.class);

}