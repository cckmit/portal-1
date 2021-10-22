package ru.protei.portal.ui.delivery.client.view.cardbatch.edit;

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
import ru.protei.portal.ui.delivery.client.activity.cardbatch.edit.AbstractCardBatchEditActivity;
import ru.protei.portal.ui.delivery.client.activity.cardbatch.edit.AbstractCardBatchEditView;

public class CardBatchEditView extends Composite implements AbstractCardBatchEditView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        ensureDebugIds();
    }

    @Override
    public void setActivity(AbstractCardBatchEditActivity activity) {
        this.activity = activity;
    }

    @Override
    public HTMLPanel getCommonInfoEditContainer() {
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
    public HTMLPanel getMetaContainer() {
        return metaContainer;
    }

    @Override
    public void setTypeRO(String value) {
        typeRO.setInnerHTML(value);
    }

    @Override
    public void setArticleRO(String value) {
        articleRO.setInnerHTML(value);
    }

    @Override
    public void setAmountRO(String value) {
        amountRO.setInnerHTML(value);
    }

    @Override
    public void setParamsRO(String value) {
        paramsRO.setInnerHTML(value);
    }

    @Override
    public void setNumberRO(String value) {
        numberRO.setText(value);
    }

    @Override
    public void setContractorsRO(String value) {
        contractorsRO.setInnerHTML(value);
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

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }
        backButton.ensureDebugId(DebugIds.ISSUE.BACK_BUTTON);
        commonInfoEditButton.ensureDebugId(DebugIds.CARD_BATCH.EDIT_COMMON_INFO_BUTTON);
        numberRO.ensureDebugId(DebugIds.CARD_BATCH.NUMBER);
    }

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
    Label numberRO;
    @UiField
    DivElement typeRO;
    @UiField
    DivElement articleRO;
    @UiField
    DivElement amountRO;
    @UiField
    DivElement paramsRO;
    @UiField
    DivElement contractorsRO;
    @UiField
    Lang lang;
    @UiField
    Button backButton;

    private AbstractCardBatchEditActivity activity;

    private static CardBatchViewUiBinder ourUiBinder = GWT.create(CardBatchViewUiBinder.class);
    interface CardBatchViewUiBinder extends UiBinder<HTMLPanel, CardBatchEditView> {}
}
