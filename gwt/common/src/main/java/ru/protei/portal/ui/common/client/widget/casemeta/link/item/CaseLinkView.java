package ru.protei.portal.ui.common.client.widget.casemeta.link.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.HeadingElement;
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
import ru.protei.portal.core.model.dict.En_CaseLink;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.ent.CaseInfo;
import ru.protei.portal.core.model.ent.CaseLink;
import ru.protei.portal.core.model.ent.YouTrackIssueInfo;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.ui.common.client.common.ImportanceStyleProvider;
import ru.protei.portal.ui.common.client.lang.En_CaseLinkLang;
import ru.protei.portal.ui.common.client.lang.En_CaseStateLang;
import ru.protei.portal.ui.common.client.lang.Lang;

import java.util.Set;

import static ru.protei.portal.core.model.dict.En_CaseState.*;
import static ru.protei.portal.core.model.helper.CollectionUtils.setOf;

public class CaseLinkView extends Composite implements HasValue<CaseLink>, HasCloseHandlers<CaseLink>, HasEnabled{


    public CaseLinkView() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setValue(CaseLink value) {
        setValue(value, false);
    }

    @Override
    public void setValue(CaseLink value, boolean fireEvents) {
        caseLink = value;
        icon.setInnerText(caseLinkLang.getCaseLinkShortName(caseLink.getType()));
        switch (caseLink.getType()) {
            case CRM: {
                icon.addClassName("link-crm");
                if (value.getCaseInfo() != null) {
                    text.setInnerText(caseLink.getCaseInfo().getCaseNumber().toString());
                    fillCaseInfo(value.getCaseInfo());
                }
                break;
            }
            case CRM_OLD: {
                icon.addClassName("link-crm-old");
                text.setInnerText(caseLink.getRemoteId());
                break;
            }
            case YT: {
                icon.addClassName("link-you-track");
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
        return remove.isEnabled();
    }

    @Override
    public void setEnabled(boolean enabled) {
        remove.setVisible(enabled);
    }

    @UiHandler("link")
    public void onClicked(ClickEvent event) {
        event.preventDefault();
        if (caseLink != null && HelperFunc.isNotEmpty(caseLink.getLink())) {
            event.preventDefault();
            Window.open(caseLink.getLink(),"_blank","");
        }
    }

    @UiHandler("link")
    public void onHover(MouseOverEvent event) {
        if ( !En_CaseLink.CRM.equals(caseLink.getType()) ){
            return;
        }
        caseInfoPanel.setVisible(true);
    }

    @UiHandler("root")
    public void onMouseOver(MouseOverEvent event) {
        if ( En_CaseLink.CRM_OLD.equals(caseLink.getType()) ){
            return;
        }
        if ( !hasInfo(caseLink) ){
            return;
        }
        caseInfoPanel.setVisible(true);
    }

    @UiHandler("link")
    public void onHover(MouseOutEvent event) {
        caseInfoPanel.setVisible(false);
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
        fillPopup( value.getName(), value.getInfo(),
                En_ImportanceLevel.getById( value.getImpLevel() ),
                En_CaseState.getById( value.getStateId() )
        );
        fillCompletionState(En_CaseState.getById( value.getStateId() ));
    }

    private void processYouTrackInfo( YouTrackIssueInfo youTrackInfo ) {
        if (youTrackInfo == null) {
            panel.getElement().addClassName( "link-broken" );
            return;
        }
        fillPopup( youTrackInfo.getSummary(), youTrackInfo.getDescription(),
                youTrackInfo.getImportance(), youTrackInfo.getCaseState()
        );
        fillCompletionState(youTrackInfo.getCaseState());
    }

    private void fillCompletionState( En_CaseState caseState ) {
        if(doneStates.contains( caseState )) {
            panel.getElement().addClassName( "link-completed" );
        }
    }

    private void fillPopup( String name, String description, En_ImportanceLevel importanceLevel, En_CaseState caseState ) {
        header.setInnerText( name );
        importance.addClassName( ImportanceStyleProvider.getImportanceIcon( importanceLevel ));
        state.addClassName( "label label-" + caseState.toString().toLowerCase() + " m-r-5");
        state.setInnerText(caseStateLang.getStateName(caseState));
        info.setInnerText(description);
    }

    private boolean hasInfo( CaseLink caseLink ) {
        return caseLink.getCaseInfo()!=null || caseLink.getYouTrackInfo()!=null;
    }

    @Inject
    En_CaseLinkLang caseLinkLang;
    @Inject
    En_CaseStateLang caseStateLang;
    @Inject
    @UiField
    Lang lang;

    @UiField
    HTMLPanel panel;
    @UiField
    SpanElement text;
    @UiField
    SpanElement icon;
    @UiField
    Anchor remove;
    @UiField
    HTMLPanel caseInfoPanel;
    @UiField
    HeadingElement header;
    @UiField
    SpanElement importance;
    @UiField
    SpanElement state;
    @UiField
    DivElement info;
    @UiField
    Anchor link;

   Set<En_CaseState> doneStates = setOf( DONE, VERIFIED, CANCELED, CLOSED, SOLVED_DUP, SOLVED_FIX, SOLVED_NOAP, IGNORED );

    private CaseLink caseLink = null;

    interface CaseLinkViewUiBinder extends UiBinder<FocusPanel, CaseLinkView> {}
    private static CaseLinkViewUiBinder ourUiBinder = GWT.create(CaseLinkViewUiBinder.class);
}
