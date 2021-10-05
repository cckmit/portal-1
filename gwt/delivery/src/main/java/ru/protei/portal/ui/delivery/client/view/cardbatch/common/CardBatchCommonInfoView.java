package ru.protei.portal.ui.delivery.client.view.cardbatch.common;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.autoresizetextarea.AutoResizeTextArea;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;
import ru.protei.portal.ui.delivery.client.activity.cardbatch.common.AbstractCardBatchCommonInfoActivity;
import ru.protei.portal.ui.delivery.client.activity.cardbatch.common.AbstractCardBatchCommonInfoView;
import ru.protei.portal.ui.delivery.client.widget.card.selector.CardTypeSelector;

import static ru.protei.portal.core.model.util.CrmConstants.Masks.CARD_BATCH_ARTICLE_PATTERN;
import static ru.protei.portal.ui.common.client.common.UiConstants.Styles.REQUIRED;

public class CardBatchCommonInfoView extends Composite implements AbstractCardBatchCommonInfoView {

    @Inject
    public void init() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        article.setRegexp(CARD_BATCH_ARTICLE_PATTERN);
        ensureDebugIds();
    }

    @Override
    public HasValue<EntityOption> type() {
        return type;
    }

    @Override
    public HasEnabled typeEnabled() {
        return type;
    }

    @Override
    public HasValue<String> number() {
        return number;
    }

    @Override
    public HasValue<String> article() {
        return article;
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
    public boolean isNumberValid() {
        return number.isValid();
    }

    @Override
    public boolean isArticleValid() {
        return article.isValid();
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
    public void setActivity(AbstractCardBatchCommonInfoActivity activity) {
        this.activity = activity;
    }

    @UiHandler("type")
    public void onTypeChanged(ValueChangeEvent<EntityOption> event) {
        activity.onCardTypeChanged(event.getValue().getId());
    }

    @UiHandler("amount")
    public void onAmountChanged(KeyUpEvent event) {
        activity.onAmountChanged();
    }

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }
        type.ensureDebugId( DebugIds.CARD_BATCH.TYPE );
        number.ensureDebugId( DebugIds.CARD_BATCH.NUMBER );
        article.ensureDebugId( DebugIds.CARD_BATCH.ARTICLE );
        amount.ensureDebugId( DebugIds.CARD_BATCH.AMOUNT );
        params.ensureDebugId( DebugIds.CARD_BATCH.PARAMS );
    }

    AbstractCardBatchCommonInfoActivity activity;

    @UiField
    Lang lang;

    @Inject
    @UiField(provided = true)
    CardTypeSelector type;
    @UiField
    ValidableTextBox number;
    @UiField
    ValidableTextBox article;
    @UiField
    IntegerBox amount;
    @UiField
    AutoResizeTextArea params;
    @UiField
    Label prevCardBatchInfo;

    interface CommonUiBinder extends UiBinder<HTMLPanel, CardBatchCommonInfoView> {}
    private static CommonUiBinder ourUiBinder = GWT.create( CommonUiBinder.class );
}
