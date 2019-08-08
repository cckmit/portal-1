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

        icon.setText(caseLinkLang.getCaseLinkShortName(caseLink.getType()));
        switch (caseLink.getType()) {
            case CRM: {
                icon.addStyleName("link-crm");
                if (value.getCaseInfo() != null) {
                    text.setText(caseLink.getCaseInfo().getCaseNumber().toString());
                    fillCaseInfo(value.getCaseInfo());
                }
                break;
            }
            case CRM_OLD: {
                icon.addStyleName("link-crm-old");
                text.setText(caseLink.getRemoteId());
                break;
            }
            case YT: {
                icon.addStyleName("link-you-track");
                text.setText(caseLink.getRemoteId());
                fillYouTrackInfo(value.getYouTrackInfo());
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
        remove.setVisible(enabled);
    }

    @UiHandler("root")
    public void onClicked(ClickEvent event) {
        if (caseLink != null && HelperFunc.isNotEmpty(caseLink.getLink())) {
            event.preventDefault();
            Window.open(caseLink.getLink(),"_blank","");
        }
    }

    @UiHandler("root")
    public void onMouseOver(MouseOverEvent event) {
        if ( En_CaseLink.CRM_OLD.equals(caseLink.getType()) ){
            return;
        }
        caseInfoPanel.setVisible(true);
    }

    @UiHandler("root")
    public void onMouseOut(MouseOutEvent event) {
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

    private void fillCaseInfo(CaseInfo value) {
        header.setInnerText( value.getName() );
        En_ImportanceLevel importanceLevel = En_ImportanceLevel.getById(value.getImpLevel());
        importance.addClassName(ImportanceStyleProvider.getImportanceIcon( importanceLevel ));
        state.addClassName( "label label-" + En_CaseState.getById( value.getStateId() ).toString().toLowerCase() + " m-r-5");
        state.setInnerText(caseStateLang.getStateName(En_CaseState.getById(value.getStateId())));
        info.setInnerText(value.getInfo());
    }

    private void fillYouTrackInfo( YouTrackIssueInfo youTrackInfo ) {
        if (youTrackInfo == null) return;
        header.setInnerText( youTrackInfo.getSummary() );
        importance.addClassName(ImportanceStyleProvider.getImportanceIcon( youTrackInfo.getImportance() ));
        state.addClassName( "label label-" + youTrackInfo.getCaseState().toString().toLowerCase() + " m-r-5");
        state.setInnerText(caseStateLang.getStateName(youTrackInfo.getCaseState()));
        info.setInnerText(youTrackInfo.getDescription());
    }

    @Inject
    En_CaseLinkLang caseLinkLang;
    @Inject
    En_CaseStateLang caseStateLang;

    @Inject
    @UiField
    Lang lang;

    @UiField
    FocusPanel root;
    @UiField
    HTMLPanel panel;
    @UiField
    InlineLabel text;
    @UiField
    InlineLabel icon;
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

    private CaseLink caseLink = null;

    interface CaseLinkViewUiBinder extends UiBinder<FocusPanel, CaseLinkView> {}
    private static CaseLinkViewUiBinder ourUiBinder = GWT.create(CaseLinkViewUiBinder.class);
}
