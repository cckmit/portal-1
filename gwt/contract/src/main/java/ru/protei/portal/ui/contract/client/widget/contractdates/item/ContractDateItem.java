package ru.protei.portal.ui.contract.client.widget.contractdates.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.brainworm.factory.core.datetimepicker.client.view.input.single.SinglePicker;
import ru.protei.portal.core.model.dict.En_ContractDatesType;
import ru.protei.portal.core.model.ent.ContractDate;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.switcher.Switcher;
import ru.protei.portal.ui.contract.client.widget.selector.ContractDatesTypeSelector;

import java.util.Date;

import static ru.protei.portal.test.client.DebugIds.DEBUG_ID_ATTRIBUTE;

public class ContractDateItem
        extends Composite
        implements TakesValue<ContractDate>, HasCloseHandlers<ContractDateItem>
{
    @Inject
    public void onInit() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        setTestAttributes();
        comment.getElement().setAttribute( "placeholder", lang.contractPaymentCommentPlaceholder() );
    }

    @Override
    public ContractDate getValue() {
        return value;
    }

    @Override
    public void setValue( ContractDate value ) {
         if ( value == null ) {
            value = new ContractDate();
        }
        this.value = value;

        type.setValue( value.getType() );
        date.setValue( value.getDate() );
        comment.setValue( value.getComment() );
        notify.setValue( value.isNotify() );
    }

    @Override
    public HandlerRegistration addCloseHandler(CloseHandler<ContractDateItem> handler ) {
        return addHandler( handler, CloseEvent.getType() );
    }

    @UiHandler( "remove" )
    public void onRemoveClicked( ClickEvent event ) {
        event.preventDefault();
        CloseEvent.fire( this, this );
    }

    @UiHandler( "comment" )
    public void onChangeComment(KeyUpEvent event) {
        value.setComment(comment.getValue());
    }

    @UiHandler( "date" )
    public void onChangeDate(ValueChangeEvent<Date> event) {
        value.setDate(date.getValue());
    }

    @UiHandler( "type" )
    public void onChangeType(ValueChangeEvent<En_ContractDatesType> event) {
        value.setType(type.getValue());
    }

    @UiHandler( "notify" )
    public void onChangeNotify(ValueChangeEvent<Boolean> event) {
        value.setNotify(notify.getValue());
    }

    private void setTestAttributes() {
        root.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.CONTRACT.DATE_ITEM.ITEM);
        type.setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.CONTRACT.DATE_ITEM.TYPE_BUTTON);
        date.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.CONTRACT.DATE_ITEM.DATE_INPUT);
        comment.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.CONTRACT.DATE_ITEM.COMMENT_INPUT);
        notify.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.CONTRACT.DATE_ITEM.NOTIFY_SWITCHER);
        remove.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.CONTRACT.DATE_ITEM.REMOVE_BUTTON);
    }

    @UiField
    TextBox comment;
    @UiField
    Button remove;
    @Inject
    @UiField(provided = true)
    ContractDatesTypeSelector type;
    @Inject
    @UiField(provided = true)
    SinglePicker date;
    @UiField
    Switcher notify;

    @UiField
    Lang lang;
    @UiField
    HTMLPanel root;

    private ContractDate value = new ContractDate();

    interface PairItemUiBinder extends UiBinder< HTMLPanel, ContractDateItem> {}
    private static PairItemUiBinder ourUiBinder = GWT.create( PairItemUiBinder.class );
}