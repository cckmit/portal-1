package ru.protei.portal.ui.delivery.client.view.module.namedescription;

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
import ru.protei.portal.ui.delivery.client.activity.module.edit.AbstractModuleNameDescriptionEditActivity;

public class ModuleNameDescriptionButtonsView extends Composite {

    @Inject
    public void init() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        ensureDebugIds();
    }

    public void setActivity( AbstractModuleNameDescriptionEditActivity activity ) {
        this.activity = activity;
    }

    @UiHandler("saveNameAndDescriptionButton")
    void onSaveNameAndDescriptionButtonClick( ClickEvent event ) {
        activity.saveModuleNameAndDescription();
    }

    @UiHandler("cancelNameAndDescriptionButton")
    void onCancelNameAndDescriptionButtonClick( ClickEvent event ) {
        activity.onNameDescriptionChanged();
    }

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }
        saveNameAndDescriptionButton.ensureDebugId(DebugIds.DELIVERY.KIT.MODULE.EDIT_NAME_AND_DESC_ACCEPT);
        cancelNameAndDescriptionButton.ensureDebugId(DebugIds.DELIVERY.KIT.MODULE.EDIT_NAME_AND_DESC_REJECT);
    }

    @UiField
    Button saveNameAndDescriptionButton;
    @UiField
    Button cancelNameAndDescriptionButton;

    private AbstractModuleNameDescriptionEditActivity activity;

    interface ModuleNameWidgetUiBinder extends UiBinder<HTMLPanel, ModuleNameDescriptionButtonsView> {}
    private static ModuleNameWidgetUiBinder ourUiBinder = GWT.create( ModuleNameWidgetUiBinder.class );
}
