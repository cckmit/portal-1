package ru.protei.portal.ui.delivery.client.view.delivery.kit.actionmenu.popup;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.UListElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.popup.BasePopupView;
import ru.protei.portal.ui.delivery.client.activity.actionmenu.AbstractKitMenuPopupActivity;

public class KitMenuPopup extends BasePopupView {

    @Inject
    public void onInit() {
        setWidget(ourUiBinder.createAndBindUi(this));
        setAutoHideEnabled(true);
        setAutoHideOnHistoryEventsEnabled(true);
    }

    @Override
    protected UIObject getPositionRoot() {
        return root;
    }

    public void setActivity(AbstractKitMenuPopupActivity activity){
        this.activity = activity;
    }

    @UiHandler("copy")
    public void onCopyClick(ClickEvent event) {
        event.preventDefault();
        activity.onCopyClick();
    }

    @UiHandler("changeState")
    public void onChangeStateClick(MouseOverEvent event) {
        statesContainer.removeClassName("hide");
    }

    @UiHandler("changeState")
    public void onChangeStateClick(MouseOutEvent event) {
        statesContainer.addClassName("hide");
    }

    @UiHandler("edit")
    public void onEditClick(ClickEvent event) {
        event.preventDefault();
        activity.onEditClick();
    }

    @UiHandler("remove")
    public void onRemoveClick(ClickEvent event) {
        event.preventDefault();
        activity.onRemoveClick();
    }

    @UiField
    Lang lang;
    @UiField
    HTMLPanel root;

    @UiField
    UListElement statesContainer;

    @UiField
    Anchor copy;
    @UiField
    Anchor changeState;
    @UiField
    Anchor edit;
    @UiField
    Anchor remove;

    AbstractKitMenuPopupActivity activity;

    interface KitMenuPopupUiBinder extends UiBinder<HTMLPanel, KitMenuPopup> {}
    private static KitMenuPopupUiBinder ourUiBinder = GWT.create(KitMenuPopupUiBinder.class);
}
