package ru.protei.portal.ui.common.client.widget.companysubscription.list;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.protei.portal.core.model.ent.CompanySubscription;
import ru.protei.portal.core.model.struct.Pair;
import ru.protei.portal.ui.common.client.widget.companysubscription.group.CompanySubscriptionGroup;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;
import ru.protei.portal.ui.common.client.widget.companysubscription.item.CompanySubscriptionItem;

import java.util.*;

import static java.util.stream.Collectors.toMap;

/**
 * Список подписчиков на рассылку для компании
 */
public class CompanySubscriptionList
        extends Composite
        implements HasValue<List<CompanySubscription>>, HasValidable, HasEnabled
{

    @Inject
    public void init() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
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
        value.forEach(companySubscription -> {
            if (!groupSubscriptionMap.containsKey(Pair.of(companySubscription.getPlatformId(), companySubscription.getProductId()))) {
                groupSubscriptionMap.put(Pair.of(companySubscription.getPlatformId(), companySubscription.getProductId()), new ArrayList<>());
            }
            groupSubscriptionMap.get(Pair.of(companySubscription.getPlatformId(), companySubscription.getProductId())).add(companySubscription);
        });

        if (!groupSubscriptionMap.containsKey(Pair.of(null, null))) {
            addEmptyItem();
        }

        for (Map.Entry<Pair<Long, Long>, List<CompanySubscription>> pairListEntry : groupSubscriptionMap.entrySet()) {
            makeGroupAndFillValue(pairListEntry.getKey(), pairListEntry.getValue());
        }

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
        return modelToView.keySet().stream().allMatch(CompanySubscriptionItem::isValid);
    }

    public void clear() {
        container.clear();
        value.clear();
        modelToView.clear();
        groupSubscriptionMap.clear();
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
    public HandlerRegistration addValueChangeHandler( ValueChangeHandler<List<CompanySubscription>> handler ) {
        return addHandler( handler, ValueChangeEvent.getType() );
    }

    public void setPlatformFilter(Long companyId){
        this.companyId = companyId;
    }

    private void makeGroupAndFillValue(Pair<Long, Long> platformAndProduct, List<CompanySubscription> subscriptionsList ) {
        CompanySubscriptionGroup companySubscriptionGroupWidget = groupProvider.get();
        companySubscriptionGroupWidget.setPlatformSelector(platformAndProduct.getA());
        companySubscriptionGroupWidget.setProductSelector(platformAndProduct.getB());
        companySubscriptionGroupWidget.setValue(subscriptionsList);
        companySubscriptionGroupWidget.setPlatformFilter(companyId);
        container.add( companySubscriptionGroupWidget );
    }

    private void addEmptyItem() {
        CompanySubscription subscription = new CompanySubscription();
        groupSubscriptionMap.put(Pair.of(null,null), new ArrayList<>());

    }

    private List<CompanySubscription> prepareValue() {
        List<CompanySubscription> companySubscriptions = new ArrayList<>();

        for (List<CompanySubscription> companySubscriptionsList : groupSubscriptionMap.values()) {
            Collection<CompanySubscription> c = companySubscriptionsList.stream()
                    .filter(value -> value.getEmail() != null && !value.getEmail().isEmpty() )
                    .collect(toMap( CompanySubscription::getEmail, p -> p, (p, q) -> p)) //filter by unique email
                    .values();

            companySubscriptions.addAll(c);
        }

        return companySubscriptions;
    }

    @UiField
    HTMLPanel container;
    @Inject
    Provider<CompanySubscriptionGroup> groupProvider;

    private List<CompanySubscription> value = new ArrayList<>();
    private Map<CompanySubscriptionItem, CompanySubscription> modelToView = new HashMap<>();
    private Map<Pair<Long, Long>, List<CompanySubscription>> groupSubscriptionMap = new HashMap<>();
    private Long companyId;

    interface SubscriptionListUiBinder extends UiBinder< HTMLPanel, CompanySubscriptionList> {}
    private static SubscriptionListUiBinder ourUiBinder = GWT.create( SubscriptionListUiBinder.class );

}