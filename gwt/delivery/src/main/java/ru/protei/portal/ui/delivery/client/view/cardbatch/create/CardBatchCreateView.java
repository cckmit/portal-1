package ru.protei.portal.ui.delivery.client.view.cardbatch.create;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.delivery.client.activity.cardbatch.create.AbstractCardBatchCreateActivity;
import ru.protei.portal.ui.delivery.client.activity.cardbatch.create.AbstractCardBatchCreateView;
import ru.protei.portal.ui.delivery.client.view.cardbatch.common.CardBatchCommonInfoView;

public class CardBatchCreateView extends Composite implements AbstractCardBatchCreateView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        ensureDebugIds();
//        meta.stateEnable().setEnabled(false);
    }

    @Override
    public void setActivity(AbstractCardBatchCreateActivity activity) {
        this.activity = activity;
    }

    @Override
    public HasEnabled saveEnabled() {
        return saveButton;
    }

    @Override
    public HasValue<EntityOption> type() { return commonInfoView.type(); }

    @Override
    public HasValue<String> number() { return commonInfoView.number(); }

    @Override
    public HasValue<String> article() { return commonInfoView.article(); }

    @Override
    public HasValue<Integer> amount() { return commonInfoView.amount(); }

    @Override
    public HasValue<String> params() { return commonInfoView.params(); }

    @Override
    public boolean isArticleValid() {
        return commonInfoView.isArticleValid();
    }

    @UiHandler("saveButton")
    public void onSaveClicked(ClickEvent event) {
        if (activity != null) {
            activity.onSaveClicked();
        }
    }

    @UiHandler({"cancelButton"})
    public void onCancelClicked(ClickEvent event) {
        if (activity != null) {
            activity.onCancelClicked();
        }
    }

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }
        saveButton.ensureDebugId(DebugIds.CARD_BATCH.SAVE_BUTTON);
        cancelButton.ensureDebugId(DebugIds.CARD_BATCH.CANCEL_BUTTON);
    }

    @UiField
    HTMLPanel root;
    @Inject
    @UiField(provided = true)
    CardBatchCommonInfoView commonInfoView;
//    @Inject
//    @UiField(provided = true)
//    DeliveryMetaView meta;

    @UiField
    Button saveButton;
    @UiField
    Button cancelButton;
    @Inject
    @UiField
    Lang lang;

    private AbstractCardBatchCreateActivity activity;

    interface ViewUiBinder extends UiBinder<HTMLPanel, CardBatchCreateView> {}
    private static ViewUiBinder ourUiBinder = GWT.create(ViewUiBinder.class);
}
