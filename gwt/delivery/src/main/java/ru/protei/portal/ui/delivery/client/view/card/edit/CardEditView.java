package ru.protei.portal.ui.delivery.client.view.card.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.delivery.client.activity.card.edit.AbstractCardEditActivity;
import ru.protei.portal.ui.delivery.client.activity.card.edit.AbstractCardEditView;

/**
 * Вид редактирования Поставки
 */
public class CardEditView extends Composite implements AbstractCardEditView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));

        ensureDebugIds();
    }

    @Override
    public void setActivity(AbstractCardEditActivity activity) {
        this.activity = activity;
    }

    @Override
    public void setSerialNumber(String value) {
        serialNumber.setInnerText(value);
    }

    public HasWidgets getNoteCommentContainer() {
        return noteCommentContainer;
    }

    @Override
    public HasWidgets getMetaContainer() {
        return metaContainer;
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
        if (activity != null) {
            activity.onNoteCommentClicked();
        }
    }


    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }
        // todo
//        noteCommentEditButton.ensureDebugId(DebugIds.DELIVERY.EDIT_NAME_AND_DESCRIPTION_BUTTON);
    }

    @UiField
    HTMLPanel root;
    @UiField
    LabelElement serialNumber;
    @UiField
    HTMLPanel noteCommentContainer;
    @UiField
    HTMLPanel metaContainer;
    @UiField
    Button noteCommentEditButton;
    @UiField
    Lang lang;
    @UiField
    Element createdBy;

    private AbstractCardEditActivity activity;

    interface ViewUiBinder extends UiBinder<HTMLPanel, CardEditView> {}
    private static ViewUiBinder ourUiBinder = GWT.create(ViewUiBinder.class);
}
