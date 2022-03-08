package ru.protei.portal.ui.contract.client.activity.edit;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import ru.brainworm.factory.context.client.events.ReplaceLastEvent;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.activity.client.enums.Type;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.dto.ProjectInfo;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.ent.Contract;
import ru.protei.portal.core.model.struct.ContractInfo;
import ru.protei.portal.core.model.struct.Money;
import ru.protei.portal.core.model.struct.MoneyWithCurrencyWithVat;
import ru.protei.portal.core.model.util.ContractSupportService;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.activity.casetag.taglist.AbstractCaseTagListActivity;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.ConfigStorage;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.ContractControllerAsync;
import ru.protei.portal.ui.common.client.service.RegionControllerAsync;
import ru.protei.portal.ui.common.shared.model.DefaultErrorHandler;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.CollectionUtils.*;
import static ru.protei.portal.core.model.helper.StringUtils.isBlank;
import static ru.protei.portal.core.model.struct.Vat.NoVat;
import static ru.protei.portal.core.model.util.CrmConstants.State.AGREEMENT;
import static ru.protei.portal.ui.common.client.util.DateUtils.*;

public abstract class ContractEditActivity implements Activity, AbstractContractEditActivity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
    }

    @Event
    public void onAuthSuccess(AuthEvents.Success event) {
        view.initCuratorsSelector(configStorage.getConfigData().contractCuratorsDepartmentsIds);
    }

    @Event
    public void onInitDetails(AppEvents.InitDetails initDetails) {
        this.initDetails = initDetails;
        fireEvent(new ContractDateEvents.Init(() -> view.cost().getValue().getMoney(), () -> view.dateSigning().getValue()));
    }

    @Event(Type.FILL_CONTENT)
    public void onShow(ContractEvents.Edit event) {
        if (!hasPrivileges(event.id)) {
            fireEvent(new ErrorPageEvents.ShowForbidden(initDetails.parent));
            return;
        }

        initDetails.parent.clear();
        Window.scrollTo(0, 0);
        initDetails.parent.add(view.asWidget());

        if (event.id == null) {
            fillView(new Contract(), false);
            return;
        }

        requestContract(event.id, requestedContract -> fillView(requestedContract, event.isCopy));
    }

    @Event
    public void onAddedContractData(ContractDateEvents.Added event) {
        contract.getContractDates().add(event.value);
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
        List<En_ContractType> types = listOf(view.type().getValue());

        boolean isFrameworkContract =
                types.contains(En_ContractType.SUPPLY_AND_WORK_FRAMEWORK_CONTRACT) ||
                types.contains(En_ContractType.LICENSE_FRAMEWORK_CONTRACT) ||
                types.contains(En_ContractType.SUPPLY_FRAMEWORK_CONTRACT);

        view.costEnabled().setEnabled(!isFrameworkContract);
        if ( isFrameworkContract ) {
            view.cost().setValue(new MoneyWithCurrencyWithVat(new Money(0L), En_Currency.RUB, NoVat));
        }
    }

    @Override
    public void onOrganizationChanged() {
        EntityOption organization = view.organization().getValue();
        setOrganization(organization);
    }

    @Override
    public void onContractParentChanged() {
        ContractInfo contractParent = view.contractParent().getValue();
        En_ContractKind kind = getContractKind(contractParent);
        view.setKind(kind);
    }

    @Override
    public void onProjectChanged() {
        ProjectInfo project = view.project().getValue();
        fillProjectSpecificFields(project);

        if (project.getManager() != null && view.contractSignManager().getValue() == null) {
            view.contractSignManager().setValue(new PersonShortView(project.getManager()));
        }
    }

    @Override
    public void onCostChanged() {
        MoneyWithCurrencyWithVat cost = view.cost().getValue();
        if (cost == null) {
            return;
        }
        Money money = cost.getMoney();
        // TODO: нужно вызывать функцию проверки суммы в сроках поставок и оплат
    }

    @Override
    public void onDateSigningChanged(Date date) {
        Date validDate = view.dateValidDate().getValue();
        if (validDate != null) {
            Long validDays = getDaysBetween(date, validDate);
            view.dateValidDays().setValue(validDays);
        } else {
            Long validDays = view.dateValidDays().getValue();
            view.dateValidDate().setValue(addDays(date, validDays));
        }
    }

    @Override
    public void onDateValidChanged(Date date) {
        Date relative = view.dateSigning().getValue();
        Long days = getDaysBetween(relative, date);
        view.dateValidDays().setValue(days);
    }

    @Override
    public void onDateValidChanged(Long days) {
        Date relative = view.dateSigning().getValue();
        Date date = addDays(relative, days);
        view.dateValidDate().setValue(date);
    }

    @Override
    public void onAddTagsClicked(IsWidget target) {
        boolean isCanEditTags = true;
        fireEvent(new CaseTagEvents.ShowSelector(target.asWidget(), En_CaseType.CONTRACT, isCanEditTags, tagListActivity));
    }

    @Override
    public void onAddDateClicked() {
        fireEvent(new ContractDateEvents.ShowEdit());
    }

    private void requestContract(Long contractId, Consumer<Contract> consumer) {
        contractService.getContract(contractId, new FluentCallback<Contract>().withSuccess(consumer));
    }

    private void requestProject(Long projectId, Consumer<ProjectInfo> consumer) {
        regionService.getProjectInfo(projectId, new FluentCallback<ProjectInfo>().withSuccess(consumer));
    }

    private void fillProject(ProjectInfo project) {
        view.project().setValue(project);
        fillProjectSpecificFields(project);
    }

    private void fillProjectSpecificFields(ProjectInfo project) {
        if (project == null) {
            clearProjectSpecificFields();
            return;
        }
        view.setDirections(joining(project.getProductDirection(), ", ", EntityOption::getDisplayText));
        view.setProjectManager(project.getManager() == null ? "" : project.getManager().getName());
    }

    private void clearProjectSpecificFields() {
        view.setDirections("");
        view.setProjectManager("");
    }

    private void fillView(Contract value, boolean isCopy) {
        this.contract = value;
        boolean isNew = isCopy || isNew(contract) ;

        view.type().setValue(contract.getContractType());
        if ( contract.getStateId() == null ) {
            contract.setStateId(AGREEMENT);
        }
        view.state().setValue(new CaseState(contract.getStateId(), contract.getStateName()));
        view.number().setValue(contract.getNumber());
        if ( contract.getCost() == null ) {
            contract.setCost(new Money(0L));
        }
        view.cost().setValue(new MoneyWithCurrencyWithVat(contract.getCost(), contract.getCurrency(), contract.getVat()));
        view.description().setValue(contract.getDescription());
        view.curator().setValue(createPersonOrNull(contract.getCuratorId(), contract.getCuratorShortName()));
        view.dateSigning().setValue(contract.getDateSigning());
        view.dateValidDate().setValue(contract.getDateValid());
        view.dateValidDays().setValue(getDaysBetween(contract.getDateSigning(), contract.getDateValid()));

        if (isNew && isNotEmpty(contract.getContractSpecifications())) {
            contract.setContractSpecifications(
                    contract.getContractSpecifications().stream()
                            .peek(contractSpecification -> {
                                contractSpecification.setId(null);
                                contractSpecification.setContractId(null);
                            })
                            .collect(Collectors.toList())
            );
        }
        view.contractSpecifications().setValue(contract.getContractSpecifications());

        view.contractParent().setValue(createContractInfoOrNull(contract.getParentContractId(), contract.getParentContractNumber()));
        view.organization().setValue(createOptionOrNull(contract.getOrganizationId(), contract.getOrganizationName()));
        view.setOrganization(contract.getOrganizationName());
        view.contractor().setValue(contract.getContractor());
        view.contractorEnabled().setEnabled(contract.getOrganizationId() != null);
        view.contractSignManager().setValue(createPersonOrNull(contract.getContractSignManagerId(), contract.getContractSignManagerShortName()));
        view.setKind(getContractKind(view.contractParent().getValue()));
        view.deliveryNumber().setValue(contract.getDeliveryNumber());
        view.dateExecution().setValue(contract.getDateExecution());
        view.dateEndWarranty().setValue(contract.getDateEndWarranty());

        if (contract.getProjectId() == null) {
            view.project().setValue(null);
            clearProjectSpecificFields();
        } else {
            requestProject(contract.getProjectId(), this::fillProject);
        }

        view.tagsVisibility().setVisible(!isNew);
        view.tagsButtonVisibility().setVisible(!isNew);
        if (isNew) {
            view.expenditureContractsVisibility().setVisible(false);
        } else {
            fireEvent(new CaseTagEvents.ShowList(view.tagsContainer(), En_CaseType.CONTRACT, contract.getId(), false, a -> tagListActivity = a));
            fireEvent(new ContractEvents.ShowConciseTable(view.expenditureContractsContainer(), contract.getId()));
        }
        if (contract.getContractDates() == null) {
            contract.setContractDates(new ArrayList<>());
        } else if (isNew) {
            contract.setContractDates(
                    stream(contract.getContractDates()).peek(contractDate -> {
                        contractDate.setId(null);
                        contractDate.setContractId(null);
                    }).collect(Collectors.toList())
            );
        }
        fireEvent(new ContractDateEvents.ShowTable(view.getContractDateTableContainer(), contract.getContractDates()));

        if (isCopy) {
            contract.setId(null);
        }
    }

    private Contract fillDto() {
        contract.setContractType(view.type().getValue());
        contract.setStateId(view.state().getValue().getId());
        contract.setNumber(view.number().getValue());
        contract.setCost(view.cost().getValue().getMoney());
        contract.setCurrency(view.cost().getValue().getCurrency());
        contract.setVat(view.cost().getValue().getVatPercent());
        contract.setDescription(view.description().getValue());
        contract.setCuratorId(getPersonIdOrNull(view.curator().getValue()));
        contract.setDateSigning(view.dateSigning().getValue());
        contract.setDateValid(view.dateValidDate().getValue());
        contract.setContractSpecifications(view.contractSpecifications().getValue());
        contract.setOrganizationId(getOptionIdOrNull(view.organization().getValue()));
        contract.setOrganizationName(view.organization().getValue() == null ? "" : view.organization().getValue().getDisplayText());
        contract.setParentContractId(getContractIdOrNull(view.contractParent().getValue()));
        contract.setProjectId(view.project().getValue() == null ? null : view.project().getValue().getId());
        contract.setContractor(view.contractor().getValue());
        contract.setContractSignManagerId(getPersonIdOrNull(view.contractSignManager().getValue()));
        contract.setDeliveryNumber(view.deliveryNumber().getValue());
        contract.setDateEndWarranty(view.dateEndWarranty().getValue());
        contract.setDateExecution(view.dateExecution().getValue());

        return contract;
    }

    private void setOrganization(EntityOption organization) {
        boolean hasOrganization = organization != null;
        String organizationDisplayText = hasOrganization
                ? organization.getDisplayText()
                : null;
        view.organization().setValue(organization);
        view.setOrganization(organizationDisplayText);
        view.contractorEnabled().setEnabled(hasOrganization);
        if (view.contractor().getValue() != null) {
            view.contractor().setValue(null);
            fireEvent(new NotifyEvents.Show(lang.contractContractorDropped(), NotifyEvents.NotifyType.INFO));
        }
    }

    private void showValidationError() {
        fireEvent(new NotifyEvents.Show(getValidationError(), NotifyEvents.NotifyType.ERROR));
    }

    private String getValidationError() {
        if (isBlank(contract.getNumber()))
            return lang.contractValidationEmptyNumber();

        if (isBlank(contract.getDescription()))
            return lang.contractValidationEmptyDescription();

        if (contract.getContractType() == null)
            return lang.contractValidationEmptyType();

        if (contract.getStateId() == null)
            return lang.contractValidationEmptyState();

        if (contract.getDateSigning() == null && !(contract.getStateId().equals(AGREEMENT)))
            return lang.contractValidationEmptyDateSigning();

        if (contract.getDateSigning() != null && contract.getDateValid() != null &&
                isBefore(contract.getDateSigning(), contract.getDateValid()))
            return lang.contractValidationInvalidDateValid();

        if (contract.getCost() == null || contract.getCost().getFull() < 0)
            return lang.contractValidationInvalidCost();

        if (contract.getProjectId() == null)
            return lang.contractValidationEmptyProject();

        if (!view.validateContractSpecifications().isValid())
            return lang.contractValidationContractSpecification();

        return null;
    }

    private void saveContract() {
        final boolean isNew = isNew(contract);
        Consumer<Long> onDone = id -> {
                ContractEditActivity.this.fireEvent(new ContractEvents.ChangeModel());
                ContractEditActivity.this.fireEvent(new ProjectEvents.ChangeModel());
                if (!isNew) {
                    ContractEditActivity.this.fireEvent(new ContractEvents.Show(true));
                } else {
                    ContractEditActivity.this.fireEvent(new ReplaceLastEvent(new ContractEvents.Edit(id)));
                }
        };

        saveContract(contract,
            throwable -> {}, onDone
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

    private En_ContractKind getContractKind(ContractInfo contractParent) {
        boolean contractParentExists = contractParent != null;
        return ContractSupportService.getContractKind(contractParentExists);
    }

    private boolean isNew(Contract contract) {
        return contract == null || contract.getId() == null;
    }

    private Long getOptionIdOrNull(EntityOption option) {
        return option == null ? null : option.getId();
    }

    private Long getContractIdOrNull(ContractInfo info) {
        return info == null ? null : info.getId();
    }

    private Long getPersonIdOrNull(PersonShortView option) {
        return option == null ? null : option.getId();
    }

    private EntityOption createOptionOrNull(Long id, String name) {
        return id == null ? null : new EntityOption(name, id);
    }
    private ContractInfo createContractInfoOrNull(Long id, String name) {
        return id == null ? null : new ContractInfo(id, name, null);
    }

    private PersonShortView createPersonOrNull(Long id, String name) {
        return id == null ? null : new PersonShortView(name, id);
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
    @Inject
    DefaultErrorHandler defaultErrorHandler;
    @Inject
    ConfigStorage configStorage;

    private Contract contract;
    private AbstractCaseTagListActivity tagListActivity;
    private AppEvents.InitDetails initDetails;
}
