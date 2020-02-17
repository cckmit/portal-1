package ru.protei.portal.ui.common.client.widget.stringselectform.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import ru.protei.portal.test.client.DebugIds;

public class StringTagInputFormItem extends Composite implements TakesValue<String> {

    public interface CloseHandler {
        void onClose(StringTagInputFormItem box);
    }

    public StringTagInputFormItem() {
        initWidget(ourUiBinder.createAndBindUi(this));
        remove.getElement().setAttribute("data-role", "remove");
        getElement().setAttribute(DebugIds.DEBUG_ID_ATTRIBUTE, DebugIds.STRING_TAG_INPUT_FORM.ITEM);
        remove.getElement().setAttribute(DebugIds.DEBUG_ID_ATTRIBUTE, DebugIds.STRING_TAG_INPUT_FORM.REMOVE_BUTTON);
    }

    @Override
    public void setValue(String value) {
        text.setInnerText(value);
    }

    @Override
    public String getValue() {
        return text.getInnerText();
    }

    public void setHandler(CloseHandler handler) {
        this.handler = handler;
    }

    @UiHandler("remove")
    public void onRemoveClicked(ClickEvent event) {
        event.preventDefault();
        if (handler != null) {
            handler.onClose(this);
        }
    }

    @UiField
    SpanElement text;
    @UiField
    Anchor remove;

    private CloseHandler handler;

    private static StringTagInputFormItemUiBinder ourUiBinder = GWT.create(StringTagInputFormItemUiBinder.class);
    interface StringTagInputFormItemUiBinder extends UiBinder<HTMLPanel, StringTagInputFormItem> {}
}
