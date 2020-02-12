package ru.protei.portal.ui.issueassignment.client.view.desk.rowissue;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.inject.Inject;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.ui.issueassignment.client.activity.desk.rowissue.AbstractDeskRowIssueView;

import java.util.List;

public class DeskRowIssueView extends Composite implements AbstractDeskRowIssueView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void setIssues(List<CaseShortView> caseShortViews) {

    }

    private Handler handler;

    interface DeskRowIssueViewBinder extends UiBinder<HTMLPanel, DeskRowIssueView> {}
    private static DeskRowIssueViewBinder ourUiBinder = GWT.create(DeskRowIssueViewBinder.class);
}
