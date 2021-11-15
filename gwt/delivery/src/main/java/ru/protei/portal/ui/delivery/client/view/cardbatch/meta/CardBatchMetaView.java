package ru.protei.portal.ui.delivery.client.view.cardbatch.meta;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import com.google.inject.Inject;
import ru.brainworm.factory.core.datetimepicker.client.view.input.single.SinglePicker;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.ent.ImportanceLevel;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.events.InputEvent;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.card.type.CardTypeOptionFormSelector;
import ru.protei.portal.ui.common.client.widget.selector.cardbatch.state.CardBatchStateFormSelector;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;
import ru.protei.portal.ui.delivery.client.activity.cardbatch.meta.AbstractCardBatchMetaActivity;
import ru.protei.portal.ui.delivery.client.activity.cardbatch.meta.AbstractCardBatchMetaView;
import ru.protei.portal.ui.delivery.client.widget.cardbatch.priority.PrioritySelector;

import java.util.Date;

import static ru.protei.portal.core.model.util.CrmConstants.Masks.CARD_BATCH_ARTICLE_PATTERN;

public class CardBatchMetaView extends Composite implements AbstractCardBatchMetaView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        article.setRegexp(CARD_BATCH_ARTICLE_PATTERN);
        ensureDebugIds();
    }

    @Override
    public void setActivity(AbstractCardBatchMetaActivity activity) {
        this.activity = activity;
    }

    @Override
    public HasValue<CaseState> state() {
        return state;
    }

    @Override
    public HasEnabled stateEnabled() {
        return state;
    }

    @Override
    public HasValue<ImportanceLevel> priority() {
        return priority;
    }

    @Override
    public HasValue<Date> deadline() {
        return deadline;
    }

    @Override
    public HasEnabled deadlineEnabled() {
        return deadline;
    }

    @Override
    public boolean isDeadlineEmpty() {
        return HelperFunc.isEmpty(deadline.getInputValue());
    }

    @Override
    public void setDeadlineValid(boolean isValid) {
        deadline.markInputValid(isValid);
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
    public HasValue<String> article() {
        return article;
    }

    @Override
    public boolean articleIsValid() {
        return article.isValid();
    }

    @UiHandler("type")
    public void onTypeChanged(ValueChangeEvent<EntityOption> event) {
        activity.onTypeChanged();
    }

    @UiHandler("deadline")
    public void onDeadlineChanged(ValueChangeEvent<Date> event) {
        activity.onDeadlineChanged();
    }

    @UiHandler("state")
    public void onStateChanged(ValueChangeEvent<CaseState> event) {
        activity.onStateChange();
    }

    @UiHandler("priority")
    public void onPriorityChanged(ValueChangeEvent<ImportanceLevel> event) {
        activity.onPriorityChange();
    }

    @UiHandler("article")
    public void onArticleChanged(InputEvent event) {
        activity.onArticleChanged();
    }

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }
        state.setEnsureDebugId(DebugIds.CARD_BATCH.STATE_SELECTOR);
        priority.setEnsureDebugId(DebugIds.CARD_BATCH.PRIORITY_SELECTOR);
        deadline.ensureDebugId(DebugIds.CARD_BATCH.DEADLINE_DATE);
        type.ensureDebugId(DebugIds.CARD_BATCH.TYPE);
        article.ensureDebugId(DebugIds.CARD_BATCH.ARTICLE);
    }

    @UiField
    HTMLPanel root;
    @Inject
    @UiField(provided = true)
    CardBatchStateFormSelector state;
    @Inject
    @UiField(provided = true)
    PrioritySelector priority;
    @Inject
    @UiField(provided = true)
    SinglePicker deadline;
    @UiField
    ValidableTextBox article;
    @Inject
    @UiField(provided = true)
    CardTypeOptionFormSelector type;

    @UiField
    Lang lang;

    private AbstractCardBatchMetaActivity activity;

    interface ViewUiBinder extends UiBinder<HTMLPanel, CardBatchMetaView> {}
    private static ViewUiBinder ourUiBinder = GWT.create(ViewUiBinder.class);
}
