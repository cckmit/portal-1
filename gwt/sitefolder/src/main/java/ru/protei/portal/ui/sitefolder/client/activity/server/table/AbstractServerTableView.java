package ru.protei.portal.ui.sitefolder.client.activity.server.table;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.Server;

import java.util.List;

public interface AbstractServerTableView extends IsWidget {
    void addRecords(List<Server> servers);

    void setActivity(AbstractServerTableActivity activity);

    HasValue<String> nameOrIp();

    HasValue<En_SortField> sortField();

    HasValue<Boolean> sortDir();

    void clearRecords();

    HasVisibility createButtonVisibility();
}
