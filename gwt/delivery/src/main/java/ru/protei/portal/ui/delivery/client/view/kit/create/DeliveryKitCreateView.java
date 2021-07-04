package ru.protei.portal.ui.delivery.client.view.kit.create;

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
import ru.protei.portal.ui.delivery.client.activity.kit.create.AbstractDeliveryKitCreateActivity;
import ru.protei.portal.ui.delivery.client.activity.kit.create.AbstractDeliveryKitCreateView;

public class DeliveryKitCreateView extends Composite implements AbstractDeliveryKitCreateView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        ensureDebugIds();
    }

    @Override
    public void setActivity(AbstractDeliveryKitCreateActivity activity) {
        this.activity = activity;
    }

    @Override
    public HasWidgets getKitsContainer() {
        return kitsContainer;
    }

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }

        kitsContainer.ensureDebugId(DebugIds.DELIVERY.KITS);
    }

    @UiField
    HTMLPanel kitsContainer;

    @UiField
    @Inject
    Lang lang;

    private AbstractDeliveryKitCreateActivity activity;

    interface ViewUiBinder extends UiBinder<HTMLPanel, DeliveryKitCreateView> {}
    private static DeliveryKitCreateView.ViewUiBinder ourUiBinder = GWT.create(DeliveryKitCreateView.ViewUiBinder.class);
}
