package ru.protei.portal.ui.contract.client.activity.edit;

import com.google.inject.Inject;
import ru.brainworm.factory.context.client.events.Back;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_ContractState;
import ru.protei.portal.core.model.dict.En_ContractType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.Contract;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.struct.ProductDirectionInfo;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.ContractEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.ContractControllerAsync;
import ru.protei.portal.ui.common.shared.model.DefaultErrorHandler;
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
        initDetails.parent.clear();

        if ( policyService.hasAnyPrivilegeOf( En_Privilege.CONTRACT_CREATE, En_Privilege.CONTRACT_EDIT )) {
            return;
        }
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
        fireEvent(new Back());
    }

    @Override
    public void onTypeChanged() {
        En_ContractType type = view.type().getValue();

        boolean isFrameworkContract = En_ContractType.SUPPLY_AND_WORK_FRAMEWORK_CONTRACT.equals(type)
                || En_ContractType.LICENSE_FRAMEWORK_CONTRACT.equals(type)
                || En_ContractType.SUPPLY_FRAMEWORK_CONTRACT.equals(type);

        view.costEnabled().setEnabled(!isFrameworkContract);
        if ( isFrameworkContract ) {
            view.cost().setValue(0L);
        }
    }

    private void requestData(Long id){
        contractService.getContract(id, new FluentCallback<Contract>()
                .withError(throwable -> errorHandler.accept(throwable))
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
        view.cost().setValue(contract.getCost());
        view.description().setValue(contract.getDescription());

        view.contragent().setValue(createOptionOrNull(contract.getContragentId(), contract.getContragentName()));
        view.manager().setValue(createPersonOrNull(contract.getManagerId(), contract.getManagerShortName()));
        view.curator().setValue(createPersonOrNull(contract.getCuratorId(), contract.getCuratorShortName()));
        view.direction().setValue(createProductOrNull(contract.getDirectionId(), contract.getDirectionName()));

        view.dateSigning().setValue(contract.getDateSigning());
        view.dateValid().setValue(contract.getDateValid());
        view.contractDates().setValue(contract.getContractDates());
    }

    private void fillDto() {
        contract.setContractType(view.type().getValue());
        contract.setState(view.state().getValue());
        contract.setNumber(view.number().getValue());
        contract.setCost(view.cost().getValue());
        contract.setDescription(view.description().getValue());

        contract.setContragentId(getOptionIdOrNull(view.contragent().getValue()));
        contract.setManagerId(getPersonIdOrNull(view.manager().getValue()));
        contract.setCuratorId(getPersonIdOrNull(view.curator().getValue()));
        contract.setDirectionId(getProductIdOrNull(view.direction().getValue()));

        contract.setDateSigning(view.dateSigning().getValue());
        contract.setDateValid(view.dateValid().getValue());
        contract.setContractDates(view.contractDates().getValue());
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

        if (contract.getDirectionId() == null)
            return lang.contractValidationEmptyDirection();

        return null;
    }

    private void saveContract() {
        view.saveEnabled().setEnabled(false);
        contractService.saveContract(contract, new FluentCallback<Long>()
                .withError(throwable -> {
                    errorHandler.accept(throwable);
                    view.saveEnabled().setEnabled(true);
                })
                .withSuccess(value -> {
                    view.saveEnabled().setEnabled(true);
                    fireEvent(new Back());
                }));
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

    @Inject
    private Lang lang;
    @Inject
    private AbstractContractEditView view;
    @Inject
    private ContractControllerAsync contractService;
    @Inject
    private DefaultErrorHandler errorHandler;
    @Inject
    PolicyService policyService;

    private Contract contract;
    private AppEvents.InitDetails initDetails;
}
