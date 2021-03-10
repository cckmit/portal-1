package ru.protei.portal.ui.common.client.widget.selector.button;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.selector.AbstractPopupSelector;
import ru.protei.portal.ui.common.client.selector.SelectorItem;
import ru.protei.portal.ui.common.client.selector.pageable.AbstractPageableSelector;
import ru.protei.portal.ui.common.client.selector.pageable.SingleValuePageableSelector;
import ru.protei.portal.ui.common.client.selector.popup.arrowselectable.ArrowSelectableSelectorPopup;
import ru.protei.portal.ui.common.client.selector.popup.item.PopupSelectorItem;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;

import static ru.protei.portal.ui.common.client.common.UiConstants.Styles.HIDE;

/**
 * Cелектор c одиночным выбором, выпадающим списком, выбранные значения отображаются в кнопке.
 */
public class ButtonPopupSingleSelector<T> extends AbstractPopupSelector<T>
        implements HasValidable, HasValue<T>, HasEnabled, HasVisibility
{

    public ButtonPopupSingleSelector() {
        initWidget( bsUiBinder.createAndBindUi( this ) );

        ArrowSelectableSelectorPopup popup
                = new ArrowSelectableSelectorPopup(KeyCodes.KEY_ENTER);
        setPopup(popup);

        setEmptyListText( lang.emptySelectorList() );
        setEmptySearchText( lang.searchNoMatchesFound() );
        setSearchAutoFocus( true );
        setPageSize( CrmConstants.DEFAULT_SELECTOR_PAGE_SIZE );
        root.add(getPopup());

        button.addDomHandler(event -> {
            if (event.getNativeKeyCode() == KeyCodes.KEY_DOWN) {
                event.preventDefault();
                popup.focus();
            }

            if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
                event.preventDefault();
                popup.hide();
            }
        }, KeyDownEvent.getType());
    }

    @Override
    public void setValue(T value) {
        setValue(value, false);
    }

    @Override
    public void setValue(T value, boolean fireEvents) {
        selector.setValue(value);
        T selectorValue = selector.getValue();
        showValue(selectorValue);
        if (fireEvents) {
            ValueChangeEvent.fire(this, selectorValue);
        }

        if (isValidable) {
            setValid(isValid());
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
        if (getPopup().isVisible()) {
            button.getElement().blur();
        } else {
            getPopup().getChildContainer().clear();
            getSelector().fillFromBegin(this);
            checkNoElements();
            getPopup().showNear(button.getElement());
        }
    }

    public void setEnsureDebugIdLabel( String company ) {
        label.setId(DebugIds.DEBUG_ID_PREFIX + company);
    }

    public void setEnsureDebugId( String companySelector ) {
        button.ensureDebugId(companySelector);
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
        this.label.removeClassName(HIDE);
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

    public void setDefaultValue( String value ) {
        this.defaultValue = value;
    }

    @Override
    protected void onSelectionChanged() {
        T value = selector.getValue();
        showValue(value);
        getPopup().hide();
        ValueChangeEvent.fire(this, value);

        if (isValidable) {
            setValid(isValid());
        }
    }


    protected void showValue(T value) {
        this.button.setValue(selector.makeElementName(value));
    }

    protected SelectorItem<T> makeSelectorItem( T element, String elementHtml ) {
        PopupSelectorItem<T> item = new PopupSelectorItem<>();
        item.setName(elementHtml);
        item.setTitle( elementHtml );
        return item;
    }

    @Override
    protected AbstractPageableSelector<T> getSelector() {
        return selector;
    }

    private SingleValuePageableSelector<T> selector = new SingleValuePageableSelector<T>();

    @UiField
    protected ValueButton button;

    @UiField
    HTMLPanel root;
    @UiField
    LabelElement label;
    @UiField
    protected Lang lang;

    private static final String ERROR_STYLENAME ="has-error";
    private boolean isValidable;

    protected String defaultValue = null;

    interface BlockSelectorUiBinder extends UiBinder<HTMLPanel, ButtonPopupSingleSelector> {
    }

    private static BlockSelectorUiBinder bsUiBinder = GWT.create(BlockSelectorUiBinder.class);

}
