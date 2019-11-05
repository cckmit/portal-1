package ru.protei.portal.ui.common.client.widget.components.client.buttonselector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.LabelElement;
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
import ru.protei.portal.ui.common.client.widget.components.client.selector.baseselector.single.SingleValueSelector;
import ru.protei.portal.ui.common.client.widget.components.client.selector.button.ValueButton;
import ru.protei.portal.ui.common.client.widget.components.client.selector.item.PopupSelectorItem;

/**
 * Cелектор c выпадающим списком, одиночный выбор
 */
public class ButtonPopupSingleSelector<T> extends AbstractPopupSelector<T>
        implements HasValue<T>, HasEnabled, HasVisibility
{

    public ButtonPopupSingleSelector() {
        initWidget(bsUiBinder.createAndBindUi(this));
    }

    @Override
    public void setValue(T value) {
        setValue(value, false);
    }

    @Override
    public void setValue(T value, boolean fireEvents) {
        selector.setValue(value);
        showValue(value);
        if (fireEvents) {
            ValueChangeEvent.fire(this, value);
        }
    }

    @Override
    public T getValue() {
        return selector.getValue();
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<T> handler) {
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
        selector.fillFromBegin(this);
        getPopup().showNear(button);
    }

    public void setEnsureDebugIdLabel( String company ) {
    }

    public void setEnsureDebugId( String companySelector ) {
    }

    public void setValidation(boolean isValidable){
        this.isValidable = isValidable;
    }

    public void setHeader( String header ) {
        this.label.removeClassName("hide");
        this.label.setInnerText( header );
    }

    @Override
    protected void onSelectionChanged() {
        T value = selector.getValue();
        showValue(value);
        ValueChangeEvent.fire(this, value);
    }

    public boolean isEmpty() {
        return selector.getSelectionModel().isEmpty();
    }

    protected void showValue(T value) {
        this.button.setValue(selector.makeElementName(value));
    }

    protected SelectorItem makeSelectorItem() {
        return new PopupSelectorItem();
    }

    @Override
    protected AbstractPageableSelector getSelector() {
        return selector;
    }

    private SingleValueSelector<T> selector = new SingleValueSelector<T>();

    @UiField
    ValueButton button;

    @UiField
    HTMLPanel root;
    @UiField
    LabelElement label;

    private boolean isValidable;

    interface BlockSelectorUiBinder extends UiBinder<HTMLPanel, ButtonPopupSingleSelector> {
    }

    private static BlockSelectorUiBinder bsUiBinder = GWT.create(BlockSelectorUiBinder.class);

}
