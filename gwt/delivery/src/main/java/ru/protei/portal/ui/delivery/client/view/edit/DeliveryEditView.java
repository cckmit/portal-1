package ru.protei.portal.ui.delivery.client.view.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.Kit;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.delivery.client.activity.edit.AbstractDeliveryEditActivity;
import ru.protei.portal.ui.delivery.client.activity.edit.AbstractDeliveryEditView;
import ru.protei.portal.ui.delivery.client.widget.kit.view.list.DeliveryKitList;

import java.util.List;

/**
 * Вид редактирования Поставки
 */
public class DeliveryEditView extends Composite implements AbstractDeliveryEditView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        ensureDebugIds();
    }

    @Override
    public void setActivity(AbstractDeliveryEditActivity activity) {
        this.activity = activity;
    }

    @Override
    public HasWidgets getNameContainer() {
        return nameContainer;
    }

    @Override
    public HasWidgets getKitsContainer() {
        return kitsContainer;
    }

    @Override
    public HasValue<List<Kit>> kits() {
        return kits;
    }

    @Override
    public void updateKitByProject(boolean isArmyProject) {
        kits.setArmyProject(isArmyProject);
    }

    @Override
    public HasWidgets getMetaContainer() {
        return metaContainer;
    }

    @UiHandler("backButton")
    public void onCancelClicked(ClickEvent event) {
        if (activity != null) {
            activity.onBackClicked();
        }
    }

    @UiHandler({"nameAndDescriptionEditButton"})
    public void onNameAndDescriptionEditButtonClicked(ClickEvent event) {
        if (activity != null) {
            activity.onNameAndDescriptionEditClicked();
        }
    }

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }
//        name.ensureDebugId(DebugIds.DELIVERY.NAME_INPUT);

        backButton.ensureDebugId(DebugIds.DELIVERY.BACK_BUTTON);
    }

    @UiField
    HTMLPanel root;
    @UiField
    HTMLPanel nameContainer;
    @UiField
    HTMLPanel kitsContainer;
    @Inject
    @UiField(provided = true)
    DeliveryKitList kits;
    @UiField
    HTMLPanel metaContainer;
    @UiField
    Button backButton;
    @UiField
    Button nameAndDescriptionEditButton;
    @UiField
    Lang lang;

    private AbstractDeliveryEditActivity activity;

    interface ViewUiBinder extends UiBinder<HTMLPanel, DeliveryEditView> {}
    private static ViewUiBinder ourUiBinder = GWT.create(ViewUiBinder.class);
}
