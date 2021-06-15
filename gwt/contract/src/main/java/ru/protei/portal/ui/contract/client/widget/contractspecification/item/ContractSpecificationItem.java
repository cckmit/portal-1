package ru.protei.portal.ui.contract.client.widget.contractspecification.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.*;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.LongBox;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_Currency;
import ru.protei.portal.core.model.ent.ContractSpecification;
import ru.protei.portal.core.model.struct.Money;
import ru.protei.portal.core.model.struct.MoneyWithCurrency;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.events.InputEvent;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.autoresizetextarea.ValiableAutoResizeTextArea;
import ru.protei.portal.ui.common.client.widget.money.MoneyCurrencyWidget;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;

import static ru.protei.portal.test.client.DebugIds.DEBUG_ID_ATTRIBUTE;
import static ru.protei.portal.ui.common.client.common.UiConstants.Styles.HIDE;

public class ContractSpecificationItem
        extends Composite
        implements TakesValue<ContractSpecification>,
        HasCloseHandlers<ContractSpecificationItem>,
        HasValueChangeHandlers<ContractSpecificationItem>
{
    @Inject
    public void onInit() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        setTestAttributes();
        clause.setRegexp(CrmConstants.Masks.CONTRACT_SPECIFICATION_CLAUSE);
        clause.getElement().setAttribute( "placeholder", lang.contractSpecificationClausePlaceholder() );
        text.getElement().setAttribute( "placeholder", lang.contractSpecificationTextPlaceholder() );
        quantity.getElement().setAttribute( "placeholder", lang.contractSpecificationQuantityPlaceholder() );
    }

    @Override
    public ContractSpecification getValue() {
        return value;
    }

    @Override
    public void setValue( ContractSpecification value ) {
        if (value == null) {
            value = new ContractSpecification();
        }
        this.value = value;

        clause.setValue( value.getClause() );
        text.setValue( value.getText() );
        quantity.setValue(value.getQuantity());
        costWithCurrency.setValue(new MoneyWithCurrency(value.getCost(), value.getCurrency()));
    }

    @Override
    public HandlerRegistration addCloseHandler(CloseHandler<ContractSpecificationItem> handler ) {
        return addHandler( handler, CloseEvent.getType() );
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<ContractSpecificationItem> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    @UiHandler( "remove" )
    public void onRemoveClicked( ClickEvent event ) {
        event.preventDefault();
        CloseEvent.fire( this, this );
    }

    @UiHandler( "clause" )
    public void onInputClause(InputEvent event) {
        value.setClause(clause.getValue());
        changeTimer.schedule(50);
    }

    @UiHandler( "text" )
    public void onChangeText(ValueChangeEvent<String> event) {
        value.setText(text.getValue());
        changeTimer.schedule(50);
    }

    @UiHandler("quantity")
    public void onQuantityText(ValueChangeEvent<Long> event) {
        value.setQuantity(quantity.getValue());
        changeTimer.schedule(50);
    }

    @UiHandler("costWithCurrency")
    public void onCostWithCurrencyChanged(ValueChangeEvent<MoneyWithCurrency> event) {
        MoneyWithCurrency val = costWithCurrency.getValue();
        Money cost = val != null ? val.getMoney() : null;
        En_Currency currency = val != null ? val.getCurrency() : null;
        value.setCost(cost);
        value.setCurrency(currency);
        changeTimer.schedule(50);
    }

    public void setError(boolean isError, String error) {
        markBoxAsError(isError);

        if (isError) {
            msg.removeClassName(HIDE);
            msg.setInnerText(error);
            return;
        }

        msg.addClassName(HIDE);
        msg.setInnerText(null);
    }

    public boolean isValid(){
        return clause.isValid() & text.isValid();
    }

    private void markBoxAsError(boolean isError) {
        if (isError) {
            root.addStyleName("has-error");
            return;
        }
        root.removeStyleName("has-error");
    }

    private void setTestAttributes() {
        root.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.CONTRACT.SPECIFICATION_ITEM.ITEM);
        clause.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.CONTRACT.SPECIFICATION_ITEM.CLAUSE_INPUT);
        text.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.CONTRACT.SPECIFICATION_ITEM.TEXT_INPUT);
        quantity.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.CONTRACT.SPECIFICATION_ITEM.QUANTITY_INPUT);
        remove.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.CONTRACT.SPECIFICATION_ITEM.REMOVE_BUTTON);
    }

    Timer changeTimer = new Timer(){
        @Override
        public void run() {
            ValueChangeEvent.fire(ContractSpecificationItem.this, ContractSpecificationItem.this);
        }
    };

    @UiField
    ValidableTextBox clause;
    @UiField
    ValiableAutoResizeTextArea text;
    @UiField
    LongBox quantity;
    @Inject
    @UiField(provided = true)
    MoneyCurrencyWidget costWithCurrency;
    @UiField
    Button remove;
    @UiField
    Element msg;

    @UiField
    Lang lang;
    @UiField
    HTMLPanel root;

    private ContractSpecification value = new ContractSpecification();

    interface ContractSpecificationUiBinder extends UiBinder< HTMLPanel, ContractSpecificationItem> {}
    private static ContractSpecificationUiBinder ourUiBinder = GWT.create( ContractSpecificationUiBinder.class );
}