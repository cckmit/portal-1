package ru.protei.portal.ui.issueassignment.client.view.desk.rowissue;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.inject.Inject;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.issueassignment.client.activity.desk.rowissue.AbstractDeskRowIssueView;
import ru.protei.portal.ui.issueassignment.client.activity.desk.rowissue.issue.AbstractDeskIssueView;

import javax.inject.Provider;
import java.util.Date;
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
        view.setImportance(issue.getImportanceCode());
        view.setState(issue.getStateName(), issue.getStateColor());
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
        if (handler != null) {
            handler.showTags(view.getTagsContainer(), issue.getTags());
        }
        if (isCardShouldBeHighlightedWarnLevel(issue)) {
            view.setWarningHighlight();
        }
        view.setHandler(new AbstractDeskIssueView.Handler() {
            @Override
            public void onOpen() {
                if (handler != null) {
                    handler.onOpenIssue(issue);
                }
            }
            @Override
            public void onOptions(UIObject relative) {
                if (handler != null) {
                    handler.onOpenOptions(relative, issue);
                }
            }
        });
        return view;
    }

    private boolean isCardShouldBeHighlightedWarnLevel(CaseShortView issue) {
        Date modified = issue.getModified();
        if (modified != null) {
            Date threshold = makeDateWithWeekShift(-ISSUE_MODIFIED_VALUE_THRESHOLD_WEEKS);
            if (modified.before(threshold)) {
                return true;
            }
        }
        return false;
    }

    private Date makeDateWithWeekShift(int weeks) {
        long now = System.currentTimeMillis();
        long shift = now + weeks * WEEK_MILLIS;
        return new Date(shift);
    }

    @Inject
    Provider<AbstractDeskIssueView> issueViewProvider;

    @UiField
    HTMLPanel root;

    private Handler handler;
    private final static int ISSUE_MODIFIED_VALUE_THRESHOLD_WEEKS = 1;
    private final static long WEEK_MILLIS = 7 * 24 * 60 * 60 * 1000;

    interface DeskRowIssueViewBinder extends UiBinder<HTMLPanel, DeskRowIssueView> {}
    private static DeskRowIssueViewBinder ourUiBinder = GWT.create(DeskRowIssueViewBinder.class);
}
