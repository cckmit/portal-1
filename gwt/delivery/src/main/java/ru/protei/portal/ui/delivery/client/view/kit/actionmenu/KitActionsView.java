package ru.protei.portal.ui.delivery.client.view.kit.actionmenu;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.stringselectpopup.StringSelectPopup;
import ru.protei.portal.ui.delivery.client.activity.kit.handler.KitActionsHandler;

import java.util.Arrays;

public class KitActionsView extends Composite {

    @Inject
    public void onInit(Lang lang) {
        initWidget(ourUiBinder.createAndBindUi(this));

        popup.setValues(Arrays.asList(lang.buttonCopy(), lang.buttonState(), lang.buttonRemove()));

        popup.addValueChangeHandler(event -> {
            if (handler == null) {
                return;
            }
            if (lang.buttonCopy().equals(event.getValue())) {
                handler.onCopy();
                return;
            }
            if (lang.buttonState().equals(event.getValue())) {
                handler.onChangeState();
                return;
            }
            if (lang.buttonRemove().equals(event.getValue())) {
                handler.onRemove();
                return;
            }
        });
    }

    @UiHandler("reloadKitsBtn")
    public void onReloadKitsBtnClicked(ClickEvent event) {
        event.preventDefault();
        if ( handler != null ) {
            handler.onReload();
        }
    }

    @UiHandler("kitsActionsBtn")
    public void setKitsActionsBtnClick(ClickEvent event) {
        event.preventDefault();
        popup.showUnderRight(kitsActionsBtn, 200);
    }

    public void setHandler(KitActionsHandler handler) {
        this.handler = handler;
    }

    private StringSelectPopup popup = new StringSelectPopup();
    private KitActionsHandler handler;

    @UiField
    Anchor reloadKitsBtn;
    @UiField
    Anchor kitsActionsBtn;

    private static KitActionsView.KitViewUiBinder ourUiBinder = GWT.create(KitActionsView.KitViewUiBinder.class);
    interface KitViewUiBinder extends UiBinder<HTMLPanel, KitActionsView> {}
}
