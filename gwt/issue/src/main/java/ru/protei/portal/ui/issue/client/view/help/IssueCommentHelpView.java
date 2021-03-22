package ru.protei.portal.ui.issue.client.view.help;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import ru.protei.portal.ui.issue.client.activity.help.AbstractIssueCommentHelpActivity;
import ru.protei.portal.ui.issue.client.activity.help.AbstractIssueCommentHelpView;

public class IssueCommentHelpView extends Composite implements AbstractIssueCommentHelpView {
    public IssueCommentHelpView() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setActivity(AbstractIssueCommentHelpActivity activity) {
        this.activity = activity;
    }

    @Override
    public DivElement helpTextContainer() {
        return helpTextContainer;
    }

    @UiField
    DivElement helpTextContainer;

    private AbstractIssueCommentHelpActivity activity;

    interface IssueCommentHelpViewUiBinder extends UiBinder<HTMLPanel, IssueCommentHelpView> {}
    private static IssueCommentHelpViewUiBinder ourUiBinder = GWT.create(IssueCommentHelpViewUiBinder.class);
}