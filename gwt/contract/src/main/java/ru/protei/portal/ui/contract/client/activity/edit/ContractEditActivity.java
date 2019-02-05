package ru.protei.portal.ui.contract.client.activity.edit;

import com.google.inject.Inject;
import ru.brainworm.factory.context.client.events.Back;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_EmploymentType;
import ru.protei.portal.core.model.dict.En_InternalResource;
import ru.protei.portal.core.model.ent.Contract;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.ContractEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.ContractControllerAsync;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.Date;
import java.util.HashSet;


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
        clearView();

        initDetails.parent.clear();
        initDetails.parent.add(view.asWidget());
    }

    @Override
    public void onSaveClicked() {
        Contract newContract = fillDto();
        if (getValidationError(newContract) != null) {
            showValidationError(newContract);
            return;
        }
        saveContract(newContract);
    }

    @Override
    public void onCancelClicked() {
        fireEvent(new Back());
    }

    private void showValidationError(Contract contract) {
        fireEvent(new NotifyEvents.Show(getValidationError(contract), NotifyEvents.NotifyType.ERROR));
    }

    private String getValidationError(Contract registration) {
        return null;
    }

    private void saveContract(Contract contract) {
        view.saveEnabled().setEnabled(false);
        contractService.createContract(contract, new RequestCallback<Long>() {
            @Override
            public void onError(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(lang.errNotCreated(), NotifyEvents.NotifyType.ERROR));
                view.saveEnabled().setEnabled(true);
            }

            @Override
            public void onSuccess(Long result) {
                fireEvent(new Back());
            }
        });
    }


    private Contract fillDto() {
        Contract q = new Contract();
        return q;
    }

    private void clearView() {
        view.saveEnabled().setEnabled(true);
    }

    @Inject
    private Lang lang;
    @Inject
    private AbstractContractEditView view;
    @Inject
    private ContractControllerAsync contractService;

    private AppEvents.InitDetails initDetails;
}
