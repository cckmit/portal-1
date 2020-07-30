package ru.protei.portal.ui.contract.client.activity.edit;

import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_ContractState;
import ru.protei.portal.core.model.dict.En_ContractType;
import ru.protei.portal.core.model.dict.En_Currency;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dto.ProductDirectionInfo;
import ru.protei.portal.core.model.dto.ProjectInfo;
import ru.protei.portal.core.model.ent.Contract;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.struct.CostWithCurrencyWithVat;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.ContractControllerAsync;
import ru.protei.portal.ui.common.client.service.RegionControllerAsync;
import ru.protei.portal.ui.common.shared.model.DefaultErrorHandler;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.function.Consumer;

import static ru.protei.portal.core.model.util.ContractSupportService.getContractKind;
import static ru.protei.portal.core.model.helper.DateUtils.addDays;
import static ru.protei.portal.core.model.helper.DateUtils.getDaysBetween;

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
            view.cost().setValue(new CostWithCurrencyWithVat(0L, En_Currency.RUB, null));
        }
    }

    @Override
    public void onOrganizationChanged() {
        EntityOption organization = view.organization().getValue();
        boolean hasOrganization = organization != null;
        String organizationDisplayText = hasOrganization
                ? organization.getDisplayText()
                : null;
        view.contractorEnabled().setEnabled(hasOrganization);
        view.setOrganization(organizationDisplayText);
        if (view.contractor().getValue() != null) {
            view.contractor().setValue(null);
            fireEvent(new NotifyEvents.Show(lang.contractContractorDropped(), NotifyEvents.NotifyType.INFO));
        }
    }

    @Override
    public void onContractParentChanged() {
        boolean contractParentExists = view.contractParent().getValue() != null;
        view.setKind(getContractKind(contractParentExists));
    }

    @Override
    public void onCreateSecondContractToggle(boolean isEnabled) {
        boolean isNew = isNew(contract);
        syncSecondContractView(isNew, isEnabled);
    }

    @Override
    public void onSecondContractOrganizationChanged() {
        EntityOption organization = view.secondContractOrganization().getValue();
        boolean hasOrganization = organization != null;
        String organizationDisplayText = hasOrganization
                ? organization.getDisplayText()
                : null;
        view.secondContractContractorEnabled().setEnabled(organization != null);
        view.setSecondContractOrganization(organizationDisplayText);
        if (view.secondContractContractor().getValue() != null) {
            view.secondContractContractor().setValue(null);
            fireEvent(new NotifyEvents.Show(lang.contractContractorDropped(), NotifyEvents.NotifyType.INFO));
        }
    }

    @Override
    public void refreshProjectSpecificFields() {
        if (view.project().getValue() == null) {
            clearProjectSpecificFields();
            return;
        }

        fillProjectSpecificFieldsOnRefresh(view.project().getValue());
    }

    private void fillProject(ProjectInfo project) {
        view.project().setValue(project);
    }

    private void fillProjectSpecificFieldsOnRefresh(ProjectInfo project) {
        view.direction().setValue(project.getProductDirection() == null ? null : new ProductDirectionInfo(project.getProductDirection()));
        view.manager().setValue(project.getManager() == null ? null : new PersonShortView(project.getManager()));
    }

    private void projectRequest(Long projectId, Consumer<ProjectInfo> consumer) {
       regionService.getProjectInfo(projectId, new FluentCallback<ProjectInfo>().withSuccess(consumer));
    }

    private void clearProjectSpecificFields() {
        view.direction().setValue(null);
        view.manager().setValue(null);
    }

    private void requestData(Long id){
        contractService.getContract(id, new FluentCallback<Contract>()
                .withSuccess(this::fillView));
    }

    private void fillView(Contract value) {
        this.contract = value;
        boolean isNew = isNew(contract);

        view.type().setValue(contract.getContractType());
        if ( contract.getState() == null ) {
            contract.setState(En_ContractState.AGREEMENT);
        }
        view.state().setValue(contract.getState());
        view.number().setValue(contract.getNumber());
        if ( contract.getCost() == null ) {
            contract.setCost(0L);
        }
        view.cost().setValue(new CostWithCurrencyWithVat(contract.getCost(), contract.getCurrency(), contract.getVat()));
        view.description().setValue(contract.getDescription());
        view.curator().setValue(createPersonOrNull(contract.getCuratorId(), contract.getCuratorShortName()));
        view.dateSigning().setValue(contract.getDateSigning());
        view.dateValidDays().setValue(getDaysBetween(contract.getDateSigning(), contract.getDateValid()));
        view.contractDates().setValue(contract.getContractDates());
        view.contractSpecifications().setValue(contract.getContractSpecifications());

        view.organization().setValue(createOptionOrNull(contract.getOrganizationId(), contract.getOrganizationName()));
        view.contractParent().setValue(createOptionOrNull(contract.getParentContractId(), contract.getParentContractNumber()));

        boolean contractParentExists = contract.getParentContractId() != null;
        view.setKind(getContractKind(contractParentExists));

        if (contract.getProjectId() == null) {
            view.project().setValue(null);
            clearProjectSpecificFields();
        } else {
            projectRequest(contract.getProjectId(), this::fillProject);
            fillProjectSpecificFieldsOnRefresh(view.project().getValue());
        }

        view.directionEnabled().setEnabled(false);
        view.managerEnabled().setEnabled(false);

        view.contractorEnabled().setEnabled(contract.getOrganizationId() != null);
        view.setOrganization(contract.getOrganizationName());
        view.contractor().setValue(contract.getContractor());
        view.organizationEnabled().setEnabled(StringUtils.isBlank(contract.getRefKey()));

        syncSecondContractView(isNew, false);
    }

    private void fillDto() {
        contract.setContractType(view.type().getValue());
        contract.setState(view.state().getValue());
        contract.setNumber(view.number().getValue());
        contract.setCost(view.cost().getValue().getCost());
        contract.setCurrency(view.cost().getValue().getCurrency());
        contract.setVat(view.cost().getValue().getVatPercent());
        contract.setDescription(view.description().getValue());
        contract.setCuratorId(getPersonIdOrNull(view.curator().getValue()));
        contract.setDateSigning(view.dateSigning().getValue());
        contract.setDateValid(addDays(contract.getDateSigning(), view.dateValidDays().getValue()));
        contract.setContractDates(view.contractDates().getValue());
        contract.setContractSpecifications(view.contractSpecifications().getValue());

        contract.setOrganizationId(getOptionIdOrNull(view.organization().getValue()));
        contract.setOrganizationName(view.organization().getValue() == null ? "" : view.organization().getValue().getDisplayText());
        contract.setParentContractId(getOptionIdOrNull(view.contractParent().getValue()));

        contract.setProjectId(view.project().getValue() == null ? null : view.project().getValue().getId());
        contract.setContractor(view.contractor().getValue());

        if (contract.getProjectId() == null) {
            contract.setCaseManagerId(getPersonIdOrNull(view.manager().getValue()));
            contract.setCaseDirectionId(getProductIdOrNull(view.direction().getValue()));
        } else {
            contract.setCaseManagerId(null);
            contract.setCaseDirectionId(null);
        }
    }

    private void fillDtoSecondContract(Contract contract, Long parentContractId) {
        contract.setNumber(view.secondContractNumber().getValue());
        contract.setOrganizationId(getOptionIdOrNull(view.secondContractOrganization().getValue()));
        contract.setContractor(view.secondContractContractor().getValue());
        contract.setParentContractId(parentContractId);
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

        if (contract.getProjectId() == null)
            return lang.contractValidationEmptyProject();

        if (!view.validateContractSpecifications().isValid())
            return lang.contractValidationContractSpecification();

        boolean isNew = isNew(contract);
        boolean createExpenditureContract = isNew && view.secondContractCheckbox().getValue();
        if (createExpenditureContract) {
            if (StringUtils.isBlank(view.secondContractNumber().getValue()))
                return lang.contractValidationEmptyNumber();
            if (view.secondContractOrganization().getValue() == null)
                return lang.errFieldsRequired();
            if (view.secondContractContractor().getValue() == null)
                return lang.errFieldsRequired();
        }

        return null;
    }

    private void saveContract() {
        boolean isNew = isNew(contract);
        boolean createExpenditureContract = isNew && view.secondContractCheckbox().getValue();
        Runnable onDone = () -> {
            fireEvent(new ContractEvents.ChangeModel());
            fireEvent(new ProjectEvents.ChangeModel());
            fireEvent(new ContractEvents.Show(!isNew));
        };
        saveContract(contract,
            throwable -> {},
            id -> {
                if (createExpenditureContract) {
                    fillDtoSecondContract(contract, id);
                    saveContract(contract,
                        throwable -> onDone.run(),
                        id2 -> onDone.run()
                    );
                    return;
                }
                onDone.run();
            }
        );
    }

    private void saveContract(Contract contract, Consumer<Throwable> onFailure, Consumer<Long> onSuccess) {
        view.saveEnabled().setEnabled(false);
        contractService.saveContract(contract, new FluentCallback<Long>()
            .withError(throwable -> {
                view.saveEnabled().setEnabled(true);
                defaultErrorHandler.accept(throwable);
                onFailure.accept(throwable);
            })
            .withSuccess(id -> {
                view.saveEnabled().setEnabled(true);
                onSuccess.accept(id);
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

    private void syncSecondContractView(boolean isNew, boolean isEnabled) {
        boolean enabled = isNew && isEnabled;
        view.secondContractCheckbox().setValue(enabled);
        view.secondContractCheckboxVisibility().setVisible(isNew);
        view.secondContractVisibility().setVisible(enabled);
        view.secondContractNumber().setValue(null);
        view.secondContractOrganization().setValue(null);
        view.secondContractContractor().setValue(null);
        view.secondContractContractorEnabled().setEnabled(false);
        view.setSecondContractOrganization(null);
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
    @Inject
    DefaultErrorHandler defaultErrorHandler;

    private Contract contract;
    private AppEvents.InitDetails initDetails;
}
