package ru.protei.portal.ui.delivery.client.widget.kit.view.list;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.protei.portal.core.model.ent.Kit;
import ru.protei.portal.core.model.helper.NumberUtils;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;
import ru.protei.portal.ui.delivery.client.widget.kit.activity.AbstractDeliveryKitListActivity;
import ru.protei.portal.ui.delivery.client.widget.kit.view.item.DeliveryKitItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static ru.protei.portal.ui.common.client.common.UiConstants.Styles.HAS_ERROR;
import static ru.protei.portal.ui.common.client.common.UiConstants.Styles.HIDE;

public class DeliveryKitList extends Composite
        implements HasValue<List<Kit>>, HasValidable
{
    public DeliveryKitList() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
    }

    @Override
    public List<Kit> getValue() {
        return value;
    }

    @Override
    public void setValue(List<Kit> value ) {
        setValue( value, false );
    }

    @Override
    public void setValue(List<Kit> value, boolean fireEvents ) {
        clear();
        this.value = value == null ? new ArrayList<>() : value;
        for ( Kit items : this.value ) {
            makeItemAndFillValue(items);
        }

        refreshMinKitNumber();

        isValid();

        if ( fireEvents ) {
            ValueChangeEvent.fire( this, this.value );
        }
    }

    public void clear() {
        container.clear();
        modelToView.clear();
        value = new ArrayList<>();
        refreshMinKitNumber();
        refreshMultiKitsAllow();

        nextAvailableSerialNumberPrefix = null;
        nextAvailableSerialNumberPostfix = null;
        isArmyProject = false;
        activity.getLastSerialNumber(isArmyProject,this::parseSerialNumber);
    }

    private void parseSerialNumber(String nextAvailableSerialNumber) {
        String[] split = nextAvailableSerialNumber.split("\\.");
        nextAvailableSerialNumberPrefix = split[0];
        nextAvailableSerialNumberPostfix = NumberUtils.parseInteger(split[1]) + 1;
        refreshSerialNumber();
    }

    private void refreshSerialNumber() {
        if (nextAvailableSerialNumberPrefix == null || nextAvailableSerialNumberPostfix == null) {
            return;
        }
        int count = nextAvailableSerialNumberPostfix;
        for (Widget widget : container) {
            DeliveryKitItem item = (DeliveryKitItem) widget;
            item.setSerialNumber(nextAvailableSerialNumberPrefix + "." +
                    NumberFormat.getFormat("000").format(count++));
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<List<Kit>> handler ) {
        return addHandler( handler, ValueChangeEvent.getType() );
    }

    @Override
    public void setValid(boolean isValid) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isValid() {
        return modelToView.keySet().stream().allMatch(DeliveryKitItem::isValid);
    }

    @UiHandler( "add" )
    public void onAddClicked( ClickEvent event ) {
        event.preventDefault();
        addEmptyItem();
        ValueChangeEvent.fire( this, value );
    }

    public void setEmptyItemProvider(Supplier<Kit> provider) {
        emptyKitProvider = provider;
    }

    public void setEnsureDebugId(String debugId) {
        add.ensureDebugId(debugId);
    }

    public void setArmyProject(boolean armyProject) {
        isArmyProject = armyProject;
        activity.getLastSerialNumber(isArmyProject,this::parseSerialNumber);
        refreshMultiKitsAllow();
    }

    public void setMinKitNumber(int minKitNumber) {
        this.minKitNumber = minKitNumber;
        refreshMinKitNumber();
    }

    public void setError(boolean isError, String error) {
        markBoxAsError(isError);

        if (isError) {
            msg.removeClassName(HIDE);
            msg.setInnerText(error);
            return;
        }

        msg.addClassName(HIDE);
        msg.setInnerText(null);
    }

    private Supplier<Kit> emptyKitProvider;

    private void addEmptyItem() {
        Kit item = emptyKitProvider.get();
        value.add(item);
        makeItemAndFillValue( item );
    }

    private void makeItemAndFillValue(final Kit value ) {
        DeliveryKitItem itemWidget = itemFactory.get();
        itemWidget.setValue( value );
        itemWidget.addCloseHandler(event -> {
            container.remove( event.getTarget() );

            Kit remove = modelToView.remove( event.getTarget() );
            DeliveryKitList.this.value.removeIf(v -> remove == v);

            refreshMultiKitsAllow();
            refreshMinKitNumber();
            refreshSerialNumber();
        });

        if (modelToView.size() < minKitNumber) {
            itemWidget.removeEnable().setEnabled(false);
        }

        refreshMultiKitsAllow();

        modelToView.put( itemWidget, value );
        container.add( itemWidget );

        refreshSerialNumber();
    }

    private void refreshMultiKitsAllow() {
        add.setEnabled(isArmyProject);
        setError(!isArmyProject && modelToView.size() > 1, lang.deliveryValidationOnlyOneKitForCivilProject());
    }

    private void refreshMinKitNumber() {
        if (modelToView.size() < minKitNumber) {
            int diff = minKitNumber - modelToView.size();
            for (int i = 0; i < diff; i++) {
                addEmptyItem();
            }
        }
    }

    private void markBoxAsError(boolean isError) {
        if (isError) {
            root.addStyleName(HAS_ERROR);
            return;
        }
        root.removeStyleName(HAS_ERROR);
    }


    @UiField
    HTMLPanel root;
    @UiField
    FlowPanel container;
    @UiField
    Button add;
    @UiField
    Element msg;
    @UiField
    Lang lang;

    @Inject
    Provider<DeliveryKitItem> itemFactory;
    List<Kit> value = new ArrayList<>();
    Map<DeliveryKitItem, Kit> modelToView = new HashMap<>();


    @Inject
    private AbstractDeliveryKitListActivity activity;
    private String nextAvailableSerialNumberPrefix;
    private Integer nextAvailableSerialNumberPostfix;


    private boolean isArmyProject = false;
    private int minKitNumber = 1;

    interface DeliveryKitListUiBinder extends UiBinder< HTMLPanel, DeliveryKitList> {}
    private static DeliveryKitListUiBinder ourUiBinder = GWT.create( DeliveryKitListUiBinder.class );

}