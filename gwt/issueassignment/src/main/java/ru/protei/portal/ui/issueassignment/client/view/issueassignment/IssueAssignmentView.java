package ru.protei.portal.ui.issueassignment.client.view.issueassignment;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
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
    public UIObject table() {
        return table;
    }

    @Override
    public UIObject desk() {
        return desk;
    }

    @Override
    public HasWidgets tableContainer() {
        return table;
    }

    @Override
    public HasWidgets deskContainer() {
        return desk;
    }

    @UiHandler("toggleTableButton")
    public void toggleTableButtonClick(ClickEvent event) {
        if (activity != null) {
            activity.onToggleTableClicked();
        }
    }

    @UiHandler("reloadButton")
    public void reloadButtonClick(ClickEvent event) {
        if (activity != null) {
            activity.onReloadClicked();
        }
    }

    @UiField
    HTMLPanel table;
    @UiField
    HTMLPanel desk;
    @UiField
    Button toggleTableButton;
    @UiField
    Button reloadButton;

    private AbstractIssueAssignmentActivity activity;

    interface IssueAssignmentViewBinder extends UiBinder<HTMLPanel, IssueAssignmentView> {}
    private static IssueAssignmentViewBinder ourUiBinder = GWT.create(IssueAssignmentViewBinder.class);
}
