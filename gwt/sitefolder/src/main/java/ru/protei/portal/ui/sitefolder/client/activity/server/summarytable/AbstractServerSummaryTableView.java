package ru.protei.portal.ui.sitefolder.client.activity.server.summarytable;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.ent.Server;
import ru.protei.portal.core.model.ent.ServerGroup;
import ru.protei.portal.ui.common.client.animation.TableAnimation;

import java.util.List;
import java.util.function.Function;

public interface AbstractServerSummaryTableView extends IsWidget {

    void setActivity(AbstractServerSummaryTableActivity activity);

    void setAnimation(TableAnimation animation);

    void clearRecords();

    void addRecords(List<Server> roomReservations);

    void updateRow(Server item);

    HasWidgets getPreviewContainer();

    HasWidgets getFilterContainer();

    HasWidgets getPagerContainer();

    void clearSelection();
}
