package ru.protei.portal.ui.common.client.view.caselink.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
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
import ru.protei.portal.ui.common.client.activity.caselink.item.AbstractCaseLinkItemActivity;
import ru.protei.portal.ui.common.client.activity.caselink.item.AbstractCaseLinkItemView;
import ru.protei.portal.ui.common.client.common.ImportanceStyleProvider;
import ru.protei.portal.ui.common.client.lang.En_CaseStateLang;
import ru.protei.portal.ui.common.client.lang.Lang;

import java.util.Set;

import static ru.protei.portal.core.model.dict.En_CaseState.*;
import static ru.protei.portal.core.model.helper.CollectionUtils.setOf;
import static ru.protei.portal.test.client.DebugIds.DEBUG_ID_ATTRIBUTE;

public class CaseLinkItemView extends Composite implements AbstractCaseLinkItemView {

    public CaseLinkItemView() {
        initWidget(ourUiBinder.createAndBindUi(this));
        setTestAttributes();
    }

    @Override
    public void setActivity(AbstractCaseLinkItemActivity activity) {
        this.activity = activity;
    }

    @Override
    public void setValue(CaseLink value) {
        this.caseLink = value;
        switch (value.getType()) {
            case CRM: {
                if (value.getCaseInfo() != null) {
                    text.setInnerText(value.getCaseInfo().getCaseNumber().toString());
                    fillCaseInfo(value.getCaseInfo());
                }
                break;
            }
            case YT: {
                text.setInnerText(value.getRemoteId());
                processYouTrackInfo(value.getYouTrackInfo());
                break;
            }
        }

        if (HelperFunc.isEmpty(value.getLink())) {
            panel.addStyleName("without-link");
        }
    }

    @Override
    public CaseLink getValue() {
        return caseLink;
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
        activity.onRemoveClicked(this);
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

    @Inject
    static En_CaseStateLang caseStateLang;

    private Set<En_CaseState> doneStates = setOf( DONE, VERIFIED, CANCELED, CLOSED, SOLVED_DUP, SOLVED_FIX, SOLVED_NOAP, IGNORED );

    private CaseLink caseLink;
    private AbstractCaseLinkItemActivity activity;

    interface CaseLinkViewUiBinder extends UiBinder<FocusPanel, CaseLinkItemView> {}
    private static CaseLinkViewUiBinder ourUiBinder = GWT.create(CaseLinkViewUiBinder.class);
}
