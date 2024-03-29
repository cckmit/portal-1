package ru.protei.portal.ui.common.client.widget.stringselect.input;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.stringselect.item.StringBox;

import java.util.LinkedList;
import java.util.List;

import static com.google.gwt.event.dom.client.KeyCodes.KEY_BACKSPACE;
import static com.google.gwt.event.dom.client.KeyCodes.KEY_ENTER;

/**
 * Виджет мультиселектора поля ввода
 */
public class StringSelectInput
        extends Composite
        implements HasKeyUpHandlers, HasValue<List<String>> {

    @Inject
    public StringSelectInput(Lang lang) {
        initWidget(ourUiBinder.createAndBindUi(this));
        input.getElement().setPropertyString("placeholder", lang.keywordInputPlaceholder());
        setUnfilledInputWidth();
    }

    @Override
    public void setValue(List<String> values, boolean fireEvents) {
        if (values == null) {
            values = new LinkedList<>();
        }
        this.values = values;

        itemContainer.clear();
        input.setText("");
        itemViews.clear();

        for (String val : values) {
            addItem(val);
        }

        scheduleAddInput(false);

        if (fireEvents) {
            ValueChangeEvent.fire(this, values);
        }
    }

    @Override
    public void setValue(List<String> value) {
        setValue(value, false);
        add.setVisible(false);
    }

    @Override
    public List<String> getValue() {
        return values;
    }

    @Override
    public HandlerRegistration addKeyUpHandler(KeyUpHandler keyUpHandler) {
        return input.addKeyUpHandler(keyUpHandler);
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<List<String>> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    public void setAddItemVisibility() {
        String text = input.getText();
        add.setVisible(!isEmpty(text));
    }

    public void setFocused() {
        input.setFocus(true);
    }

    public void setPlaceholder(String placeholder) {
        input.getElement().setPropertyString("placeholder", placeholder);
    }

    @UiHandler("input")
    public void onTextChanged(KeyDownEvent event) {
        //  Исключительно для BACKSPACE нужен KeyDownEvent
        String text = input.getText();
        add.setVisible(!isEmpty(text));

        if (KEY_BACKSPACE == event.getNativeKeyCode()) {
            if (isEmpty(text) && values.size() > 0) {
                removeItem(getLastView());
            }
        }
    }

    @UiHandler("input")
    public void onTextChanged(KeyUpEvent event) {
        String text = input.getText();
        add.setVisible(!isEmpty(text));

        if (KEY_ENTER == event.getNativeKeyCode()) {
            addValue(text);
        }
    }

    @UiHandler("input")
    public void onInputChanged(ChangeEvent event) {
        addValue(input.getText());
    }

    @UiHandler("add")
    public void onAddClicked(ClickEvent event) {
        addValue(input.getText());
    }

    public void setEnsureDebugId(String debugId) {
        input.ensureDebugId(debugId);
    }

    private void addValue(String val) {
        add.setVisible(false);
        if (isEmpty(val)) {
            return;
        }

        values.add(val);
        widgetContainer.remove(inputContainer);
        addItem(val);
        scheduleAddInput(true);
        input.setText("");

        ValueChangeEvent.fire(this, values);
    }

    private void addItem(String val) {
        if (!isAttached()) {
            return;
        }

        StringBox itemView = new StringBox();
        itemView.setValue(val);

        itemViews.add(itemView);

        itemView.setHandler(itemView1 -> {
            widgetContainer.remove(inputContainer);
            scheduleAddInput(true);

            removeItem(itemView1);
        });
        itemContainer.add(itemView);
    }

    private void removeItem(StringBox itemView) {
        values.remove(itemView.getValue());
        itemContainer.remove(itemView);
        itemViews.remove(itemView);

        ValueChangeEvent.fire(this, values);
    }

    private void setUnfilledInputWidth() {
        int unfilledWidth = itemContainer.getOffsetWidth();
        if (values != null && !values.isEmpty()) {
            Widget lastItemWidget = itemContainer.getWidget(values.size() - 1);
            int containerRightBorder = itemContainer.getAbsoluteLeft() + itemContainer.getOffsetWidth();
            int lastItemRightBorder = itemContainer.getAbsoluteLeft() + lastItemWidget.getAbsoluteLeft() + lastItemWidget.getOffsetWidth();

            unfilledWidth = containerRightBorder - lastItemRightBorder;
        }
        if (unfilledWidth < 180) {
            unfilledWidth = 180;
        }

        input.setWidth(unfilledWidth + "px");
    }

    private void scheduleAddInput(boolean focused) {
        if (!isAttached()) {
            return;
        }
        widgetContainer.add(inputContainer);
        input.setFocus(focused);
    }

    private StringBox getLastView() {
        return itemViews.get(itemViews.size() - 1);
    }

    private boolean isEmpty(String string) {
        return null == string || string.isEmpty();
    }

    List<String> values = new LinkedList<>();

    @UiField
    HTMLPanel itemContainer;
    @UiField
    TextBox input;
    @UiField
    HTMLPanel inputContainer;
    @UiField
    HTMLPanel widgetContainer;
    @UiField
    Anchor add;

    List<StringBox> itemViews = new LinkedList<>();

    interface SelectViewUiBinder extends UiBinder<HTMLPanel, StringSelectInput> {
    }

    private static SelectViewUiBinder ourUiBinder = GWT.create(SelectViewUiBinder.class);
}