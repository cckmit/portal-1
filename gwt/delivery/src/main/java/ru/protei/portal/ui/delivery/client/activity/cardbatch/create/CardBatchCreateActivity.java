package ru.protei.portal.ui.delivery.client.activity.cardbatch.create;

import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import ru.brainworm.factory.context.client.events.Back;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.activity.client.enums.Type;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.CardBatch;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.CardBatchControllerAsync;
import ru.protei.portal.ui.common.client.service.CaseStateControllerAsync;
import ru.protei.portal.ui.common.shared.model.DefaultErrorHandler;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.delivery.client.activity.cardbatch.common.AbstractCardBatchCommonInfoActivity;
import ru.protei.portal.ui.delivery.client.activity.cardbatch.common.AbstractCardBatchCommonInfoView;
import ru.protei.portal.ui.delivery.client.activity.cardbatch.meta.AbstractCardBatchMetaActivity;
import ru.protei.portal.ui.delivery.client.activity.cardbatch.meta.AbstractCardBatchMetaView;
import ru.protei.portal.ui.delivery.client.activity.delivery.meta.DeliveryCommonMeta;

import java.util.Date;
import java.util.function.Consumer;

import static ru.protei.portal.core.model.helper.StringUtils.isEmpty;
import static ru.protei.portal.core.model.helper.StringUtils.isNotEmpty;
import static ru.protei.portal.ui.common.client.events.NotifyEvents.NotifyType.ERROR;
import static ru.protei.portal.ui.common.client.events.NotifyEvents.NotifyType.SUCCESS;

public abstract class CardBatchCreateActivity implements Activity,
        AbstractCardBatchCreateActivity, AbstractCardBatchCommonInfoActivity, AbstractCardBatchMetaActivity {

    @Inject
    public void onInit() {
        view.setActivity(this);
        commonInfoView.setActivity(this);
        metaView.setActivity(this);

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
        view.getCommonInfoContainer().add(commonInfoView);
        view.getMetaContainer().add(metaView);

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
    public void onCardTypeChanged(Long cardTypeId) {

        cardBatchService.getLastCardBatch(cardTypeId, new FluentCallback<CardBatch>()
                        .withError(defaultErrorHandler)
                        .withSuccess(getLastCardBatchConsumer));
    }

    @Override
    public void getCaseState(Long id, Consumer<CaseState> success) {
        caseStateService.getCaseStateWithoutCompaniesOmitPrivileges(id, new FluentCallback<CaseState>()
                .withError(defaultErrorHandler)
                .withSuccess(success));
    }

    @Override
    public void onStateChange() {

    }

    @Override
    public void onDeadlineChanged() {

    }

    private void prepare() {
        commonInfoView.type().setValue(null);
        commonInfoView.number().setValue(null);
        commonInfoView.article().setValue(null);
        commonInfoView.amount().setValue(null);
        commonInfoView.params().setValue(null);
        commonInfoView.hidePrevCardBatchInfo();
        metaView.deadline().setValue(null);
        metaView.stateEnable().setEnabled(false);
        fillStateSelector(CrmConstants.State.PRELIMINARY);
    }

    private CardBatch fillDto() {
        CardBatch cardBatch = new CardBatch();
        cardBatch.setTypeId(commonInfoView.type().getValue().getId());
        cardBatch.setNumber(commonInfoView.number().getValue());
        cardBatch.setArticle(commonInfoView.article().getValue());
        cardBatch.setAmount(commonInfoView.amount().getValue());
        cardBatch.setParams(commonInfoView.params().getValue());
        cardBatch.setStateId(metaView.state().getValue().getId());
        cardBatch.setDeadline(metaView.deadline().getValue() != null? metaView.deadline().getValue().getTime() : null);

        //TODO remove stub
        cardBatch.setImportance(1L);

        return cardBatch;
    }

    private void showValidationError(String error) {
        fireEvent(new NotifyEvents.Show(error, ERROR));
    }

    private String getValidationError() {
        if (null == commonInfoView.type().getValue()) {
            return "Тип не может быть пустым";
        }

        if (isEmpty(commonInfoView.number().getValue()) || !commonInfoView.isNumberValid()) {
            return "Номер должен быть валиден";
        }

        if (isEmpty(commonInfoView.article().getValue()) || !commonInfoView.isArticleValid()) {
            return "Артикул должен быть валиден";
        }

        if (null == commonInfoView.amount().getValue() || commonInfoView.amount().getValue() <= 0) {
            return "Количество должно быть больше нуля";
        }

//        String error = commonMeta.getValidationError();
//        if (error != null) {
//            return error;
//        }
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
                fireEvent(new NotifyEvents.Show(lang.cardBatchCreated(), SUCCESS));
                onSuccess.run();
            }));
    }

    private boolean hasPrivileges() {
        return policyService.hasPrivilegeFor(En_Privilege.DELIVERY_CREATE);
    }

    private void fillStateSelector(Long id) {
        metaView.state().setValue(new CaseState(id));
        getCaseState(id, caseState -> metaView.state().setValue(caseState));
    }

    Consumer<CardBatch> getLastCardBatchConsumer = new Consumer<CardBatch>() {
        @Override
        public void accept(CardBatch lastNumberCardBatch) {
            String releaseNumber = START_CARD_BATCH_NUMBER;

            if (isNotEmpty(lastNumberCardBatch.getNumber())){
                releaseNumber = getNextNumber(lastNumberCardBatch.getNumber());
            }

            commonInfoView.number().setValue(releaseNumber);

            if (isEmpty(releaseNumber)){
                commonInfoView.hidePrevCardBatchInfo();
                return;
            }

            commonInfoView.setPrevCardBatchInfo(lastNumberCardBatch.getNumber(), lastNumberCardBatch.getAmount(), lastNumberCardBatch.getState().getState());
        }
    };

    private String getNextNumber(String lastNumberStr) {

        int lastNumber;
        try {
            lastNumber = Integer.parseInt(lastNumberStr) + 1;
        } catch (NumberFormatException e){
            fireEvent(new NotifyEvents.Show("Ошибка при получении следующего номера партии плат", ERROR));
            return "";
        }

        if (lastNumber >= CARD_BATCH_MAX_NUMBER){
            fireEvent(new NotifyEvents.Show("Невозможно выделить следующий номер партии плат. Превышено ограничение по номерам: " + CARD_BATCH_MAX_NUMBER, ERROR));
            return "";
        }

        return addLeadingZeros(lastNumber, CARD_BATCH_NUMBER_LENGTH);
    }

    public static native String addLeadingZeros(int num, int number_length) /*-{
        return num.toString().padStart(number_length, "0");
    }-*/;

    @Inject
    private Lang lang;
    @Inject
    private AbstractCardBatchCreateView view;
    @Inject
    AbstractCardBatchCommonInfoView commonInfoView;
    @Inject
    AbstractCardBatchMetaView metaView;
    @Inject
    private CardBatchControllerAsync cardBatchService;
    @Inject
    private CaseStateControllerAsync caseStateService;
    @Inject
    private PolicyService policyService;

    @Inject
    private DefaultErrorHandler defaultErrorHandler;

    private AppEvents.InitDetails initDetails;
    private static final String START_CARD_BATCH_NUMBER = "001";
    private static final int CARD_BATCH_MAX_NUMBER = 999;
    private static final int CARD_BATCH_NUMBER_LENGTH = 3;
}
