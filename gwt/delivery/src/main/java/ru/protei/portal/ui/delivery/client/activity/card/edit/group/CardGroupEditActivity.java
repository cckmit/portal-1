package ru.protei.portal.ui.delivery.client.activity.card.edit.group;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.ent.Card;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.util.UiResult;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsActivity;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsView;
import ru.protei.portal.ui.common.client.events.CardEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.CardControllerAsync;
import ru.protei.portal.ui.common.shared.model.DefaultErrorHandler;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.Date;
import java.util.Objects;
import java.util.Set;

import static ru.protei.portal.core.model.util.CrmConstants.SOME_CARDS_NOT_UPDATED;

public abstract class CardGroupEditActivity implements AbstractCardGroupEditActivity,
        AbstractDialogDetailsActivity, Activity {

    @PostConstruct
    public void onInit() {
        prepareDialog(dialogView);
    }

    @Event
    public void onShow(CardEvents.GroupEdit event) {
        if (CollectionUtils.isEmpty(event.selectedCards)) {
            return;
        }
        this.selectedCards = event.selectedCards;

        prepareView();
        dialogView.showPopup();
    }

    @Override
    public void onSaveClicked() {
        String error = getValidationError();
        if (error != null) {
            showValidationError(error);
            return;
        }

        fillCards();
        update();
    }

    @Override
    public void onCancelClicked() {
        dialogView.hidePopup();
    }

    private void prepareDialog(AbstractDialogDetailsView dialog) {
        dialog.setActivity(this);
        dialog.getBodyContainer().clear();
        dialog.getBodyContainer().add(view.asWidget());
        dialog.setHeader(lang.cardGroupModify());
        dialog.removeButtonVisibility().setVisible(false);
    }

    private void prepareView() {
        view.state().setValue(null);
        view.article().setValue(null);
        view.manager().setValue(null);
        view.testDate().setValue(null);
        view.setTestDateValid(true);
        view.note().setValue(null);
        view.comment().setValue(null);
        setWarnings();
    }

    private void fillCards() {
        for (Card card : selectedCards) {
            if (view.state().getValue() != null) {
                card.setState(view.state().getValue());
                card.setStateId(view.state().getValue().getId());
            }
            if (StringUtils.isNotEmpty(view.article().getValue())) {
                card.setArticle(view.article().getValue());
            }
            if (view.manager().getValue() != null) {
                card.setManager(view.manager().getValue());
            }
            if (view.testDate().getValue() != null) {
                card.setTestDate(view.testDate().getValue());
            }
            if (StringUtils.isNotEmpty(view.note().getValue())) {
                card.setNote(view.note().getValue());
            }
            if (StringUtils.isNotEmpty(view.comment().getValue())) {
                card.setComment(view.comment().getValue());
            }
        }
    }

    private void update() {
        controller.updateCards(selectedCards, new FluentCallback<UiResult<Set<Card>>>()
                .withError(defaultErrorHandler)
                .withSuccess(result -> {
                    if (SOME_CARDS_NOT_UPDATED.equals(result.getMessage())) {
                        fireEvent(new NotifyEvents.Show(lang.cardSomeNotUpdated(), NotifyEvents.NotifyType.INFO));
                    }
                    fireEvent(new NotifyEvents.Show(lang.msgObjectsSaved(), NotifyEvents.NotifyType.SUCCESS));

                    dialogView.hidePopup();
                    fireEvent(new CardEvents.GroupChanged());
                }));
    }

    private void setWarnings() {
        Card compareCard = selectedCards.iterator().next();
        boolean isStateFieldsEqual = selectedCards.stream().allMatch(card -> Objects.equals(card.getStateId(), compareCard.getStateId()));
        view.setStateWarning(!isStateFieldsEqual);

        boolean isArticleFieldsEqual = selectedCards.stream().allMatch(card -> Objects.equals(card.getArticle(), compareCard.getArticle()));
        view.setArticleWarning(!isArticleFieldsEqual);

        boolean isManagerFieldsEqual = selectedCards.stream().allMatch(card -> Objects.equals(card.getManager(), compareCard.getManager()));
        view.setManagerWarning(!isManagerFieldsEqual);

        boolean isTestDateFieldsEqual = selectedCards.stream().allMatch(card -> Objects.equals(card.getTestDate(), compareCard.getTestDate()));
        view.setTestDateWarning(!isTestDateFieldsEqual);

        boolean isNoteFieldsEqual = selectedCards.stream().allMatch(card -> Objects.equals(card.getNote(), compareCard.getNote()));
        view.setNoteWarning(!isNoteFieldsEqual);

        boolean isCommentFieldsEqual = selectedCards.stream().allMatch(card -> Objects.equals(card.getComment(), compareCard.getComment()));
        view.setCommentWarning(!isCommentFieldsEqual);
    }

    private String getValidationError() {
        if (!view.articleIsValid()) {
            return lang.cardValidationErrorArticle();
        }
        if (!isTestDateFieldValid()) {
            return lang.cardValidationErrorTestDate();
        }
        return null;
    }

    public boolean isTestDateFieldValid() {
        Date date = view.testDate().getValue();
        if (date == null) {
            return true;
        }

        return date.getTime() > System.currentTimeMillis();
    }

    private void showValidationError(String error) {
        fireEvent(new NotifyEvents.Show(error, NotifyEvents.NotifyType.ERROR));
    }

    @Inject
    Lang lang;

    @Inject
    AbstractDialogDetailsView dialogView;
    @Inject
    AbstractCardGroupEditView view;

    @Inject
    private CardControllerAsync controller;
    @Inject
    private DefaultErrorHandler defaultErrorHandler;

    private Set<Card> selectedCards;
}
