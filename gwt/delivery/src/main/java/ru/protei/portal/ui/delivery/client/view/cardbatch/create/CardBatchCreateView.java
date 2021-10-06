package ru.protei.portal.ui.delivery.client.view.cardbatch.create;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.inject.Inject;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.delivery.client.activity.cardbatch.create.AbstractCardBatchCreateActivity;
import ru.protei.portal.ui.delivery.client.activity.cardbatch.create.AbstractCardBatchCreateView;

public class CardBatchCreateView extends Composite implements AbstractCardBatchCreateView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        ensureDebugIds();
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
    public HTMLPanel getCommonInfoContainer() {
        return commonInfoContainer;
    }

    @Override
    public HTMLPanel getMetaContainer() {
        return metaContainer;
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
    @UiField
    HTMLPanel commonInfoContainer;
    @UiField
    HTMLPanel metaContainer;
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
