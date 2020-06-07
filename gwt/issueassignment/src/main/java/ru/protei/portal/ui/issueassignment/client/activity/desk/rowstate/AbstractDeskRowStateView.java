package ru.protei.portal.ui.issueassignment.client.activity.desk.rowstate;

import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.view.EntityOption;

import java.util.List;

public interface AbstractDeskRowStateView extends IsWidget {

    void setHandler(Handler handler);

    void setStates(List<EntityOption> states, int issuesCount);

    interface Handler {
        void onEdit();
        void onRemove();
    }
}
