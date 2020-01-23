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
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.popup.BasePopupView;

public class PasswordGenPopup extends BasePopupView implements HasClickHandlers {

    public PasswordGenPopup() {
        setWidget(ourUiBinder.createAndBindUi(this));
        setAutoHideEnabled(true);
    }

    @Override
    public HandlerRegistration addClickHandler(ClickHandler handler) {
        return addHandler(handler, ClickEvent.getType());
    }

    @Override
    public void showNear( UIObject view ) {
        showOverRight(view, null);
    }

    @UiHandler("applyButton")
    public void onApplyButtonClicked(ClickEvent event) {
        ClickEvent.fireNativeEvent(event.getNativeEvent(), this);
        hide();
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
        }, KeyUpEvent.getType());
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