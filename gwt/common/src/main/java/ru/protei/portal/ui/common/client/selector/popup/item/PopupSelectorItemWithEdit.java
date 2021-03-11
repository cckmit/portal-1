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
import ru.protei.portal.ui.common.client.selector.SelectorItem;

import static ru.protei.portal.ui.common.client.common.UiConstants.Styles.HIDE;

public class PopupSelectorItemWithEdit<T> extends Composite implements HasValue<T>, HasAddHandlers, HasEditHandlers, HasClickHandlers, SelectorItem<T> {

    public PopupSelectorItemWithEdit() {
        initWidget(ourUiBinder.createAndBindUi(this));
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
    public void onItemClicked() {
        selectorItemHandler.onSelectorItemClicked(this);
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<T> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    @Override
    public HandlerRegistration addAddHandler(AddHandler handler) {
        return addHandler(handler, AddEvent.getType());
    }

    @Override
    public HandlerRegistration addEditHandler(EditHandler handler) {
        return addHandler(handler, EditEvent.getType());
    }

    @Override
    public HandlerRegistration addClickHandler(ClickHandler handler) {
        return addHandler(handler, ClickEvent.getType());
    }

    @Override
    public void addSelectorHandler(SelectorItemHandler<T> selectorItemHandler) {
        this.selectorItemHandler = selectorItemHandler;
    }

    @Override
    public void setElementHtml(String elementHtml) {
        panel.getElement().setInnerHTML( elementHtml );
    }

    @Override
    public HandlerRegistration addKeyUpHandler(KeyUpHandler handler) {
        return addHandler( handler, KeyUpEvent.getType() );
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
    private SelectorItemHandler<T> selectorItemHandler;
    private Long id;

    interface PopupSelectorItemWithBinder extends UiBinder<HTMLPanel, PopupSelectorItemWithEdit> {}
    private static PopupSelectorItemWithBinder ourUiBinder = GWT.create(PopupSelectorItemWithBinder.class);
}
