package ru.protei.portal.ui.issueassignment.client.activity.desk.rowperson;

import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.view.PersonShortView;

import java.util.List;

public interface AbstractDeskRowPersonView extends IsWidget {

    void setHandler(Handler handler);

    void setPeople(List<PersonShortView> people, int issuesCount);

    void setIconExpanded(boolean isExpanded);

    interface Handler {
        void onEdit();
        void onRemove();
        void onToggleIssuesVisibility();
    }
}
