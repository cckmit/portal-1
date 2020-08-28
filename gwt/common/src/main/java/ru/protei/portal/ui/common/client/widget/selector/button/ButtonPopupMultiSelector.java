package ru.protei.portal.ui.common.client.widget.selector.button;

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
import com.google.inject.Inject;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.selector.AbstractPopupSelector;
import ru.protei.portal.ui.common.client.selector.pageable.AbstractPageableSelector;
import ru.protei.portal.ui.common.client.selector.SelectorItem;
import ru.protei.portal.ui.common.client.selector.pageable.MultiValuePageableSelector;
import ru.protei.portal.ui.common.client.widget.selector.button.ValueButton;
import ru.protei.portal.ui.common.client.selector.popup.item.PopupSelectorItem;

import java.util.Set;

/**
 * Cелектор c выпадающим списком, множественный выбор
 */
public class ButtonPopupMultiSelector<T> extends AbstractPopupSelector<T>
        implements HasValue<Set<T>>, HasEnabled, HasVisibility {

    public ButtonPopupMultiSelector() {
        initWidget(bsUiBinder.createAndBindUi(this));
        setEmptyListText( lang.emptySelectorList() );
        setEmptySearchText( lang.searchNoMatchesFound() );
        setSearchAutoFocus( true );
        setPageSize( CrmConstants.DEFAULT_SELECTOR_PAGE_SIZE );
        root.add(getPopup());
    }

    @Override
    public void setValue(Set<T> value) {
        setValue(value, false);
    }

    @Override
    public void setValue(Set<T> value, boolean fireEvents) {
        selector.setValue(value);
        Set<T> selectorValue = selector.getValue();
        showValue(selectorValue);
        if (fireEvents) {
            ValueChangeEvent.fire(this, selectorValue);
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

    public void setButtonDebugId(String debugId) {
        button.ensureDebugId(debugId);
    }

    @UiHandler("button")
    public void onShowPopupClicked(ClickEvent event) {
        if (!getPopup().isVisible()) {
            getPopup().getChildContainer().clear();
            getSelector().fillFromBegin(this);
            getPopup().showNear(button.getElement());
        }
    }

    @Override
    protected void onSelectionChanged() {
        Set<T> value = selector.getValue();
        showValue(value);
        ValueChangeEvent.fire(this, value);
    }

    public boolean isEmpty() {
        return selector.getSelection().isEmpty();
    }

    protected void showValue(Set<T> values) {
        StringBuilder sb = new StringBuilder();
        for (T value : values) {
            if (sb.length() > 0) sb.append(",");
            sb.append(selector.makeElementName(value));
        }
        this.button.setValue(sb.toString());
    }

    protected SelectorItem<T> makeSelectorItem( T element, String elementHtml ) {
        PopupSelectorItem<T> item = new PopupSelectorItem<>();
        item.setName(elementHtml);
        return item;
    }

    @Override
    protected AbstractPageableSelector<T> getSelector() {
        return selector;
    }

    protected MultiValuePageableSelector<T> selector = new MultiValuePageableSelector<T>();

    @UiField
    ValueButton button;

    @UiField
    HTMLPanel root;

    @UiField
    Lang lang;

    interface BlockSelectorUiBinder extends UiBinder<HTMLPanel, ButtonPopupMultiSelector> {
    }

    private static BlockSelectorUiBinder bsUiBinder = GWT.create(BlockSelectorUiBinder.class);

}
