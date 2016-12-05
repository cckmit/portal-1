package ru.protei.portal.ui.issue.client.activity.simpletable;

import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.ent.CaseObject;

import java.util.List;

/**
 * Created by bondarenko on 01.12.16.
 */
public interface AbstractIssueTableView  extends IsWidget {

    void setActivity( AbstractIssueTableActivity activity );
    void putRecords(List<CaseObject> cases);

}
