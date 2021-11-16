package ru.protei.portal.ui.delivery.client.activity.card.create;

import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import ru.brainworm.factory.context.client.events.Back;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.Card;
import ru.protei.portal.core.model.ent.CardCreateRequest;
import ru.protei.portal.core.model.ent.CardType;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.service.CardControllerAsync;
import ru.protei.portal.ui.common.client.service.CaseStateControllerAsync;
import ru.protei.portal.ui.common.shared.model.DefaultErrorHandler;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.delivery.client.activity.card.meta.AbstractCardCreateMetaActivity;
import ru.protei.portal.ui.delivery.client.activity.card.meta.CardCommonMeta;

import java.util.List;
import java.util.function.Consumer;

import static ru.protei.portal.ui.common.client.events.NotifyEvents.NotifyType.SUCCESS;

public abstract class CardCreateActivity extends CardCommonMeta implements Activity, AbstractCardCreateActivity, AbstractCardCreateMetaActivity {

    @Inject
    public void onInit() {
        view.setActivity(this);

        setMetaView(view.getMetaView());
        view.getMetaView().setCreateActivity(this);
    }

    @Event
    public void onInitDetails(AppEvents.InitDetails initDetails) {
        this.initDetails = initDetails;
    }

    @Event
    public void onShow(CardEvents.Create event) {
        if (!hasPrivileges()) {
            fireEvent(new ErrorPageEvents.ShowForbidden(initDetails.parent));
            return;
        }

        initDetails.parent.clear();
        Window.scrollTo(0, 0);
        initDetails.parent.add(view.asWidget());

        prepare(event);
    }

    @Override
    public void onSaveClicked() {
        String error = getValidationError();
        if (error != null) {
            showValidationError(error);
            return;
        }
        save(fillCardCreateRequest());
    }

    @Override
    public void onCancelClicked() {
        fireEvent(new Back());
    }

    @Override
    public void onTypeChange() {
        CardType cardType = view.type().getValue();
        if (cardType == null || !cardType.equals(view.cardBatchModel().getCardType())) {
            view.cardBatch().setValue(null);
            view.cardBatchModel().updateCardType(cardType);
            view.serialNumber().setValue(null);
        }
    }

    @Override
    public void onCardBatchChange() {
        if (isCanPresetArticle) {
            view.article().setValue(view.cardBatch().getValue().getArticle());
            isCanPresetArticle = false;
        }
        setSerialNumber();
    }

    @Override
    public void onArticleChanged() {
        isCanPresetArticle = false;
    }

    @Override
    public void onAmountChanged() {
        validateAmount();
    }

    private void setSerialNumber() {
        Long typeId = view.type().getValue().getId();
        Long cardBatchId = view.cardBatch().getValue().getId();
        cardController.getLastNumber(typeId, cardBatchId, new FluentCallback<Long>()
                .withError(throwable -> defaultErrorHandler.accept(throwable))
                .withSuccess(size -> {
                    String serialNumber =
                            view.type().getValue().getCode() + "." +
                            view.cardBatch().getValue().getNumber() + "." +
                            NumberFormat.getFormat("000").format(size + 1);
                    view.serialNumber().setValue(serialNumber);
                }));
    }

    private void prepare(CardEvents.Create event) {
        view.serialNumber().setValue(null);
        view.type().setValue(event.typeCode != null ? new CardType(event.typeName, event.typeCode, true, true) : null);
        view.cardBatch().setValue(event.cardBatch != null ? event.cardBatch : null);

        if (event.typeId != null) {
            CardType cardType = new CardType(event.typeName, event.typeCode, true, true);
            cardType.setId(event.typeId);
            view.type().setValue(cardType);
            onTypeChange();
        } else {
            view.type().setValue(null);
        }

        isCanPresetArticle = true;
        if (event.cardBatch != null) {
            view.cardBatch().setValue(event.cardBatch);
            onCardBatchChange();
        } else {
            view.cardBatch().setValue(null);
            view.article().setValue(null);
        }

        view.cardBatchModel().updateCardType(null);
        view.testDate().setValue(null);
        view.setTestDateValid(true);
        view.comment().setValue(null);
        fillStateSelector(CrmConstants.State.TESTING);
        view.manager().setValue(null);
        view.note().setValue(null);
        view.amount().setValue(1);
    }

    private CardCreateRequest fillCardCreateRequest() {
        CardCreateRequest createRequest = new CardCreateRequest();
        createRequest.setTypeId(view.type().getValue().getId());
        createRequest.setSerialNumber(view.serialNumber().getValue());
        createRequest.setCardBatchId(view.cardBatch().getValue().getId());
        createRequest.setArticle(view.article().getValue());
        createRequest.setTestDate(view.testDate().getValue());
        createRequest.setStateId(view.state().getValue().getId());
        createRequest.setManager(view.manager().getValue());
        createRequest.setComment(view.comment().getValue());
        createRequest.setNote(view.note().getValue());
        createRequest.setAmount(view.amount().getValue());
        return createRequest;
    }

    private void showValidationError(String error) {
        fireEvent(new NotifyEvents.Show(error, NotifyEvents.NotifyType.ERROR));
    }

    public String getValidationError() {
        String error = super.getValidationError();
        if (error != null) {
            return error;
        }
        if (view.serialNumber().getValue() == null) {
            return lang.cardValidationErrorSerialNumber();
        }
        if (!isTestDateFieldValid()) {
            return lang.cardValidationErrorTestDate();
        }
        if (!validateAmount()) {
            return lang.cardAmountValidationError();
        }

        return null;
    }

    private void save(CardCreateRequest cardCreateRequest) {
        view.saveEnabled().setEnabled(false);
        cardController.createCards(cardCreateRequest, new FluentCallback<List<Card>>()
            .withError(throwable -> {
                view.saveEnabled().setEnabled(true);
                defaultErrorHandler.accept(throwable);
            })
            .withSuccess(cards -> {
                view.saveEnabled().setEnabled(true);
                fireEvent(new NotifyEvents.Show(lang.cardsCreated(), SUCCESS));
                fireEvent(new Back());
            }));
    }

    private boolean hasPrivileges() {
        return policyService.hasPrivilegeFor(En_Privilege.CARD_CREATE);
    }

    private void fillStateSelector(Long id) {
        view.state().setValue(new CaseState(id));
        getCaseState(id, caseState -> view.state().setValue(caseState));
    }

    private void getCaseState(Long id, Consumer<CaseState> success) {
        caseStateController.getCaseStateWithoutCompaniesOmitPrivileges(id, new FluentCallback<CaseState>()
                .withError(defaultErrorHandler)
                .withSuccess(success));
    }

    private boolean validateAmount() {
        Integer value = view.amount().getValue();
        boolean isValid = value != null && value > 0;
        view.setAmountValid(isValid);
        return isValid;
    }

    @Inject
    private AbstractCardCreateView view;
    @Inject
    private CardControllerAsync cardController;
    @Inject
    private CaseStateControllerAsync caseStateController;
    @Inject
    private PolicyService policyService;
    @Inject
    private DefaultErrorHandler defaultErrorHandler;

    private boolean isCanPresetArticle;
    private AppEvents.InitDetails initDetails;
}
