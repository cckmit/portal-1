package ru.protei.portal.ui.common.client.widget.selector.input;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TextBox;
import ru.protei.portal.ui.common.client.widget.selector.base.Selector;

/**
 * Вид селектора
 */
public class InputSelector<T> extends Selector<T> {

    public InputSelector() {

        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void fillSelectorView(String selectedValue) {
        input.setText(selectedValue == null ? "" : selectedValue);
    }

     @UiHandler( "button" )
    public void onBtnClick (ClickEvent event)
    {
        showPopup(input);
    }

    @UiField
    HTMLPanel inputContainer;
    @UiField
    TextBox input;
    @UiField
    Button button;
    @UiField
    LabelElement label;

    interface InputSelectorUiBinder extends UiBinder<HTMLPanel, InputSelector> { }
    private static InputSelectorUiBinder ourUiBinder = GWT.create(InputSelectorUiBinder.class);

}