package ru.protei.portal.ui.common.client.widget.casemeta.link.popup;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.*;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CaseLink;
import ru.protei.portal.core.model.ent.CaseLink;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.enterabletextbox.EnterableTextBox;

public class CreateCaseLinkPopup extends PopupPanel implements HasValueChangeHandlers<CaseLink> {

    @Inject
    public void onInit() {
        setWidget(ourUiBinder.createAndBindUi(this));
        setAutoHideEnabled(true);
        setAutoHideOnHistoryEventsEnabled(true);

        resizeHandler = resizeEvent -> {
            if (isAttached()) {
                showNear(relative, false);
            }
        };

        windowScrollHandler = event -> {
            if (isAttached()) {
                showNear(relative, false);
            }
        };
    }

    @Override
    protected void onLoad() {
        resizeHandlerReg = Window.addResizeHandler(resizeHandler);
        scrollHandlerReg = Window.addWindowScrollHandler(windowScrollHandler);
    }

    @Override
    protected void onUnload() {
        if (resizeHandlerReg != null) {
            resizeHandlerReg.removeHandler();
        }
        if (scrollHandlerReg != null) {
            scrollHandlerReg.removeHandler();
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<CaseLink> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    public void showNear(IsWidget nearWidget) {
        showNear(nearWidget, true);
    }

    public void showNear(IsWidget nearWidget, boolean reset) {
        this.relative = nearWidget;

        root.getElement().getStyle().setPosition(Style.Position.RELATIVE);
        root.getElement().getStyle().setDisplay(Style.Display.BLOCK);
        setPopupPositionAndShow((popupWidth, popupHeight) -> {
            int relativeLeft = nearWidget.asWidget().getAbsoluteLeft();
            int widthDiff = popupWidth - nearWidget.asWidget().getOffsetWidth();
            int popupLeft = relativeLeft - widthDiff;
            int relativeTop = nearWidget.asWidget().getAbsoluteTop();
            int popupTop = relativeTop + nearWidget.asWidget().getOffsetHeight();
            setPopupPosition(popupLeft, popupTop);
        });

        if (reset) {
            typeSelector.fillOptions();
            typeSelector.setValue(En_CaseLink.CRM);
            remoteIdInput.setValue("");
            remoteIdInput.setFocus(true);
        }
    }

    @UiHandler( "remoteIdInput" )
    public void onRemoteIdInputChanged( ValueChangeEvent<String> event ) {
        String remoteId = event.getValue();
        if (HelperFunc.isEmpty(remoteId)) {
            return;
        }

        En_CaseLink type = typeSelector.getValue();
        if (type == null) {
            return;
        }

        CaseLink caseLink = new CaseLink();
        caseLink.setRemoteId(remoteId);
        caseLink.setType(type);
        ValueChangeEvent.fire(this, caseLink);

        hide();
    }

    @UiHandler("typeSelector")
    public void typeSelectorChanged(ValueChangeEvent<En_CaseLink> event) {
        remoteIdInput.setFocus(true);
    }

    public void setEnsureDebugIdSelector(String debugId) {
        typeSelector.setEnsureDebugId(debugId);
    }

    public void setEnsureDebugIdTextBox(String debugId) {
        remoteIdInput.setEnsureDebugIdTextBox(debugId);
    }

    public void setEnsureDebugIdApply(String debugId) {
        remoteIdInput.setEnsureDebugIdAction(debugId);
    }

    @UiField
    HTMLPanel root;
    @Inject
    @UiField(provided = true)
    CaseLinkTypeSelector typeSelector;
    @UiField
    EnterableTextBox remoteIdInput;
    @Inject
    @UiField
    Lang lang;

    private IsWidget relative;
    private ResizeHandler resizeHandler;
    private Window.ScrollHandler windowScrollHandler;
    private HandlerRegistration resizeHandlerReg;
    private HandlerRegistration scrollHandlerReg;

    interface CreateLinkPopupViewUiBinder extends UiBinder<HTMLPanel, CreateCaseLinkPopup> {}
    private static CreateLinkPopupViewUiBinder ourUiBinder = GWT.create(CreateLinkPopupViewUiBinder.class);
}
