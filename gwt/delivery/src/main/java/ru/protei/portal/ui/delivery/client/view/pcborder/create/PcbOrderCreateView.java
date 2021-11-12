package ru.protei.portal.ui.delivery.client.view.pcborder.create;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.delivery.client.activity.pcborder.create.AbstractPcbOrderCreateActivity;
import ru.protei.portal.ui.delivery.client.activity.pcborder.create.AbstractPcbOrderCreateView;

public class PcbOrderCreateView extends Composite implements AbstractPcbOrderCreateView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        ensureDebugIds();
    }

    @Override
    public void setActivity(AbstractPcbOrderCreateActivity activity) {
        this.activity = activity;
    }

    @Override
    public HasEnabled saveEnabled() {
        return saveButton;
    }

    @Override
    public HasWidgets getCommonInfoContainer() {
        return commonInfoContainer;
    }

    @Override
    public HasWidgets getMetaContainer() {
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
        saveButton.ensureDebugId(DebugIds.PCB_ORDER.SAVE_BUTTON);
        cancelButton.ensureDebugId(DebugIds.PCB_ORDER.CANCEL_BUTTON);
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

    private AbstractPcbOrderCreateActivity activity;

    interface ViewUiBinder extends UiBinder<HTMLPanel, PcbOrderCreateView> {}
    private static ViewUiBinder ourUiBinder = GWT.create(ViewUiBinder.class);
}
