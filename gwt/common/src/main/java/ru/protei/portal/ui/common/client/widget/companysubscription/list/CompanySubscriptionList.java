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

import java.util.*;
import java.util.stream.Collectors;

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
        values = values == null ? new ArrayList<>() : values;
        values.forEach(companySubscription -> {
            if (!existedGroupsMap.containsKey(Pair.of(companySubscription.getPlatformId(), companySubscription.getProductId()))) {
                existedGroupsMap.put(Pair.of(companySubscription.getPlatformId(), companySubscription.getProductId()), new ArrayList<>());
            }
            existedGroupsMap.get(Pair.of(companySubscription.getPlatformId(), companySubscription.getProductId())).add(companySubscription);
        });

        if (!existedGroupsMap.containsKey(Pair.of(null, null))) {
            addEmptyItem();
        }

        makeFirstGroup(existedGroupsMap);
        makeOtherGroups(existedGroupsMap);

        if(fireEvents) {
            ValueChangeEvent.fire( this, values );
        }
    }

    @Override
    public void setValid(boolean isValid) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isValid() {
        return allGroupsList.stream().allMatch(CompanySubscriptionGroup::isValid);
    }

    public void clear() {
        groupContainer.clear();
        existedGroupsMap.clear();
        newGroupsList.clear();
        allGroupsList.clear();
    }

    @Override
    public boolean isEnabled() {
        return groupContainer.getStyleName().contains("disabled");
    }

    @Override
    public void setEnabled(boolean isEnabled) {
        groupContainer.setStyleName("disabled", !isEnabled);

        for (CompanySubscriptionGroup group : allGroupsList) {
            group.setEnabled(isEnabled);
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler( ValueChangeHandler<List<CompanySubscription>> handler ) {
        return addHandler( handler, ValueChangeEvent.getType() );
    }

    @UiHandler("addButton")
    public void onAddClicked (ClickEvent event){
        Pair<Pair<Long, Long>, List<CompanySubscription>> newGroup = Pair.of(Pair.of(null,null), new ArrayList<>());
        newGroupsList.add(newGroup);
        makeGroupAndFillValue(newGroup.getA(), newGroup.getB(), newGroupsList.size()-1);
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
        if (platformAndProduct.getB() == null && platformAndProduct.getA() == null && groupIndex == null){
            companySubscriptionGroupWidget.expandGroupAndHideRemoveButton();
        }
        groupContainer.add( companySubscriptionGroupWidget );
        allGroupsList.add(companySubscriptionGroupWidget);

        companySubscriptionGroupWidget.addCloseHandler(event -> {
            fireEvent(new ConfirmDialogEvents.Show(lang.companySubscriptionGroupRemoveConfirmMessage(), () -> {
                if (groupIndex != null){
                    newGroupsList.remove(groupIndex);
                } else {
                    existedGroupsMap.remove(platformAndProduct);
                }
                groupContainer.remove(companySubscriptionGroupWidget);
                allGroupsList.remove(companySubscriptionGroupWidget);
            }));
        });
    }

    private void addEmptyItem() {
        existedGroupsMap.put(Pair.of(null,null), new ArrayList<>());
    }

    private List<CompanySubscription> prepareValue() {
        List<CompanySubscription> companySubscriptions = new ArrayList<>();

        for (List<CompanySubscription> companySubscriptionsList : existedGroupsMap.values()) {
            Collection<CompanySubscription> c = companySubscriptionsList.stream()
                    .filter(value -> value.getEmail() != null && !value.getEmail().isEmpty() )
                    .collect(Collectors.toList());

            companySubscriptions.addAll(c);
        }
        for (Pair<Pair<Long, Long>, List<CompanySubscription>> newGroup : newGroupsList) {
            Collection<CompanySubscription> c = newGroup.getB().stream()
                    .filter(value -> value.getEmail() != null && !value.getEmail().isEmpty() )
                    .collect(Collectors.toList());

            companySubscriptions.addAll(c);
        }

        return companySubscriptions.stream().distinct().collect(Collectors.toList());
    }

    private void makeFirstGroup(Map<Pair<Long, Long>, List<CompanySubscription>> groupSubscriptionMap) {
        makeGroupAndFillValue(Pair.of(null,null), groupSubscriptionMap.get(Pair.of(null,null)), null);
    }

    private void makeOtherGroups(Map<Pair<Long, Long>, List<CompanySubscription>> groupSubscriptionMap) {
        for (Map.Entry<Pair<Long, Long>, List<CompanySubscription>> pairListEntry : groupSubscriptionMap.entrySet()) {
            if (!pairListEntry.getKey().equals(Pair.of(null,null))) {
                makeGroupAndFillValue(pairListEntry.getKey(), pairListEntry.getValue(), null);
            }
        }
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

    private List<CompanySubscriptionGroup> allGroupsList = new ArrayList<>();
    private Map<Pair<Long, Long>, List<CompanySubscription>> existedGroupsMap = new HashMap<>();
    private List<Pair<Pair<Long, Long>, List<CompanySubscription>>> newGroupsList = new ArrayList<>();
    private Long companyId;

    interface SubscriptionListUiBinder extends UiBinder< HTMLPanel, CompanySubscriptionList> {}
    private static SubscriptionListUiBinder ourUiBinder = GWT.create( SubscriptionListUiBinder.class );

}