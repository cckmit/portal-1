package ru.protei.portal.ui.common.client.widget.stringselectform;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.TextBox;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.ui.common.client.widget.stringselectform.item.StringTagInputFormItem;

import java.util.LinkedList;
import java.util.List;

public class StringTagInputForm extends Composite implements HasValue<List<String>> {

    public StringTagInputForm() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public List<String> getValue() {
        return value;
    }

    @Override
    public void setValue(List<String> values) {
        setValue(values, false);
    }

    @Override
    public void setValue(List<String> values, boolean fireEvents) {
        value.clear();
        if (CollectionUtils.isNotEmpty(values)) {
            value.addAll(values);
        }
        render();
        if (fireEvents) {
            ValueChangeEvent.fire(this, value);
        }
    }

    public void setHeader(String text) {
        label.setInnerText(text);
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        render();
    }

    public void setOnlyUnique(boolean isOnlyUnique) {
        this.isOnlyUnique = isOnlyUnique;
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<List<String>> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    private void addValue(String text) {
        if (StringUtils.isEmpty(text)) return;
        if (isOnlyUnique && value.contains(text)) return;
        value.add(text);
        ValueChangeEvent.fire(this, value);
        render(true);
    }

    private void removeValue(String text) {
        value.remove(text);
        ValueChangeEvent.fire(this, value);
        render(true);
    }

    private void render() {
        render(false);
    }

    private void render(boolean setFocus) {
        items.clear();
        value.forEach(text -> items.add(makeItem(text)));

        TextBox input = makeInput();
        items.add(input);
        input.setFocus(setFocus);
    }

    private TextBox makeInput() {
        TextBox input = new TextBox();
        input.ensureDebugId("123");
        input.getElement().setAttribute("size", "1");
        if (StringUtils.isNotEmpty(placeholder)) {
            input.getElement().setAttribute("placeholder", placeholder);
        }
        input.addKeyUpHandler(event -> {
            if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                addValue(input.getText());
            }
        });
        return input;
    }

    private StringTagInputFormItem makeItem(String text) {
        StringTagInputFormItem item = new StringTagInputFormItem();
        item.setValue(text);
        item.setHandler(box -> removeValue(box.getValue()));
        return item;
    }

    @UiField
    LabelElement label;
    @UiField
    HTMLPanel items;

    private String placeholder;
    private boolean isOnlyUnique = true;
    private List<String> value = new LinkedList<>();

    private static StringTagInputFormUiBinder ourUiBinder = GWT.create(StringTagInputFormUiBinder.class);
    interface StringTagInputFormUiBinder extends UiBinder<HTMLPanel, StringTagInputForm> {}
}
