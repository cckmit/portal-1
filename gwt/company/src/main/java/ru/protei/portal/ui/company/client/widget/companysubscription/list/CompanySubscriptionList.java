package ru.protei.portal.ui.company.client.widget.companysubscription.list;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.protei.portal.core.model.ent.CompanySubscription;
import ru.protei.portal.core.model.struct.Pair;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.company.client.widget.companysubscription.group.CompanySubscriptionGroup;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;

import java.util.*;
import java.util.stream.Collectors;

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
        ensureDebugId(DebugIds.COMPANY.SUBSCRIPTIONS);
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

        addEmptyFirstGroup();

        values.forEach(companySubscription -> {
            boolean isGroupExisted = false;
            for (Pair<Pair<Long, Long>, List<CompanySubscription>> pair : groupsMap.values()) {
                if (Pair.of(companySubscription.getPlatformId(), companySubscription.getProductId()).equals(pair.getA())){
                    pair.getB().add(companySubscription);
                    isGroupExisted = true;
                    break;
                }
            }

            if (!isGroupExisted){
                List<CompanySubscription> newGroup = new ArrayList<>();
                newGroup.add(companySubscription);
                groupsMap.put(groupsMap.size(), Pair.of(Pair.of(companySubscription.getPlatformId(), companySubscription.getProductId()), newGroup));
            }
        });

        makeFirstGroup();
        makeOtherGroups();

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
        return widgetGroupsList.stream().allMatch(CompanySubscriptionGroup::isValid);
    }

    public void clear() {
        groupContainer.clear();
        groupsMap.clear();
        widgetGroupsList.clear();
    }

    @Override
    public boolean isEnabled() {
        return groupContainer.getStyleName().contains("disabled");
    }

    @Override
    public void setEnabled(boolean isEnabled) {
        groupContainer.setStyleName("disabled", !isEnabled);
        addButton.setStyleName("disabled", !isEnabled);

        for (CompanySubscriptionGroup group : widgetGroupsList) {
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
        groupsMap.put(groupsMap.size(), newGroup);
        makeGroupAndFillValue(newGroup.getA(), newGroup.getB(), groupsMap.size()-1);
    }

    public void setCompanyId(Long companyId){
        this.companyId = companyId;
    }

    private void makeGroupAndFillValue(Pair<Long, Long> platformAndProduct, List<CompanySubscription> subscriptionsList, int groupIndex) {
        CompanySubscriptionGroup companySubscriptionGroupWidget = groupProvider.get();
        companySubscriptionGroupWidget.setPlatformSelector(platformAndProduct.getA());
        companySubscriptionGroupWidget.setProductSelector(platformAndProduct.getB());
        companySubscriptionGroupWidget.setValue(subscriptionsList);
        companySubscriptionGroupWidget.setCompanyIdToSubscriptionsGroup(companyId);
        if (platformAndProduct.getB() == null && platformAndProduct.getA() == null){
            companySubscriptionGroupWidget.expandGroup();
            if (groupIndex == 0) {
                companySubscriptionGroupWidget.hideRemoveButton();
            }
        }

        companySubscriptionGroupWidget.addCloseHandler(event -> {
            groupsMap.remove(groupIndex);
            widgetGroupsList.remove(companySubscriptionGroupWidget);
            companySubscriptionGroupWidget.addStyleName("zero-opacity");

            Timer timer = new Timer() {
                @Override
                public void run() {
                    groupContainer.remove(companySubscriptionGroupWidget);
                }
            };

            timer.schedule(500);
        });

        groupContainer.add( companySubscriptionGroupWidget );
        widgetGroupsList.add(companySubscriptionGroupWidget);
    }

    private void addEmptyFirstGroup() {
        groupsMap.put(0, Pair.of(Pair.of(null, null), new ArrayList<>()));
    }

    private List<CompanySubscription> prepareValue() {
        List<CompanySubscription> companySubscriptions = new ArrayList<>();

        for (Pair<Pair<Long, Long>, List<CompanySubscription>> newGroup : groupsMap.values()) {
            Collection<CompanySubscription> c = newGroup.getB().stream()
                    .filter(value -> value.getEmail() != null && !value.getEmail().isEmpty() )
                    .collect(Collectors.toList());

            companySubscriptions.addAll(c);
        }

        return companySubscriptions.stream().distinct().collect(Collectors.toList());
    }

    private void makeFirstGroup() {
        makeGroupAndFillValue(Pair.of(null,null), groupsMap.get(0).getB(), 0);
    }

    private void makeOtherGroups() {
        int currentIndex = 1;
        for (Pair<Pair<Long, Long>, List<CompanySubscription>> pair : groupsMap.values()) {
            if (!pair.getA().equals(Pair.of(null, null))) {
                makeGroupAndFillValue(pair.getA(), pair.getB(), currentIndex++);
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

    private List<CompanySubscriptionGroup> widgetGroupsList = new ArrayList<>();
    private Map<Integer, Pair<Pair<Long, Long>, List<CompanySubscription>>> groupsMap = new HashMap<>();
    private Long companyId;

    interface SubscriptionListUiBinder extends UiBinder< HTMLPanel, CompanySubscriptionList> {}
    private static SubscriptionListUiBinder ourUiBinder = GWT.create( SubscriptionListUiBinder.class );

}