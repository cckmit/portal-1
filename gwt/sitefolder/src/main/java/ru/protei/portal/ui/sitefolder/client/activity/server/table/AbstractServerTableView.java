package ru.protei.portal.ui.sitefolder.client.activity.server.table;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.ent.Server;
import ru.protei.portal.ui.common.client.animation.TableAnimation;

public interface AbstractServerTableView extends IsWidget {

    void setActivity(AbstractServerTableActivity activity);

    void setAnimation(TableAnimation animation);

    void clearRecords();

    void setServersCount(Long count);

    int getPageSize();

    int getPageCount();

    void scrollTo(int page);

    void updateRow(Server item);

    HasWidgets getPreviewContainer();

    HasWidgets getFilterContainer();
}
