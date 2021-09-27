package ru.protei.portal.ui.delivery.client.activity.card.meta;

import com.google.inject.Inject;
import ru.brainworm.factory.context.client.annotation.ContextAware;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.ent.Card;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.events.CardEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.CardControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.delivery.client.view.card.meta.CardMetaView;

import java.util.Date;
import java.util.Objects;


public abstract class CardMetaActivity implements Activity, AbstractCardMetaActivity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
    }

    @Override
    public void onStateChanged() {
        CaseState caseState = view.state().getValue();
        card.setState(caseState);
        card.setStateId(caseState.getId());
        onCaseMetaChanged();
    }

    @Override
    public void onArticleChanged() {
        card.setArticle(view.article().getValue());
        onCaseMetaChanged();
    }

    @Override
    public void onManagerChanged() {
        PersonShortView manager = view.manager().getValue();
        card.setManager(manager);
        onCaseMetaChanged();
    }

    @Override
    public void onTestDateChanged() {
        view.setTestDateValid(isTestDateFieldValid());
        if (isDateEquals(view.testDate().getValue(), card.getTestDate())) {
            return;
        }
        card.setTestDate(view.testDate().getValue());
        onCaseMetaChanged();
    }

    private String getValidationError() {
/*        CaseState state = view.state().getValue();
        if (state == null) {
            return lang.deliveryValidationEmptyState();
        }*/

        return null;
    }

    private void fillView(Card updatedCard, boolean afterUpdate) {
        card = updatedCard;
        view.state().setValue(card.getState());
        view.type().setValue(new EntityOption(card.getTypeId()));
        view.article().setValue(card.getArticle());
        view.manager().setValue(card.getManager());

        if (!afterUpdate) {
            view.testDate().setValue(card.getTestDate());
//            view.setBuildDateValid(true);
        }
    }

    private void onCaseMetaChanged() {
        String error = getValidationError();
        if (error != null) {
            showValidationError(error);
            return;
        }

        controller.updateMeta(card, new FluentCallback<Card>()
                .withSuccess(caseMetaUpdated -> {
                    fireEvent(new NotifyEvents.Show(lang.msgObjectSaved(), NotifyEvents.NotifyType.SUCCESS));
                    fireEvent(new CardEvents.Change(caseMetaUpdated.getId()));
                    fillView( caseMetaUpdated, true );
                }));
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

    public boolean isTestDateFieldValid() {
        Date date = view.testDate().getValue();
        if (date == null) {
            return view.isTestDateEmpty();
        }

        return date.getTime() > System.currentTimeMillis();
    }

    @Inject
    private Lang lang;
    @Inject
    private CardMetaView view;
    @Inject
    private CardControllerAsync controller;

    @ContextAware
    Card card;
}
