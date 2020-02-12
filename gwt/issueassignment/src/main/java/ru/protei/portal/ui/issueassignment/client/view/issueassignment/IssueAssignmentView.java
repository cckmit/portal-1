package ru.protei.portal.ui.issueassignment.client.view.issueassignment;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.protei.portal.ui.issueassignment.client.activity.issueassignment.AbstractIssueAssignmentActivity;
import ru.protei.portal.ui.issueassignment.client.activity.issueassignment.AbstractIssueAssignmentView;

public class IssueAssignmentView extends Composite implements AbstractIssueAssignmentView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setActivity(AbstractIssueAssignmentActivity activity) {
        this.activity = activity;
    }

    @Override
    public HasWidgets tableContainer() {
        return table;
    }

    @Override
    public HasWidgets deskContainer() {
        return desk;
    }

    @UiField
    HTMLPanel table;
    @UiField
    HTMLPanel desk;

    private AbstractIssueAssignmentActivity activity;

    interface IssueAssignmentViewBinder extends UiBinder<HTMLPanel, IssueAssignmentView> {}
    private static IssueAssignmentViewBinder ourUiBinder = GWT.create(IssueAssignmentViewBinder.class);
}
