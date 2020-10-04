package ru.protei.portal.ui.issue.client.activity.create.subtask;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;

public interface AbstractSubtaskCreateView extends IsWidget {
    void setActivity(AbstractSubtaskCreateActivity activity);
    HasValue<String> name();
    HasValue<String> description();
    HasValidable nameValidator();
    String DESCRIPTION = "description";
}