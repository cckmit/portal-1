package ru.protei.portal.ui.issueassignment.client.view.desk.rowissue.issue;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.ui.common.client.common.ImportanceStyleProvider;
import ru.protei.portal.ui.common.client.lang.En_CaseStateLang;
import ru.protei.portal.ui.issueassignment.client.activity.desk.rowissue.issue.AbstractDeskIssueView;

public class DeskIssueView extends Composite implements AbstractDeskIssueView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void setWarningHighlight() {
        root.addStyleName("warning");
    }

    @Override
    public void setImportance(En_ImportanceLevel importance) {
        if (importance == null) {
            this.importance.setClassName("hide");
        } else {
            this.importance.setClassName(ImportanceStyleProvider.getImportanceIcon(importance));
        }
    }

    @Override
    public void setState(En_CaseState state) {
        if (state == null) {
            this.state.setClassName("hide");
        } else {
            this.state.setClassName("label label-" + state.toString().toLowerCase());
            this.state.setInnerText(caseStateLang.getStateName(state));
        }
    }

    @Override
    public void setPrivacy(boolean isPrivate) {
        if (!isPrivate) {
            this.privacy.addClassName("hide");
        }
    }

    @Override
    public void setNumber(long number) {
        this.number.setInnerHTML(String.valueOf(number));
    }

    @Override
    public void setName(String name) {
        this.name.setInnerHTML(name);
    }

    @Override
    public void setInitiatorCompany(String initiatorCompany) {
        this.company.setInnerHTML(initiatorCompany);
    }

    @Override
    public void setInitiatorName(String initiatorName) {
        this.person.setInnerHTML(initiatorName);
    }

    @Override
    public void setProduct(String product) {
        this.product.setInnerHTML(product);
    }

    @Override
    public void setCreated(String created) {
        this.created.setInnerHTML(created);
    }

    @Override
    public void setModified(String modified) {
        this.modified.setInnerHTML(modified);
    }

    @UiHandler("openButton")
    public void openButtonClick(ClickEvent event) {
        if (handler != null) {
            handler.onOpen();
        }
    }

    @UiHandler("optionsButton")
    public void optionsButtonClick(ClickEvent event) {
        if (handler != null) {
            handler.onOptions();
        }
    }

    @Inject
    En_CaseStateLang caseStateLang;

    @UiField
    HTMLPanel root;
    @UiField
    Button openButton;
    @UiField
    Button optionsButton;
    @UiField
    SpanElement importance;
    @UiField
    SpanElement state;
    @UiField
    SpanElement privacy;
    @UiField
    SpanElement number;
    @UiField
    SpanElement name;
    @UiField
    SpanElement company;
    @UiField
    SpanElement person;
    @UiField
    DivElement product;
    @UiField
    SpanElement created;
    @UiField
    SpanElement modified;

    private Handler handler;

    interface DeskIssueViewBinder extends UiBinder<HTMLPanel, DeskIssueView> {}
    private static DeskIssueViewBinder ourUiBinder = GWT.create(DeskIssueViewBinder.class);
}
