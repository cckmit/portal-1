package ru.protei.portal.ui.common.client.widget.companysubscription.list;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.protei.portal.core.model.ent.CompanySubscription;
import ru.protei.portal.core.model.struct.Pair;
import ru.protei.portal.ui.common.client.events.ConfirmDialogEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.companysubscription.group.CompanySubscriptionGroup;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;
import ru.protei.portal.ui.common.client.widget.companysubscription.item.CompanySubscriptionItem;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

/**
 * Список подписчиков на рассылку для компании
 */
public abstract class CompanySubscriptionList
        extends Composite
        implements HasValue<List<CompanySubscription>>, HasValidable, HasEnabled, Activity
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
            makeGroupAndFillValue(pairListEntry.getKey(), pairListEntry.getValue(), null);
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
        groupContainer.clear();
        value.clear();
        modelToView.clear();
        groupSubscriptionMap.clear();
        newGroups.clear();
    }

    @Override
    public boolean isEnabled() {
        return groupContainer.getStyleName().contains("disabled");
    }

    @Override
    public void setEnabled(boolean isEnabled) {
        groupContainer.addStyleName(isEnabled ? "" : "disabled");
        for (CompanySubscriptionItem item : modelToView.keySet()) {
            item.setEnabled(isEnabled);
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler( ValueChangeHandler<List<CompanySubscription>> handler ) {
        return addHandler( handler, ValueChangeEvent.getType() );
    }

    @UiHandler("addButton")
    public void onAddClicked (ClickEvent event){
        Pair<Pair<Long, Long>, List<CompanySubscription>> newGroup = Pair.of(Pair.of(null,null), new ArrayList<>());
        newGroups.add(newGroup);
        makeGroupAndFillValue(newGroup.getA(), newGroup.getB(), newGroups.size()-1);
    }

    public void setPlatformFilter(Long companyId){
        this.companyId = companyId;
    }

    private void makeGroupAndFillValue(Pair<Long, Long> platformAndProduct, List<CompanySubscription> subscriptionsList, Integer groupIndex) {
        CompanySubscriptionGroup companySubscriptionGroupWidget = groupProvider.get();
        companySubscriptionGroupWidget.setPlatformSelector(platformAndProduct.getA());
        companySubscriptionGroupWidget.setProductSelector(platformAndProduct.getB());
        companySubscriptionGroupWidget.setValue(subscriptionsList);
        companySubscriptionGroupWidget.setPlatformFilter(companyId);
        groupContainer.add( companySubscriptionGroupWidget );

        companySubscriptionGroupWidget.addCloseHandler(event -> {
            fireEvent(new ConfirmDialogEvents.Show(lang.companySubscriptionGroupRemoveConfirmMessage(), () -> {
                if (groupIndex != null){
                    newGroups.remove(groupIndex);
                } else {
                    groupSubscriptionMap.remove(platformAndProduct);
                }
                groupContainer.remove(companySubscriptionGroupWidget);
            }));
        });
    }

    private void addEmptyItem() {
        groupSubscriptionMap.put(Pair.of(null,null), new ArrayList<>());
    }

    private List<CompanySubscription> prepareValue() {
        List<CompanySubscription> companySubscriptions = new ArrayList<>();

        for (List<CompanySubscription> companySubscriptionsList : groupSubscriptionMap.values()) {
            Collection<CompanySubscription> c = companySubscriptionsList.stream()
                    .filter(value -> value.getEmail() != null && !value.getEmail().isEmpty() )
                    .collect(Collectors.toList());

            companySubscriptions.addAll(c);
        }
        for (Pair<Pair<Long, Long>, List<CompanySubscription>> newGroup : newGroups) {
            Collection<CompanySubscription> c = newGroup.getB().stream()
                    .filter(value -> value.getEmail() != null && !value.getEmail().isEmpty() )
                    .collect(Collectors.toList());

            companySubscriptions.addAll(c);
        }

        return companySubscriptions.stream().distinct().collect(Collectors.toList());
    }

    @UiField
    HTMLPanel groupContainer;
    @UiField
    Button addButton;
    @Inject
    Provider<CompanySubscriptionGroup> groupProvider;
    @UiField
    @Inject
    Lang lang;

    private List<CompanySubscription> value = new ArrayList<>();
    private Map<CompanySubscriptionItem, CompanySubscription> modelToView = new HashMap<>();
    private Map<Pair<Long, Long>, List<CompanySubscription>> groupSubscriptionMap = new HashMap<>();
    private List<Pair<Pair<Long, Long>, List<CompanySubscription>>> newGroups = new ArrayList<>();
    private Long companyId;

    interface SubscriptionListUiBinder extends UiBinder< HTMLPanel, CompanySubscriptionList> {}
    private static SubscriptionListUiBinder ourUiBinder = GWT.create( SubscriptionListUiBinder.class );

}