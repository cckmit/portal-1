package ru.protei.portal.ui.common.client.view.passwordgen.popup;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.UIObject;
import ru.protei.portal.ui.common.client.events.ApproveEvent;
import ru.protei.portal.ui.common.client.events.ApproveHandler;
import ru.protei.portal.ui.common.client.events.HasApproveHandlers;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.popup.BasePopupView;

public class PasswordGenPopup extends BasePopupView implements HasApproveHandlers {

    public PasswordGenPopup() {
        setWidget(ourUiBinder.createAndBindUi(this));
        setAutoHideEnabled(true);
    }

    @Override
    public HandlerRegistration addApproveHandler(ApproveHandler handler) {
        return addHandler(handler, ApproveEvent.getType());
    }

    @Override
    public void showNear( UIObject view ) {
        showOverRight(view, null);
    }

    @UiHandler("applyButton")
    public void onApplyButtonClicked(ClickEvent event) {
        fireApproveEventAndHidePopup();
    }

    @UiHandler("cancelButton")
    public void onCancelClicked(ClickEvent event) {
        hide();
    }

    @Override
    protected Element getArrow() {
        return arrow.getElement();
    }

    @Override
    protected UIObject getPositionRoot() {
        return root;
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        registerHandlers();
    }

    @Override
    protected void onDetach() {
        super.onDetach();

        keyHandlerRegistration.removeHandler();
    }

    private void registerHandlers() {
        keyHandlerRegistration = RootPanel.get().addDomHandler(keyUpEvent -> {
            if (KeyCodes.KEY_ESCAPE == keyUpEvent.getNativeKeyCode()) {
                hide();
            }
            if (KeyCodes.KEY_ENTER == keyUpEvent.getNativeKeyCode()) {
                fireApproveEventAndHidePopup();
            }
        }, KeyUpEvent.getType());
    }

    private void fireApproveEventAndHidePopup() {
        ApproveEvent.fire(this);
        hide();
    }

    @UiField
    HTMLPanel root;

    @UiField
    Lang lang;

    @UiField
    Button applyButton;

    @UiField
    Button cancelButton;

    @UiField
    HTMLPanel arrow;

    private HandlerRegistration keyHandlerRegistration;

    interface PopupContainerViewUiBinder extends UiBinder<HTMLPanel, PasswordGenPopup> {}
    private static PopupContainerViewUiBinder ourUiBinder = GWT.create(PopupContainerViewUiBinder.class);

}