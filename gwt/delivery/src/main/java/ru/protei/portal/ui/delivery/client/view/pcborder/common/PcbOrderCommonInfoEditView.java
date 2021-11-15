package ru.protei.portal.ui.delivery.client.view.pcborder.common;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.autoresizetextarea.AutoResizeTextArea;
import ru.protei.portal.ui.common.client.widget.selector.card.type.CardTypeOptionFormSelector;
import ru.protei.portal.ui.delivery.client.activity.pcborder.common.AbstractPcbOrderCommonInfoEditActivity;
import ru.protei.portal.ui.delivery.client.activity.pcborder.common.AbstractPcbOrderCommonInfoEditView;

import static ru.protei.portal.ui.common.client.common.UiConstants.Styles.REQUIRED;

public class PcbOrderCommonInfoEditView extends Composite implements AbstractPcbOrderCommonInfoEditView {

    @Inject
    public void init() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        ensureDebugIds();
    }

    @Override
    public void setActivity(AbstractPcbOrderCommonInfoEditActivity activity) {
        this.activity = activity;
    }

    @Override
    public HasValue<EntityOption> cardType() {
        return cardType;
    }

    @Override
    public HasValue<Integer> amount() {
        return amount;
    }

    @Override
    public HasValue<String> modification() {
        return modification;
    }

    @Override
    public HasValue<String> comment() {
        return comment;
    }

    @Override
    public HasVisibility buttonsContainerVisibility() {
        return buttonsContainer;
    }

    @Override
    public HasEnabled saveEnabled(){
        return saveButton;
    }

    @Override
    public void setAmountValid(boolean isValid) {
        amount.setStyleName(REQUIRED, !isValid);
    }

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }
        cardType.ensureDebugId( DebugIds.PCB_ORDER.CARD_TYPE_SELECTOR);
        amount.ensureDebugId( DebugIds.PCB_ORDER.AMOUNT );
        modification.ensureDebugId( DebugIds.PCB_ORDER.MODIFICATION );
        comment.ensureDebugId(DebugIds.PCB_ORDER.COMMENT);
        saveButton.ensureDebugId(DebugIds.PCB_ORDER.SAVE_BUTTON);
        cancelButton.ensureDebugId(DebugIds.PCB_ORDER.CANCEL_BUTTON);
    }

    @UiHandler("amount")
    public void onAmountChanged(KeyUpEvent event) {
        activity.onAmountChanged();
    }

    @UiHandler("saveButton")
    void onSaveButtonClick(ClickEvent event ) {
        activity.onSaveCommonInfoClicked();
    }

    @UiHandler("cancelButton")
    void onCancelButtonClick(ClickEvent event ) {
        activity.onCancelSaveCommonInfoClicked();
    }

    @UiField
    Lang lang;

    @Inject
    @UiField(provided = true)
    CardTypeOptionFormSelector cardType;
    @UiField
    IntegerBox amount;
    @UiField
    TextBox modification;
    @UiField
    AutoResizeTextArea comment;
    @UiField
    HTMLPanel buttonsContainer;
    @UiField
    Button saveButton;
    @UiField
    Button cancelButton;

    AbstractPcbOrderCommonInfoEditActivity activity;

    interface CommonUiBinder extends UiBinder<HTMLPanel, PcbOrderCommonInfoEditView> {}
    private static CommonUiBinder ourUiBinder = GWT.create( CommonUiBinder.class );
}
