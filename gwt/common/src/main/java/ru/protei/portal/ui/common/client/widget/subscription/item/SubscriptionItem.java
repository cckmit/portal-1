package ru.protei.portal.ui.common.client.widget.subscription.item;

import com.google.gwt.core.client.GWT;
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
import ru.protei.portal.ui.common.client.events.AddEvent;
import ru.protei.portal.ui.common.client.events.AddHandler;
import ru.protei.portal.ui.common.client.events.HasAddHandlers;
import ru.protei.portal.ui.common.client.widget.subscription.model.Subscription;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;
import ru.protei.portal.ui.common.client.widget.subscription.locale.LocaleButtonSelector;


/**
 * Один элемент списка чекбоксов
 */
public class SubscriptionItem
        extends Composite
        implements TakesValue<Subscription>,
        HasCloseHandlers<SubscriptionItem>,
        HasAddHandlers
{
    @Inject
    public void onInit() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
    }

    @Override
    public Subscription getValue() {
        value.setEmail( email.getValue());
        value.setLangCode( locale.getValue());

        return value;
    }

    @Override
    public void setValue( Subscription value ) {
         if ( value == null ) {
            value = new Subscription();
        }
        this.value = value;

        email.setValue( value.getEmail() );
        locale.setValue( value.getLangCode() );
    }

    @Override
    public HandlerRegistration addCloseHandler( CloseHandler<SubscriptionItem> handler ) {
        return addHandler( handler, CloseEvent.getType() );
    }

    @Override
    public HandlerRegistration addAddHandler( AddHandler handler ) {
        return addHandler( handler, AddEvent.getType() );
    }

    @UiHandler( "email" )
    public void onEmailChanged(KeyUpEvent event) {
        if ( email.getValue().isEmpty() ) {
            CloseEvent.fire( this, this );
        }

        if ( isChangedStatusFromNewToFilled() ) {
            AddEvent.fire( this );
        }

        value.setEmail( email.getValue() );
    }

    @UiHandler( "locale" )
    public void onLocaleChanged(ValueChangeEvent<String> event) {
        value.setLangCode( locale.getValue() );
    }

    public boolean isValid(){
        return email.getValue().isEmpty() || email.isValid();
    }

    private boolean isChangedStatusFromNewToFilled() {
        return ( value.getEmail() == null || value.getEmail().trim().isEmpty() )
                && email.getValue().length() > 0;
    }

    @UiField
    ValidableTextBox email;
    @Inject
    @UiField(provided = true)
    LocaleButtonSelector locale;

    private Subscription value;

    interface PairItemUiBinder extends UiBinder< HTMLPanel, SubscriptionItem> {}
    private static PairItemUiBinder ourUiBinder = GWT.create( PairItemUiBinder.class );
}