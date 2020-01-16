package ru.protei.portal.ui.common.client.widget.caselink.popup;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.logical.shared.*;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CaseLink;
import ru.protei.portal.core.model.ent.CaseLink;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.ui.common.client.events.InputEvent;
import ru.protei.portal.ui.common.client.events.InputHandler;
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
                showRelativeTo(relative);
            }
        };

        windowScrollHandler = event -> {
            if (isAttached()) {
                showRelativeTo(relative);
            }
        };

        setInputTextHandler();
    }

    @Override
    protected void onLoad() {
        resizeHandlerReg = Window.addResizeHandler(resizeHandler);
        scrollHandlerReg = Window.addWindowScrollHandler(windowScrollHandler);

        typeSelector.fillOptions();
        typeSelector.setValue(En_CaseLink.CRM);
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

    public void resetValueAndShow(UIObject target) {
        this.relative = target;

        showRelativeTo(target);

        remoteIdInput.setValue("");
        remoteIdInput.setFocus(true);
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<CaseLink> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
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

    @UiHandler("remoteIdInput")
    public void onChangeText(KeyPressEvent event) {
        if (event.getCharCode() == KeyCodes.KEY_ENTER) {
            keyTapTimer.run();
        }
    }

    public void setInputTextHandler() {
        remoteIdInput.addInputHandler(event -> {
            keyTapTimer.cancel();
            keyTapTimer.schedule(300);
        });
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

    private UIObject relative;
    private ResizeHandler resizeHandler;
    private Window.ScrollHandler windowScrollHandler;
    private HandlerRegistration resizeHandlerReg;
    private HandlerRegistration scrollHandlerReg;
    private RegExp youTrackPattern = RegExp.compile("^\\w+-\\d+$");
    private Timer keyTapTimer = new Timer() {
        @Override
        public void run() {
            MatchResult youTrackMatcher = youTrackPattern.exec(remoteIdInput.getValue());

            if (youTrackMatcher != null) {
                typeSelector.setValue(En_CaseLink.YT);
            } else{
                typeSelector.setValue(En_CaseLink.CRM);
            }
        }
    };


    interface CreateLinkPopupViewUiBinder extends UiBinder<HTMLPanel, CreateCaseLinkPopup> {}
    private static CreateLinkPopupViewUiBinder ourUiBinder = GWT.create(CreateLinkPopupViewUiBinder.class);
}
