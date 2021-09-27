package ru.protei.portal.ui.delivery.client.view.delivery.create;

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
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.struct.ContractInfo;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.delivery.client.activity.delivery.create.AbstractDeliveryCreateActivity;
import ru.protei.portal.ui.delivery.client.activity.delivery.create.AbstractDeliveryCreateView;
import ru.protei.portal.ui.delivery.client.view.delivery.meta.DeliveryMetaView;
import ru.protei.portal.ui.delivery.client.view.delivery.namedescription.DeliveryNameDescriptionEditView;

import java.util.Date;
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
    public HasWidgets getKitsContainer() {
        return kitsContainer;
    }

    @UiHandler("saveButton")
    public void onSaveClicked(ClickEvent event) {
        if (activity != null) {
            activity.onSaveClicked();
        }
    }

    @UiHandler({"cancelButton"})
    public void onCancelClicked(ClickEvent event) {
        if (activity != null) {
            activity.onCancelClicked();
        }
    }

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }
        kitsContainer.ensureDebugId(DebugIds.DELIVERY.KITS);
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
    DeliveryMetaView meta;
    @UiField
    HTMLPanel kitsContainer;

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
