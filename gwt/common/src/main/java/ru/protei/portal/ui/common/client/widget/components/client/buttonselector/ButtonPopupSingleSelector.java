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
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.components.client.selector.logic.AbstractPageableSelector;
import ru.protei.portal.ui.common.client.widget.components.client.selector.logic.SelectorItem;
import ru.protei.portal.ui.common.client.widget.components.client.selector.logic.single.SingleValueSelector;
import ru.protei.portal.ui.common.client.widget.components.client.selector.button.ValueButton;
import ru.protei.portal.ui.common.client.widget.components.client.selector.item.PopupSelectorItem;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;

/**
 * Cелектор c одиночным выбором, выпадающим списком, выбранные значения отображаются в кнопке.
 */
public class ButtonPopupSingleSelector<T> extends AbstractPopupSelector<T>
        implements HasValidable, HasValue<T>, HasEnabled, HasVisibility
{

    public ButtonPopupSingleSelector() {
        initWidget( bsUiBinder.createAndBindUi( this ) );
        setEmptyListText( lang.searchNoMatchesFound() );
        setSearchAutoFocus( true );
        setPageSize( CrmConstants.DEFAULT_SELECTOR_PAGE_SIZE );
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
        getSelector().fillFromBegin(this);
        getPopup().showNear(button);
    }

    public void setEnsureDebugIdLabel( String company ) {
    }

    public void setEnsureDebugId( String companySelector ) {
    }

    @Override
    public boolean isValid(){
        return getValue() != null;
    }

    @Override
    public void setValid(boolean isValid){
        if(isValid)
            button.removeStyleName(ERROR_STYLENAME);
        else
            button.addStyleName(ERROR_STYLENAME);
    }

    public void setValidation(boolean isValidable){
        this.isValidable = isValidable;
    }

    public void setHeader( String header ) {
        this.label.removeClassName("hide");
        this.label.setInnerText( header );
    }

    public void setButtonDebugId( String debugId ) {
        button.ensureDebugId( debugId );
    }

    public void setDisplayStyle( String style ) {
        button.setStyleName( style );
    }

    public boolean isEmpty() {
        return selector.getSelection().isEmpty();
    }

    @Override
    protected void onSelectionChanged() {
        T value = selector.getValue();
        showValue(value);
        getPopup().hide();
        ValueChangeEvent.fire(this, value);
    }


    protected void showValue(T value) {
        this.button.setValue(selector.makeElementName(value));
    }

    protected SelectorItem makeSelectorItem( T element, String elementHtml ) {
        PopupSelectorItem item = new PopupSelectorItem();
        item.setName(elementHtml);
        item.getElement().addClassName( UiConstants.Styles.TEXT_CENTER);
        return item;
    }

    @Override
    protected AbstractPageableSelector getSelector() {
        return selector;
    }

    private SingleValueSelector<T> selector = new SingleValueSelector<T>();

    @UiField
    protected ValueButton button;

    @UiField
    HTMLPanel root;
    @UiField
    LabelElement label;
    @UiField
    Lang lang;

    private static final String ERROR_STYLENAME ="has-error";
    private boolean isValidable;

    interface BlockSelectorUiBinder extends UiBinder<HTMLPanel, ButtonPopupSingleSelector> {
    }

    private static BlockSelectorUiBinder bsUiBinder = GWT.create(BlockSelectorUiBinder.class);

}
