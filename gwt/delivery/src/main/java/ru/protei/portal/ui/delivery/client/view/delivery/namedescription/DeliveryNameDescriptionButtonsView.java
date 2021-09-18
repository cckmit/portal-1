package ru.protei.portal.ui.delivery.client.view.delivery.namedescription;

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
import ru.protei.portal.ui.delivery.client.activity.delivery.edit.AbstractDeliveryNameDescriptionEditActivity;

public class DeliveryNameDescriptionButtonsView extends Composite {

    @Inject
    public void init() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        ensureDebugIds();
    }

    public void setActivity( AbstractDeliveryNameDescriptionEditActivity activity ) {
        this.activity = activity;
    }

    @UiHandler("saveNameAndDescriptionButton")
    void onSaveNameAndDescriptionButtonClick( ClickEvent event ) {
        activity.saveIssueNameAndDescription();
    }

    @UiHandler("cancelNameAndDescriptionButton")
    void onCancelNameAndDescriptionButtonClick( ClickEvent event ) {
        activity.onNameDescriptionChanged();
    }

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }
        saveNameAndDescriptionButton.ensureDebugId(DebugIds.ISSUE.EDIT_NAME_AND_DESC_ACCEPT);
        cancelNameAndDescriptionButton.ensureDebugId(DebugIds.ISSUE.EDIT_NAME_AND_DESC_REJECT);
    }

    @UiField
    Button saveNameAndDescriptionButton;
    @UiField
    Button cancelNameAndDescriptionButton;

    private AbstractDeliveryNameDescriptionEditActivity activity;

    interface IssueNameWidgetUiBinder extends UiBinder<HTMLPanel, DeliveryNameDescriptionButtonsView> {}
    private static IssueNameWidgetUiBinder ourUiBinder = GWT.create( IssueNameWidgetUiBinder.class );
}
