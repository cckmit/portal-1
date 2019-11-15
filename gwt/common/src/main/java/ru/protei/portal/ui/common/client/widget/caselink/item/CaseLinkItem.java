package ru.protei.portal.ui.common.client.widget.caselink.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.logical.shared.*;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.ent.CaseInfo;
import ru.protei.portal.core.model.ent.CaseLink;
import ru.protei.portal.core.model.ent.YouTrackIssueInfo;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.common.ImportanceStyleProvider;
import ru.protei.portal.ui.common.client.lang.En_CaseStateLang;
import ru.protei.portal.ui.common.client.lang.Lang;

import java.util.Set;

import static ru.protei.portal.core.model.dict.En_CaseState.*;
import static ru.protei.portal.core.model.helper.CollectionUtils.setOf;
import static ru.protei.portal.test.client.DebugIds.DEBUG_ID_ATTRIBUTE;

public class CaseLinkItem extends Composite implements HasValue<CaseLink>, HasCloseHandlers<CaseLink>, HasEnabled {

    public CaseLinkItem() {
        initWidget(ourUiBinder.createAndBindUi(this));
        setTestAttributes();
    }

    @Override
    public void setValue(CaseLink value) {
        setValue(value, false);
    }

    @Override
    public void setValue(CaseLink value, boolean fireEvents) {
        caseLink = value;
        switch (caseLink.getType()) {
            case CRM: {
                if (value.getCaseInfo() != null) {
                    text.setInnerText(caseLink.getCaseInfo().getCaseNumber().toString());
                    fillCaseInfo(value.getCaseInfo());
                }
                break;
            }
            case YT: {
                text.setInnerText(caseLink.getRemoteId());
                processYouTrackInfo(value.getYouTrackInfo());
                break;
            }
        }
        if (HelperFunc.isEmpty(caseLink.getLink())) {
            panel.addStyleName("without-link");
        }

        if (fireEvents) {
            ValueChangeEvent.fire(this, value);
        }
    }

    @Override
    public CaseLink getValue() {
        return caseLink;
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<CaseLink> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    @Override
    public boolean isEnabled() {
        return remove.isVisible();
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (!enabled) {
            remove.setVisible(false);
        } else {
            root.addDomHandler(event -> remove.addStyleName("case-link-close-hide"), MouseOutEvent.getType());
            root.addDomHandler(event -> remove.removeStyleName("case-link-close-hide"), MouseOverEvent.getType());
        }
    }

    @UiHandler("root")
    public void onRootClick(ClickEvent event) {
        event.preventDefault();
        if (caseLink != null && HelperFunc.isNotEmpty(caseLink.getLink())) {
            Window.open(caseLink.getLink(),"_blank","");
        }
    }

    @UiHandler("remove")
    public void closeClick(ClickEvent event) {
        event.preventDefault();
        event.stopPropagation();
        if (!remove.isVisible()) {
            return;
        }
        CloseEvent.fire(this, caseLink);
    }

    @Override
    public HandlerRegistration addCloseHandler(CloseHandler<CaseLink> handler) {
        return addHandler(handler, CloseEvent.getType());
    }

    private void fillCaseInfo( CaseInfo value ) {
        fillData( value.getName(),
                En_ImportanceLevel.getById( value.getImpLevel() ),
                En_CaseState.getById( value.getStateId() )
        );
        fillCompletionState(En_CaseState.getById( value.getStateId() ));
    }

    private void processYouTrackInfo( YouTrackIssueInfo youTrackInfo ) {
        if (youTrackInfo == null) {
            text.addClassName("link-broken");
            return;
        }
        fillData( youTrackInfo.getSummary(), youTrackInfo.getImportance(), youTrackInfo.getCaseState());
        fillCompletionState(youTrackInfo.getCaseState());
    }

    private void fillCompletionState( En_CaseState caseState ) {
        if(doneStates.contains( caseState )) {
            text.addClassName("line-through");
        }
    }

    private void fillData(String name, En_ImportanceLevel importanceLevel, En_CaseState caseState ) {
        header.setInnerText( name );
        importance.addClassName( ImportanceStyleProvider.getImportanceIcon( importanceLevel ));
        state.setInnerHTML(caseStateLang.getStateName(caseState) + "<i class=\"fas fa-circle m-l-5 state-" + caseState.toString().toLowerCase() + "\"></i>");
    }

    private void setTestAttributes() {
        remove.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.ISSUE.LINK_REMOVE_BUTTON);
    }

    @Inject
    static En_CaseStateLang caseStateLang;
    @UiField
    Lang lang;

    @UiField
    HTMLPanel panel;
    @UiField
    SpanElement text;
    @UiField
    Anchor remove;
    @UiField
    SpanElement header;
    @UiField
    SpanElement importance;
    @UiField
    SpanElement state;
    @UiField
    FocusPanel root;

    Set<En_CaseState> doneStates = setOf( DONE, VERIFIED, CANCELED, CLOSED, SOLVED_DUP, SOLVED_FIX, SOLVED_NOAP, IGNORED );

    private CaseLink caseLink = null;

    interface CaseLinkViewUiBinder extends UiBinder<FocusPanel, CaseLinkItem> {}
    private static CaseLinkViewUiBinder ourUiBinder = GWT.create(CaseLinkViewUiBinder.class);
}
