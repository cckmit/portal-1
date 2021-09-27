package ru.protei.portal.ui.delivery.client.activity.card.edit;

import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.brainworm.factory.context.client.annotation.ContextAware;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_TextMarkup;
import ru.protei.portal.core.model.ent.Card;
import ru.protei.portal.core.model.util.TransliterationUtils;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.CardEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.CardControllerAsync;
import ru.protei.portal.ui.common.client.service.TextRenderControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.common.shared.model.Profile;
import ru.protei.portal.ui.delivery.client.activity.card.meta.CardMetaActivity;
import ru.protei.portal.ui.delivery.client.view.card.infoComment.CardNoteCommentButtonsView;
import ru.protei.portal.ui.delivery.client.view.card.infoComment.CardNoteCommentEditView;
import ru.protei.portal.ui.delivery.client.view.card.infoComment.CardNoteCommentView;
import ru.protei.portal.ui.delivery.client.view.card.meta.CardMetaView;

import java.util.Objects;
import java.util.function.Consumer;

public abstract class CardEditActivity implements Activity, AbstractCardEditActivity,
        AbstractCardNoteCommentEditActivity {

    @Inject
    public void onInit() {
        view.setActivity(this);
        noteCommentButtonsView.setActivity(this);
        noteCommentEditView.getButtonContainer().add(noteCommentButtonsView);
        switchNameDescriptionToEdit(false);
    }

    @Event
    public void onAuthSuccess(AuthEvents.Success event) {
        this.authProfile = event.profile;
    }

    @Event
    public void onShow( CardEvents.Edit event ) {
        if (event.card == null || event.card.getId() == null ) {
            return;
        }

        if (!hasAccess()) {
            event.closeHandle.run();
            return;
        }

        event.parent.clear();
        event.parent.add(view.asWidget());

        card = event.card;

        fillView(card);
        showMeta(card);
    }

    @Override
    public void onCancelNoteCommentClicked() {
        switchNameDescriptionToEdit(false);
        fillView(card);
        showMeta(card);
    }

    @Override
    public void onSaveNoteCommentClicked() {
        card.setNote(noteCommentEditView.note().getValue());
        card.setComment(noteCommentEditView.comment().getValue());
        controller.updateMeta( card, new FluentCallback<Card>()
                .withSuccess( result -> {
                    card = result;
                    fireEvent( new NotifyEvents.Show( lang.msgObjectSaved(), NotifyEvents.NotifyType.SUCCESS ) );
                    onCancelNoteCommentClicked();
                } ) );
    }

    @Override
    public void onNoteCommentClicked() {
        noteCommentEditView.note().setValue(card.getNote());
        noteCommentEditView.comment().setValue(card.getComment());
        view.noteCommentEditButtonVisibility().setVisible(false);
        switchNameDescriptionToEdit(true);
    }


    private void request(Long id) {
        controller.getCard(id, new FluentCallback<Card>()
                .withError((throwable, defaultErrorHandler, status) -> defaultErrorHandler.accept(throwable))
                .withSuccess(card -> {
                    this.card = card;
                    switchNameDescriptionToEdit(false);
                    fillView(card);
                    showMeta(card);
                }));
    }

    private void fillView(Card card) {
        view.setCreatedBy(lang.createBy(card.getCreator() == null ? "" : transliteration(card.getCreator().getDisplayShortName()),
                DateFormatter.formatDateTime(card.getCreated())));

        view.setSerialNumber(card.getSerialNumber());
        noteCommentView.setNote(card.getNote());
        noteCommentView.setComment(card.getComment());

        view.noteCommentEditButtonVisibility().setVisible(hasEditPrivileges() && isSelfDelivery(card.getCreatorId()));

        renderMarkupText(card.getNote(), En_TextMarkup.MARKDOWN, html -> noteCommentView.setNote(html));
        renderMarkupText(card.getComment(), En_TextMarkup.MARKDOWN, html -> noteCommentView.setComment(html));
    }

    private void showMeta(Card card) {
        fireEvent(new CardEvents.EditMeta(card, view.getMetaContainer()));
    }

    private boolean isSelfDelivery(Long creatorId) {
        return Objects.equals(creatorId, authProfile.getId());
    }

    private boolean hasAccess() {
        return policyService.hasPrivilegeFor(En_Privilege.DELIVERY_VIEW);
    }

    private boolean hasEditPrivileges() {
        return policyService.hasPrivilegeFor(En_Privilege.DELIVERY_EDIT);
    }

    private void renderMarkupText(String text, En_TextMarkup markup, Consumer<String> consumer ) {
        textRenderController.render( text, markup, new FluentCallback<String>()
                .withError( throwable -> consumer.accept( null ) )
                .withSuccess( consumer ) );
    }

    private void switchNameDescriptionToEdit(boolean isEdit) {
        HasWidgets noteCommentContainer = view.getNoteCommentContainer();
        noteCommentContainer.clear();
        if (isEdit) {
            noteCommentContainer.add(noteCommentEditView);
        } else {
            noteCommentContainer.add(noteCommentView);
        }
    }

    private String transliteration(String input) {
        return TransliterationUtils.transliterate(input, LocaleInfo.getCurrentLocale().getLocaleName());
    }

    @Inject
    private Lang lang;
    @Inject
    private AbstractCardEditView view;
    @Inject
    private CardNoteCommentView noteCommentView;
    @Inject
    private CardNoteCommentEditView noteCommentEditView;
    @Inject
    private CardNoteCommentButtonsView noteCommentButtonsView;
    @Inject
    private CardControllerAsync controller;

    @Inject
    CardMetaActivity cardMetaActivity;
    @Inject
    CardMetaView cardMetaView;

    @Inject
    PolicyService policyService;
    @Inject
    TextRenderControllerAsync textRenderController;

    @ContextAware
    Card card;

    private Profile authProfile;
}
