package ru.protei.portal.ui.common.client.widget.commonmanager.item;

import com.google.gwt.core.client.GWT;
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
import ru.protei.portal.core.model.ent.CommonManager;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.client.events.AddEvent;
import ru.protei.portal.ui.common.client.events.AddHandler;
import ru.protei.portal.ui.common.client.events.HasAddHandlers;
import ru.protei.portal.ui.common.client.widget.selector.person.PersonButtonSelector;
import ru.protei.portal.ui.common.client.widget.selector.product.devunit.DevUnitButtonSelector;

import java.util.Objects;

public class CommonManagerItem
        extends Composite
        implements TakesValue<CommonManager>,
        HasCloseHandlers<CommonManagerItem>,
        HasAddHandlers, HasEnabled
{
    @Inject
    public void onInit() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
    }

    @Override
    public CommonManager getValue() {
        value.setProductId( product.getValue().getId() );
        value.setManagerId( commonManager.getValue().getId() );

        return value;
    }

    @Override
    public void setValue( CommonManager value ) {
         if ( value == null ) {
            value = new CommonManager();
        }
        this.value = value;

        product.setValue( new ProductShortView(value.getProductId(), value.getProductName(), value.getProductState() ));
        commonManager.setValue( new PersonShortView(value.getManagerName(), value.getManagerId()) );
    }

    @Override
    public boolean isEnabled() {
        return product.isEnabled() && commonManager.isEnabled();
    }

    @Override
    public void setEnabled(boolean b) {
        product.setEnabled(b);
        commonManager.setEnabled(b);
    }

    @Override
    public HandlerRegistration addCloseHandler( CloseHandler<CommonManagerItem> handler ) {
        return addHandler( handler, CloseEvent.getType() );
    }

    @Override
    public HandlerRegistration addAddHandler( AddHandler handler ) {
        return addHandler( handler, AddEvent.getType() );
    }

    @UiHandler( "product" )
    public void onProductChanged(ValueChangeEvent<ProductShortView> event) {
        if ( product.getValue() == null ) {
            CloseEvent.fire( this, this );
            return;
        }

        if ( isChangedProduct() ) {
            AddEvent.fire( this );

            value.setProductId( product.getValue().getId() );
        }
    }

    @UiHandler( "commonManager" )
    public void onCommonManagerChanged(ValueChangeEvent<PersonShortView> event) {
        value.setManagerId( commonManager.getValue().getId() );
    }

    public boolean isValid(){
        return commonManager.isValid();
    }

    private boolean isChangedProduct() {
        return !Objects.equals(value.getProductId(), product.getValue().getId());
    }

    @Inject
    @UiField(provided = true)
    DevUnitButtonSelector product;

    @Inject
    @UiField(provided = true)
    PersonButtonSelector commonManager;

    private CommonManager value;


    interface CommonManagerItemUiBinder extends UiBinder< HTMLPanel, CommonManagerItem> {}
    private static CommonManagerItemUiBinder ourUiBinder = GWT.create( CommonManagerItemUiBinder.class );
}