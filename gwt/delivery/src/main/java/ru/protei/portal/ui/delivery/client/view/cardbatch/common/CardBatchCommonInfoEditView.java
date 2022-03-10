package ru.protei.portal.ui.delivery.client.view.cardbatch.common;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.view.PersonProjectMemberView;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.autoresizetextarea.AutoResizeTextArea;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;
import ru.protei.portal.ui.delivery.client.activity.cardbatch.common.AbstractCardBatchCommonInfoEditActivity;
import ru.protei.portal.ui.delivery.client.activity.cardbatch.common.AbstractCardBatchCommonInfoEditView;
import ru.protei.portal.ui.delivery.client.widget.cardbatch.contractors.ContractorsSelector;
import java.util.Set;

import static ru.protei.portal.ui.common.client.common.UiConstants.Styles.REQUIRED;

public class CardBatchCommonInfoEditView extends Composite implements AbstractCardBatchCommonInfoEditView {

    @Inject
    public void init() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        ensureDebugIds();
    }

    @Override
    public HasVisibility buttonsContainerVisibility() {
        return buttonsContainer;
    }

    @Override
    public HasValue<String> number() {
        return number;
    }

    @Override
    public HasValue<Integer> amount() {
        return amount;
    }

    @Override
    public void setAmountValid(boolean isValid) {
        amount.setStyleName(REQUIRED, !isValid);
    }

    @Override
    public HasValue<String> params(){
        return params;
    }

    @Override
    public HasValue<Set<PersonProjectMemberView>> contractors() {
        return contractors;
    }

    @Override
    public boolean isNumberValid() {
        return number.isValid();
    }

    @Override
    public void hidePrevCardBatchInfo() {
        prevCardBatchInfo.setText(null);
        prevCardBatchInfo.setVisible(false);
    }

    @Override
    public void setPrevCardBatchInfo(String number, int amount, String state) {
        prevCardBatchInfo.setText( lang.cardBatchPreviousInfo(number, amount, state) );
        prevCardBatchInfo.setVisible(true);
    }

    @Override
    public void hideNumberContainer() {
        numberContainer.addClassName(CrmConstants.Style.HIDE);
        amountContainer.replaceClassName("col-md-6", "col-md-12 p-l-0");
    }

    @Override
    public void setActivity(AbstractCardBatchCommonInfoEditActivity activity) {
        this.activity = activity;
    }

    @UiHandler("amount")
    public void onAmountChanged(KeyUpEvent event) {
        activity.onAmountChanged();
    }

    @UiHandler("saveButton")
    void onSaveButtonClick(ClickEvent event ) {
        activity.onSaveCommonInfoClicked();
    }

    @Override
    public HasEnabled saveEnabled(){
        return saveButton;
    }

    @UiHandler("cancelButton")
    void onCancelButtonClick(ClickEvent event ) {
        activity.onCancelSaveCommonInfoClicked();
    }

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }
        number.ensureDebugId( DebugIds.CARD_BATCH.NUMBER_INPUT);
        amount.ensureDebugId( DebugIds.CARD_BATCH.AMOUNT );
        params.ensureDebugId( DebugIds.CARD_BATCH.PARAMS );
        contractors.ensureDebugId( DebugIds.CARD_BATCH.CONTRACTOR_SELECTOR );
        saveButton.ensureDebugId(DebugIds.CARD_BATCH.SAVE_BUTTON);
        cancelButton.ensureDebugId(DebugIds.CARD_BATCH.CANCEL_BUTTON);
    }

    AbstractCardBatchCommonInfoEditActivity activity;

    @UiField
    Lang lang;

    @UiField
    ValidableTextBox number;
    @UiField
    IntegerBox amount;
    @UiField
    AutoResizeTextArea params;
    @Inject
    @UiField(provided = true)
    ContractorsSelector contractors;
    @UiField
    Label prevCardBatchInfo;
    @UiField
    HTMLPanel buttonsContainer;
    @UiField
    Button saveButton;
    @UiField
    Button cancelButton;
    @UiField
    DivElement numberContainer;
    @UiField
    DivElement amountContainer;

    interface CommonUiBinder extends UiBinder<HTMLPanel, CardBatchCommonInfoEditView> {}
    private static CommonUiBinder ourUiBinder = GWT.create( CommonUiBinder.class );
}
