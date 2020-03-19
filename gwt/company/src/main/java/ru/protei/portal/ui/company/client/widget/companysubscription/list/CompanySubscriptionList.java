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
            boolean isGroupExisted = false;
            for (Pair<Pair<Long, Long>, List<CompanySubscription>> pair : groupsList) {
                if (Pair.of(companySubscription.getPlatformId(), companySubscription.getProductId()).equals(pair.getA())){
                    pair.getB().add(companySubscription);
                    isGroupExisted = true;
                    break;
                }
            }

            if (!isGroupExisted){
                List<CompanySubscription> subscriptions = new ArrayList<>();
                subscriptions.add(companySubscription);
                groupsList.add(Pair.of(Pair.of(companySubscription.getPlatformId(), companySubscription.getProductId()), subscriptions));
            }
        });

        boolean isNullNullGroupExisted = false;
        for (Pair<Pair<Long, Long>, List<CompanySubscription>> pair : groupsList) {
            if (Pair.of(null, null).equals(pair.getA())){
                isNullNullGroupExisted = true;
                break;
            }
        }

        if (!isNullNullGroupExisted) {
            addEmptyItem();
        }

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
        groupsList.clear();
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
        groupsList.add(newGroup);
        makeGroupAndFillValue(newGroup.getA(), newGroup.getB(), groupsList.size()-1);
    }

    public void setPlatformFilter(Long companyId){
        this.companyId = companyId;
    }

    private void makeGroupAndFillValue(Pair<Long, Long> platformAndProduct, List<CompanySubscription> subscriptionsList, int groupIndex) {
        CompanySubscriptionGroup companySubscriptionGroupWidget = groupProvider.get();
        companySubscriptionGroupWidget.setPlatformSelector(platformAndProduct.getA());
        companySubscriptionGroupWidget.setProductSelector(platformAndProduct.getB());
        companySubscriptionGroupWidget.setValue(subscriptionsList);
        companySubscriptionGroupWidget.setPlatformFilter(companyId);
        if (platformAndProduct.getB() == null && platformAndProduct.getA() == null){
            companySubscriptionGroupWidget.expandGroup();
            if (groupIndex == 0) {
                companySubscriptionGroupWidget.hideRemoveButton();
            }
        }

        companySubscriptionGroupWidget.addCloseHandler(event -> {
            groupsList.remove(groupIndex);
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

    private void addEmptyItem() {
        groupsList.add(Pair.of(Pair.of(null, null), new ArrayList<>()));
    }

    private List<CompanySubscription> prepareValue() {
        List<CompanySubscription> companySubscriptions = new ArrayList<>();

        for (Pair<Pair<Long, Long>, List<CompanySubscription>> newGroup : groupsList) {
            Collection<CompanySubscription> c = newGroup.getB().stream()
                    .filter(value -> value.getEmail() != null && !value.getEmail().isEmpty() )
                    .collect(Collectors.toList());

            companySubscriptions.addAll(c);
        }

        return companySubscriptions.stream().distinct().collect(Collectors.toList());
    }

    private void makeFirstGroup() {
        for (Pair<Pair<Long, Long>, List<CompanySubscription>> pair : groupsList) {
            if (pair.getA().equals(Pair.of(null, null))) {
                makeGroupAndFillValue(Pair.of(null,null), pair.getB(), 0);
            }
        }
    }

    private void makeOtherGroups() {
        int currentIndex = 1;
        for (Pair<Pair<Long, Long>, List<CompanySubscription>> pair : groupsList) {
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
    private List<Pair<Pair<Long, Long>, List<CompanySubscription>>> groupsList = new ArrayList<>();
    private Long companyId;

    interface SubscriptionListUiBinder extends UiBinder< HTMLPanel, CompanySubscriptionList> {}
    private static SubscriptionListUiBinder ourUiBinder = GWT.create( SubscriptionListUiBinder.class );

}