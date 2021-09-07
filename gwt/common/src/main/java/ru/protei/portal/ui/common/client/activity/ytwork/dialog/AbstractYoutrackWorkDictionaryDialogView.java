package ru.protei.portal.ui.common.client.activity.ytwork.dialog;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.ent.YoutrackProject;

import java.util.Set;

public interface AbstractYoutrackWorkDictionaryDialogView extends IsWidget {
    HasValue<String> name();

    HasValue<Set<YoutrackProject>> projects();

    void refreshProjects();
}
