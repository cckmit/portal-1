package ru.protei.portal.ui.delivery.client.view.cardbatch.common;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.inject.Inject;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.autoresizetextarea.AutoResizeTextArea;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;
import ru.protei.portal.ui.delivery.client.activity.cardbatch.create.AbstractCardBatchCommonInfoActivity;
import ru.protei.portal.ui.delivery.client.widget.card.selector.CardTypeSelector;

import static ru.protei.portal.core.model.util.CrmConstants.Masks.CARD_BATCH_ARTICLE_PATTERN;

public class CardBatchCommonInfoView extends Composite {

    @Inject
    public void init() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        article.setRegexp(CARD_BATCH_ARTICLE_PATTERN);
        ensureDebugIds();
    }

    public HasValue<EntityOption> type() {
        return type;
    }

    public HasValue<String> number() {
        return number;
    }

    public HasValue<String> article() {
        return article;
    }

    public HasValue<Integer> amount() {
        return amount;
    }

    public HasValue<String> params(){
        return params;
    }

    public boolean isArticleValid() {
        return article.isValid();
    }

    public void setActivity(AbstractCardBatchCommonInfoActivity activity) {
        this.activity = activity;
    }

    @UiHandler("type")
    public void onTypeChanged(ValueChangeEvent<EntityOption> event) {
        activity.onCardTypeChanged(event.getValue().getId());
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

    interface CommonUiBinder extends UiBinder<HTMLPanel, CardBatchCommonInfoView> {}
    private static CommonUiBinder ourUiBinder = GWT.create( CommonUiBinder.class );
}
