package ru.protei.portal.ui.issueassignment.client.view.desk.rowissue.issue;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.stringselectpopup.StringSelectPopup;
import ru.protei.portal.ui.issueassignment.client.activity.desk.rowissue.issue.AbstractDeskIssueView;

import java.util.ArrayList;
import java.util.List;

import static ru.protei.portal.ui.common.client.util.ColorUtils.makeContrastColor;

public class DeskIssueView extends Composite implements AbstractDeskIssueView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        actions.clear();
        actions.add(lang.issueAssignmentIssueReassignTo());
        popup.addValueChangeHandler(event -> {
            if (handler == null) {
                return;
            }
            if (lang.issueAssignmentIssueReassignTo().equals(event.getValue())) {
                handler.onOptions(optionsButton);
                return;
            }
        });
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
    public void setImportance(String importanceCode, String importanceColor) {
        if (importanceCode == null) {
            this.importance.setClassName("hide");
        } else {
            this.importance.setInnerText(importanceCode.substring(0, 1).toUpperCase());
            this.importance.getStyle().setBackgroundColor(importanceColor);
            this.importance.getStyle().setColor(makeContrastColor(importanceColor));
        }
    }

    @Override
    public void setState(String state, String color) {
        if (state == null) {
            this.state.setClassName("hide");
        } else {
            this.state.getStyle().setBackgroundColor(color);
            this.state.setInnerText(state);
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

    @Override
    public HasWidgets getTagsContainer() {
        return tagsContainer;
    }

    @UiHandler("openButton")
    public void openButtonClick(ClickEvent event) {
        if (handler != null) {
            handler.onOpen();
        }
    }

    @UiHandler("optionsButton")
    public void optionsButtonClick(ClickEvent event) {
        showOptionsPopup();
    }

    private void showOptionsPopup() {
        if (CollectionUtils.isEmpty(actions)) {
            return;
        }
        popup.setValues(actions);
        popup.showUnderRight(optionsButton, 200);
    }

    @Inject
    @UiField
    Lang lang;

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
    SpanElement product;
    @UiField
    SpanElement created;
    @UiField
    SpanElement modified;
    @UiField
    HTMLPanel tagsContainer;

    private List<String> actions = new ArrayList<>();
    private StringSelectPopup popup = new StringSelectPopup();
    private Handler handler;

    interface DeskIssueViewBinder extends UiBinder<HTMLPanel, DeskIssueView> {}
    private static DeskIssueViewBinder ourUiBinder = GWT.create(DeskIssueViewBinder.class);
}
