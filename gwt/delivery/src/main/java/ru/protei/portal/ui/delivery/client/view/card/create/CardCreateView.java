package ru.protei.portal.ui.delivery.client.view.card.create;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.CardBatch;
import ru.protei.portal.core.model.ent.CardType;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.cardbatch.CardBatchModel;
import ru.protei.portal.ui.delivery.client.activity.card.create.AbstractCardCreateActivity;
import ru.protei.portal.ui.delivery.client.activity.card.create.AbstractCardCreateView;
import ru.protei.portal.ui.delivery.client.view.card.infoComment.CardNoteCommentEditView;
import ru.protei.portal.ui.delivery.client.view.card.meta.CardMetaView;

import java.util.Date;

import static ru.protei.portal.ui.common.client.common.UiConstants.Styles.REQUIRED;


public class CardCreateView extends Composite implements AbstractCardCreateView {

    @Inject
    public void init() {
        initWidget(ourUiBinder.createAndBindUi(this));
        serialNumber.getElement().setAttribute("placeholder", lang.cardSerialNumberPlaceholder());
        ensureDebugIds();
    }

    @Override
    public void setActivity(AbstractCardCreateActivity activity) {
        this.activity = activity;
    }

    @Override
    public HasEnabled saveEnabled() {
        return saveButton;
    }

    @Override
    public HasValue<String> serialNumber() {
        return serialNumber;
    }

    @Override
    public HasValue<String> note() {
        return noteComment.note();
    }

    @Override
    public HasValue<String> comment() {
        return noteComment.comment();
    }

    @Override
    public HasValue<CaseState> state() {
        return meta.state();
    }

    @Override
    public HasValue<CardType> type() {
        return meta.type();
    }

    @Override
    public HasValue<CardBatch> cardBatch() {
        return meta.cardBatch();
    }

    @Override
    public CardBatchModel cardBatchModel() {
        return meta.cardBatchModel();
    }

    @Override
    public HasValue<String> article() {
        return meta.article();
    }

    @Override
    public HasValue<PersonShortView> manager() {
        return meta.manager();
    }

    @Override
    public HasValue<Date> testDate() {
        return meta.testDate();
    }

    @Override
    public void setTestDateValid(boolean value) {
        meta.setTestDateValid(value);
    }

    @Override
    public CardMetaView getMetaView() {
        return meta;
    }

    @Override
    public HasValue<Integer> amount() {
        return amount;
    }

    @Override
    public void setAmountValid(boolean isValid) {
        amount.setStyleName(REQUIRED, !isValid);
    }

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }

        saveButton.ensureDebugId(DebugIds.CARD.SAVE_BUTTON);
        cancelButton.ensureDebugId(DebugIds.CARD.CANCEL_BUTTON);
        serialNumber.ensureDebugId(DebugIds.CARD.SERIAL_NUMBER);
        amount.ensureDebugId(DebugIds.CARD.AMOUNT);
    }

    @UiHandler("saveButton")
    public void onSaveClicked(ClickEvent event) {
        if (activity != null) {
            activity.onSaveClicked();
        }
    }

    @UiHandler("cancelButton")
    public void onCancelClicked(ClickEvent event) {
        if (activity != null) {
            activity.onCancelClicked();
        }
    }

    @UiHandler("amount")
    public void onAmountChanged(KeyUpEvent event) {
        activity.onAmountChanged();
    }

    @UiField
    Lang lang;
    @UiField
    TextBox serialNumber;
    @Inject
    @UiField(provided = true)
    CardNoteCommentEditView noteComment;
    @Inject
    @UiField(provided = true)
    CardMetaView meta;
    @UiField
    Button saveButton;
    @UiField
    Button cancelButton;
    @UiField
    IntegerBox amount;

    private AbstractCardCreateActivity activity;

    private static ViewUiBinder ourUiBinder = GWT.create(ViewUiBinder.class);
    interface ViewUiBinder extends UiBinder<HTMLPanel, CardCreateView> {}
}