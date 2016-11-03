package ru.protei.portal.ui.common.client.widget.selector.button;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import ru.protei.portal.ui.common.client.widget.selector.base.Selector;

/**
 * Вид селектора
 */
public class ButtonSelector<T> extends Selector<T> {

    public ButtonSelector() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void fillSelectorView(String selectedValue) {
        text.setInnerText(selectedValue == null ? "" : selectedValue);
    }

    @UiHandler( "button" )
    public void onBtnClick (ClickEvent event)
    {
        showPopup(button);
    }

    public void setHeader( String header ) {
        this.label.setInnerText( header );
    }

    @UiField
    HTMLPanel inputContainer;
    @UiField
    Button button;
    @UiField
    LabelElement label;
    @UiField
    SpanElement text;

    interface InputSelectorUiBinder extends UiBinder<HTMLPanel, ButtonSelector > { }
    private static InputSelectorUiBinder ourUiBinder = GWT.create(InputSelectorUiBinder.class);

}