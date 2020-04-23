package ru.protei.portal.ui.common.client.widget.selector.button;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasEnabled;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.base.Selector;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;

/**
 * Вид селектора
 * @deprecated  следует использовать {@link ButtonPopupSingleSelector}
 */
@Deprecated
public class ButtonSelector<T> extends Selector<T> implements HasValidable, HasEnabled{

    public ButtonSelector() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void fillSelectorView(DisplayOption selectedValue) {
        String value = selectedValue == null ? "" : selectedValue.getName() == null ? "" : selectedValue.getName();
        button.setTitle(value);
        text.setInnerText(value);
        text.setClassName("selector-val "+ (selectedValue != null && selectedValue.getStyle() != null? selectedValue.getStyle(): ""));
        icon.setClassName(selectedValue == null ? "" : selectedValue.getIcon() == null ? "" : selectedValue.getIcon());
    }

    @Override
    public void onClick( ClickEvent event ) {
        super.onClick(event);
        checkValueIsValid();
    }

    @Override
    public void setValue(T value) {
        super.setValue(value);
        checkValueIsValid();
    }

    @Override
    public boolean isValid(){
        return getValue() != null;
    }

    @Override
    public void setValid(boolean isValid){
        if(isValid)
            button.removeStyleName(ERROR_STYLENAME);
        else
            button.addStyleName(ERROR_STYLENAME);
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public void setEnabled(boolean b) {
        this.isEnabled = b;

        if(isEnabled){
            button.removeStyleName(INACTIVE_STYLE_NAME);
        }else {
            button.addStyleName(INACTIVE_STYLE_NAME);
        }
        button.setEnabled(isEnabled);
    }

    @UiHandler( "button" )
    public void onBtnClick (ClickEvent event) {
        showPopup(button);
    }

    public void setHeader( String header ) {
        this.label.removeClassName("hide");
        this.label.setInnerText( header );
    }

    public void setValidation(boolean isValidable){
        this.isValidable = isValidable;
    }

    public void addBtnStyleName(String style) {
        if (style == null || style.isEmpty()) {
            return;
        }
        button.addStyleName(style);
    }

    public void checkValueIsValid() {
        if(isValidable)
            setValid( isValid() );
    }

    public void setEnsureDebugId(String debugId) {
        button.ensureDebugId(debugId);
    }

    public void setEnsureDebugIdLabel(String debugId) {
        label.setId(DebugIds.DEBUG_ID_PREFIX + debugId);
    }

    public void setAttribute(String name, String value) {
        button.getElement().setAttribute(name, value);
    }

    @UiField
    HTMLPanel inputContainer;
    @UiField
    Button button;
    @UiField
    LabelElement label;
    @UiField
    SpanElement text;
    @UiField
    Element icon;

    private boolean isValidable;
    private static final String ERROR_STYLENAME ="has-error";
    private static final String REQUIRED_STYLE_NAME ="required";
    private static final String INACTIVE_STYLE_NAME="inactive";
    private boolean isEnabled;

    interface InputSelectorUiBinder extends UiBinder<HTMLPanel, ButtonSelector > { }
    private static InputSelectorUiBinder ourUiBinder = GWT.create(InputSelectorUiBinder.class);

}