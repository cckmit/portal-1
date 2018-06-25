package ru.protei.portal.ui.common.client.widget.issuelinks.link;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.CaseLink;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.ui.common.client.activity.caselinkprovider.CaseLinkProvider;

public class IssueLink extends Composite implements HasValue<CaseLink> {

    public IssueLink() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setValue(CaseLink value) {
        setValue(value, false);
    }

    @Override
    public void setValue(CaseLink value, boolean fireEvents) {

        caseLink = value;
        caseLink.setLink(caseLinkProvider.getLink(value.getType(), value.getRemoteId()));

        text.setText(caseLink.getRemoteId());
        switch (caseLink.getType()) {
            case CRM: icon.addStyleName("link-crm"); break;
            case CRM_OLD: icon.addStyleName("link-crm-old"); break;
            case YT: icon.addStyleName("link-you-track"); break;
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

    @UiHandler("root")
    public void onClicked(ClickEvent event) {
        if (caseLink != null && HelperFunc.isNotEmpty(caseLink.getLink())) {
            event.preventDefault();
            Window.open(caseLink.getLink(),"_blank","");
        }
    }

    @UiHandler("remove")
    public void closeClick(ClickEvent event) {
        if (removeHandler != null) {
            event.preventDefault();
            event.stopPropagation();
            removeHandler.onRemove(caseLink);
        }
    }

    public void setRemoveHandler(RemoveEvent removeEvent) {
        this.removeHandler = removeEvent;
    }

    @Inject
    CaseLinkProvider caseLinkProvider;

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

    private CaseLink caseLink = null;
    private RemoveEvent removeHandler = null;

    interface IssueLinkViewUiBinder extends UiBinder<FocusPanel, IssueLink> {}
    private static IssueLinkViewUiBinder ourUiBinder = GWT.create(IssueLinkViewUiBinder.class);
}
