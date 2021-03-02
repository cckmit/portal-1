package ru.protei.portal.ui.issue.client.view.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import ru.protei.portal.ui.issue.client.activity.item.AbstractAddingIssueCommentHelpItemView;

public class AddingIssueCommentHelpItemView extends Composite implements AbstractAddingIssueCommentHelpItemView {
    public AddingIssueCommentHelpItemView() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void addRootStyle(String style) {
        root.addStyleName(style);
    }

    @Override
    public void setHeader(String header) {
        this.header.setInnerText(header);
    }

    @Override
    public void setHelpText(String helpText) {
        this.helpText.setInnerHTML(helpText);
    }

    @UiField
    HTMLPanel root;

    @UiField
    DivElement header;

    @UiField
    DivElement helpText;

    interface TopBrassItemViewUiBinder extends UiBinder<HTMLPanel, AddingIssueCommentHelpItemView> {}
    private static TopBrassItemViewUiBinder ourUiBinder = GWT.create(TopBrassItemViewUiBinder.class);
}