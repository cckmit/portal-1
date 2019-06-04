package ru.protei.portal.ui.project.client.activity.table.detailed;

import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.struct.ProjectInfo;

import java.util.List;

public interface AbstractProjectDetailedTableView extends IsWidget {

    void setActivity( AbstractProjectDetailedTableActivity activity );
    void addRecords( List< ProjectInfo > projects );
    void clearRecords();
}
