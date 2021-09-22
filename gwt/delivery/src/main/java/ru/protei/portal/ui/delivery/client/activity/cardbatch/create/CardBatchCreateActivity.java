package ru.protei.portal.ui.delivery.client.activity.cardbatch.create;

import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import ru.brainworm.factory.context.client.events.Back;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.activity.client.enums.Type;
import ru.protei.portal.core.model.dict.En_CustomerType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dto.ProjectInfo;
import ru.protei.portal.core.model.ent.CardBatch;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.ent.Delivery;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.CardBatchControllerAsync;
import ru.protei.portal.ui.common.client.service.CaseStateControllerAsync;
import ru.protei.portal.ui.common.client.service.DeliveryControllerAsync;
import ru.protei.portal.ui.common.shared.model.DefaultErrorHandler;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.delivery.client.activity.delivery.meta.DeliveryCommonMeta;
import ru.protei.portal.ui.delivery.client.view.delivery.meta.DeliveryMetaView;
import ru.protei.portal.ui.delivery.client.widget.kit.list.DeliveryKitList;

import java.util.Objects;
import java.util.function.Consumer;

import static ru.protei.portal.core.model.helper.StringUtils.isBlank;

public abstract class CardBatchCreateActivity implements Activity, AbstractCardBatchCreateActivity {

    @Inject
    public void onInit() {
        view.setActivity(this);

//        DeliveryMetaView metaView = view.getMetaView();
//        commonMeta.setDeliveryMetaView(metaView);
//        view.getMetaView().setActivity(commonMeta);
    }

    @Event
    public void onInitDetails(AppEvents.InitDetails initDetails) {
        this.initDetails = initDetails;
    }

    @Event(Type.FILL_CONTENT)
    public void onShow(CardBatchEvents.Create event) {
        if (!hasPrivileges()) {
            fireEvent(new ErrorPageEvents.ShowForbidden(initDetails.parent));
            return;
        }

        initDetails.parent.clear();
        Window.scrollTo(0, 0);
        initDetails.parent.add(view.asWidget());

        prepare();
    }

    @Override
    public void onSaveClicked() {
        String error = getValidationError();
        if (error != null) {
            showValidationError(error);
            return;
        }
        CardBatch cardBatch = fillDto();
        save(cardBatch);
    }

    @Override
    public void onCancelClicked() {
        fireEvent(new Back());
    }

    @Override
    public void getLastNumber(Consumer<String> success) {
//        ProjectInfo projectInfo = view.type().getValue();
//        if (projectInfo == null) {
//            return;
//        }
//        cardBatchService.getLastNumber(projectInfo.getCustomerType() == En_CustomerType.MINISTRY_OF_DEFENCE, new FluentCallback<String>()
//                .withError(defaultErrorHandler)
//                .withSuccess(success));
    }

    @Override
    public void getCaseState(Long id, Consumer<CaseState> success) {
        caseStateService.getCaseStateWithoutCompaniesOmitPrivileges(id, new FluentCallback<CaseState>()
                .withError(defaultErrorHandler)
                .withSuccess(success));
    }

    private void prepare() {
        view.number().setValue(null);
        view.params().setValue(null);
        fillStateSelector(CrmConstants.State.PRELIMINARY);
    }

    private CardBatch fillDto() {
        CardBatch cardBatch = new CardBatch();
        cardBatch.setNumber(view.number().getValue());
        cardBatch.setParams(view.params().getValue());
//        cardBatch.setStateId(view.state().getValue().getId());
//        cardBatch.setType(view.type().getValue());
        return cardBatch;
    }

    private void showValidationError(String error) {
        fireEvent(new NotifyEvents.Show(error, NotifyEvents.NotifyType.ERROR));
    }

    private String getValidationError() {
        if (isBlank(view.number().getValue())) {
            return lang.deliveryValidationEmptyName();
        }

        String error = commonMeta.getValidationError();
        if (error != null) {
            return error;
        }
//        CaseState state = view.state().getValue();
//         if (!Objects.equals(CrmConstants.State.PRELIMINARY, state.getId())) {
//            return lang.deliveryValidationInvalidStateAtCreate();
//        }

        return null;
    }

    private void save(CardBatch cardBatch) {
        save(cardBatch, throwable -> {}, () -> {
            fireEvent(new Back());
        });
    }

    private void save(CardBatch cardBatch, Consumer<Throwable> onFailure, Runnable onSuccess) {
        view.saveEnabled().setEnabled(false);
        cardBatchService.saveCardBatch(cardBatch, new FluentCallback<CardBatch>()
            .withError(throwable -> {
                view.saveEnabled().setEnabled(true);
                defaultErrorHandler.accept(throwable);
                onFailure.accept(throwable);
            })
            .withSuccess(id -> {
                view.saveEnabled().setEnabled(true);
                onSuccess.run();
            }));
    }

    private boolean hasPrivileges() {
        return policyService.hasPrivilegeFor(En_Privilege.DELIVERY_CREATE);
    }

    private void fillStateSelector(Long id) {
//        view.state().setValue(new CaseState(id));
//        getCaseState(id, caseState -> view.state().setValue(caseState));
    }

    @Inject
    private Lang lang;
    @Inject
    private AbstractCardBatchCreateView view;
    @Inject
    private DeliveryCommonMeta commonMeta;
    @Inject
    private CardBatchControllerAsync cardBatchService;
    @Inject
    private CaseStateControllerAsync caseStateService;
    @Inject
    private PolicyService policyService;
    @Inject
    private DefaultErrorHandler defaultErrorHandler;

    private AppEvents.InitDetails initDetails;
}
