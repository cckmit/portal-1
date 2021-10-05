package ru.protei.portal.ui.delivery.client.view.card.meta;

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
import ru.protei.portal.core.model.ent.CardBatch;
import ru.protei.portal.core.model.ent.CardType;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.widget.selector.card.state.CardStateFormSelector;
import ru.protei.portal.ui.common.client.widget.selector.card.type.CardTypeFormSelector;
import ru.protei.portal.ui.common.client.widget.selector.cardbatch.CardBatchFormSelector;
import ru.protei.portal.ui.common.client.widget.selector.cardbatch.CardBatchModel;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeFormSelector;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;
import ru.protei.portal.ui.delivery.client.activity.card.meta.AbstractCardCommonMeta;
import ru.protei.portal.ui.delivery.client.activity.card.meta.AbstractCardCreateMetaActivity;
import ru.protei.portal.ui.delivery.client.activity.card.meta.AbstractCardEditMetaActivity;
import ru.protei.portal.ui.delivery.client.activity.card.meta.AbstractCardMetaView;

import java.util.Date;

import static ru.protei.portal.core.model.util.CrmConstants.Masks.CARD_BATCH_ARTICLE_PATTERN;

public class CardMetaView extends Composite implements AbstractCardMetaView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        cardBatch.setModel(cardBatchModel);
        article.setRegexp(CARD_BATCH_ARTICLE_PATTERN);
        ensureDebugIds();
    }

    @Override
    public void setCreateActivity(AbstractCardCreateMetaActivity activity) {
        this.commonActivity = activity;
        type.addValueChangeHandler(event -> activity.onTypeChange());
        cardBatch.addValueChangeHandler(event -> activity.onCardBatchChange());
    }

    @Override
    public void setEditActivity(AbstractCardEditMetaActivity activity) {
        this.commonActivity = activity;
        state.addValueChangeHandler(event -> activity.onStateChanged());
        manager.addValueChangeHandler(event -> activity.onManagerChanged());
    }

    @Override
    public HasValue<CaseState> state() {
        return state;
    }

    @Override
    public HasValue<CardType> type() {
        return type;
    }

    @Override
    public HasEnabled typeEnable() {
        return type;
    }

    @Override
    public HasValue<CardBatch> cardBatch() {
        return cardBatch;
    }

    @Override
    public HasEnabled cardBatchEnable() {
        return cardBatch;
    }

    @Override
    public CardBatchModel cardBatchModel() {
        return cardBatchModel;
    }

    @Override
    public HasValue<String> article() {
        return article;
    }

    @Override
    public boolean articleIsValid() {
        return article.isValid();
    }

    @Override
    public HasValue<PersonShortView> manager() {
        return manager;
    }

    @Override
    public HasValue<Date> testDate() {
        return testDate;
    }

    @Override
    public boolean isTestDateEmpty() {
        return HelperFunc.isEmpty(testDate.getInputValue());
    }

    @Override
    public void setTestDateValid(boolean isValid) {
        testDate.markInputValid(isValid);
    }

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }
        state.setEnsureDebugId(DebugIds.CARD.STATE);
        type.ensureDebugId(DebugIds.CARD.TYPE);
        cardBatch.ensureLabelDebugId(DebugIds.CARD.CARD_BATCH);
        article.ensureDebugId(DebugIds.CARD.ARTICLE);
        manager.ensureDebugId(DebugIds.CARD.MANAGER);
        testDate.ensureDebugId(DebugIds.CARD.TEST_DATE);
    }

    @UiHandler("article")
    public void onArticleChanged(ValueChangeEvent<String> event) {
        commonActivity.onArticleChanged();
    }

    @UiHandler("testDate")
    public void onTestDateChanged(ValueChangeEvent<Date> event) {
        commonActivity.onTestDateChanged();
    }

    @Inject
    @UiField( provided = true )
    CardStateFormSelector state;
    @Inject
    @UiField( provided = true )
    CardTypeFormSelector type;
    @Inject
    @UiField( provided = true )
    CardBatchFormSelector cardBatch;
    @UiField
    ValidableTextBox article;
    @Inject
    @UiField(provided = true)
    EmployeeFormSelector manager;
    @Inject
    @UiField(provided = true)
    SinglePicker testDate;

    @Inject
    CardBatchModel cardBatchModel;

    private AbstractCardCommonMeta commonActivity;

    private static ViewUiBinder ourUiBinder = GWT.create(ViewUiBinder.class);
    interface ViewUiBinder extends UiBinder<HTMLPanel, CardMetaView> {}
}
