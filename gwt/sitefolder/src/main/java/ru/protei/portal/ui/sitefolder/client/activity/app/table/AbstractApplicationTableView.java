package ru.protei.portal.ui.sitefolder.client.activity.app.table;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.ent.Application;
import ru.protei.portal.ui.common.client.animation.TableAnimation;

public interface AbstractApplicationTableView extends IsWidget {

    void setActivity(AbstractApplicationTableActivity activity);

    void setAnimation(TableAnimation animation);

    void clearRecords();

    void setAppsCount(Long count);

    int getPageSize();

    int getPageCount();

    void scrollTo(int page);

    void updateRow(Application item);

    HasWidgets getPreviewContainer();

    HasWidgets getFilterContainer();
}
