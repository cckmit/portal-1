package ru.protei.portal.ui.contract.client.activity.edit;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.activity.client.enums.Type;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.dto.ProductDirectionInfo;
import ru.protei.portal.core.model.dto.ProjectInfo;
import ru.protei.portal.core.model.ent.Contract;
import ru.protei.portal.core.model.ent.Contractor;
import ru.protei.portal.core.model.struct.Money;
import ru.protei.portal.core.model.struct.MoneyWithCurrencyWithVat;
import ru.protei.portal.core.model.util.ContractSupportService;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.activity.casetag.taglist.AbstractCaseTagListActivity;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.ContractControllerAsync;
import ru.protei.portal.ui.common.client.service.RegionControllerAsync;
import ru.protei.portal.ui.common.shared.model.DefaultErrorHandler;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import static java.util.Collections.emptyList;
import static ru.protei.portal.core.model.helper.CollectionUtils.listOf;
import static ru.protei.portal.core.model.helper.StringUtils.isBlank;
import static ru.protei.portal.core.model.struct.Vat.NoVat;
import static ru.protei.portal.ui.common.client.util.DateUtils.*;

public abstract class ContractEditActivity implements Activity, AbstractContractEditActivity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
    }

    @Event
    public void onInitDetails(AppEvents.InitDetails initDetails) {
        this.initDetails = initDetails;
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

        if(event.id == null) {
            fillView(new Contract());
            return;
        }

        requestContract(event.id, this::fillView);
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
        setOrganization(organization, makePrimaryContractEditContractorView());
        updateOrganizationBasedOnParentContract(fillDto(contract), makeSecondaryContractEditContractorView());
    }

    @Override
    public void onContractParentChanged() {
        EntityOption contractParent = view.contractParent().getValue();
        Long contractParentId = contractParent != null
                ? contractParent.getId()
                : null;
        En_ContractKind kind = getContractKind(contractParent);
        view.setKind(kind);
        updateOrganizationBasedOnParentContract(contractParentId, makePrimaryContractEditContractorView(), () -> {
            updateOrganizationBasedOnParentContract(fillDto(contract), makeSecondaryContractEditContractorView());
        });
    }

    @Override
    public void onCreateSecondContractToggle(boolean isEnabled) {
        boolean isNew = isNew(contract);
        syncSecondContractView(isNew, isEnabled);
    }

    @Override
    public void onSecondContractOrganizationChanged() {
        EntityOption organization = view.secondContractOrganization().getValue();
        setOrganization(organization, makeSecondaryContractEditContractorView());
    }

    @Override
    public void onProjectChanged() {
        refreshProjectSpecificFields(view.project().getValue());
    }

    @Override
    public void onCostChanged() {
        MoneyWithCurrencyWithVat cost = view.cost().getValue();
        if (cost == null) {
            return;
        }
        Money money = cost.getMoney();
        view.contractDatesList().onContractCostChanged(money);
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

    private void requestContract(Long contractId, Consumer<Contract> consumer) {
        contractService.getContract(contractId, new FluentCallback<Contract>().withSuccess(consumer));
    }

    private void requestProject(Long projectId, Consumer<ProjectInfo> consumer) {
        regionService.getProjectInfo(projectId, new FluentCallback<ProjectInfo>().withSuccess(consumer));
    }

    private void fillProject(ProjectInfo project) {
        view.project().setValue(project, true);
    }

    private void refreshProjectSpecificFields(ProjectInfo project) {
        if (project == null) {
            clearProjectSpecificFields();
            return;
        }
        view.direction().setValue(project.getProductDirection() == null ? null : new ProductDirectionInfo(project.getProductDirection()));
        view.manager().setValue(project.getManager() == null ? null : new PersonShortView(project.getManager()));
    }

    private void clearProjectSpecificFields() {
        view.direction().setValue(null);
        view.manager().setValue(null);
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
            contract.setCost(new Money(0L));
        }
        view.cost().setValue(new MoneyWithCurrencyWithVat(contract.getCost(), contract.getCurrency(), contract.getVat()));
        view.description().setValue(contract.getDescription());
        view.curator().setValue(createPersonOrNull(contract.getCuratorId(), contract.getCuratorShortName()));
        view.dateSigning().setValue(contract.getDateSigning());
        view.dateValidDate().setValue(contract.getDateValid());
        view.dateValidDays().setValue(getDaysBetween(contract.getDateSigning(), contract.getDateValid()));
        view.contractDatesList().setContractCostSupplier(() -> view.cost().getValue().getMoney());
        view.contractDates().setValue(contract.getContractDates());
        view.contractSpecifications().setValue(contract.getContractSpecifications());

        view.contractParent().setValue(createOptionOrNull(contract.getParentContractId(), contract.getParentContractNumber()));
        view.organization().setValue(createOptionOrNull(contract.getOrganizationId(), contract.getOrganizationName()));
        view.setOrganization(contract.getOrganizationName());
        view.contractor().setValue(contract.getContractor());
        view.contractorEnabled().setEnabled(contract.getOrganizationId() != null);

        En_ContractKind kind = getContractKind(view.contractParent().getValue());
        view.setKind(kind);

        if (contract.getProjectId() == null) {
            view.project().setValue(null);
            clearProjectSpecificFields();
        } else {
            requestProject(contract.getProjectId(), this::fillProject);
        }

        if (!isNew) {
            fireEvent(new ContractEvents.ShowConciseTable(view.expenditureContractsContainer(), contract.getId()));
            view.expenditureContractsVisibility().setVisible(true);
        } else {
            view.expenditureContractsVisibility().setVisible(false);
        }

        if (isNew) {
            showTags(null);
        } else {
            showTags(contract.getId());
        }

        updateOrganizationBasedOnParentContract(contract.getParentContractId(), makePrimaryContractEditContractorView());
        syncSecondContractView(isNew, false);
    }

    private void showTags(Long contractId) {
        view.tagsVisibility().setVisible(true);
        if (contractId == null) {
            view.tagsVisibility().setVisible(false);
            return;
        }
        boolean readOnly = false;
        fireEvent(new CaseTagEvents.ShowList(view.tagsContainer(), En_CaseType.CONTRACT, contractId, readOnly, a -> tagListActivity = a));
    }

    private void fillDto() {
        fillDto(contract);
    }

    private Contract fillDto(Contract contract) {
        contract.setContractType(view.type().getValue());
        contract.setState(view.state().getValue());
        contract.setNumber(view.number().getValue());
        contract.setCost(view.cost().getValue().getMoney());
        contract.setCurrency(view.cost().getValue().getCurrency());
        contract.setVat(view.cost().getValue().getVatPercent());
        contract.setDescription(view.description().getValue());
        contract.setCuratorId(getPersonIdOrNull(view.curator().getValue()));
        contract.setDateSigning(view.dateSigning().getValue());
        contract.setDateValid(view.dateValidDate().getValue());
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

        return contract;
    }

    private void fillDtoSecondContract(Contract contract, Long parentContractId) {
        contract.setNumber(view.secondContractNumber().getValue());
        contract.setOrganizationId(getOptionIdOrNull(view.secondContractOrganization().getValue()));
        contract.setContractor(view.secondContractContractor().getValue());
        contract.setParentContractId(parentContractId);
    }

    private void updateOrganizationBasedOnParentContract(Long contractId, AbstractContractEditContractorView view) {
        updateOrganizationBasedOnParentContract(contractId, view, () -> {});
    }

    private void updateOrganizationBasedOnParentContract(Long contractId, AbstractContractEditContractorView view, Runnable onDone) {
        if (contractId == null) {
            updateOrganizationBasedOnParentContract((Contract) null, view);
            onDone.run();
        } else {
            requestContract(contractId, contract -> {
                updateOrganizationBasedOnParentContract(contract, view);
                onDone.run();
            });
        }
    }

    private void updateOrganizationBasedOnParentContract(Contract contract, AbstractContractEditContractorView view) {
        Long contractParentOrganizationId = contract != null
                ? contract.getOrganizationId()
                : null;
        if (contractParentOrganizationId == null) {
            view.setNotAvailableOrganizations(emptyList());
            return;
        }
        Long selectedOrganizationId = view.organization().getValue() != null
                ? view.organization().getValue().getId()
                : null;
        view.setNotAvailableOrganizations(listOf(contractParentOrganizationId));
        if (Objects.equals(selectedOrganizationId, contractParentOrganizationId)) {
            setOrganization(null, view);
            fireEvent(new NotifyEvents.Show(lang.contractOrganizationDropped(), NotifyEvents.NotifyType.INFO));
        }
    }

    private void setOrganization(EntityOption organization, AbstractContractEditContractorView view) {
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

        if (contract.getDateSigning() == null)
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

        boolean isNew = isNew(contract);
        boolean createExpenditureContract = isNew && view.secondContractCheckbox().getValue();
        if (createExpenditureContract) {
            if (isBlank(view.secondContractNumber().getValue()))
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

    private En_ContractKind getContractKind(EntityOption contractParent) {
        boolean contractParentExists = contractParent != null;
        return ContractSupportService.getContractKind(contractParentExists);
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
        updateOrganizationBasedOnParentContract(fillDto(contract), makeSecondaryContractEditContractorView());
    }

    private AbstractContractEditContractorView makePrimaryContractEditContractorView() {
        return new AbstractContractEditContractorView() {
            public void setOrganization(String organization) {
                view.setOrganization(organization);
            }
            public void setNotAvailableOrganizations(List<Long> organizationsToHide) {
                view.setNotAvailableOrganizations(organizationsToHide);
            }
            public HasValue<EntityOption> organization() {
                return view.organization();
            }
            public HasValue<Contractor> contractor() {
                return view.contractor();
            }
            public HasEnabled contractorEnabled() {
                return view.contractorEnabled();
            }
        };
    }

    private AbstractContractEditContractorView makeSecondaryContractEditContractorView() {
        return new AbstractContractEditContractorView() {
            public void setOrganization(String organization) {
                view.setSecondContractOrganization(organization);
            }
            public void setNotAvailableOrganizations(List<Long> organizationsToHide) {
                view.setSecondContractNotAvailableOrganizations(organizationsToHide);
            }
            public HasValue<EntityOption> organization() {
                return view.secondContractOrganization();
            }
            public HasValue<Contractor> contractor() {
                return view.secondContractContractor();
            }
            public HasEnabled contractorEnabled() {
                return view.secondContractContractorEnabled();
            }
        };
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
    private AbstractCaseTagListActivity tagListActivity;
    private AppEvents.InitDetails initDetails;
}
