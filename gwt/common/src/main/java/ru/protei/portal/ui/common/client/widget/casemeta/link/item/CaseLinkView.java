package ru.protei.portal.ui.common.client.widget.casemeta.link.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.*;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.ent.CaseLink;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.activity.caselinkprovider.CaseLinkProvider;
import ru.protei.portal.ui.common.client.lang.Lang;

import java.util.Objects;

import static ru.protei.portal.core.model.dict.En_CaseLink.CRM;
import static ru.protei.portal.core.model.dict.En_CaseLink.YT;
import static ru.protei.portal.test.client.DebugIds.DEBUG_ID_ATTRIBUTE;

public class CaseLinkView extends Composite implements HasValue<CaseLink>, HasCloseHandlers<CaseLink>, HasEnabled{

    public CaseLinkView() {
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
        String linkId = null;
        if ( Objects.equals(value.getType(), CRM) && value.getCaseInfo() != null) {
            linkId = String.valueOf(value.getCaseInfo().getCaseNumber());
            number.setText(lang.crmPrefix() + linkId);
            header.setText(value.getCaseInfo().getName());

            setState(En_CaseState.getById(value.getCaseInfo().getStateId()));
        } else if ( Objects.equals(value.getType(), YT) && value.getYouTrackInfo() != null) {
            linkId = value.getRemoteId();
            number.setText(linkId);
            header.setText(value.getYouTrackInfo().getSummary());

            setState(value.getYouTrackInfo().getCaseState());
        }

        setHref(caseLinkProvider.getLink(value.getType(), linkId));

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

    private void setState(En_CaseState value) {
        if (value == null) return;
        state.addClassName("state-" + value.name().toLowerCase());
        if ( value.isTerminalState() ) {
            addStyleName("case-link-completed");
        }
    }

    private void setHref(String link) {
        if (HelperFunc.isEmpty(link)) {
            number.addStyleName("without-link");
            header.addStyleName("without-link");
        } else {
            number.setHref(link);
            header.setHref(link);
        }
    }

    private void setTestAttributes() {
        root.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.ISSUE.LINK_ELEMENT);
        number.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.ISSUE.LINK_NUMBER);
        header.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.ISSUE.LINK_DESCRIPTION);
        state.setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.ISSUE.LINK_STATE);
        remove.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.ISSUE.LINK_REMOVE_BUTTON);
    }

    @UiField
    Lang lang;

    @UiField
    Anchor remove;
    @UiField
    Anchor header;
    @UiField
    Element state;
    @UiField
    HTMLPanel root;
    @UiField
    Anchor number;

    @Inject
    CaseLinkProvider caseLinkProvider;

    private CaseLink caseLink = null;

    interface CaseLinkViewUiBinder extends UiBinder<HTMLPanel, CaseLinkView> {}
    private static CaseLinkViewUiBinder ourUiBinder = GWT.create(CaseLinkViewUiBinder.class);
}
