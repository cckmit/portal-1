package ru.protei.portal.ui.delivery.client.activity.delivery.meta;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CustomerType;
import ru.protei.portal.core.model.dict.En_DeliveryAttribute;
import ru.protei.portal.core.model.dict.En_PersonRoleType;
import ru.protei.portal.core.model.dto.ProjectInfo;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.struct.ContractInfo;
import ru.protei.portal.core.model.view.PersonProjectMemberView;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.client.lang.En_CustomerTypeLang;
import ru.protei.portal.ui.common.client.lang.En_PersonRoleTypeLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.delivery.client.view.delivery.meta.DeliveryMetaView;

import java.util.Date;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.CollectionUtils.*;

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
        if (En_DeliveryAttribute.DELIVERY.equals(view.attribute().getValue()) && view.project().getValue() != null) {
            view.contractEnable().setEnabled(true);
        } else {
            view.contractEnable().setEnabled(false);
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
        Date departureDate = view.departureDate().getValue();
        if (departureDate == null) {
            return view.isDepartureDateEmpty();
        }

        return departureDate.getTime() > System.currentTimeMillis();
    }

    @Override
    public void clearProjectSpecificFields() {
        view.setCustomerCompany(null);
        view.setCustomerType(null);
        view.initiatorEnable().setEnabled(false);
        view.initiator().setValue(null);
        view.updateInitiatorModel(null);
        view.setProducts(null);
        view.setTeam("");
        view.contract().setValue(null);
        view.contractEnable().setEnabled(false);
        view.setContractCompany(null);
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
        return null;
    }

    protected String makeTeam(ProjectInfo projectInfo) {
        if(isEmpty(projectInfo.getTeam())) {
            return "";
        }
        StringBuilder teamBuilder = new StringBuilder();
        projectInfo.getTeam().stream()
                .filter(personProjectMemberView -> En_PersonRoleType.isDeliveryRole(personProjectMemberView.getRole()))
                .collect(Collectors.groupingBy(PersonProjectMemberView::getRole,
                        Collectors.mapping(PersonProjectMemberView::getDisplayShortName, Collectors.joining(", "))))
                .forEach((role, team) ->
                        teamBuilder.append("<span class='bold' title='")
                                .append(roleTypeLang.getName(role))
                                .append("'>")
                                .append(roleTypeLang.getShortName(role))
                                .append("</span>: ")
                                .append(team)
                                .append("<br/>"));
        return teamBuilder.toString();
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
        view.setProducts(joining(projectInfo.getProducts(), ", ", ProductShortView::getName));
        view.setTeam(makeTeam(projectInfo));
        view.contract().setValue(null);
        view.setContractCompany(null);
        view.updateContractModel(projectInfo.getId());
        isMilitaryNumbering.accept(projectInfo.getCustomerType() == En_CustomerType.MINISTRY_OF_DEFENCE);
        if (En_DeliveryAttribute.DELIVERY.equals(view.attribute().getValue())) {
            view.contractEnable().setEnabled(true);
        }
    }

    @Inject
    private En_CustomerTypeLang customerTypeLang;
    @Inject
    private Lang lang;
    @Inject
    En_PersonRoleTypeLang roleTypeLang;

    private Consumer<Boolean> isMilitaryNumbering;
    private DeliveryMetaView view;
}
