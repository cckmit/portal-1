package ru.protei.portal.ui.issueassignment.client.activity.desk.rowstate;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.UIObject;
import ru.protei.portal.core.model.view.EntityOption;

import java.util.List;

public interface AbstractDeskRowStateView extends IsWidget {

    UIObject rootContainer();

    void setHandler(Handler handler);

    void setStates(List<EntityOption> states, int issuesCount);

    interface Handler {
        void onEdit();
        void onRemove();
    }
}
