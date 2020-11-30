package ru.protei.portal.ui.common.client.widget.popupselector;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.RootPanel;
import ru.protei.portal.ui.common.client.selector.AbstractPopupSelector;
import ru.protei.portal.ui.common.client.selector.SelectorItem;
import ru.protei.portal.ui.common.client.selector.SelectorPopup;
import ru.protei.portal.ui.common.client.selector.pageable.AbstractPageableSelector;
import ru.protei.portal.ui.common.client.selector.pageable.SingleValuePageableSelector;
import ru.protei.portal.ui.common.client.selector.popup.item.PopupSelectorItem;
import ru.protei.portal.ui.common.client.widget.composite.popper.PopperComposite;

public class PopupSingleSelector<T> extends AbstractPopupSelector<T> implements HasValue<T> {

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
    public boolean isAttached() {
        return true;
    }

    public void setRelative(Element relative) {
        setRelative(relative, false);
    }

    public void setRelative(Element relative, boolean isAutoResize) {
        this.relative = relative;
        getPopup().setAutoResize(isAutoResize);

        if (relative.getParentElement() != null) {
            relative.getParentElement().appendChild(getPopup().asWidget().getElement());
        } else {
            Scheduler.get().scheduleDeferred((Command) () -> relative.getParentElement().appendChild(getPopup().asWidget().getElement()));
        }
    }

    public void fill() {
        getSelector().fillFromBegin(this);
    }

    public void showPopup() {
        getPopup().showNear(relative);
        RootPanel.get().add(getPopup());
    }

    public void showPopup(PopperComposite.Placement placement, int skidding, int distance) {
        getPopup().showNear(relative, placement, skidding, distance);
        RootPanel.get().add(getPopup());
    }

    public void hidePopup() {
        getPopup().hide();
        RootPanel.get().remove(getPopup());
    }

    public void clearPopup() {
        getPopup().getChildContainer().clear();
    }

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

    protected SingleValuePageableSelector<T> selector = new SingleValuePageableSelector<>();
    protected Element relative;
}
