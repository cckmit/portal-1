package ru.protei.portal.ui.delivery.client.activity.meta;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CustomerType;
import ru.protei.portal.core.model.dict.En_DeliveryAttribute;
import ru.protei.portal.core.model.dto.ProjectInfo;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.struct.ContractInfo;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.client.lang.En_CustomerTypeLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.delivery.client.view.meta.DeliveryMetaView;

import java.util.function.Consumer;

import static ru.protei.portal.core.model.helper.CollectionUtils.joining;

public class DeliveryCommonMeta implements AbstractDeliveryCommonMeta {

    public void setDeliveryMetaView(DeliveryMetaView view, Consumer<Boolean> isMilitaryNumbering) {
        this.view = view;
        this.isMilitaryNumbering = isMilitaryNumbering;
    }

    @Override
    public void onProjectChanged() {
        ProjectInfo project = view.project().getValue();
        fillProjectSpecificFields(project);
    }

    @Override
    public void onAttributeChanged() {
        if (En_DeliveryAttribute.DELIVERY.equals(view.attribute().getValue())) {
            if (view.project().getValue() != null) {
                view.contractEnable().setEnabled(true);
            }
            view.setContractFieldMandatory(true);
        } else {
            view.contractEnable().setEnabled(false);
            view.setContractFieldMandatory(false);
            view.contract().setValue(null);
            view.setContractCompany(null);
        }
    }

    @Override
    public void onContractChanged() {
        ContractInfo contract = view.contract().getValue();
        if (contract == null) {
            view.setContractCompany(null);
        } else {
            view.setContractCompany(contract.getOrganizationName());
        }
    }

    @Override
    public void onDepartureDateChanged() {
        view.setDepartureDateValid(isDepartureDateFieldValid());
    }

    public boolean isDepartureDateFieldValid() {
        if (view.departureDate().getValue() == null) {
            return view.isDepartureDateEmpty();
        }

        return true;
    }

    private void fillProjectSpecificFields(ProjectInfo projectInfo) {
        if (projectInfo == null) {
            clearProjectSpecificFields();
            return;
        }
        view.setCustomerCompany(projectInfo.getContragent().getDisplayText());
        view.setCustomerType(customerTypeLang.getName(projectInfo.getCustomerType()));
        view.initiator().setValue(null);
        view.updateInitiatorModel(projectInfo.getContragent().getId());
        view.initiatorEnable().setEnabled(true);
        view.setManager(projectInfo.getManager().getDisplayText());
        view.setProducts(joining(projectInfo.getProducts(), ", ", ProductShortView::getName));
        view.contract().setValue(null);
        view.setContractCompany(null);
        view.updateContractModel(projectInfo.getId());
        isMilitaryNumbering.accept(projectInfo.getCustomerType() == En_CustomerType.MINISTRY_OF_DEFENCE);
        if (En_DeliveryAttribute.DELIVERY.equals(view.attribute().getValue())) {
            view.contractEnable().setEnabled(true);
        }
    }

    @Override
    public void clearProjectSpecificFields() {
        view.setCustomerCompany(null);
        view.setCustomerType(null);
        view.initiatorEnable().setEnabled(false);
        view.initiator().setValue(null);
        view.updateInitiatorModel(null);
        view.setManager(null);
        view.setProducts(null);
        view.contract().setValue(null);
        view.contractEnable().setEnabled(false);
        view.setContractCompany(null);
        view.setContractFieldMandatory(false);
        isMilitaryNumbering.accept(false);
        view.updateContractModel(null);
    }

    @Override
    public String getValidationError() {
        CaseState state = view.state().getValue();
        if (state == null) {
            return lang.deliveryValidationEmptyState();
        }
        if (view.type().getValue() == null) {
            return lang.deliveryValidationEmptyType();
        }
        if (view.project().getValue() == null) {
            return lang.deliveryValidationEmptyProject();
        }
        En_DeliveryAttribute attribute = view.attribute().getValue();
        if (En_DeliveryAttribute.DELIVERY == attribute && view.contract().getValue() == null) {
            return lang.deliveryValidationEmptyContractAtAttributeDelivery();
        }

        return null;
    }

    @Inject
    private En_CustomerTypeLang customerTypeLang;
    @Inject
    private Lang lang;

    private Consumer<Boolean> isMilitaryNumbering;
    private DeliveryMetaView view;
}
