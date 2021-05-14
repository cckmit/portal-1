package ru.protei.portal.ui.common.client.widget.caselink.popup;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_BundleType;
import ru.protei.portal.core.model.dict.En_CaseLink;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.CaseLink;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.enterabletextbox.EnterableTextBox;

import static ru.protei.portal.core.model.helper.CollectionUtils.listOf;
import static ru.protei.portal.core.model.helper.StringUtils.trim;

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
        bundleTypeSelector.setModel(bundleTypeModel);
    }

    @Override
    protected void onLoad() {
        resizeHandlerReg = Window.addResizeHandler(resizeHandler);
        scrollHandlerReg = Window.addWindowScrollHandler(windowScrollHandler);

        fillOptionsToTypeSelector();
        setValueToTypeSelector(En_CaseLink.CRM);
        updateBundleTypeSelector();
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

    public void resetValueAndShow(En_CaseType caseType, UIObject target) {
        this.relative = target;
        this.caseType = caseType;

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
        String remoteId = trim(event.getValue());
        if (HelperFunc.isEmpty(remoteId)) {
            return;
        }

        En_CaseLink type = typeSelector.getValue();
        if (type == null) {
            return;
        }

        En_BundleType bundleType = bundleTypeSelector.getValue();
        if (bundleType == null) {
            return;
        }

        CaseLink caseLink = new CaseLink();
        caseLink.setRemoteId(remoteId);
        caseLink.setType(type);
        caseLink.setBundleType(bundleType);
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
        updateBundleTypeSelector();
    }

    @UiHandler("bundleTypeSelector")
    public void bundleTypeSelectorChanged(ValueChangeEvent<En_BundleType> event) {
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

    private void setValueToTypeSelector(En_CaseLink value){
        if (policyService.hasSystemScopeForPrivilege(En_Privilege.ISSUE_VIEW) || !value.isForcePrivacy()) {
            typeSelector.setValue(value);
        }
    }

    private void fillOptionsToTypeSelector(){
        for (En_CaseLink value : En_CaseLink.values()) {
            if (!policyService.hasSystemScopeForPrivilege(En_Privilege.ISSUE_VIEW) && value.isForcePrivacy()) {
                continue;
            }
            typeSelector.addOption(value);
        }
    }

    private void updateBundleTypeSelector() {
        bundleTypeModel.fill(
                En_CaseType.CRM_SUPPORT.equals(caseType) &&
                        En_CaseLink.CRM.equals(typeSelector.getValue()) &&
                        policyService.hasSystemScopeForPrivilege(En_Privilege.ISSUE_VIEW) ?
                        listOf(En_BundleType.values()) :
                        listOf(En_BundleType.LINKED_WITH));
        bundleTypeSelector.setValue(En_BundleType.LINKED_WITH);
    }

    @UiField
    HTMLPanel root;
    @Inject
    @UiField(provided = true)
    CaseLinkTypeSelector typeSelector;
    @Inject
    @UiField(provided = true)
    CaseLinkBundleTypeSelector bundleTypeSelector;
    @UiField
    EnterableTextBox remoteIdInput;
    @Inject
    @UiField
    Lang lang;
    @Inject
    PolicyService policyService;
    @Inject
    CaseLinkBundleTypeModel bundleTypeModel;

    private UIObject relative;
    private En_CaseType caseType;
    private ResizeHandler resizeHandler;
    private Window.ScrollHandler windowScrollHandler;
    private HandlerRegistration resizeHandlerReg;
    private HandlerRegistration scrollHandlerReg;
    private final RegExp youTrackPattern = RegExp.compile("^\\w+-\\d+$");
    private final Timer keyTapTimer = new Timer() {
        @Override
        public void run() {
            MatchResult youTrackMatcher = youTrackPattern.exec(trim(remoteIdInput.getValue()));

            if (youTrackMatcher == null) {
                if (typeSelector.getValue() == En_CaseLink.UITS){
                    return;
                }
                setValueToTypeSelector(En_CaseLink.CRM);
            } else {
                setValueToTypeSelector(En_CaseLink.YT);
            }
        }
    };

    interface CreateLinkPopupViewUiBinder extends UiBinder<HTMLPanel, CreateCaseLinkPopup> {}
    private static CreateLinkPopupViewUiBinder ourUiBinder = GWT.create(CreateLinkPopupViewUiBinder.class);
}
