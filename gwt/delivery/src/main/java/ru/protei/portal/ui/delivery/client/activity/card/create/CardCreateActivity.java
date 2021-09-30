package ru.protei.portal.ui.delivery.client.activity.card.create;

import com.google.gwt.i18n.client.NumberFormat;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.Card;
import ru.protei.portal.core.model.ent.CardType;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.CardEvents;
import ru.protei.portal.ui.common.client.events.ErrorPageEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.service.CardControllerAsync;
import ru.protei.portal.ui.common.client.service.CaseStateControllerAsync;
import ru.protei.portal.ui.common.shared.model.DefaultErrorHandler;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.delivery.client.activity.card.meta.AbstractCardCreateMetaActivity;
import ru.protei.portal.ui.delivery.client.activity.card.meta.CardCommonMeta;
import ru.protei.portal.ui.delivery.client.view.card.meta.CardMetaView;

import java.util.function.Consumer;

public abstract class CardCreateActivity extends CardCommonMeta implements Activity, AbstractCardCreateActivity, AbstractCardCreateMetaActivity {

    @Inject
    public void onInit() {
        view.setActivity(this);

        CardMetaView metaView = view.getMetaView();
        commonMeta.setMetaView(metaView);
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

        closeHandle = event.closeHandle;

        event.parent.clear();
        event.parent.add(view.asWidget());

        prepare();
    }

    @Override
    public void onSaveClicked() {
        String error = getValidationError();
        if (error != null) {
            showValidationError(error);
            return;
        }
        Card card = fillDto();
        save(card, closeHandle);
    }

    @Override
    public void onCancelClicked() {
        closeHandle.run();
    }

    @Override
    public void onTypeChange() {
        CardType cardType = view.type().getValue();
        if (!cardType.equals(view.cardBatchModel().getCardType())) {
            view.cardBatch().setValue(null);
            view.cardBatchModel().updateCardType(cardType);
            view.serialNumber().setValue(null);
        }
    }

    @Override
    public void onCardBatchChange() {
        setSerialNumber();
    }

    private void setSerialNumber() {
        cardController.getSizeByBatchId(view.cardBatch().getValue().getId(), new FluentCallback<Long>()
                .withError(throwable -> defaultErrorHandler.accept(throwable))
                .withSuccess(size -> {
                    String serialNumber =
                            view.type().getValue().getCode() + "." +
                            view.cardBatch().getValue().getNumber() + "." +
                            NumberFormat.getFormat("000").format(size + 1);
                    view.serialNumber().setValue(serialNumber);
                }));
    }

    private void prepare() {
        view.serialNumber().setValue(null);
        view.type().setValue(null);
        view.cardBatch().setValue(null);
        view.article().setValue(null);
        view.testDate().setValue(null);
        view.setTestDateValid(true);
        view.comment().setValue(null);
        fillStateSelector(CrmConstants.State.TESTING);
        view.manager().setValue(null);
        view.note().setValue(null);
    }

    private Card fillDto() {
        Card card = new Card();
        card.setTypeId(view.type().getValue().getId());
        card.setCardType(view.type().getValue());
        card.setSerialNumber(view.serialNumber().getValue());
        card.setCardBatchId(view.cardBatch().getValue().getId());
        card.setArticle(view.article().getValue());
        card.setTestDate(view.testDate().getValue());
        card.setComment(view.comment().getValue());
        card.setStateId(view.state().getValue().getId());
        card.setState(view.state().getValue());
        card.setManager(view.manager().getValue());
        card.setNote(view.note().getValue());
        return card;
    }

    private void showValidationError(String error) {
        fireEvent(new NotifyEvents.Show(error, NotifyEvents.NotifyType.ERROR));
    }

    public String getValidationError() {
/*        if (isBlank(view.name().getValue())) {
            return lang.deliveryValidationEmptyName();
        }

        String error = commonMeta.getValidationError();
        if (error != null) {
            return error;
        }
        CaseState state = view.state().getValue();
         if (!Objects.equals(CrmConstants.State.PRELIMINARY, state.getId())) {
            return lang.deliveryValidationInvalidStateAtCreate();
        }
        if (!kitList.isValid()) {
            return lang.deliveryValidationInvalidKits();
        }*/

        return null;
    }

    private void save(Card card, Runnable onSuccess) {
        view.saveEnabled().setEnabled(false);
        cardController.createCard(card, new FluentCallback<Card>()
            .withError(throwable -> {
                view.saveEnabled().setEnabled(true);
                defaultErrorHandler.accept(throwable);
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
        view.state().setValue(new CaseState(id));
        getCaseState(id, caseState -> view.state().setValue(caseState));
    }

    private void getCaseState(Long id, Consumer<CaseState> success) {
        caseStateController.getCaseStateWithoutCompaniesOmitPrivileges(id, new FluentCallback<CaseState>()
                .withError(defaultErrorHandler)
                .withSuccess(success));
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
    @Inject
    private CardCommonMeta commonMeta;

    private AppEvents.InitDetails initDetails;
    private Runnable closeHandle;
}
