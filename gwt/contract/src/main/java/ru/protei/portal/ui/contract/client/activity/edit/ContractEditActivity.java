package ru.protei.portal.ui.contract.client.activity.edit;

import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.ent.Contract;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.struct.CostWithCurrency;
import ru.protei.portal.core.model.struct.ProductDirectionInfo;
import ru.protei.portal.core.model.struct.ProjectInfo;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.ContractControllerAsync;
import ru.protei.portal.ui.common.client.service.RegionControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

public abstract class ContractEditActivity implements Activity, AbstractContractEditActivity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
    }

    @Event
    public void onInitDetails(AppEvents.InitDetails initDetails) {
        this.initDetails = initDetails;
    }

    @Event
    public void onShow(ContractEvents.Edit event) {
        if (!hasPrivileges(event.id)) {
            fireEvent(new ErrorPageEvents.ShowForbidden());
            return;
        }

        initDetails.parent.clear();
        Window.scrollTo(0, 0);
        initDetails.parent.add(view.asWidget());

        if(event.id == null) {
            fillView(new Contract());
            return;
        }

        requestData(event.id);
    }

    @Override
    public void onSaveClicked() {
        fillDto();
        if (getValidationError() != null) {
            showValidationError();
            return;
        }
        saveContract();
    }

    @Override
    public void onCancelClicked() {
        fireEvent(new ContractEvents.Show(!isNew(contract)));
    }

    @Override
    public void onTypeChanged() {
        En_ContractType type = view.type().getValue();

        boolean isFrameworkContract = En_ContractType.SUPPLY_AND_WORK_FRAMEWORK_CONTRACT.equals(type)
                || En_ContractType.LICENSE_FRAMEWORK_CONTRACT.equals(type)
                || En_ContractType.SUPPLY_FRAMEWORK_CONTRACT.equals(type);

        view.costEnabled().setEnabled(!isFrameworkContract);
        if ( isFrameworkContract ) {
            view.cost().setValue(new CostWithCurrency(0L, En_Currency.RUB));
        }
    }

    @Override
    public void onOrganizationChanged() {
        view.contractorEnabled().setEnabled(view.organization().getValue() != null);
        view.setOrganization(view.organization().getValue().getDisplayText());
        if (view.contractor().getValue() != null) fireEvent(new NotifyEvents.Show(lang.contractContractorDropped(), NotifyEvents.NotifyType.INFO));
        view.contractor().setValue(null);
    }

    @Override
    public void refreshProjectSpecificFields() {
        if (view.project().getValue() == null) {
            clearProjectSpecificFields();
            return;
        }
        regionService.getProjectInfo(view.project().getValue().getId(), new FluentCallback<ProjectInfo>()
                .withSuccess(project -> {
                    view.direction().setValue(project.getProductDirection() == null ? null : new ProductDirectionInfo(project.getProductDirection()));
                    view.manager().setValue(project.getManager() == null ? null : new PersonShortView(project.getManager()));
                    view.directionEnabled().setEnabled(false);
                    view.managerEnabled().setEnabled(false);
                })
        );
    }

    private void clearProjectSpecificFields() {
        view.direction().setValue(null);
        view.manager().setValue(null);
        view.directionEnabled().setEnabled(true);
        view.managerEnabled().setEnabled(true);
    }

    private void requestData(Long id){
        contractService.getContract(id, new FluentCallback<Contract>()
                .withSuccess(this::fillView));
    }

    private void fillView(Contract value) {
        this.contract = value;

        view.type().setValue(contract.getContractType());
        if ( contract.getState() == null ) {
            contract.setState(En_ContractState.AGREEMENT);
        }
        view.state().setValue(contract.getState());
        view.number().setValue(contract.getNumber());
        if ( contract.getCost() == null ) {
            contract.setCost(0L);
        }
        view.cost().setValue(new CostWithCurrency(contract.getCost(), contract.getCurrency()));
        view.description().setValue(contract.getDescription());
        view.curator().setValue(createPersonOrNull(contract.getCuratorId(), contract.getCuratorShortName()));
        view.dateSigning().setValue(contract.getDateSigning());
        view.dateValid().setValue(contract.getDateValid());
        view.contractDates().setValue(contract.getContractDates());
        view.contractSpecifications().setValue(contract.getContractSpecifications());

        view.organization().setValue(createOptionOrNull(contract.getOrganizationId(), contract.getOrganizationName()));
        view.contractParent().setValue(createOptionOrNull(contract.getParentContractId(), contract.getParentContractNumber()));

        view.project().setValue(createOptionOrNull(contract.getProjectId(), contract.getProjectName()));
        refreshProjectSpecificFields();

        view.contractorEnabled().setEnabled(contract.getOrganizationId() != null);
        view.setOrganization(contract.getOrganizationName());
        view.contractor().setValue(contract.getContractor());

        if (view.project().getValue() == null) {
            view.manager().setValue(createPersonOrNull(contract.getCaseManagerId(), contract.getCaseManagerShortName()));
            view.direction().setValue(createProductOrNull(contract.getCaseDirectionId(), contract.getCaseDirectionName()));
        }
    }

    private void fillDto() {
        contract.setContractType(view.type().getValue());
        contract.setState(view.state().getValue());
        contract.setNumber(view.number().getValue());
        contract.setCost(view.cost().getValue().getCost());
        contract.setCurrency(view.cost().getValue().getCurrency());
        contract.setDescription(view.description().getValue());
        contract.setCuratorId(getPersonIdOrNull(view.curator().getValue()));
        contract.setDateSigning(view.dateSigning().getValue());
        contract.setDateValid(view.dateValid().getValue());
        contract.setContractDates(view.contractDates().getValue());
        contract.setContractSpecifications(view.contractSpecifications().getValue());

        contract.setOrganizationId(getOptionIdOrNull(view.organization().getValue()));
        contract.setParentContractId(getOptionIdOrNull(view.contractParent().getValue()));

        contract.setProjectId(view.project().getValue() == null ? null : view.project().getValue().getId());
        contract.setContractor((view.contractor().getValue()));

        if (contract.getProjectId() == null) {
            contract.setCaseManagerId(getPersonIdOrNull(view.manager().getValue()));
            contract.setCaseDirectionId(getProductIdOrNull(view.direction().getValue()));
        } else {
            contract.setCaseManagerId(null);
            contract.setCaseDirectionId(null);
        }
    }

    private void showValidationError() {
        fireEvent(new NotifyEvents.Show(getValidationError(), NotifyEvents.NotifyType.ERROR));
    }

    private String getValidationError() {
        if (StringUtils.isBlank(contract.getNumber()))
            return lang.contractValidationEmptyNumber();

        if (StringUtils.isBlank(contract.getDescription()))
            return lang.contractValidationEmptyDescription();

        if (contract.getContractType() == null)
            return lang.contractValidationEmptyType();

        if (contract.getStateId() == null)
            return lang.contractValidationEmptyState();

        if (contract.getDateSigning() == null)
            return lang.contractValidationEmptyDateSigning();

        if (contract.getDateValid() == null)
            return lang.contractValidationEmptyDateValid();

        if ((contract.getProjectId() == null && contract.getCaseDirectionId() == null))
            return lang.contractValidationEmptyDirection();

        if (!view.validateContractSpecifications().isValid())
            return lang.contractValidationContractSpecification();

        return null;
    }

    private void saveContract() {
        view.saveEnabled().setEnabled(false);
        contractService.saveContract(contract, new FluentCallback<Long>()
                .withError(t -> {
                    view.saveEnabled().setEnabled(true);
                })
                .withSuccess(value -> {
                    view.saveEnabled().setEnabled(true);
                    fireEvent(new ContractEvents.ChangeModel());
                    fireEvent(new ProjectEvents.ChangeModel());
                    fireEvent(new ContractEvents.Show(!isNew(contract)));
                }));
    }

    private boolean isNew(Contract contract) {
        return contract == null || contract.getId() == null;
    }

    private Long getOptionIdOrNull(EntityOption option) {
        return option == null ? null : option.getId();
    }

    private Long getPersonIdOrNull(PersonShortView option) {
        return option == null ? null : option.getId();
    }

    private Long getProductIdOrNull(ProductDirectionInfo option) {
        return option == null ? null : option.id;
    }

    private EntityOption createOptionOrNull(Long id, String name) {
        return id == null ? null : new EntityOption(name, id);
    }

    private PersonShortView createPersonOrNull(Long id, String name) {
        return id == null ? null : new PersonShortView(name, id);
    }

    private ProductDirectionInfo createProductOrNull(Long id, String name) {
        return id == null ? null : new ProductDirectionInfo(id, name);
    }

    private boolean hasPrivileges(Long contractId) {
        if (contractId == null && policyService.hasPrivilegeFor(En_Privilege.CONTRACT_CREATE)) {
            return true;
        }

        if (contractId != null && policyService.hasPrivilegeFor(En_Privilege.CONTRACT_EDIT)) {
            return true;
        }

        return false;
    }

    @Inject
    private Lang lang;
    @Inject
    private AbstractContractEditView view;
    @Inject
    private ContractControllerAsync contractService;
    @Inject
    PolicyService policyService;
    @Inject
    RegionControllerAsync regionService;

    private Contract contract;
    private AppEvents.InitDetails initDetails;
}
