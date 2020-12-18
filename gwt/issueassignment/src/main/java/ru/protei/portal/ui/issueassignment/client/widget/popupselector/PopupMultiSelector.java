package ru.protei.portal.ui.issueassignment.client.widget.popupselector;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasValue;
import ru.protei.portal.ui.common.client.selector.AbstractPopupSelector;
import ru.protei.portal.ui.common.client.selector.SelectorItem;
import ru.protei.portal.ui.common.client.selector.pageable.AbstractPageableSelector;
import ru.protei.portal.ui.common.client.selector.pageable.MultiValuePageableSelector;
import ru.protei.portal.ui.common.client.widget.selector.item.PopupSelectableItem;

import java.util.Set;

public abstract class PopupMultiSelector<T> extends AbstractPopupSelector<T> implements HasValue<Set<T>> {

    @Override
    public Set<T> getValue() {
        return selector.getValue();
    }

    @Override
    public void setValue(Set<T> value) {
        setValue(value, false);
    }

    @Override
    public void setValue(Set<T> value, boolean fireEvents) {
        selector.setValue(value);
        if (fireEvents) {
            ValueChangeEvent.fire(this, selector.getValue());
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Set<T>> handler) {
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
    protected SelectorItem<T> makeSelectorItem( T element, String elementHtml ) {
        PopupSelectableItem<T> item = new PopupSelectableItem<>();
        item.setElementHtml(elementHtml);
        item.setSelected(isSelected(element));
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

    protected MultiValuePageableSelector<T> selector = new MultiValuePageableSelector<>();
    protected Element relative;
}
