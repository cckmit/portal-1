package ru.protei.portal.ui.delivery.client.activity.cardbatch.edit;

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
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.CardBatchEvents;
import ru.protei.portal.ui.common.client.events.ErrorPageEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.CardBatchControllerAsync;
import ru.protei.portal.ui.common.client.service.CaseStateControllerAsync;
import ru.protei.portal.ui.common.client.service.ImportanceLevelControllerAsync;
import ru.protei.portal.ui.common.shared.model.DefaultErrorHandler;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.delivery.client.activity.cardbatch.common.AbstractCardBatchCommonInfoActivity;
import ru.protei.portal.ui.delivery.client.activity.cardbatch.common.AbstractCardBatchCommonInfoView;
import ru.protei.portal.ui.delivery.client.activity.cardbatch.meta.AbstractCardBatchMetaActivity;
import ru.protei.portal.ui.delivery.client.activity.cardbatch.meta.AbstractCardBatchMetaView;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.function.Consumer;

import static ru.protei.portal.core.model.helper.StringUtils.isEmpty;
import static ru.protei.portal.ui.common.client.events.NotifyEvents.NotifyType.ERROR;
import static ru.protei.portal.ui.common.client.events.NotifyEvents.NotifyType.SUCCESS;

public abstract class CardBatchEditActivity implements Activity, AbstractCardBatchEditActivity,
        AbstractCardBatchCommonInfoActivity, AbstractCardBatchMetaActivity {

    @Inject
    public void onInit() {
        view.setActivity(this);
        commonInfoView.setActivity(this);
        metaView.setActivity(this);
    }

    @Event
    public void onInitDetails(AppEvents.InitDetails initDetails) {
        this.initDetails = initDetails;
    }

    @Event(Type.FILL_CONTENT)
    public void onShow(CardBatchEvents.Edit event) {
        if (!hasViewPrivileges()) {
            fireEvent(new ErrorPageEvents.ShowForbidden(initDetails.parent));
            return;
        }

        initDetails.parent.clear();
        Window.scrollTo(0, 0);
        initDetails.parent.add(view.asWidget());

        view.getCommonInfoContainer().add(commonInfoView);
        view.getMetaContainer().add(metaView);
        commonInfoView.hidePrevCardBatchInfo();
        commonInfoView.typeEnabled().setEnabled(false);
        commonInfoView.numberEnabled().setEnabled(false);

        requestCardBatch(event.id, this::fillView);
    }

    @Override
    public void onSaveClicked() {
        String error = getCommonValidationError();
        if (error != null) {
            showError(error);
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
    public void getCaseState(Long id, Consumer<CaseState> success) {
        caseStateService.getCaseStateWithoutCompaniesOmitPrivileges(id, new FluentCallback<CaseState>()
                .withError(defaultErrorHandler)
                .withSuccess(success));
    }

    @Override
    public void onStateChange() {
        if (null == metaView.state().getValue()) {
            return;
        }
        cardBatch.setStateId(metaView.state().getValue().getId());
        onMetaChanged();
    }

    @Override
    public void onDeadlineChanged() {
        if (!validateDeadline()) {
            showError(lang.cardBatchDeadlineValidationError());
            return;
        }
        cardBatch.setDeadline(metaView.deadline().getValue() != null? metaView.deadline().getValue().getTime() : null);
        onMetaChanged();
    }

    @Override
    public void onPriorityChange() {
        cardBatch.setPriority(metaView.priority().getValue().getId());
        onMetaChanged();
    }

    @Override
    public void onContractorsChange() {
        if (CollectionUtils.isEmpty(metaView.contractors().getValue())) {
            showError(lang.cardBatchContractorsValidationError());
            return;
        }
        cardBatch.setContractors(new ArrayList<>(metaView.contractors().getValue()));
        onMetaChanged();
    }

    private void onMetaChanged() {
        if (!hasEditPrivileges()) {
            showError(lang.errPermissionDenied());
            return;
        }

        String error = getMetaValidationError();
        if (error != null) {
            showError(error);
            return;
        }

        cardBatchService.updateMeta(cardBatch, new FluentCallback<CardBatch>()
                .withSuccess(caseState -> {
                    fireEvent(new NotifyEvents.Show(lang.msgObjectSaved(), NotifyEvents.NotifyType.SUCCESS));
                    fillView( caseState );
                }));
    }

    private void requestCardBatch(Long cardBatchId, Consumer<CardBatch> cardBatchConsumer) {
        cardBatchService.getCardBatch(cardBatchId, new FluentCallback<CardBatch>()
                .withSuccess(cardBatchConsumer));
    }

    private boolean validateDeadline() {
        boolean isValid = metaView.deadline().getValue().after(new Date());
        metaView.setDeadlineValid(isValid);
        return isValid;
    }


    @Override
    public void onAmountChanged() {
        validateAmount();
    }

    private boolean validateAmount() {
        boolean isValid = null == commonInfoView.amount().getValue() || commonInfoView.amount().getValue() > 0;
        commonInfoView.setAmountValid(isValid);
        return isValid;
    }

    private void fillView(CardBatch cardBatch) {

        this.cardBatch = cardBatch;
        if (cardBatch == null) return;

        commonInfoView.type().setValue(new EntityOption(cardBatch.getTypeName(), cardBatch.getTypeId()));
        commonInfoView.number().setValue(cardBatch.getNumber());
        commonInfoView.article().setValue(cardBatch.getArticle());
        commonInfoView.amount().setValue(cardBatch.getAmount());
        commonInfoView.params().setValue(cardBatch.getParams());
        metaView.state().setValue(cardBatch.getState());
        fillPrioritySelector(cardBatch.getPriority());
        metaView.deadline().setValue(new Date(cardBatch.getDeadline()));
        metaView.contractors().setValue(new HashSet<>(cardBatch.getContractors()));
    }

    private CardBatch fillDto() {
        cardBatch.setTypeId(commonInfoView.type().getValue().getId());
        cardBatch.setNumber(commonInfoView.number().getValue());
        cardBatch.setArticle(commonInfoView.article().getValue());
        cardBatch.setAmount(commonInfoView.amount().getValue());
        cardBatch.setParams(commonInfoView.params().getValue());

        return cardBatch;
    }

    private void showError(String error) {
        fireEvent(new NotifyEvents.Show(error, ERROR));
    }

    private String getMetaValidationError() {
        if (!validateDeadline()) {
            return lang.cardBatchDeadlineValidationError();
        }

        if (CollectionUtils.isEmpty(metaView.contractors().getValue())) {
            return lang.cardBatchContractorsValidationError();
        }

        return null;
    }

    private String getCommonValidationError() {
        if (null == commonInfoView.type().getValue()) {
            return lang.cardBatchTypeValidationError();
        }

        if (isEmpty(commonInfoView.number().getValue()) || !commonInfoView.isNumberValid()) {
            return lang.cardBatchNumberValidationError();
        }

        if (isEmpty(commonInfoView.article().getValue()) || !commonInfoView.isArticleValid()) {
            return lang.cardBatchArticleValidationError();
        }

        if (!validateAmount()) {
            return lang.cardBatchAmountValidationError();
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
        cardBatchService.updateMeta(cardBatch, new FluentCallback<CardBatch>()
                .withError(throwable -> {
                    view.saveEnabled().setEnabled(true);
                    defaultErrorHandler.accept(throwable);
                    onFailure.accept(throwable);
                })
                .withSuccess(id -> {
                    view.saveEnabled().setEnabled(true);
                    fireEvent(new NotifyEvents.Show(lang.cardBatchSaved(), SUCCESS));
                    onSuccess.run();
                }));
    }

    private boolean hasViewPrivileges() {
        return policyService.hasPrivilegeFor(En_Privilege.DELIVERY_VIEW);
    }

    private boolean hasEditPrivileges() {
        return policyService.hasPrivilegeFor(En_Privilege.DELIVERY_EDIT);
    }

    private void fillPrioritySelector(Integer id) {
        importanceService.getImportanceLevel( id, new FluentCallback<ImportanceLevel>()
                .withSuccess(new Consumer<ImportanceLevel>() {
                    @Override
                    public void accept(ImportanceLevel level) {
                        metaView.priority().setValue(level);
                    }
                }));
    }

    public static native String addLeadingZeros(int num, int number_length) /*-{
        return num.toString().padStart(number_length, "0");
    }-*/;

    @Inject
    private Lang lang;
    @Inject
    private AbstractCardBatchEditView view;
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
    ImportanceLevelControllerAsync importanceService;

    @Inject
    private DefaultErrorHandler defaultErrorHandler;

    CardBatch cardBatch;

    private AppEvents.InitDetails initDetails;
}
