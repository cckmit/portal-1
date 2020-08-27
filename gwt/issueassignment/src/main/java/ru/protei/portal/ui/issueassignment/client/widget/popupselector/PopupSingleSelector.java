package ru.protei.portal.ui.issueassignment.client.widget.popupselector;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import ru.protei.portal.ui.common.client.selector.AbstractPopupSelector;
import ru.protei.portal.ui.common.client.selector.SelectorItem;
import ru.protei.portal.ui.common.client.selector.pageable.AbstractPageableSelector;
import ru.protei.portal.ui.common.client.selector.pageable.SingleValuePageableSelector;
import ru.protei.portal.ui.common.client.selector.popup.item.PopupSelectorItem;

public abstract class PopupSingleSelector<T> extends AbstractPopupSelector<T> implements HasValue<T> {

    @Override
    public T getValue() {
        return selector.getValue();
    }

    @Override
    public void setValue(T value) {
        setValue(value, false);
    }

    @Override
    public void setValue(T value, boolean fireEvents) {
        selector.setValue(value);
        if (fireEvents) {
            ValueChangeEvent.fire(this, selector.getValue());
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<T> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    @Override
    public boolean isEnabled() { return true; }

    @Override
    public void setEnabled(boolean enabled) {}

    @Override
    protected void onSelectionChanged() {
        getPopup().showNear(relative);
        ValueChangeEvent.fire(this, getValue());
    }

    @Override
    protected SelectorItem<T> makeSelectorItem(T element, String elementHtml) {
        PopupSelectorItem<T> item = new PopupSelectorItem<>();
        item.setName(elementHtml);
        return item;
    }

    @Override
    protected AbstractPageableSelector<T> getSelector() {
        return selector;
    }

    @Override
    public boolean isAttached() {
        return true;
    }

    public void setRelative(UIObject relative) {
        this.relative = relative;
    }

    public void fill() {
        getSelector().fillFromBegin(this);
    }

    protected SingleValuePageableSelector<T> selector = new SingleValuePageableSelector<>();
    protected UIObject relative;
}
