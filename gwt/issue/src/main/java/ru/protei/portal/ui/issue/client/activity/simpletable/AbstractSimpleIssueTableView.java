package ru.protei.portal.ui.issue.client.activity.simpletable;

import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.view.CaseShortView;

import java.util.List;

/**
 * Created by bondarenko on 01.12.16.
 */
public interface AbstractSimpleIssueTableView extends IsWidget {

    void setActivity( AbstractSimpleIssueTableActivity activity );
    void putRecords(List<CaseShortView> cases);

}
