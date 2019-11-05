package ru.protei.portal.ui.common.client.widget.components.client.buttonselector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVisibility;
import ru.protei.portal.ui.common.client.widget.components.client.selector.baseselector.AbstractPageableSelector;
import ru.protei.portal.ui.common.client.widget.components.client.selector.baseselector.SelectorItem;
import ru.protei.portal.ui.common.client.widget.components.client.selector.baseselector.multi.MultiValueSelector;
import ru.protei.portal.ui.common.client.widget.components.client.selector.button.ValueButton;
import ru.protei.portal.ui.common.client.widget.components.client.selector.item.PopupSelectorItem;

import java.util.Set;

/**
 * Cелектор c выпадающим списком, множественный выбор
 */
public class ButtonPopupMultiSelector<T> extends AbstractPopupSelector<T>
        implements HasValue<Set<T>>, HasEnabled, HasVisibility {


    public ButtonPopupMultiSelector() {
        initWidget(bsUiBinder.createAndBindUi(this));
    }

    @Override
    public void setValue(Set<T> value) {
        setValue(value, false);
    }

    @Override
    public void setValue(Set<T> value, boolean fireEvents) {
        selector.setValue(value);
        showValue(value);
        if (fireEvents) {
            ValueChangeEvent.fire(this, value);
        }
    }

    @Override
    public Set<T> getValue() {
        return selector.getValue();
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Set<T>> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    @Override
    public boolean isEnabled() {
        return button.isEnabled();
    }

    @Override
    public void setEnabled(boolean enabled) {
        button.setEnabled(enabled);

        if (enabled) {
            root.removeStyleName(DISABLED);
        } else {
            root.addStyleName(DISABLED);
        }
    }

    @UiHandler("button")
    public void onShowPopupClicked(ClickEvent event) {
        getPopup().getChildContainer().clear();
        getSelector().fillFromBegin(this);
        getPopup().showNear(button);
    }

    @Override
    protected void onSelectionChanged() {
        Set<T> value = selector.getValue();
        showValue(value);
        ValueChangeEvent.fire(this, value);
    }

    public boolean isEmpty() {
        return selector.getSelectionModel().isEmpty();
    }

    protected void showValue(Set<T> values) {
        StringBuilder sb = new StringBuilder();
        for (T value : values) {
            if (sb.length() > 0) sb.append(",");
            sb.append(selector.makeElementName(value));
        }
        this.button.setValue(sb.toString());
    }

    protected SelectorItem makeSelectorItem() {
        return new PopupSelectorItem();
    }

    @Override
    protected AbstractPageableSelector getSelector() {
        return selector;
    }

    protected MultiValueSelector<T> selector = new MultiValueSelector<T>();

    @UiField
    ValueButton button;

    @UiField
    HTMLPanel root;

    interface BlockSelectorUiBinder extends UiBinder<HTMLPanel, ButtonPopupMultiSelector> {
    }

    private static BlockSelectorUiBinder bsUiBinder = GWT.create(BlockSelectorUiBinder.class);

}
