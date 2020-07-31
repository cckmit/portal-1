package ru.protei.portal.ui.casestate.client.activity.table;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.events.CaseStateEvents;

import java.util.List;

public interface AbstractCaseStateTableView extends IsWidget {
    void setActivity(AbstractCaseStateTableActivity activity);

    void setAnimation(TableAnimation animation);

    HasWidgets getPreviewContainer ();

    void setData(List<CaseState> result);

    void updateRow(CaseState changedCaseState);

    void clearRecords();

    void clearSelection();
}
