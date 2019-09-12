package ru.protei.portal.ui.common.client.widget.form;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.base.Selector;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;

/**
 * Вид селектора
 */
public class FormSelector<T> extends Selector<T> implements HasValidable, HasEnabled{

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        initHandler();
    }

    @Override
    public void fillSelectorView(DisplayOption selectedValue) {
        if ( selectedValue == null ) {
            return;
        }

        String innerHtml = "";
        if ( selectedValue.getIcon() != null ) {
            innerHtml += "<i class='" + selectedValue.getIcon() + "'></i>";
        }
        innerHtml += selectedValue.getName() == null ? "" : selectedValue.getName();

        text.setInnerHTML(innerHtml);
    }

    @Override
    public void onClick( ClickEvent event ) {
        formContainer.removeStyleName(FOCUS_STYLENAME);
        super.onClick(event);

        if(isValidable)
            setValid( isValid() );
    }

    @Override
    public void setValue(T value) {
        super.setValue(value);
        if(isValidable)
            setValid( isValid() );
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

    public void setEnsureDebugId(String debugId) {
        formContainer.ensureDebugId(debugId);
    }

    private void initHandler() {
        formContainer.sinkEvents(Event.ONCLICK);
        formContainer.addHandler(event -> {
            formContainer.addStyleName(FOCUS_STYLENAME);
            showPopup(formContainer);
        }, ClickEvent.getType());

        popup.addCloseHandler(event -> formContainer.removeStyleName(FOCUS_STYLENAME));
    }

    @UiField
    HTMLPanel formContainer;
    @UiField
    LabelElement label;
    @UiField
    SpanElement text;

    private boolean isValidable;

    private static final String ERROR_STYLENAME ="has-error";
    private static final String REQUIRED_STYLENAME ="required";
    private static final String DISABLE_STYLENAME ="disabled";
    private static final String FOCUS_STYLENAME ="focused";

    interface InputSelectorUiBinder extends UiBinder<HTMLPanel, FormSelector> { }
    private static InputSelectorUiBinder ourUiBinder = GWT.create(InputSelectorUiBinder.class);

}