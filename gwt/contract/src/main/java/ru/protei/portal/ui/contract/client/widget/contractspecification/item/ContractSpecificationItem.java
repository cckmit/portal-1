package ru.protei.portal.ui.contract.client.widget.contractspecification.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.ContractSpecification;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.Lang;

import static ru.protei.portal.test.client.DebugIds.DEBUG_ID_ATTRIBUTE;

public class ContractSpecificationItem
        extends Composite
        implements TakesValue<ContractSpecification>, HasCloseHandlers<ContractSpecificationItem>
{
    @Inject
    public void onInit() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        setTestAttributes();
        text.getElement().setAttribute( "placeholder", lang.contractPaymentCommentPlaceholder() );
    }

    @Override
    public ContractSpecification getValue() {
        return value;
    }

    @Override
    public void setValue( ContractSpecification value ) {
         if ( value == null ) {
            value = new ContractSpecification();
        }
        this.value = value;

        clause.setValue( value.getClause() );
        text.setValue( value.getText() );
    }

    @Override
    public HandlerRegistration addCloseHandler(CloseHandler<ContractSpecificationItem> handler ) {
        return addHandler( handler, CloseEvent.getType() );
    }

    @UiHandler( "remove" )
    public void onRemoveClicked( ClickEvent event ) {
        event.preventDefault();
        CloseEvent.fire( this, this );
    }

    @UiHandler( "clause" )
    public void onChangeClause(KeyUpEvent event) {
        value.setClause(clause.getValue());
    }

    @UiHandler( "text" )
    public void onChangeText(KeyUpEvent event) {
        value.setText(text.getValue());
    }

    private void setTestAttributes() {
        root.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.CONTRACT.SPECIFICATION_ITEM.ITEM);
        clause.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.CONTRACT.SPECIFICATION_ITEM.CLAUSE_INPUT);
        text.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.CONTRACT.SPECIFICATION_ITEM.TEXT_INPUT);
        remove.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.CONTRACT.SPECIFICATION_ITEM.REMOVE_BUTTON);
    }

    @UiField
    TextBox clause;
    @UiField
    TextBox text;
    @UiField
    Button remove;

    @UiField
    Lang lang;
    @UiField
    HTMLPanel root;

    private ContractSpecification value = new ContractSpecification();

    interface ContractSpecificationUiBinder extends UiBinder< HTMLPanel, ContractSpecificationItem> {}
    private static ContractSpecificationUiBinder ourUiBinder = GWT.create( ContractSpecificationUiBinder.class );
}