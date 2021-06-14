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
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.ent.Kit;
import ru.protei.portal.core.model.helper.NumberUtils;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;
import ru.protei.portal.ui.delivery.client.widget.kit.activity.AbstractDeliveryKitListActivity;
import ru.protei.portal.ui.delivery.client.widget.kit.view.item.DeliveryKitItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

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
        prepare();
        this.value = value == null ? new ArrayList<>() : value;
        for ( Kit items : this.value ) {
            makeItemAndFillValue(items);
        }

        refresh();
        isValid();

        if ( fireEvents ) {
            ValueChangeEvent.fire( this, this.value );
        }
    }

    public void prepare() {
        container.clear();
        modelToView.clear();
        value = new ArrayList<>();
        resetSerialNumber();
        activity.getLastSerialNumber(isArmyProject, lastSerialNumberCallback);
        activity.getCaseState(CrmConstants.State.PRELIMINARY, caseState -> preliminaryCaseState = caseState);
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<List<Kit>> handler) {
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

    @UiHandler( "refreshSerialNumber" )
    public void onRefreshSerialNumberClicked( ClickEvent event ) {
        activity.getLastSerialNumber(isArmyProject, lastSerialNumberCallback);
    }

    public void setEnsureDebugId(String debugId) {
        root.ensureDebugId(debugId);
        add.ensureDebugId(debugId + "-add");
        refreshSerialNumber.ensureDebugId(debugId + "-refresh");
    }

    public void setArmyProject(boolean armyProject) {
        isArmyProject = armyProject;
        activity.getLastSerialNumber(isArmyProject, lastSerialNumberCallback);
        refreshMultiKitsAllow();
    }

    public void setMinimumKitNumber(int minimumKitNumber) {
        this.minimumKitNumber = minimumKitNumber;
        refreshMinimumKitNumber();
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

    private void markBoxAsError(boolean isError) {
        if (isError) {
            root.addStyleName(HAS_ERROR);
            return;
        }
        root.removeStyleName(HAS_ERROR);
    }

    private void addEmptyItem() {
        Kit kit = new Kit();
        kit.setState(preliminaryCaseState);
        value.add(kit);
        makeItemAndFillValue(kit);
        refresh();
    }

    private void makeItemAndFillValue(final Kit value) {
        DeliveryKitItem itemWidget = itemFactory.get();
        itemWidget.setValue( value );
        itemWidget.addCloseHandler(event -> {
            remove(event.getTarget());
            refresh();
        });

        modelToView.put( itemWidget, value );
        container.add( itemWidget );
    }

    private void remove(DeliveryKitItem item){
        container.remove( item );
        Kit remove = modelToView.remove( item );
        DeliveryKitList.this.value.removeIf(v -> remove == v);
    }

    private void refresh() {
        refreshMinimumKitNumber();
        refreshMultiKitsAllow();
        refreshSerialNumber();
    }

    private void refreshMultiKitsAllow() {
        add.setEnabled(isKitsAddButtonEnabled && isArmyProject);
        if (!isArmyProject) {
            int count = 1;
            List<DeliveryKitItem> toRemove = new ArrayList<>();
            for (Widget widget : container) {
                if (count > minimumKitNumber) {
                    toRemove.add((DeliveryKitItem)widget);
                }
                count++;
            }

            for (DeliveryKitItem item : toRemove) {
                remove(item);
            }
        }
        refreshMinimumKitNumber();
    }

    private void refreshMinimumKitNumber() {
        if (modelToView.size() < minimumKitNumber) {
            int diff = minimumKitNumber - modelToView.size();
            for (int i = 0; i < diff; i++) {
                addEmptyItem();
            }
        }
        container.forEach(widget -> ((DeliveryKitItem)widget)
                .removeEnable().setEnabled(modelToView.size() > minimumKitNumber));
    }

    private void resetSerialNumber() {
        lastSerialNumberPrefix = null;
        lastSerialNumberPostfix = null;
    }

    private void parseSerialNumber(String lastSerialNumber) {
        String[] split = lastSerialNumber.split("\\.");
        lastSerialNumberPrefix = NumberUtils.parseInteger(split[0]);
        lastSerialNumberPostfix = NumberUtils.parseInteger(split[1]);
    }

    private void refreshSerialNumber() {
        if (lastSerialNumberPrefix == null || lastSerialNumberPostfix == null) {
            return;
        }

        int prefix = isArmyProject ? lastSerialNumberPrefix + 1 : lastSerialNumberPrefix;
        int count = isArmyProject ? 1 : lastSerialNumberPostfix + 1;
        for (Widget widget : container) {
            DeliveryKitItem item = (DeliveryKitItem) widget;
            if (modelToView.get(item).getId() == null) {
                String number =
                        NumberFormat.getFormat("000").format(prefix)
                        + "." +
                        NumberFormat.getFormat("000").format(count++);
                item.setSerialNumber(number);
            }
        }
    }

    public Button getRefreshKitsSerialNumberButton() {
        return refreshSerialNumber;
    }

    public void setKitsAddButtonEnabled(boolean isKitsAddButtonEnabled) {
        this.isKitsAddButtonEnabled = isKitsAddButtonEnabled;
    }

    @UiField
    HTMLPanel root;
    @UiField
    FlowPanel container;
    @UiField
    Button add;
    @UiField
    Button refreshSerialNumber;
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

    private Integer lastSerialNumberPrefix;
    private Integer lastSerialNumberPostfix;
    private boolean isArmyProject = false;
    private boolean isKitsAddButtonEnabled = false;
    private CaseState preliminaryCaseState = new CaseState(CrmConstants.State.PRELIMINARY);

    private int minimumKitNumber = 1;
    private final Consumer<String> lastSerialNumberCallback =
            ((Consumer<String>)this::parseSerialNumber).andThen(ignore -> refreshSerialNumber());

    interface DeliveryKitListUiBinder extends UiBinder< HTMLPanel, DeliveryKitList> {}
    private static DeliveryKitListUiBinder ourUiBinder = GWT.create( DeliveryKitListUiBinder.class );

}