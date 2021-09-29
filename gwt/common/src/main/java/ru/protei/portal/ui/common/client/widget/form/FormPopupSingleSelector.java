package ru.protei.portal.ui.common.client.widget.form;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVisibility;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.events.HasAddHandlers;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.selector.AbstractPopupSelector;
import ru.protei.portal.ui.common.client.selector.SelectorItem;
import ru.protei.portal.ui.common.client.selector.SelectorPopup;
import ru.protei.portal.ui.common.client.selector.pageable.AbstractPageableSelector;
import ru.protei.portal.ui.common.client.selector.pageable.SelectorItemRenderer;
import ru.protei.portal.ui.common.client.selector.pageable.SingleValuePageableSelector;
import ru.protei.portal.ui.common.client.selector.popup.item.PopupSelectorItem;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;

import java.util.function.Supplier;

import static ru.protei.portal.ui.common.client.common.UiConstants.Styles.HIDE;

/**
 * Вид селектора
 */
public class FormPopupSingleSelector<T> extends AbstractPopupSelector<T>
        implements HasValidable, HasValue<T>, HasEnabled, HasVisibility, HasAddHandlers
{

    public FormPopupSingleSelector() {
        initWidget(ourUiBinder.createAndBindUi(this));
        initHandler();

        setSearchEnabled( true );
        setPageSize( CrmConstants.DEFAULT_SELECTOR_PAGE_SIZE );
        setEmptyListText( lang.emptySelectorList() );
        setEmptySearchText( lang.searchNoMatchesFound() );

        root.add(getPopup());
    }

    public interface SelectedValueRenderer<T> {
        String render(T value);
    }

    @Override
    protected void onSelectionChanged() {
        formContainer.removeStyleName(FOCUS_STYLENAME);
        T value = selector.getValue();
        showValue(value);

        if(isValidable)
            setValid( isValid() );

        ValueChangeEvent.fire(this, value);
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
        if(isValidable)
            setValid( isValid() );
    }

    @Override
    public T getValue() {
        return selector.getValue();
    }

    @Override
    public HandlerRegistration addValueChangeHandler( ValueChangeHandler<T> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    public void setAddButtonText(String addButtonText){
        getPopup().setAddButton( true, addButtonText );
    }

    public void setAddButtonVisible( boolean isVisible ) {
        setAddButtonVisibility( isVisible );
    }

    public void setValueRenderer(SelectedValueRenderer<T> renderer) {
        this.selectedValueRenderer = renderer;
    }

    @Override
    public boolean isValid(){
        return getValue() != null;
    }

    @Override
    public void setValid(boolean isValid){
        if(isValid)
            formContainer.removeStyleName(ERROR_STYLENAME);
        else
            formContainer.addStyleName(ERROR_STYLENAME);
    }

    @Override
    public boolean isEnabled() {
        return formContainer.getStyleName().contains(DISABLE_STYLENAME);
    }

    @Override
    public void setEnabled(boolean enabled) {
        if ( enabled ) {
            formContainer.removeStyleName(DISABLE_STYLENAME);
            return;
        }
        formContainer.addStyleName(DISABLE_STYLENAME);
    }

    public void setValidation(boolean isValidable){
        this.isValidable = isValidable;
    }

    public void setHeader( String header ) {
        label.removeClassName(HIDE);
        label.setInnerText( header );
    }

    public void setMandatory( boolean mandatory ) {
        if ( mandatory ) {
            formContainer.addStyleName(REQUIRED_STYLENAME);
            return;
        }
        formContainer.removeStyleName(REQUIRED_STYLENAME);
    }

    public void onShowPopupClicked(HTMLPanel button) {
        getPopup().getContainer().clear();
        String externalMessage = externalPopupMessage.get();
        if (externalMessage != null) {
            getPopup().setNoElements(true, externalMessage);
        } else {
            getSelector().fillFromBegin(this);
        }
        getPopup().showNear(button.getElement());
    }

    public void setEnsureDebugId(String debugId) {
        formContainer.ensureDebugId(debugId);
        text.setId(DebugIds.DEBUG_ID_PREFIX + debugId + "-text");
    }

    public void ensureLabelDebugId(String debugId) {
        label.setId(DebugIds.DEBUG_ID_PREFIX + debugId);
    }

    private void initHandler() {
        formContainer.sinkEvents(Event.ONCLICK);
        formContainer.addHandler(event -> {
            if (!getPopup().isVisible()) {
                formContainer.addStyleName(FOCUS_STYLENAME);
                onShowPopupClicked(formContainer);
            }
        }, ClickEvent.getType());
    }

    @Override
    public void onPopupHide(SelectorPopup selectorPopup ) {
        super.onPopupHide( selectorPopup );
        formContainer.removeStyleName(FOCUS_STYLENAME);
    }

    protected void showValue( T value) {
        text.setInnerHTML(selectedValueRenderer.render(value));
        text.setTitle(selector.makeElementName(value));
    }

    @Override
    protected SelectorItem<T> makeSelectorItem( T element, String elementHtml ) {
        PopupSelectorItem<T> item = new PopupSelectorItem<>();
        item.setName(elementHtml);
        item.setTitle(selector.makeElementName(element));
        return item;
    }

    public void setItemRendererWithDefault( String defaultValueIfNull, SelectorItemRenderer<T> selectorItemRenderer) {
        this.getSelector().setItemRenderer( new SelectorItemRenderer<T>() {
            @Override
            public String getElementName( T t ) {
                return t == null ? defaultValueIfNull : selectorItemRenderer.getElementName( t );
            }
        } );
    }

    public void setDefaultValue( String value ) {
        this.defaultValue = value;
    }

    public void setExternalPopupMessage(Supplier<String> externalPopupMessage) {
        this.externalPopupMessage = externalPopupMessage;
    }

    protected String defaultValue = null;

    @UiField
    HTMLPanel root;
    @UiField
    HTMLPanel formContainer;
    @UiField
    LabelElement label;
    @UiField
    SpanElement text;
    @UiField
    protected Lang lang;

    @Override
    protected AbstractPageableSelector<T> getSelector() {
        return selector;
    }
    private SingleValuePageableSelector<T> selector = new SingleValuePageableSelector<T>();
    private boolean isValidable;
    private SelectedValueRenderer<T> selectedValueRenderer = selector::makeElementHtml;
    private Supplier<String> externalPopupMessage = () -> null;

    private static final String ERROR_STYLENAME ="has-error";
    private static final String REQUIRED_STYLENAME ="required";
    private static final String DISABLE_STYLENAME ="disabled";
    private static final String FOCUS_STYLENAME ="focused";
    interface FormPopupSingleSelectorUiBinder extends UiBinder<HTMLPanel, FormPopupSingleSelector> { }
    private static FormPopupSingleSelectorUiBinder ourUiBinder = GWT.create(FormPopupSingleSelectorUiBinder.class);

}
