package ru.protei.portal.ui.common.client.widget.commonmanager.list;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.protei.portal.core.model.ent.CommonManager;
import ru.protei.portal.ui.common.client.widget.commonmanager.item.CommonManagerItem;

import java.util.*;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.CollectionUtils.stream;

public class CommonManagerList
        extends Composite
        implements HasValue<List<CommonManager>>
{
    @Inject
    public void onInit() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
    }

    @Override
    public List<CommonManager> getValue() {
        return prepareValue();
    }

    private List<CommonManager> prepareValue() {
        return stream(modelToView.values()).filter(commonManager -> commonManager.getManagerId() != null)
                .collect(Collectors.toList());
    }

    @Override
    public void setValue( List<CommonManager> value ) {
        setValue( value, false );
    }

    @Override
    public void setValue( List<CommonManager > values, boolean fireEvents ) {
        clear();
        if (values != null) {
            values.forEach(this::addItemAndFillValue);
        }
        addEmptyItem();

        if(fireEvents) {
            ValueChangeEvent.fire(this, new ArrayList<>(this.modelToView.values()));
        }
    }

    public void clear() {
        container.clear();
        modelToView.clear();
    }

    @Override
    public HandlerRegistration addValueChangeHandler( ValueChangeHandler<List<CommonManager>> handler ) {
        return addHandler( handler, ValueChangeEvent.getType() );
    }

    private void addItemAndFillValue(CommonManager commonManager ) {
        CommonManagerItem itemWidget = itemProvider.get();
        itemWidget.setValue( commonManager );
        itemWidget.addCloseHandler( event -> {
            if ( container.getWidgetCount() == 1 ) {
                return;
            }
            container.remove( event.getTarget() );
            modelToView.remove( event.getTarget() );
            setProductFilter();
        } );

        itemWidget.addAddHandler( event -> {
            if (!hasEmptyValue()) {
                addEmptyItem();
            }
        });

        modelToView.put( itemWidget, commonManager );
        container.add( itemWidget );

        setProductFilter();
    }

    private void addEmptyItem() {
        CommonManager manager = new CommonManager();
        addItemAndFillValue( manager );
    }

    private void setProductFilter() {
        Set<Long> productIds = makeProductId();
        modelToView.keySet().forEach(item -> item.setSelectedProductFilter(productIds));
    }

    private Set<Long> makeProductId() {
        return stream(modelToView.values()).map(CommonManager::getProductId).collect(Collectors.toSet());
    }

    private boolean hasEmptyValue() {
        return stream(modelToView.values())
                .anyMatch(commonManager -> commonManager.getManagerId() == null && commonManager.getProductId() == null);
    }

    @UiField
    HTMLPanel container;
    @Inject
    Provider<CommonManagerItem> itemProvider;

    Map<CommonManagerItem, CommonManager> modelToView = new HashMap<>();

    interface CommonManagerListUiBinder extends UiBinder< HTMLPanel, CommonManagerList> {}
    private static CommonManagerListUiBinder ourUiBinder = GWT.create( CommonManagerListUiBinder.class );

}