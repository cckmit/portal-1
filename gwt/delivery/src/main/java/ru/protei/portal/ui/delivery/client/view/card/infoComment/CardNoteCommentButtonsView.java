package ru.protei.portal.ui.delivery.client.view.card.infoComment;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.inject.Inject;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.delivery.client.activity.card.edit.AbstractCardNoteCommentEditActivity;

public class CardNoteCommentButtonsView extends Composite {

    @Inject
    public void init() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        ensureDebugIds();
    }

    public void setActivity(AbstractCardNoteCommentEditActivity activity ) {
        this.activity = activity;
    }

    @UiHandler("saveButton")
    void onSaveButtonClick(ClickEvent event ) {
        activity.onSaveNoteCommentClicked();
    }

    @UiHandler("cancelButton")
    void onCancelButtonClick(ClickEvent event ) {
        activity.onCancelNoteCommentClicked();
    }

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }
        saveButton.ensureDebugId(DebugIds.ISSUE.EDIT_NAME_AND_DESC_ACCEPT);
        cancelButton.ensureDebugId(DebugIds.ISSUE.EDIT_NAME_AND_DESC_REJECT);
    }

    @UiField
    Button saveButton;
    @UiField
    Button cancelButton;

    private AbstractCardNoteCommentEditActivity activity;

    interface WidgetUiBinder extends UiBinder<HTMLPanel, CardNoteCommentButtonsView> {}
    private static WidgetUiBinder ourUiBinder = GWT.create( WidgetUiBinder.class );
}
