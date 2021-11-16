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
import ru.protei.portal.ui.common.client.widget.stringselectpopup.StringSelectPopup;
import ru.protei.portal.ui.delivery.client.activity.delivery.kit.handler.KitActionsHandler;

import java.util.Arrays;

public class KitMenu extends Composite {

    @Inject
    public void onInit(Lang lang) {
        initWidget(ourUiBinder.createAndBindUi(this));

        String ACTION_COPY = lang.buttonCopy();
        String ACTION_CHANGE_STATE = lang.buttonState();
        String ACTION_EDIT = lang.buttonModify();
        String ACTION_REMOVE = lang.buttonRemove();
        popup.setValues(Arrays.asList(ACTION_COPY, ACTION_CHANGE_STATE, ACTION_EDIT, ACTION_REMOVE));

        popup.addValueChangeHandler(event -> {
            if (handler == null) {
                return;
            }
            if (ACTION_COPY.equals(event.getValue())) {
                handler.onCopy();
                return;
            }
            if (ACTION_CHANGE_STATE.equals(event.getValue())) {
                handler.onGroupChangeState();
                return;
            }
            if (ACTION_EDIT.equals(event.getValue())) {
                handler.onEdit();
                return;
            }
            if (ACTION_REMOVE.equals(event.getValue())) {
                handler.onGroupRemove();
                return;
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
        popup.showUnderRight(actionsBtn, 200);
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

    private StringSelectPopup popup = new StringSelectPopup();
    private KitActionsHandler handler;

    @UiField
    Anchor actionsBtn;
    @UiField
    Anchor backBtn;

    private static KitMenu.KitViewUiBinder ourUiBinder = GWT.create(KitMenu.KitViewUiBinder.class);

    interface KitViewUiBinder extends UiBinder<HTMLPanel, KitMenu> {}
}