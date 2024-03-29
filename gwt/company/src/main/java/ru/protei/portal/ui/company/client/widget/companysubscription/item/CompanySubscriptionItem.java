package ru.protei.portal.ui.company.client.widget.companysubscription.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
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
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.CompanySubscription;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.events.AddEvent;
import ru.protei.portal.ui.common.client.events.AddHandler;
import ru.protei.portal.ui.common.client.events.HasAddHandlers;
import ru.protei.portal.ui.common.client.widget.subscription.locale.LocaleButtonSelector;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;

import static ru.protei.portal.test.client.DebugIds.DEBUG_ID_ATTRIBUTE;


/**
 * Один элемент списка
 */
public class CompanySubscriptionItem
        extends Composite
        implements TakesValue<CompanySubscription>,
        HasCloseHandlers<CompanySubscriptionItem>,
        HasAddHandlers, HasEnabled
{
    @Inject
    public void onInit() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        email.setRegexp( CrmConstants.Masks.EMAIL );
        setTestAttributes();
    }

    @Override
    public CompanySubscription getValue() {
        value.setEmail( email.getValue());
        value.setLangCode( locale.getValue());

        return value;
    }

    @Override
    public void setValue( CompanySubscription value ) {
         if ( value == null ) {
            value = new CompanySubscription();
        }
        this.value = value;

        email.setValue( value.getEmail() );
        locale.setValue( value.getLangCode() );
    }

    @Override
    public boolean isEnabled() {
        return email.isEnabled() && locale.isEnabled();
    }

    @Override
    public void setEnabled(boolean b) {
        email.setEnabled(b);
        locale.setEnabled(b);
    }

    @Override
    public HandlerRegistration addCloseHandler( CloseHandler<CompanySubscriptionItem> handler ) {
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

    private void setTestAttributes() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }
        root.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.COMPANY.GROUP.ITEM.ROOT);
        locale.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.COMPANY.GROUP.ITEM.LOCALE);
        email.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.COMPANY.GROUP.ITEM.EMAIL);
    }

    @UiField
    HTMLPanel root;

    @UiField
    ValidableTextBox email;
    @Inject
    @UiField(provided = true)
    LocaleButtonSelector locale;

    private CompanySubscription value;


    interface PairItemUiBinder extends UiBinder< HTMLPanel, CompanySubscriptionItem> {}
    private static PairItemUiBinder ourUiBinder = GWT.create( PairItemUiBinder.class );
}