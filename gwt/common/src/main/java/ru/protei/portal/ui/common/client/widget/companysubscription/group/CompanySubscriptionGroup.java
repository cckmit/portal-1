package ru.protei.portal.ui.common.client.widget.companysubscription.group;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.protei.portal.core.model.dict.En_DevUnitType;
import ru.protei.portal.core.model.ent.CompanySubscription;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.core.model.ent.Platform;
import ru.protei.portal.core.model.view.PlatformOption;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.ProductControllerAsync;
import ru.protei.portal.ui.common.client.service.SiteFolderControllerAsync;
import ru.protei.portal.ui.common.client.widget.companysubscription.item.CompanySubscriptionItem;
import ru.protei.portal.ui.common.client.widget.selector.platform.PlatformFormSelector;
import ru.protei.portal.ui.common.client.widget.selector.product.devunit.DevUnitFormSelector;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.*;

import static java.util.stream.Collectors.toMap;

public class CompanySubscriptionGroup  extends Composite
        implements HasValue<List<CompanySubscription>>, HasValidable, HasEnabled {

    @Inject
    public void init() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        platformSelector.setDefaultValue(lang.selectPlatform());
        productSelector.setDefaultValue(lang.selectIssueProduct());
        productSelector.setTypes(En_DevUnitType.PRODUCT);
    }

    @Override
    public List<CompanySubscription> getValue() {
        return prepareValue();
    }

    @Override
    public void setValue( List<CompanySubscription> value ) {
        setValue( value, false );
    }

    @Override
    public void setValue(List<CompanySubscription> values, boolean fireEvents ) {
        clear();
        this.value = values == null ? new ArrayList<>() : values;

        value.forEach( this :: makeItemAndFillValue );
        addEmptyItem();

        if(fireEvents) {
            ValueChangeEvent.fire( this, this.value );
        }
    }

    public void setPlatformFilter(Long companyId){
        platformSelector.setFilter(platformOption -> companyId != null && companyId.equals(platformOption.getCompanyId()));
    }

    public void setPlatformSelector(Long platformId){
        if (platformId == null) {
            platformSelector.setValue(null);
            return;
        }
        this.platformId = platformId;
        siteFolderController.getPlatform(platformId, new FluentCallback<Platform>()
                .withSuccess(platform -> platformSelector.setValue(new PlatformOption(platform.getName(), platformId)))
        );
    }

    public void setProductSelector(Long productId){
        if (productId == null) {
            productSelector.setValue(null);
            return;
        }
        this.productId = productId;
        productControllerAsync.getProduct(productId, new FluentCallback<DevUnit>()
                .withSuccess(product -> productSelector.setValue(new ProductShortView(productId, product.getName(), product.getStateId())))
        );
    }

    @Override
    public void setValid(boolean isValid) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isValid() {
        return modelToView.keySet().stream().allMatch(CompanySubscriptionItem::isValid);
    }

    private void clear() {
        container.clear();
        value.clear();
        modelToView.clear();
        platformSelector.setValue(null);
        productSelector.setValue(null);
    }

    @Override
    public boolean isEnabled() {
        return container.getStyleName().contains("disabled");
    }

    @Override
    public void setEnabled(boolean isEnabled) {
        container.addStyleName(isEnabled ? "" : "disabled");
        for (CompanySubscriptionItem item : modelToView.keySet()) {
            item.setEnabled(isEnabled);
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<List<CompanySubscription>> handler ) {
        return addHandler( handler, ValueChangeEvent.getType() );
    }

    @UiHandler("productSelector")
    public void onProductChanged(ValueChangeEvent<ProductShortView> event) {
        productId = event.getValue() == null ? null : event.getValue().getId();
        value.forEach(companySubscription -> companySubscription.setProductId(productId));
    }

    @UiHandler("platformSelector")
    public void onPlatformChanged(ValueChangeEvent<PlatformOption> event) {
        platformId = event.getValue() == null ? null : event.getValue().getId();
        value.forEach(companySubscription -> companySubscription.setPlatformId(platformId));
    }

    private void makeItemAndFillValue( CompanySubscription subscription ) {
        CompanySubscriptionItem companySubscriptionItem = itemProvider.get();
        companySubscriptionItem.setValue( subscription );
        companySubscriptionItem.addCloseHandler( event -> {
            if ( container.getWidgetCount() == 1 ) {
                return;
            }
            container.remove( event.getTarget() );
            CompanySubscription remove = modelToView.remove( event.getTarget() );
            value.remove( remove );
            boolean isHasEmptyItem = modelToView.values().stream().anyMatch(s -> s.getEmail() == null || s.getEmail().isEmpty());
            if(!isHasEmptyItem)
                addEmptyItem();
        } );

        companySubscriptionItem.addAddHandler( event -> {
            addEmptyItem();
            companySubscriptionItem.getValue().setPlatformId(platformId);
            companySubscriptionItem.getValue().setProductId(productId);
            value.add( companySubscriptionItem.getValue() );
        } );

        modelToView.put( companySubscriptionItem, subscription );
        container.add( companySubscriptionItem );
    }

    private void addEmptyItem() {
        CompanySubscription subscription = new CompanySubscription();
        makeItemAndFillValue( subscription );
    }

    private List<CompanySubscription> prepareValue() {
        Collection<CompanySubscription> c = value.stream()
                .filter(value -> value.getEmail() != null && !value.getEmail().isEmpty() )
                .collect(toMap( CompanySubscription::getEmail, p -> p, (p, q) -> p)) //filter by unique email
                .values();

        return new ArrayList<>(c);
    }

    @UiField
    HTMLPanel container;
    @Inject
    Provider<CompanySubscriptionItem> itemProvider;
    @Inject
    @UiField(provided = true)
    DevUnitFormSelector productSelector;
    @Inject
    @UiField(provided = true)
    PlatformFormSelector platformSelector;
    @Inject
    SiteFolderControllerAsync siteFolderController;
    @Inject
    ProductControllerAsync productControllerAsync;
    @UiField
    @Inject
    Lang lang;

    private List<CompanySubscription> value = new ArrayList<>();
    private Map<CompanySubscriptionItem, CompanySubscription> modelToView = new HashMap<>();
    private Long platformId;
    private Long productId;

    interface CompanySubscriptionGroupUiBinder extends UiBinder<HTMLPanel, CompanySubscriptionGroup> {}
    private static CompanySubscriptionGroupUiBinder ourUiBinder = GWT.create(CompanySubscriptionGroupUiBinder.class);
}