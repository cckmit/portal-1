package ru.protei.portal.ui.issueassignment.client.view.desk.rowissue;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.issueassignment.client.activity.desk.rowissue.AbstractDeskRowIssueView;
import ru.protei.portal.ui.issueassignment.client.activity.desk.rowissue.issue.AbstractDeskIssueView;

import javax.inject.Provider;
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
        root.clear();
        for (CaseShortView issue : caseShortViews) {
            AbstractDeskIssueView view = makeIssueView(issue);
            root.add(view.asWidget());
        }
    }

    private AbstractDeskIssueView makeIssueView(CaseShortView issue) {
        AbstractDeskIssueView view = issueViewProvider.get();
        view.setImportance(En_ImportanceLevel.getById(issue.getImpLevel()));
        view.setState(En_CaseState.getById(issue.getStateId()));
        view.setPrivacy(issue.isPrivateCase());
        view.setNumber(issue.getCaseNumber());
        view.setName(issue.getName());
        view.setInitiatorCompany(issue.getInitiatorCompanyName());
        view.setInitiatorName(StringUtils.isNotEmpty(issue.getInitiatorShortName())
                ? "(" + issue.getInitiatorShortName() + ")"
                : "");
        view.setProduct(issue.getProductName());
        view.setCreated(issue.getCreated() != null
                ? DateFormatter.formatDateTime(issue.getCreated())
                : "");
        view.setModified(issue.getModified() != null
                ? DateFormatter.formatDateTime(issue.getModified())
                : "");
        view.setHandler(new AbstractDeskIssueView.Handler() {
            @Override
            public void onOpen() {
                if (handler != null) {
                    handler.onOpenIssue(issue);
                }
            }
            @Override
            public void onOptions() {
                if (handler != null) {
                    handler.onOpenOptions(issue);
                }
            }
        });
        return view;
    }

    @Inject
    Provider<AbstractDeskIssueView> issueViewProvider;

    @UiField
    HTMLPanel root;

    private Handler handler;

    interface DeskRowIssueViewBinder extends UiBinder<HTMLPanel, DeskRowIssueView> {}
    private static DeskRowIssueViewBinder ourUiBinder = GWT.create(DeskRowIssueViewBinder.class);
}
