package ru.protei.portal.ui.delivery.client.view.kit.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.delivery.client.activity.kit.edit.AbstractDeliveryKitEditActivity;
import ru.protei.portal.ui.delivery.client.activity.kit.edit.AbstractDeliveryKitEditView;

public class DeliveryKitEditView extends Composite implements AbstractDeliveryKitEditView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setActivity(AbstractDeliveryKitEditActivity activity) {
        this.activity = activity;
    }

    @Override
    public HasWidgets getKitsContainer() {
        return kitsContainer;
    }

    @UiField
    HTMLPanel kitsContainer;

    @UiField
    @Inject
    Lang lang;

    private AbstractDeliveryKitEditActivity activity;

    interface ViewUiBinder extends UiBinder<HTMLPanel, DeliveryKitEditView> {}
    private static DeliveryKitEditView.ViewUiBinder ourUiBinder = GWT.create(DeliveryKitEditView.ViewUiBinder.class);
}
