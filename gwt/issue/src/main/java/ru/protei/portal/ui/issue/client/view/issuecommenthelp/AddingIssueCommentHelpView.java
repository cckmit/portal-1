package ru.protei.portal.ui.issue.client.view.issuecommenthelp;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import ru.protei.portal.ui.issue.client.activity.issuecommenthelp.AbstractAddingIssueCommentHelpActivity;
import ru.protei.portal.ui.issue.client.activity.issuecommenthelp.AbstractAddingIssueCommentHelpView;

public class AddingIssueCommentHelpView extends Composite implements AbstractAddingIssueCommentHelpView {
    public AddingIssueCommentHelpView() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setActivity(AbstractAddingIssueCommentHelpActivity activity) {
        this.activity = activity;
    }

    @Override
    public HasWidgets textContainer() {
        return textContainer;
    }

    @UiField
    HTMLPanel textContainer;

    private AbstractAddingIssueCommentHelpActivity activity;

    interface TopBrassViewUiBinder extends UiBinder<HTMLPanel, AddingIssueCommentHelpView> {}
    private static TopBrassViewUiBinder ourUiBinder = GWT.create(TopBrassViewUiBinder.class);
}