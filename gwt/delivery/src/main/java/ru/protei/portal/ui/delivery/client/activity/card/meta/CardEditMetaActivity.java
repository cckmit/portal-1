package ru.protei.portal.ui.delivery.client.activity.card.meta;

import com.google.inject.Inject;
import ru.brainworm.factory.context.client.annotation.ContextAware;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.ent.Card;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.events.CardEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.service.CardControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.Date;
import java.util.Objects;

public abstract class CardEditMetaActivity extends CardCommonMeta implements Activity, AbstractCardEditMetaActivity {

    @Inject
    public void onInit() {
        view.setEditActivity(this);
        view.typeEnable().setEnabled(false);
        view.cardBatchEnable().setEnabled(false);
        setMetaView(view);
    }

    @Event
    public void onShow(CardEvents.EditMeta event) {
        event.parent.clear();
        event.parent.add(view.asWidget());

        card = event.card;

        fillView( card);
    }

    @Override
    public void onStateChanged() {
        CaseState caseState = view.state().getValue();
        if (Objects.equals(caseState.getId(), card.getStateId())) {
            return;
        }
        card.setState(caseState);
        card.setStateId(caseState.getId());
        onCaseMetaChanged();
    }

    @Override
    public void onArticleChanged() {
        if (Objects.equals(view.article().getValue(), card.getArticle())) {
            return;
        }
        if (!view.articleIsValid()) {
            return;
        }
        card.setArticle(view.article().getValue());
        onCaseMetaChanged();
    }

    @Override
    public void onManagerChanged() {
        PersonShortView manager = view.manager().getValue();
        if (Objects.equals(manager, card.getManager())) {
            return;
        }
        card.setManager(manager);
        onCaseMetaChanged();
    }

    @Override
    public void onTestDateChanged() {
        super.onTestDateChanged();
        if (!isTestDateFieldValid()) {
            return;
        }

        if (isDateEquals(view.testDate().getValue(), card.getTestDate())) {
            return;
        }
        card.setTestDate(view.testDate().getValue());
        onCaseMetaChanged();
    }

    private void onCaseMetaChanged() {
        String error = getValidationError();
        if (error != null) {
            showValidationError(error);
            return;
        }

        controller.updateMeta(card, new FluentCallback<Card>()
                .withSuccess(metaUpdated -> {
                    fireEvent(new NotifyEvents.Show(lang.msgObjectSaved(), NotifyEvents.NotifyType.SUCCESS));
                    fireEvent(new CardEvents.Change(metaUpdated));
                    fillView( metaUpdated );
                }));
    }

    private void fillView(Card card) {
        view.state().setValue(card.getState());
        view.type().setValue(card.getCardType());
        view.cardBatch().setValue(card.getCardBatch());
        view.article().setValue(card.getArticle());
        view.manager().setValue(card.getManager());
        view.testDate().setValue(card.getTestDate());
        view.setTestDateValid(true);
    }

    private void showValidationError(String error) {
        fireEvent(new NotifyEvents.Show(error, NotifyEvents.NotifyType.ERROR));
    }

    private boolean isDateEquals(Date dateField, Date dateMeta) {
        if (dateField == null) {
            return dateMeta == null;
        } else {
            return Objects.equals(dateField, dateMeta);
        }
    }

    @Inject
    private CardControllerAsync controller;

    @ContextAware
    Card card;
}
