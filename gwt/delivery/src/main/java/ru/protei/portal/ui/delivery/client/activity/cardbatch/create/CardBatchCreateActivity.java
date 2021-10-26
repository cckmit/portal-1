package ru.protei.portal.ui.delivery.client.activity.cardbatch.create;

import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import ru.brainworm.factory.context.client.events.Back;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.activity.client.enums.Type;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.CardBatch;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.ent.ImportanceLevel;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.CardBatchEvents;
import ru.protei.portal.ui.common.client.events.ErrorPageEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.CardBatchStateLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.CardBatchControllerAsync;
import ru.protei.portal.ui.common.client.service.CaseStateControllerAsync;
import ru.protei.portal.ui.common.client.service.ImportanceLevelControllerAsync;
import ru.protei.portal.ui.common.shared.model.DefaultErrorHandler;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.delivery.client.activity.cardbatch.common.AbstractCardBatchCommonInfoEditActivity;
import ru.protei.portal.ui.delivery.client.activity.cardbatch.common.AbstractCardBatchCommonInfoEditView;
import ru.protei.portal.ui.delivery.client.activity.cardbatch.meta.AbstractCardBatchMetaActivity;
import ru.protei.portal.ui.delivery.client.activity.cardbatch.meta.AbstractCardBatchMetaView;

import java.util.ArrayList;
import java.util.Date;
import java.util.function.Consumer;

import static ru.protei.portal.core.model.helper.StringUtils.isEmpty;
import static ru.protei.portal.core.model.helper.StringUtils.isNotEmpty;
import static ru.protei.portal.core.model.util.CrmConstants.ImportanceLevel.BASIC;
import static ru.protei.portal.ui.common.client.events.NotifyEvents.NotifyType.ERROR;
import static ru.protei.portal.ui.common.client.events.NotifyEvents.NotifyType.SUCCESS;

public abstract class CardBatchCreateActivity implements Activity,
        AbstractCardBatchCreateActivity, AbstractCardBatchCommonInfoEditActivity, AbstractCardBatchMetaActivity {

    @Inject
    public void onInit() {
        view.setActivity(this);

        metaView.setActivity(this);
        view.getMetaContainer().add(metaView);

        commonInfoEditView.setActivity(this);
        commonInfoEditView.typeEnabled().setEnabled(true);
        commonInfoEditView.buttonsContainerVisibility().setVisible(false);
        view.getCommonInfoContainer().add(commonInfoEditView);
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
    public void onCardTypeChanged(Long cardTypeId) {

        cardBatchService.getLastCardBatch(cardTypeId, new FluentCallback<CardBatch>()
                        .withError(defaultErrorHandler)
                        .withSuccess(this::lastCardBatchConsumer));
    }

    @Override
    public void getCaseState(Long id, Consumer<CaseState> success) {
        caseStateService.getCaseStateWithoutCompaniesOmitPrivileges(id, new FluentCallback<CaseState>()
                .withError(defaultErrorHandler)
                .withSuccess(success));
    }

    @Override
    public void onDeadlineChanged() {
        validateDeadline();
    }

    @Override
    public void onAmountChanged() {
        validateAmount();
    }

    private boolean validateDeadline() {
        Date deadline = metaView.deadline().getValue();
        boolean isValid = deadline != null && deadline.after(new Date());
        metaView.setDeadlineValid(isValid);
        return isValid;
    }

    private boolean validateAmount() {
        Integer value = commonInfoEditView.amount().getValue();
        boolean isValid = value != null && value > 0;
        commonInfoEditView.setAmountValid(isValid);
        return isValid;
    }

    private void prepare() {
        commonInfoEditView.type().setValue(null);
        commonInfoEditView.number().setValue(null);
        commonInfoEditView.article().setValue(null);
        commonInfoEditView.amount().setValue(null);
        commonInfoEditView.params().setValue(null);
        commonInfoEditView.contractors().setValue(null);
        commonInfoEditView.hidePrevCardBatchInfo();
        metaView.deadline().setValue(null);
        metaView.setDeadlineValid(true);
        metaView.stateEnable().setEnabled(false);
        fillPrioritySelector(BASIC);
        fillStateSelector(CrmConstants.State.BUILD_EQUIPMENT_IN_QUEUE);
    }

    private CardBatch fillDto() {
        CardBatch cardBatch = new CardBatch();
        cardBatch.setTypeId(commonInfoEditView.type().getValue().getId());
        cardBatch.setNumber(commonInfoEditView.number().getValue());
        cardBatch.setArticle(commonInfoEditView.article().getValue());
        cardBatch.setAmount(commonInfoEditView.amount().getValue());
        cardBatch.setParams(commonInfoEditView.params().getValue());
        cardBatch.setContractors(commonInfoEditView.contractors().getValue());
        cardBatch.setStateId(metaView.state().getValue().getId());
        cardBatch.setImportance(metaView.priority().getValue().getId());
        cardBatch.setDeadline(metaView.deadline().getValue() != null? metaView.deadline().getValue().getTime() : null);

        return cardBatch;
    }

    private void showValidationError(String error) {
        fireEvent(new NotifyEvents.Show(error, ERROR));
    }

    private String getValidationError() {
        if (null == commonInfoEditView.type().getValue()) {
            return lang.cardBatchTypeValidationError();
        }

        if (isEmpty(commonInfoEditView.number().getValue()) || !commonInfoEditView.isNumberValid()) {
            return lang.cardBatchNumberValidationError();
        }

        if (isEmpty(commonInfoEditView.article().getValue()) || !commonInfoEditView.isArticleValid()) {
            return lang.cardBatchArticleValidationError();
        }

        if (CollectionUtils.isEmpty(commonInfoEditView.contractors().getValue())) {
            return lang.cardBatchContractorsValidationError();
        }

        if (!validateAmount()) {
            return lang.cardBatchAmountValidationError();
        }

        if (!validateDeadline()) {
            return lang.cardBatchDeadlineValidationError();
        }

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
        return policyService.hasPrivilegeFor(En_Privilege.CARD_BATCH_CREATE);
    }

    private void fillStateSelector(Long id) {
        metaView.state().setValue(new CaseState(id));
        getCaseState(id, caseState -> metaView.state().setValue(caseState));
    }

    private void fillPrioritySelector(Integer id) {
        importanceService.getImportanceLevel( id, new FluentCallback<ImportanceLevel>()
                        .withError(defaultErrorHandler)
                        .withSuccess(level -> metaView.priority().setValue(level)));
    }

    private void lastCardBatchConsumer(CardBatch lastNumberCardBatch) {
        String releaseNumber = START_CARD_BATCH_NUMBER;

        if (lastNumberCardBatch != null && isNotEmpty(lastNumberCardBatch.getNumber())){
            releaseNumber = getNextNumber(lastNumberCardBatch.getNumber());
        }

        commonInfoEditView.number().setValue(releaseNumber);

        if (lastNumberCardBatch == null || isEmpty(releaseNumber)){
            commonInfoEditView.hidePrevCardBatchInfo();
            return;
        }

        commonInfoEditView.setPrevCardBatchInfo(lastNumberCardBatch.getNumber(), lastNumberCardBatch.getAmount(), cardBatchStateLang.getStateName(lastNumberCardBatch.getState()));
    };

    private String getNextNumber(String lastNumberStr) {

        int lastNumber;
        try {
            lastNumber = Integer.parseInt(lastNumberStr) + 1;
        } catch (NumberFormatException e){
            fireEvent(new NotifyEvents.Show(lang.cardBatchGetLastNumberError(), ERROR));
            return "";
        }

        if (lastNumber >= CARD_BATCH_MAX_NUMBER){
            fireEvent(new NotifyEvents.Show(lang.cardBatchNumberExceedLimitError() + CARD_BATCH_MAX_NUMBER, ERROR));
            return "";
        }

        return NumberFormat.getFormat(CARD_BATCH_NUMBER_PATTERN).format(lastNumber);
    }

    @Inject
    private Lang lang;
    @Inject
    private AbstractCardBatchCreateView view;
    @Inject
    AbstractCardBatchCommonInfoEditView commonInfoEditView;
    @Inject
    AbstractCardBatchMetaView metaView;
    @Inject
    private CardBatchControllerAsync cardBatchService;
    @Inject
    private CaseStateControllerAsync caseStateService;
    @Inject
    private PolicyService policyService;
    @Inject
    ImportanceLevelControllerAsync importanceService;
    @Inject
    CardBatchStateLang cardBatchStateLang;
    @Inject
    private DefaultErrorHandler defaultErrorHandler;

    private AppEvents.InitDetails initDetails;
    private static final String START_CARD_BATCH_NUMBER = "001";
    private static final int CARD_BATCH_MAX_NUMBER = 999;
    private static final String CARD_BATCH_NUMBER_PATTERN = "000";
}
