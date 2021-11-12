package ru.protei.portal.ui.delivery.client.view.pcborder.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.delivery.client.activity.pcborder.edit.AbstractPcbOrderEditActivity;
import ru.protei.portal.ui.delivery.client.activity.pcborder.edit.AbstractPcbOrderEditView;

public class PcbOrderEditView extends Composite implements AbstractPcbOrderEditView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        ensureDebugIds();
    }

    @Override
    public void setActivity(AbstractPcbOrderEditActivity activity) {
        this.activity = activity;
    }

    @Override
    public HasWidgets getCommonInfoEditContainer() {
        return commonInfoEditContainer;
    }

    @Override
    public HasVisibility commonInfoEditContainerVisibility() {
        return commonInfoEditContainer;
    }

    @Override
    public HasVisibility commonInfoContainerVisibility() {
        return commonInfoContainer;
    }

    @Override
    public HasWidgets getMetaContainer() {
        return metaContainer;
    }

    @Override
    public void setCardTypeRO(String value) {
        cardTypeRO.setInnerHTML(value);
    }

    @Override
    public void setAmountRO(String value) {
        amountRO.setInnerHTML(value);
    }

    @Override
    public void setModificationRO(String value) {
        modificationRO.setInnerHTML(value);
    }

    @Override
    public void setCommentRO(String value) {
        commentRO.setInnerHTML(value);
    }

    @Override
    public HasVisibility backButtonVisibility() {
        return backButton;
    }

    @Override
    public HasVisibility commonInfoEditButtonVisibility() {
        return commonInfoEditButton;
    }

    @Override
    public void setCreatedBy(String value) {
        this.createdBy.setInnerHTML( value );
    }

    @Override
    public void setPreviewStyles(boolean isPreview) {
        root.removeStyleName("card-default");
        root.removeStyleName("card-transparent");
        root.removeStyleName("card-fixed");
        if (isPreview) {
            root.addStyleName("card-default");
            root.addStyleName("card-fixed");
        } else {
            root.addStyleName("card-transparent");
        }
    }

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }
        backButton.ensureDebugId(DebugIds.ISSUE.BACK_BUTTON);
        commonInfoEditButton.ensureDebugId(DebugIds.PCB_ORDER.EDIT_COMMON_INFO_BUTTON);
    }

    @UiHandler("backButton")
    public void onBackButtonClick(ClickEvent event) {
        if (activity != null) {
            activity.onBackClicked();
        }
    }

    @UiHandler({"commonInfoEditButton"})
    public void onCommonInfoEditButtonClicked(ClickEvent event) {
        if (activity != null) {
            activity.onCommonInfoEditClicked();
        }
    }

    @UiField
    Lang lang;
    @UiField
    HTMLPanel root;
    @UiField
    HTMLPanel commonInfoContainer;
    @UiField
    HTMLPanel commonInfoEditContainer;
    @UiField
    HTMLPanel metaContainer;
    @UiField
    Button commonInfoEditButton;
    @UiField
    Element createdBy;
    @UiField
    DivElement cardTypeRO;
    @UiField
    DivElement amountRO;
    @UiField
    DivElement modificationRO;
    @UiField
    DivElement commentRO;
    @UiField
    Button backButton;

    private AbstractPcbOrderEditActivity activity;

    private static PcbOrderEditViewUiBinder ourUiBinder = GWT.create(PcbOrderEditViewUiBinder.class);
    interface PcbOrderEditViewUiBinder extends UiBinder<HTMLPanel, PcbOrderEditView> {}
}
