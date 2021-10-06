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
    public HasVisibility noteCommentEditButtonVisibility() {
        return noteCommentEditButton;
    }

    @Override
    public void setCreatedBy(String value) {
        this.createdBy.setInnerHTML( value );
    }

    @UiHandler({"noteCommentEditButton"})
    public void onNameAndDescriptionEditButtonClicked(ClickEvent event) {
        activity.onMainInfoEditClicked();
    }

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }
        noteCommentEditButton.ensureDebugId(DebugIds.CARD_BATCH.EDIT_NOTE_COMMENT_BUTTON);
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
    Button noteCommentEditButton;
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
    Lang lang;

    private AbstractCardBatchEditActivity activity;

    private static CardBatchViewUiBinder ourUiBinder = GWT.create(CardBatchViewUiBinder.class);
    interface CardBatchViewUiBinder extends UiBinder<HTMLPanel, CardBatchEditView> {}
}
