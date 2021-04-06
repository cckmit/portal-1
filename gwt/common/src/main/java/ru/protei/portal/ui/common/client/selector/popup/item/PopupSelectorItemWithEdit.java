package ru.protei.portal.ui.common.client.selector.popup.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.InlineLabel;
import org.apache.poi.hssf.record.HideObjRecord;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.selector.AbstractSelectorItem;
import ru.protei.portal.ui.common.client.selector.SelectorItem;

import static ru.protei.portal.ui.common.client.common.UiConstants.Styles.HIDE;
import static ru.protei.portal.ui.common.client.selector.util.SelectorItemKeyboardKey.isSelectorItemKeyboardKey;

public class PopupSelectorItemWithEdit<T> extends Composite implements HasValue<T>, HasEditHandlers, SelectorItem<T> {
    public PopupSelectorItemWithEdit() {
        initWidget(ourUiBinder.createAndBindUi(this));

        addDomHandler(event -> {
            event.preventDefault();
            if (selectorItemHandler != null) {
                selectorItemHandler.onMouseClickEvent(this, event);
            }
        }, ClickEvent.getType());

        addDomHandler(event -> {
            if (!isSelectorItemKeyboardKey(event.getNativeKeyCode())) {
                return;
            }

            event.preventDefault();
            if (selectorItemHandler != null) {
                selectorItemHandler.onKeyboardButtonDown(this, event);
            }
        }, KeyDownEvent.getType());
    }

    @Override
    public void setValue(T value) {
        setValue(value, false);
    }

    @Override
    public void setValue(T value, boolean fireEvents) {
        this.value = value;

        if (fireEvents) {
            ValueChangeEvent.fire(this, value);
        }
    }

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<T> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    @Override
    public HandlerRegistration addEditHandler(EditHandler handler) {
        return addHandler(handler, EditEvent.getType());
    }

    @Override
    public void setElementHtml(String elementHtml) {
        panel.getElement().setInnerHTML( elementHtml );
    }

    @Override
    public HandlerRegistration addKeyUpHandler(KeyUpHandler handler) {
        return addHandler( handler, KeyUpEvent.getType() );
    }

    @Override
    public void addSelectorHandler(SelectorItemHandler<T> selectorItemHandler) {
        this.selectorItemHandler = selectorItemHandler;
    }

    public void setEditable(boolean isEditable) {
        editIcon.setStyleName(HIDE, !isEditable);
    }

    public void setName (String name) {
        text.setText(name);
    }

    public void setId (Long id) {
       this.id = id;
    }

    @UiHandler("editIcon")
    public void editClick(ClickEvent event) {
        event.stopPropagation();
        EditEvent.fire(this, id, text.getText());
    }

    @UiField
    HTMLPanel panel;
    @UiField
    InlineLabel text;
    @UiField
    InlineLabel editIcon;

    private T value = null;
    private Long id;

    private SelectorItemHandler<T> selectorItemHandler;

    interface PopupSelectorItemWithBinder extends UiBinder<HTMLPanel, PopupSelectorItemWithEdit> {}
    private static PopupSelectorItemWithBinder ourUiBinder = GWT.create(PopupSelectorItemWithBinder.class);
}
