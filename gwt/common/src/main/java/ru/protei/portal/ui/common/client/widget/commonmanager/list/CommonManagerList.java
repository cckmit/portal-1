package ru.protei.portal.ui.common.client.widget.commonmanager.list;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.protei.portal.core.model.ent.CommonManager;
import ru.protei.portal.ui.common.client.widget.commonmanager.item.CommonManagerItem;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommonManagerList
        extends Composite
        implements HasValue<List<CommonManager>>, HasValidable, HasEnabled
{
    public CommonManagerList() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
//        ensureDebugId(DebugIds.PRODUCT.SUBSCRIPTIONS); todo
    }

    @Override
    public List<CommonManager> getValue() {
        return value;
    }

    @Override
    public void setValue( List<CommonManager> value ) {
        setValue( value, false );
    }

    @Override
    public void setValue( List<CommonManager > values, boolean fireEvents ) {
        clear();
        this.value = values == null ? new ArrayList<>() : values;
        value.forEach( this :: makeItemAndFillValue );
        addEmptyItem();

        if(fireEvents) {
            ValueChangeEvent.fire( this, this.value );
        }
    }

    @Override
    public void setValid(boolean isValid) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isValid() {
        return modelToView.keySet().stream().allMatch(CommonManagerItem::isValid);
    }

    public void clear() {
        container.clear();
        value.clear();
        modelToView.clear();
    }

    @Override
    public boolean isEnabled() {
        return container.getStyleName().contains("disabled");
    }

    @Override
    public void setEnabled(boolean isEnabled) {
        container.addStyleName(isEnabled ? "" : "disabled");
        for (CommonManagerItem item : modelToView.keySet()) {
            item.setEnabled(isEnabled);
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler( ValueChangeHandler<List<CommonManager>> handler ) {
        return addHandler( handler, ValueChangeEvent.getType() );
    }

    private void makeItemAndFillValue( CommonManager commonManager ) {
        CommonManagerItem itemWidget = itemProvider.get();
        itemWidget.setValue( commonManager );
        itemWidget.addCloseHandler( event -> {
            if ( container.getWidgetCount() == 1 ) {
                return;
            }
            container.remove( event.getTarget() );
            CommonManager remove = modelToView.remove( event.getTarget() );
            value.remove( remove );
        } );

        itemWidget.addAddHandler( event -> {
            addEmptyItem();
            value.add( itemWidget.getValue() );
        } );

        modelToView.put( itemWidget, commonManager );
        container.add( itemWidget );
    }

    private void addEmptyItem() {
        CommonManager subscription = new CommonManager();
        makeItemAndFillValue( subscription );
    }

    @UiField
    HTMLPanel container;
    @Inject
    Provider<CommonManagerItem> itemProvider;

    List<CommonManager> value = new ArrayList<>();
    Map<CommonManagerItem, CommonManager> modelToView = new HashMap<>();

    interface SubscriptionListUiBinder extends UiBinder< HTMLPanel, CommonManagerList> {}
    private static SubscriptionListUiBinder ourUiBinder = GWT.create( SubscriptionListUiBinder.class );

}