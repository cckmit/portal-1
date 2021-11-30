package ru.protei.portal.ui.delivery.client.view.delivery.kit.actionmenu;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.inject.Inject;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.delivery.client.activity.actionmenu.AbstractKitMenuPopupActivity;
import ru.protei.portal.ui.delivery.client.activity.delivery.kit.handler.KitActionsHandler;
import ru.protei.portal.ui.delivery.client.view.delivery.kit.actionmenu.popup.KitMenuPopup;

public class KitMenu extends Composite {

    @Inject
    public void onInit(Lang lang) {
        initWidget(ourUiBinder.createAndBindUi(this));
        kitMenuPopup.setActivity(new AbstractKitMenuPopupActivity() {
            @Override
            public void onCopyClick() {
                handler.onCopy();
            }

            @Override
            public void onEditClick() {
                handler.onEdit();
            }

            @Override
            public void onRemoveClick() {
                handler.onGroupRemove();
            }
        });

        ensureDebugIds();
    }

    @UiHandler("backBtn")
    public void onBackBtnClicked(ClickEvent event) {
        event.preventDefault();
        if (handler != null) {
            handler.onBack();
        }
    }

    @UiHandler("actionsBtn")
    public void onActionsBtnClick(ClickEvent event) {
        event.preventDefault();
        kitMenuPopup.showUnderRight(actionsBtn, 200);
    }

    public void setHandler(KitActionsHandler handler) {
        this.handler = handler;
    }

    public void setActionsEnabled(boolean isEnabled) {
        actionsBtn.setStyleName("link-disabled", !isEnabled);
    }

    public void setBackVisible(boolean isVisible) {
        backBtn.setVisible(isVisible);
    }

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }
        backBtn.ensureDebugId(DebugIds.DELIVERY.KIT.BACK_BUTTON);
        actionsBtn.ensureDebugId(DebugIds.DELIVERY.KIT.ACTION_MENU_BUTTON);
    }

    private KitActionsHandler handler;

    @UiField
    Anchor actionsBtn;
    @UiField
    Anchor backBtn;
    @Inject
    KitMenuPopup kitMenuPopup;

    private static KitMenu.KitViewUiBinder ourUiBinder = GWT.create(KitMenu.KitViewUiBinder.class);

    interface KitViewUiBinder extends UiBinder<HTMLPanel, KitMenu> {}
}
