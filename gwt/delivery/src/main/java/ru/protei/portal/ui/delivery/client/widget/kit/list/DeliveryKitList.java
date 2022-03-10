package ru.protei.portal.ui.delivery.client.widget.kit.list;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.ent.Kit;
import ru.protei.portal.core.model.helper.NumberUtils;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;
import ru.protei.portal.ui.delivery.client.widget.kit.list.item.DeliveryKitItem;

import java.util.*;
import java.util.function.Consumer;

import static ru.protei.portal.ui.common.client.common.UiConstants.Styles.HAS_ERROR;
import static ru.protei.portal.ui.common.client.common.UiConstants.Styles.HIDE;


public class DeliveryKitList extends Composite implements TakesValue<List<Kit>>, HasValidable {

    public DeliveryKitList() {
        initWidget(ourUiBinder.createAndBindUi(this));
        ensureDebugIds();
    }

    @Override
    public List<Kit> getValue() {
        return value;
    }

    @Override
    public void setValue(List<Kit> value) {
        clear();
        this.value = value == null ? new ArrayList<>() : value;
        for (Kit items : this.value) {
            makeItemAndFillValue(items);
        }
        refresh();
    }

    @Override
    public boolean isValid() {
        return modelToView.keySet().stream().allMatch(DeliveryKitItem::isValid);
    }

    @Override
    public void setValid(boolean isValid) {
        throw new UnsupportedOperationException();
    }

    public void setActivity(AbstractDeliveryKitListActivity activity) {
        this.activity = activity;
        activity.getCaseState(CrmConstants.State.PRELIMINARY, caseState -> preliminaryCaseState = caseState);
    }

    public void clear() {
        container.clear();
        modelToView.clear();
        value = new ArrayList<>();
        resetLastSerialNumber();
        invisibleButtons();
        activity.getLastSerialNumber(lastSerialNumberCallback);
    }

    public void updateSerialNumbering(boolean isMilitaryNumbering) {
        this.isMilitaryNumbering = isMilitaryNumbering;
        activity.getLastSerialNumber(lastSerialNumberCallback);
        refresh();
    }

    public void updateAllowChangingState(final boolean isAllowChangingState) {
        this.isAllowChangingState = isAllowChangingState;
        modelToView.keySet().forEach(deliveryKitItem -> deliveryKitItem.stateEnabled().setEnabled(isAllowChangingState));
    }

    public void setAddMode(boolean isAddMode) {
        this.isAddMode = isAddMode;
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

    @UiHandler("addButton")
    public void onAddClicked(ClickEvent event) {
        event.preventDefault();
        addEmptyItem();
    }

    @UiHandler("refreshSerialNumberButton")
    public void onRefreshSerialNumberClicked(ClickEvent event) {
        activity.getLastSerialNumber(lastSerialNumberCallback);
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
        itemWidget.setValue(value);
        itemWidget.stateEnabled().setEnabled(isAddMode && isAllowChangingState);
        itemWidget.addCloseHandler(event -> {
            remove(event.getTarget());
            refresh();
        });

        modelToView.put(itemWidget, value);
        container.add(itemWidget);
    }

    private void remove(DeliveryKitItem item){
        container.remove(item);
        Kit remove = modelToView.remove(item);
        value.removeIf(v -> remove == v);
    }

    private void refresh() {
        refreshMinimumKitsNumber();
        refreshMultiKitsAllow();
        refreshSerialNumbers();
    }

    private void refreshMultiKitsAllow() {
        addButton.setVisible(isMilitaryNumbering);

        if (isMilitaryNumbering) {
            return;
        }

        int count = 1;
        List<DeliveryKitItem> toRemove = new ArrayList<>();
        for (Widget widget : container) {
            if (count > minimumKitsNumber) {
                toRemove.add((DeliveryKitItem)widget);
            }
            count++;
        }

        for (DeliveryKitItem item : toRemove) {
            remove(item);
        }
    }

    private void refreshMinimumKitsNumber() {
        if (modelToView.size() < minimumKitsNumber) {
            int diff = minimumKitsNumber - modelToView.size();
            for (int i = 0; i < diff; i++) {
                addEmptyItem();
            }
        }
        container.forEach(widget -> ((DeliveryKitItem)widget)
                .removeEnable().setEnabled(modelToView.size() > minimumKitsNumber));

        refreshSerialNumberButton.setVisible(!value.isEmpty());
    }

    private void resetLastSerialNumber() {
        lastSerialNumberPrefix = null;
        lastSerialNumberPostfix = null;
    }

    private void invisibleButtons() {
        addButton.setVisible(false);
        refreshSerialNumberButton.setVisible(false);
    }

    private void parseLastSerialNumber(String lastSerialNumber) {
        String[] split = lastSerialNumber.split("\\.");
        lastSerialNumberPrefix = NumberUtils.parseInteger(split[0]);
        lastSerialNumberPostfix = NumberUtils.parseInteger(split[1]);
    }

    private void refreshSerialNumbers() {
        if (lastSerialNumberPrefix == null || lastSerialNumberPostfix == null) {
            return;
        }

        int prefix = isMilitaryNumbering && !isAddMode ? lastSerialNumberPrefix + 1 : lastSerialNumberPrefix;
        int count = isMilitaryNumbering && !isAddMode? 1 : lastSerialNumberPostfix + 1;
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

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }

        root.ensureDebugId(DebugIds.DELIVERY.KITS);
        addButton.ensureDebugId(DebugIds.DELIVERY.KIT.ADD_BUTTON);
        refreshSerialNumberButton.ensureDebugId(DebugIds.DELIVERY.KIT.REFRESH_BUTTON);
    }

    @UiField
    HTMLPanel root;
    @UiField
    Element msg;
    @UiField
    FlowPanel container;
    @UiField
    Button addButton;
    @UiField
    Button refreshSerialNumberButton;
    @UiField
    Lang lang;

    @Inject
    Provider<DeliveryKitItem> itemFactory;

    private AbstractDeliveryKitListActivity activity;

    private List<Kit> value = new ArrayList<>();
    private Map<DeliveryKitItem, Kit> modelToView = new HashMap<>();

    private boolean isMilitaryNumbering = false;
    private boolean isAddMode = false;
    private boolean isAllowChangingState = false;
    private Integer lastSerialNumberPrefix;
    private Integer lastSerialNumberPostfix;

    private CaseState preliminaryCaseState = new CaseState(CrmConstants.State.PRELIMINARY);

    private final int minimumKitsNumber = 1;
    private final Consumer<String> lastSerialNumberCallback =
            ((Consumer<String>)this::parseLastSerialNumber).andThen(ignore -> refreshSerialNumbers());

    interface DeliveryKitListUiBinder extends UiBinder< HTMLPanel, DeliveryKitList> {}
    private static DeliveryKitListUiBinder ourUiBinder = GWT.create( DeliveryKitListUiBinder.class );
}