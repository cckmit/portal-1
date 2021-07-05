package ru.protei.portal.ui.delivery.client.view.create;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_DeliveryAttribute;
import ru.protei.portal.core.model.dict.En_DeliveryType;
import ru.protei.portal.core.model.dto.ProjectInfo;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.ent.Kit;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.struct.ContractInfo;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;
import ru.protei.portal.ui.delivery.client.activity.create.AbstractDeliveryCreateActivity;
import ru.protei.portal.ui.delivery.client.activity.create.AbstractDeliveryCreateView;
import ru.protei.portal.ui.delivery.client.view.meta.DeliveryMetaView;
import ru.protei.portal.ui.delivery.client.view.namedescription.DeliveryNameDescriptionEditView;
import ru.protei.portal.ui.delivery.client.widget.kit.view.list.DeliveryKitList;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Вид создания Поставки
 */
public class DeliveryCreateView extends Composite implements AbstractDeliveryCreateView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        ensureDebugIds();
        meta.stateEnable().setEnabled(false);
    }

    @Override
    public void setActivity(AbstractDeliveryCreateActivity activity) {
        this.activity = activity;
    }

    @Override
    public HasEnabled saveEnabled() {
        return saveButton;
    }

    @Override
    public HasValue<String> name() { return nameDescription.name(); }

    @Override
    public HasValue<String> description() { return nameDescription.description(); }

    @Override
    public HasValue<List<Kit>> kits() {
        return kits;
    }

    @Override
    public void kitsClear() {
        kits.prepare();
    }

    @Override
    public void updateKitByProject(boolean isArmyProject) {
        kits.setArmyProject(isArmyProject);
    }

    @Override
    public HasValidable kitsValidate() {
        return kits;
    }

    @Override
    public DeliveryMetaView getMetaView() {
        return meta;
    }

    @Override
    public HasValue<CaseState> state() {
        return meta.state();
    }

    @Override
    public HasValue<En_DeliveryType> type() {
        return meta.type();
    }

    @Override
    public HasValue<ProjectInfo> project() {
        return meta.project();
    }

    @Override
    public HasValue<PersonShortView> initiator() {
        return meta.initiator();
    }

    @Override
    public HasValue<En_DeliveryAttribute> attribute() {
        return meta.attribute();
    }

    @Override
    public HasValue<ContractInfo> contract() {
        return meta.contract();
    }

    @Override
    public HasValue<Date> departureDate() {
        return meta.departureDate();
    }

    @Override
    public void setDepartureDateValid(boolean isValid) {
        meta.setDepartureDateValid(isValid);
    }

    @Override
    public void setSubscribers(Set<Person> persons) {
        meta.setSubscribers(persons);
    }

    @Override
    public Set<Person> getSubscribers() {
        return meta.getSubscribers();
    }

    @Override
    public HasEnabled refreshKitsSerialNumberEnabled() {
        return kits.getRefreshKitsSerialNumberButton();
    }

    @Override
    public void setKitsAddButtonEnabled(boolean isKitsAddButtonEnabled) {
        kits.setKitsAddButtonEnabled(isKitsAddButtonEnabled);
    }

    @Override
    public HasValue<PersonShortView> hwManager() {
        return meta.hwManager();
    }

    @Override
    public HasValue<PersonShortView> qcManager() {
        return meta.qcManager();
    }

    @UiHandler("saveButton")
    public void onSaveClicked(ClickEvent event) {
        if (activity != null) {
            activity.onSaveClicked();
        }
    }

    @UiHandler({"cancelButton", "backButton"})
    public void onCancelClicked(ClickEvent event) {
        if (activity != null) {
            activity.onCancelClicked();
        }
    }

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }
        kits.setEnsureDebugId(DebugIds.DELIVERY.KITS);

        backButton.ensureDebugId(DebugIds.DELIVERY.BACK_BUTTON);
        saveButton.ensureDebugId(DebugIds.DELIVERY.SAVE_BUTTON);
        cancelButton.ensureDebugId(DebugIds.DELIVERY.CANCEL_BUTTON);
    }

    @UiField
    HTMLPanel root;
    @Inject
    @UiField(provided = true)
    DeliveryNameDescriptionEditView nameDescription;
    @Inject
    @UiField(provided = true)
    DeliveryKitList kits;
    @Inject
    @UiField(provided = true)
    DeliveryMetaView meta;

    @UiField
    Button backButton;
    @UiField
    Button saveButton;
    @UiField
    Button cancelButton;
    @Inject
    @UiField
    Lang lang;

    private AbstractDeliveryCreateActivity activity;

    interface ViewUiBinder extends UiBinder<HTMLPanel, DeliveryCreateView> {}
    private static ViewUiBinder ourUiBinder = GWT.create(ViewUiBinder.class);
}
