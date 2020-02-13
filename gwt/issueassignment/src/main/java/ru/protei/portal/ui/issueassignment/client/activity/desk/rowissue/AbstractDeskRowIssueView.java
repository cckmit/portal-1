package ru.protei.portal.ui.issueassignment.client.activity.desk.rowissue;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.UIObject;
import ru.protei.portal.core.model.view.CaseShortView;

import java.util.List;

public interface AbstractDeskRowIssueView extends IsWidget {

    void setHandler(Handler handler);

    void setIssues(List<CaseShortView> caseShortViews);

    interface Handler {
        void onOpenIssue(CaseShortView issue);
        void onOpenOptions(UIObject relative, CaseShortView issue);
    }
}
