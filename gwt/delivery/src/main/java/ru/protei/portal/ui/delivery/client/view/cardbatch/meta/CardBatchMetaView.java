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
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.cardbatch.state.CardBatchStateFormSelector;
import ru.protei.portal.ui.delivery.client.activity.cardbatch.meta.AbstractCardBatchMetaActivity;
import ru.protei.portal.ui.delivery.client.activity.cardbatch.meta.AbstractCardBatchMetaView;
import ru.protei.portal.ui.delivery.client.widget.cardbatch.priority.PrioritySelector;

import java.util.Date;

public class CardBatchMetaView extends Composite implements AbstractCardBatchMetaView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
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
    public HasEnabled stateEnable() {
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

    @UiHandler("deadline")
    public void onDeadlineChanged(ValueChangeEvent<Date> event) {
        activity.onDeadlineChanged();
    }

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }
        state.setEnsureDebugId(DebugIds.CARD_BATCH.STATE_SELECTOR);
        priority.setEnsureDebugId(DebugIds.CARD_BATCH.PRIORITY_SELECTOR);
        deadline.ensureDebugId(DebugIds.CARD_BATCH.DEADLINE_DATE);
    }

    @UiField
    HTMLPanel root;
    @Inject
    @UiField( provided = true )
    CardBatchStateFormSelector state;
    @Inject
    @UiField( provided = true )
    PrioritySelector priority;
    @Inject
    @UiField(provided = true)
    SinglePicker deadline;
    @UiField
    Lang lang;

    private AbstractCardBatchMetaActivity activity;

    interface ViewUiBinder extends UiBinder<HTMLPanel, CardBatchMetaView> {}
    private static ViewUiBinder ourUiBinder = GWT.create(ViewUiBinder.class);
}
